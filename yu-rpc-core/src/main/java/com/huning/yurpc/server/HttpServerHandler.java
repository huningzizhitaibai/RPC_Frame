package com.huning.yurpc.server;

import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.registry.LocalRegistry;
import com.huning.yurpc.serializer.JDKSerializer;
import com.huning.yurpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

/**
 * http处理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //制定序列化容器
        final Serializer serializer = new JDKSerializer();

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        //异步处理http请求
        request.bodyHandler(body-> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try{
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }

            //构造响应
            RpcResponse rpcResponse = new RpcResponse();
            //非正常处理流程
            if (rpcRequest == null) {
                rpcResponse.setMessage("rcpRequest is null");
                doResponse(request, rpcResponse,serializer);
                return;
            }
            //正常处理流程
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
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应
     */
    void  doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type","application/json");
        try{
            //序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        }catch (Exception e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
