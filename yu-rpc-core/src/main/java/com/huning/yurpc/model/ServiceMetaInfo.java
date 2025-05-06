package com.huning.yurpc.model;


import cn.hutool.core.util.StrUtil;
import io.netty.util.internal.StringUtil;
import lombok.Data;

/**
 * 一个在注册中心注册的服务所需要包含的元数据模型
 */
@Data

public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion= "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组
     */
    private String serviceGroup = "default";


    /**
     * 获取服务键名
     * 这里只是服务的名称和版本
     */
    public String getServiceKey() {
        //后续可以扩展
        return String.format("%s:%s", serviceName,serviceVersion);
    }

    /**
     * 获取服务注册节点名
     * 将服务的地址和服务键名进行返回
     * 此处没有用到注册中心, 而是直接通过写死的服务主机地址进行获取服务
     */
    public String getServiceNodeKey() {
        //表明这个version的service 是由serviceHost:servicePost的主机进行提供的
        return String.format("%s/%s:%s", getServiceKey(),serviceHost,servicePort);
    }

    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")){
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
