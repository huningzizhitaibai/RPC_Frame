package com.huning.yurpc.registry;

import com.huning.yurpc.config.RegistryConfig;
import com.huning.yurpc.model.ServiceMetaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定义了对于yuRPC框架下, 注册中心应该具有的一些功能
 * 当然这些注册中心可以有不同的实现,
 */
public interface Registry {
    /**
     * 初始化
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 销毁
     */
    void destroy();

    void heartBeat();

    void watch(String serviceNodeKey);
}
