package com.huning.yurpc.config;

import com.huning.yurpc.loadbalancer.LoadBalancer;
import com.huning.yurpc.loadbalancer.LoadBalancerKeys;
import com.huning.yurpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "yu-rpc";

    private String version = "1.0.0";

    private String host ="localhost";

    private Integer serverPort = 8080;

    /**
     * 是否开启mock功能
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private  RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
}
