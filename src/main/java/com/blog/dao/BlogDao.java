package com.blog.dao;

import com.blog.entity.Blog;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BlogDao {
    private final SqlSession sqlSession;

    @Inject
    public BlogDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<Blog> getBlogs(Integer page, Integer pageSize, Integer userId) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("user_id", userId);
        parameter.put("offset", (page - 1) * pageSize);
        parameter.put("limit", pageSize);
        return this.sqlSession.selectList("selectBlogs", parameter);
    }

    public int getBlogCount(Integer userId) {
        return this.sqlSession.selectOne("selectBlogCount", userId);
    }

    public Blog getBlogInfoById(Integer blogId) {
        return this.sqlSession.selectOne("selectBlogById", blogId);
    }
}
