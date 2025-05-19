package com.huning.yurpc.loadbalancer;

import com.huning.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 当用户请求服务时, 通过代理经过这个LoaderBalancer
 * 其中的select方法通过策略, 选取合适的节点发送请求
 */
public interface LoadBalancer {
    /**
     * 选择服务, 通过策略选择合适的节点, 将requestParams构造成请求发送给节点.
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 所有可用服务的列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
