package com.huning.yurpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServerProxy implements InvocationHandler {
    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成制定值的默认对象, 可以自定义
     */
    private Object getDefaultObject(Class<?> type) {
        //基本类型
        if (type.isPrimitive()){
            if (type == boolean.class){
                return false;
            }else if (type == short.class){
                return (short) 0;
            }else if (type == int.class){
                return 0;
            }else if (type == long.class){
                return 0L;
            }
        }

        //对象类型
        return null;
    }


}
