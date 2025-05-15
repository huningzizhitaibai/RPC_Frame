package com.huning.yurpc.registry;

import com.huning.yurpc.config.RegistryConfig;
import com.huning.yurpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

/**
 * 注册中心测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">程序员鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://121.36.203.34:2379");
        registry.init(registryConfig);
    }

    @Test
    public void heartBeatTest() throws Exception {
        //调用下方的register执行注册
        register();
        //阻塞这个函数运行, 看看能否实现heartBeat
        Thread.sleep(60 * 1000L);
        serviceDestroy();
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("121.36.203.34");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("121.36.203.34");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("121.36.203.34");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("121.36.203.34");
        serviceMetaInfo.setServicePort(1235);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }

    @Test
    public void serviceDestroy() {
        registry.destroy();
    }

    @Test
    public void watchTest() throws Exception {
        //服务注册
        register();
        //第一次查询, 从注册中心
        serviceDiscovery();

        //第二次查询, 从本地缓存
        serviceDiscovery();
//        Thread.sleep(60 *1000L);
        //注销其中一个服务
        unRegister();

//        Thread.sleep(60 * 1000L);
        //第三次查询, 从注册中心
        serviceDiscovery();

//        Thread.sleep(60* 1000L);
        serviceDestroy();

    }

}
