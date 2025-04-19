package com.huning.example.provider;

import com.huning.example.common.model.User;
import com.huning.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名: " + user.getName());
        return user;
    }
}

