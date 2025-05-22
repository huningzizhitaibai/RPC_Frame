package com.huning.yurpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.huning.yurpc.RpcApplication;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.model.ServiceMetaInfo;
import com.huning.yurpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

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


    public static RpcResponse doStart(RpcRequest rpcRequest, ServiceMetaInfo selectedServiceMetaInfo) throws Exception {
        Vertx vertx = Vertx.vertx();
        NetClient netClient=vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(),
                response -> {
                    if(response.succeeded()){
                        //获取套接字, 建立连接
                        System.out.println("Connect to Tcp server");
                        io.vertx.core.net.NetSocket netSocket = response.result();

                        //构建发送数据
                        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                        ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        //将Enum视作一个元组
                        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);

                        //编码请求
                        try{
                            Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                            netSocket.write(encodeBuffer);
                        }catch (IOException e){
                            throw new RuntimeException("协议信息序列化错误");
                        }

                        //接受响应
                        netSocket.handler(buffer -> {
                            try{
                                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                responseFuture.complete(rpcResponseProtocolMessage.getBody());
                            }catch (IOException e){
                                throw new RuntimeException("协议解码错误");
                            }
                        });
                    }else{
                        System.out.println("Connect to Tcp server failed");
                    }
                });
        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse;
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
