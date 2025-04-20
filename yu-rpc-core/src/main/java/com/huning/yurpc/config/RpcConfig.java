package com.huning.yurpc.config;

import lombok.Data;

@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "yu-rpc";

    private String version = "1.0.0";

    private String host ="localhost";

    private Integer serverPort = 8080;

    /**
     * 是否开启mock功能
     */
    private boolean mock = false;
}
