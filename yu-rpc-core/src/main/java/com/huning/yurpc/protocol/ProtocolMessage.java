package com.huning.yurpc.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定义响应体的信息应该是什么样的
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    //消息头
    private Header header;

    //消息体
    private T body;

    @Data
    public static class Header {
        //魔术, 保证安全性
        private byte magic;

        //版本号
        private byte version;

        //序列化器
        private byte serializer;

        //消息类型
        private byte type;

        private byte status;

        private long requestId;

        private int bodyLength;
    }
}
