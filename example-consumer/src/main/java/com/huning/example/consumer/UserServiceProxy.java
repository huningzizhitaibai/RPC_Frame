package com.huning.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.huning.example.common.model.User;
import com.huning.example.common.service.UserService;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.serializer.JDKSerializer;
import com.huning.yurpc.serializer.Serializer;
import com.huning.yurpc.server.HttpServerHandler;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    public User getUser(User user) {
        //制定序列化容器
        Serializer serializer = new JDKSerializer();

        //发送请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}

