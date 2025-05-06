package com.huning.yurpc.model;

import com.huning.yurpc.constant.RpcConstant;
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


    /**
     * 默认所需请求服务的版本
     * 提供的服务可以有不同的版本, 这里只是默认用户请求的版本为最初始的版本, 后续可以根据开发要求进行更改
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;


}
