package com.huning.example.common.service;

import com.huning.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    default short getNumber(){
        return 1;
    }
}
