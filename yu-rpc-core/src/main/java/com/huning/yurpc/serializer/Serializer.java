package com.huning.yurpc.serializer;

import java.io.IOException;

public interface Serializer {
    /**
     * 序列化接口
     *
     * @param object 传入结构体
     * @param <T> 类型
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes 字节流
     * @param type 目标类型
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
