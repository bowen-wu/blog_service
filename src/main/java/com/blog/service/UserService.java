package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserById(int id) {
        return this.userDao.selectUserById(id);
    }

    public void register(User user) {
        userDao.insertUser(user);
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
