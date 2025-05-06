package com.huning.yurpc.registry;

import com.huning.yurpc.spi.SpiLoader;

public class RegistryFactory {
    //加载这个类的相关的所有包, 因为所有的不同类型的Registry
    // 都需要实现这个接口才能够满足RPC框架的使用标准
    static {
        SpiLoader.load(Registry.class);
    }

    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例,根据用户编写的配置文件
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class,key);
    }
}
