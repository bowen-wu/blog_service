package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.AuthResponse;
import com.blog.entity.Response;
import com.blog.entity.ResponseStatus;
import com.blog.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDao userDao;

    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserDao userDao) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDao = userDao;
    }

    public User getUserById(int id) {
        return this.userDao.selectUserById(id);
    }

    public User register(String username, String password) {
        if (userDao.getUserByUsername(username) != null) {
            return null;
        }
        String encodePassword = bCryptPasswordEncoder.encode(password);
        userDao.insertUser(username, encodePassword);
        return userDao.getUserByUsername(username);
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
