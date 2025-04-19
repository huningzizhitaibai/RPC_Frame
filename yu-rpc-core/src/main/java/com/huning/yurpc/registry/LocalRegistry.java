package com.huning.yurpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 */
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName
     * @param implClass
     *用于服务的注册
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 查询服务
     *
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除失效服务
     *
     * @param serviceName
     *
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }



}
