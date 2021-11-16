package com.blog.dao;

import com.blog.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;

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

    public User getUserByUsername(String username) {
        return this.sqlSession.selectOne("selectUserByUsername", username);
    }

    public int insertUser(String username, String encryptedPassword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("encryptedPassword", encryptedPassword);
        return this.sqlSession.insert("insertUser", params);
    }
}
