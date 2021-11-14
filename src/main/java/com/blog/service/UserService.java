package com.blog.service;

import com.blog.dao.UserDao;
import com.blog.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserService {
    private final UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserById(int id) {
        return this.userDao.selectUserById(id);
    }
}
