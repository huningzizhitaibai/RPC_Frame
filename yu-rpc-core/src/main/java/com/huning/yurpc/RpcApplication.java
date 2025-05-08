package com.huning.yurpc;

import com.huning.yurpc.config.RegistryConfig;
import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.constant.RpcConstant;
import com.huning.yurpc.registry.Registry;
import com.huning.yurpc.registry.RegistryFactory;
import com.huning.yurpc.spi.SpiLoader;
import com.huning.yurpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;


    //在newConfig中可以指定使用哪个config对象进行初始化
    public static void init(RpcConfig newConfig) {
        //框架相关的初始化
        rpcConfig = newConfig;
        log.info("rpc init, config = {}", newConfig.toString());

        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, registry = {}", registry.toString());


        //在当前服务节点jvm停止服务时, 意味着节点下线, 需要执行destroy操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 默认初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if(rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if(rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
