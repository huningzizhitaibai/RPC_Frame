package com.huning.yurpc.registry;

import com.huning.yurpc.model.ServiceMetaInfo;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceCache {

    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     * @param serviceName
     * @param newServiceMetaInfos
     */
    void writeCache(String serviceName, List<ServiceMetaInfo> newServiceMetaInfos) {
        this.serviceCache.put(serviceName, newServiceMetaInfos);
    }


    /**
     * 读缓存
     * @param serviceName
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceName) {
        return this.serviceCache.get(serviceName);
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache.clear();
    }
}