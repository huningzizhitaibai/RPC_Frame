package com.huning.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.huning.yurpc.RpcApplication;
import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.constant.RpcConstant;
import com.huning.yurpc.model.RpcRequest;
import com.huning.yurpc.model.RpcResponse;
import com.huning.yurpc.model.ServiceMetaInfo;
import com.huning.yurpc.registry.Registry;
import com.huning.yurpc.registry.RegistryFactory;
import com.huning.yurpc.serializer.JDKSerializer;
import com.huning.yurpc.serializer.Serializer;
import com.huning.yurpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
            serviceMetaInfo.setServiceName(method.getName());

            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂时没有服务地址");
            }
            //todo:后续根据优化改进选择算法
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
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
