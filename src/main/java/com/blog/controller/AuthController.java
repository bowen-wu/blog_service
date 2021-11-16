package com.blog.controller;

import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.ResponseStatus;
import com.blog.entity.User;
import com.blog.service.AuthService;
import com.blog.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Inject
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @GetMapping("/auth")
    @ResponseBody
    public AuthResponse auth() {
        return this.authService.getLoginStatus();
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Response login(@RequestBody Map<String, String> params) {
        return this.authService.login(params.get("username"), params.get("password"));
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Response register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || password == null) {
            return new Response(ResponseStatus.fail, "用户名|密码为空");
        }
        if (username.length() < 1 || username.length() > 15) {
            return new Response(ResponseStatus.fail, "用户名长度 1 - 15 个字符");
        }
        if (password.length() < 6 || password.length() > 16) {
            return new Response(ResponseStatus.fail, "密码长度 6 - 16 个字符");
        }

        User user = this.userService.register(username, password);
        if (user == null) {
            return new Response(ResponseStatus.fail, "该用户名已经注册");
        }
        login(params);
        return new AuthResponse(ResponseStatus.ok, "注册成功", false, user);
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Response logout() {
        return authService.logout();
    }
}
