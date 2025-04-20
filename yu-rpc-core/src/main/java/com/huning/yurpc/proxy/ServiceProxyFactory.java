package com.huning.yurpc.proxy;

import com.huning.yurpc.RpcApplication;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    /**
     * 根据服务类创建代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        //如果开启mock设置就使用mock直接进行测试返回
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }



    /**
     * 根据服务获取mock类型
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServerProxy()
        );
    }
}
