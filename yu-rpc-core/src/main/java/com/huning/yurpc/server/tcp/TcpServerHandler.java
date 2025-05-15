package com.huning.yurpc.server.tcp;

import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.protocol.ProtocolMessage;
import com.huning.yurpc.protocol.ProtocolMessageDecoder;
import com.huning.yurpc.protocol.ProtocolMessageEncoder;
import com.huning.yurpc.protocol.ProtocolMessageTypeEnum;
import com.huning.yurpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        //处理连接
        socket.handler(buffer -> {
            //接受请求,并解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try{
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            }catch (IOException e){
                throw new RuntimeException("协议信息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            //处理请求
            RpcResponse rpcResponse = new RpcResponse();
            try{
                //获取要调用的服务,通过反射处理
                //返回的是一个Server对应的class对象.
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                //返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(result.getClass());
                rpcResponse.setMessage("ok");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }


            //重新编码, 然后发送
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte)ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try{
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encode);
            }catch (IOException e){
                throw new RuntimeException("协议信息编码错误");
            }

        });
    }
}
