package com.blog.service;

import com.blog.entity.AuthResponse;
import com.blog.entity.ResponseStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public AuthResponse getLoginStatus() {
        // 用 cookie 换 user，如果有 user 则 已经登录，如果没有 user，则 没有登录
        return new AuthResponse(ResponseStatus.OK, "", false);
    }

    public AuthResponse login(String username, String password) {
        System.out.println(username);
        System.out.println(password);
        return new AuthResponse(ResponseStatus.OK, "", true);
    }
}
