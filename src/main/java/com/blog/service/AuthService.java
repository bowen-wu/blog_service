package com.blog.service;

import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.ResponseStatus;
import com.blog.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Inject
    public AuthService(UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse getLoginStatus() {
        // 用 cookie 换 user，如果有 user 则 已经登录，如果没有 user，则 没有登录
        return new AuthResponse(ResponseStatus.ok, "", false);
    }

    public Response login(String username, String password) {
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return new Response(ResponseStatus.fail, "用户不存在");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);

            User loggedInUser = new User(1, "张三");
            return new AuthResponse(ResponseStatus.ok, "登录成功", true, loggedInUser);
        } catch (BadCredentialsException e) {
            return new Response(ResponseStatus.fail, "密码不正确");
        }
    }
}