package com.huning.example.provider;

import com.huning.example.common.service.UserService;
import com.huning.yurpc.RpcApplication;
import com.huning.yurpc.config.RegistryConfig;
import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.model.ServiceMetaInfo;
import com.huning.yurpc.registry.LocalRegistry;
import com.huning.yurpc.registry.Registry;
import com.huning.yurpc.registry.RegistryFactory;
import com.huning.yurpc.server.HttpServer;
import com.huning.yurpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //初始化框架
        RpcApplication.init();

        //注册服务
        //哪一个名称的服务, 被哪一个服务类实现, 一个接口可以换不同的实现
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        //将服务注册到注册中心
        RpcConfig rpcConfig = new RpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try{
            registry.register(serviceMetaInfo);
        }catch (Exception e){
            throw new RuntimeException(e);
        }


        //提供服务
        //创建一个接口类对象, 指向一个实现了这个接口的实例, 方便运用不同的实现实例
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
