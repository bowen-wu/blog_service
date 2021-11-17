package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(User user) {
        userDao.insertUser(user);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public AuthResponse getLoginStatus() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return AuthResponse.success("", false, null);
        }

        return AuthResponse.success("", true, user);
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
