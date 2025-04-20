package com.huning.example.consumer;

import com.huning.example.common.model.User;
import com.huning.example.common.service.UserService;
import com.huning.yurpc.config.RpcConfig;
import com.huning.yurpc.proxy.ServiceProxyFactory;
import com.huning.yurpc.utils.ConfigUtils;

public class EasyConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class,"rpc");
        System.out.println(rpcConfig);


        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("huning");

        //调用
//        User newUser = userService.getUser(user);
//        if (newUser != null) {
//            System.out.println(newUser.getName());
//        }else {
//            System.out.println("user is null");
//        }
        long number = userService.getNumber();
        System.out.println(number);

    }
}
