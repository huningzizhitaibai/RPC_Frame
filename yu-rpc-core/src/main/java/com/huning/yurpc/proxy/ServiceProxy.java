package com.huning.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.huning.yurpc.RpcApplication;
import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.constant.RpcConstant;
import com.huning.yurpc.fault.retry.RetryStrategy;
import com.huning.yurpc.fault.retry.RetryStrategyFactory;
import com.huning.yurpc.loadbalancer.LoadBalancer;
import com.huning.yurpc.loadbalancer.LoadBalancerFactory;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.model.ServiceMetaInfo;
import com.huning.yurpc.protocol.*;
import com.huning.yurpc.registry.Registry;
import com.huning.yurpc.registry.RegistryFactory;
import com.huning.yurpc.serializer.JDKSerializer;
import com.huning.yurpc.serializer.Serializer;
import com.huning.yurpc.serializer.SerializerFactory;
import com.huning.yurpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        /**
         * 通过获取在配置文件中制定的serializer名称, 在SerializerFactory中通过getInstance方法获取相关实例
         * 通过配置文件在获取相关的序列化器
         */
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        String serviceName = method.getDeclaringClass().getName();
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
            //从config中获取注册中心地址
            RpcConfig rpcConfig = new RpcConfig();
            //在RegistryConfig中包含了注册中心类型, 账户密码等一系列数据, 其中在获取其实现时, 只需要类型即可
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();

            //原来直接写的serviceName不知道是怎么做的
            serviceMetaInfo.setServiceName(serviceName);

            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂时没有服务地址");
            }


            //根据策略选择合适的节点进行访问
            LoadBalancer newLoadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            //感觉在这里, 哪怕不使用methodName做参数也没什么关系,
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = newLoadBalancer.select(requestParams, serviceMetaInfoList);


            //尝试使用http协议进行网络发送

//            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()){
//                byte[] result = httpResponse.bodyBytes();
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }



            //尝试使用自定义协议, 基于Tcp服务进行传输

            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(()->
                    VertxTcpClient.doStart(rpcRequest, selectedServiceMetaInfo));

            return rpcResponse.getData();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
