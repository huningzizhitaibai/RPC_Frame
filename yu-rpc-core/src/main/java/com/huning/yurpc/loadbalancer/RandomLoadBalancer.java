package com.huning.yurpc.loadbalancer;

import com.huning.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()){
            return null;
        }
        if (serviceMetaInfoList.size() == 1){
            return serviceMetaInfoList.get(0);
        }
        int index = random.nextInt(serviceMetaInfoList.size());
        return serviceMetaInfoList.get(index);
    }
}
