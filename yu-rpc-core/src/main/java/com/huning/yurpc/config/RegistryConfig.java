package com.huning.yurpc.config;

import com.huning.yurpc.registry.RegistryKeys;
import lombok.Data;

@Data
public class RegistryConfig {
    /**
     * 注册中心类型
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String address = "http://121.36.203.34:2379";


    /**
     * 用户名
     */
    private String username;

    private String password;

    /**
     * 连接超时时间(单位毫秒)
     */
    private Long timeout = 10000L;

}
