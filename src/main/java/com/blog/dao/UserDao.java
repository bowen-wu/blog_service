package com.blog.dao;

import com.blog.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserDao {
    private final SqlSession sqlSession;

    @Inject
    public UserDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public User getUserByUsername(String username) {
        return this.sqlSession.selectOne("selectUserByUsername", username);
    }

    public void insertUser(User user) {
        this.sqlSession.insert("insertUser", user);
    }
}
