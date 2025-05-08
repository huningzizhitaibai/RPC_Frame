package com.huning.yurpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.huning.yurpc.config.RegistryConfig;
import com.huning.yurpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {
    /**
     * 创建一个etcd的client端与etcd服务端进行交互
     * 这个client相当于是一个总的交互客户端. 和其设计思路有关.
     * 比如直接通过etcd启动的就是服务, 而需要通过etcdctl脚本才能进行值交互.
     * 通过client获取kvclient, leaseclient这些特殊工作的client
     */
    private Client client;

    /**
     * 由于很多的方法都需要使用到kvclient(应该也就是设置键值对的交互client)所以选择将这个client设置成了一个类变量
     */
    private KV kvClient;

    /**
     * 设置根节点
     * 默认将所有有关于rpc的相关数据存储在'/rpc/'目录下
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    //用于记录所有注册的节点服务
    private final Set<String> localRegistryNodeSet = new HashSet<>();

    /**
     * 初始化etcd服务控制端
     * @param registryConfig
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())) //使用Duration将Long转化为Millis单位毫秒
                .build();
        kvClient = client.getKVClient();

        /**
         * 该类是一个服务类或客户类本地的Etcd交互类, 其实就是本地的一个客户端
         * 所以直接在初始化时, 就开启这个heartBeat定时任务即可
         */
        heartBeat();


    }


    /**
     * 注册器, 用于注册服务
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{
        //创建一个lease的client用于获取lease
        Lease leaseClient = client.getLeaseClient();
        //获取一个创建一个lease获取其id
        long leaseId = leaseClient.grant(30).get().getID();

        //记录就是存储在根路径下,记录提供服务的Node地址和service的名称与version作为键名
        //注册中心需要的是查找服务和提供服务的对应地址, 所以需要同时存储节点的名称, 作为value
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();

        //将对应服务的存储路径作为键名, 相关信息作为value
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);


        /**
         * 创建一个kv的相关信息PutOption可以看作是写入的选项, 在这里只在选项中创建一个lease, 将这个lease放入
         * 在创建kv时, 就会根据这个PutOption进行kv的相关设置.
         */
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        //kvClient应该就是Key-Value的客户端.
        kvClient.put(key, value, putOption).get();

        localRegistryNodeSet.add(registerKey);
    }


    /**
     * 服务注销
     * @param serviceMetaInfo
     */
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH +serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        localRegistryNodeSet.remove(serviceMetaInfo.getServiceNodeKey());
    }

    /**
     * 服务搜索查询
     * @param serviceKey
     * @return
     */
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //前缀搜索,结尾一定要加'/'
        //应为存储结构为ETCD_ROOT_PATH + serviceKey + host:port
        String searchPrefix = ETCD_ROOT_PATH +serviceKey + '/';

        try {
            //确定查询为前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();

            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefix,StandardCharsets.UTF_8),
                    getOption).get().getKvs();

            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    /**
     * 下线节点
     */
    public void destroy() {
        System.out.println("当前节点下线");

        //主动注销节点服务, 否则可能会存在至多ttl时间的空窗期
        for (String nodeKey : localRegistryNodeSet) {
            try{
                kvClient.delete(ByteSequence.from(nodeKey, StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(nodeKey + "节点下线失败");
            }
        }

        //关闭etcd连接
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    public void heartBeat() {
        //10秒续约一次
        CronUtil.schedule("*/10 * * * * *", new Task(){
            @Override
            public void execute() {
                //遍历本节点所有的key
                for (String key : localRegistryNodeSet) {
                    try{
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();

                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key+"续签失败");
                    }

                }
            }
        });

        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }


}
