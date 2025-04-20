package com.huning.yurpc;

import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.constant.RpcConstant;
import com.huning.yurpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;


    public static void init(RpcConfig newConfig) {
        rpcConfig = newConfig;
        log.info("rpc init, config = {}", newConfig.toString());
    }

    /**
     * 初始化
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
