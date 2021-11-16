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

    public User selectUserById(int id) {
        return this.sqlSession.selectOne("selectUserById", id);
    }

    public int insertUser(String username, String password) {
        return this.sqlSession.insert("insertUser", new String[]{username, password});
    }
}
