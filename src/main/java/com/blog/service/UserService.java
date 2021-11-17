package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
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
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Inject
    UserDao userDao;
    @Inject
    AuthenticationManager authenticationManager;

    public void register(User user) {
        userDao.insertUser(user);
    }

    public AuthResponse getLoginStatus() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return AuthResponse.success("", false, null);
        }

        return AuthResponse.success("", true, user);
    }

    public Response login(String username, String password) {
        UserDetails userDetails;
        try {
            userDetails = loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return Response.failure("用户不存在");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);

            return AuthResponse.success("登录成功", true, userDao.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return Response.failure("密码不正确");
        }
    }

    public Response logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return Response.failure("用户尚未登录");
        } else {
            SecurityContextHolder.clearContext();
            return Response.success("注销成功");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " 不存在！");
        }

        return new org.springframework.security.core.userdetails.User(username, user.getEncryptedPassword(), Collections.emptyList());
    }
}
