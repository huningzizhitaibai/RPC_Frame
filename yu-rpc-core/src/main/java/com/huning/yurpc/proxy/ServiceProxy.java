package com.huning.yurpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.serializer.JDKSerializer;
import com.huning.yurpc.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    //编写一个通用的代理处理方法, 将能够程式化处理的部分进行统一
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化容器
        Serializer serializer = new JDKSerializer();

        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            //发送请求
            //todo 地址不应该被硬编码, 应该通过注册中心进行查找
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
