package com.huning.yurpc.server.tcp;

import com.huning.yurpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * 使用Vertx框架提供的tcp服务来
 */
public class VertxTcpServer implements HttpServer {
    //测试用的handle函数
    private byte[]  handleRequest(byte[] requestData){
        return "Hello World!".getBytes();
    }

    @Override
    public void doStart(int port){
        //创建Vertx实例
        Vertx vertx = Vertx.vertx();

        //创建TCP服务器
        NetServer server = vertx.createNetServer();

        //处理请求
        server.connectHandler(socket -> {
            //处理连接
            TcpServerHandler handler = new TcpServerHandler();
            handler.handle(socket);
        });

        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server started on port " + port);
            }else {
                System.out.println("Failed to start server");
            }
        });
    }

    public static void main(String[] agrs) {
        new VertxTcpServer().doStart(8080);
    }
}
