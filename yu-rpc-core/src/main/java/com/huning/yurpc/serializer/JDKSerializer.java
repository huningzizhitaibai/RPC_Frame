package com.huning.yurpc.serializer;

import java.io.*;

//使用java自带的序列化方法实现
public class JDKSerializer implements Serializer {
    /**
     * 序列化
     *
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        //类中包含toByte方法, 可以将其中的数据序列化为字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        //将对象写入
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }


    /**
     * 反序列化
     * @param bytes 字节流
     * @param type 目标类型
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try{
            return (T) objectInputStream.readObject();
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }finally {
            objectInputStream.close();
        }
    }
}
