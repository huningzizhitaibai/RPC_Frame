package com.huning.yurpc.loadbalancer;

import com.huning.yurpc.model.ServiceMetaInfo;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LoadBalancerTest {
    final LoadBalancer loadBalancer = new ConsistentHashLoaderBalancer();

    @Test
    public void select() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "apple");

        //构建单元测试的模式有点像是mock, 直接提供测试函数所需要的数据, 只是看方法接收到数据后的逻辑是否有问题.
    }
}
