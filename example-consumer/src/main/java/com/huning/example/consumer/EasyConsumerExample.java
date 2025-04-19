package com.huning.example.consumer;

import com.huning.example.common.model.User;
import com.huning.example.common.service.UserService;
import com.huning.yurpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("huning");

        //调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        }else {
            System.out.println("user is null");
        }
    }
}
