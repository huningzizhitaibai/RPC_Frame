package com.huning.yurpc.server;

import io.vertx.core.Vertx;

//框架封装了一个web服务器, 方便provider进行启动
//provider只需要注重处理逻辑的编写就行, 然后通过提供的方法进行注册, 直接启动提供服务即可

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建Vertx实例
        //文章中指出,Vertx并不是一个web Server, 应该就是一个调度器, 对异步编程进行调度用的.
        Vertx vertx = Vertx.vertx();

        //创建http服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //监听接口处理相应的请求
        server.requestHandler(new HttpServerHandler());

        //启动Server监听指定端口
        server.listen(port,result -> {
            if (result.succeeded()) {
                System.out.println("Server started on port " + port);
            }else {
                System.out.println("Failed to start server on port :" + result.cause().getMessage());
            }
        });
    }
}
