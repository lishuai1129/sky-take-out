package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 使用微信临时授权码登录。
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
