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


        /**
         * 在这里实例化了一个服务接口, 其实已经完成了实例化.
         * 通过调用这个接口中提供的方法就可以使用provider提供的实现了
         * UserService就是provider提供的一个接口
         */
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
