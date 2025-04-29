package com.huning.yurpc.serializer;

import com.huning.yurpc.spi.SpiLoader;

public class SerializerFactory {
    /**
     * 静态初始化模块
     * 有点像go包中了init()函数, 当包引用的时候会自动加载一次
     * 当调用SerializerFactory, 就会自动执行
     */
    static {
        SpiLoader.load(Serializer.class);
    }
    /**
     * 默认序列化器
     * 提供一个默认的序列化器可以直接进行调用
     */
    private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    /**
     * 获取实例
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
