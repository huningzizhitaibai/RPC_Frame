package com.huning.yurpc.server.tcp;

import io.vertx.core.Vertx;

import java.awt.*;

public class VertxTcpClient {
    public void start() {
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8080, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connected to TCP Server");
                io.vertx.core.net.NetSocket socket = result.result();

                //发送数据
//                socket.write("Hello Server");

                //接收响应
                socket.handler(buffer -> {
                    System.out.println("Received data: " + buffer.toString());
                });
            }else{
                System.out.println("Failed to connect to TCP Server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
