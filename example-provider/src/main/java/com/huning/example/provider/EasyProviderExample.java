package com.huning.example.provider;

import com.huning.example.common.service.UserService;
import com.huning.yurpc.registry.LocalRegistry;
import com.huning.yurpc.server.HttpServer;
import com.huning.yurpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        //哪一个名称的服务, 被哪一个服务类实现, 一个接口可以换不同的实现
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //提供服务
        //创建一个接口类对象, 指向一个实现了这个接口的实例, 方便运用不同的实现实例
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
