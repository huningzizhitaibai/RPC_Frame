package com.huning.yurpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     * 用于表示传输进来的参数分别都是些什么类型的数据
     */
    private Class<?>[] parameterTypes;  //一个关于Class的列表

    /**
     * 参数列表
     */
    private Object[] args;
}
