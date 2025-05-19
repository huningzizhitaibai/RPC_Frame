package com.huning.yurpc.loadbalancer;

import com.huning.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoaderBalancer implements LoadBalancer{
    /**
     * 一致性hash环, 存放虚拟节点
     * 由于需要通过hash值的远近定义节点远近, 同时需要获取到对应的ServiceMetaInfo
     * 使用TreeNode结构, 会对Key进行排序, 同时根据Key进行查找时也满足hash
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_SIZE = 100;

    /**
     *
     * @param requestParams 请求参数, 存放的是调用请求的hash值
     * @param serviceMetaInfoList 所有可用服务的列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        //构建虚拟节点环
        for(ServiceMetaInfo serviceMetaInfo: serviceMetaInfoList){
            for (int i = 0; i< VIRTUAL_NODE_SIZE; i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        //获取调用请求的哈希值
        int hash = getHash(requestParams);

        //根据请求id的hash值, 分配最近的hash节点, 能在一定程度上分散请求
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){
            //当前环是抽象的, 其实首尾连接
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();

    }

    private int getHash(Object key){
        return key.hashCode();
    }
}
