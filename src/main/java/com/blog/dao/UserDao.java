package com.blog.dao;

import com.blog.entity.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserDao {
    private final SqlSession sqlSession;

    @Inject
    public UserDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public User getUserByUsername(String username) {
        return this.sqlSession.selectOne("selectUserByUsername", username);
    }

    public User getUserById(Integer userId) {
        return this.sqlSession.selectOne("selectUserById", userId);
    }

    public void insertUser(User user) {
        this.sqlSession.insert("insertUser", user);
    }
}
