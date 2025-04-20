package com.huning.yurpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
public class ConfigUtils {

    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix,"");
    }

    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {

        //读取用户传入的参数, 了解用户想要使用的配置文件
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");

        //创建读取器, 读取对应文件
        Props props = new Props(configFileBuilder.toString());

        //最后将信息转化成可以操作的java类对象
        return props.toBean(clazz, prefix);

    }
}
