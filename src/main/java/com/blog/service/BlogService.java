package com.blog.service;

import com.blog.dao.BlogDao;
import com.blog.dao.UserDao;
import com.blog.entity.Blog;
import com.blog.entity.BlogResult;
import com.blog.entity.Result;
import com.blog.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class BlogService {
    BlogDao blogDao;
    UserDao userDao;

    @Inject
    public BlogService(BlogDao blogDao, UserDao userDao) {
        this.blogDao = blogDao;
        this.userDao = userDao;
    }

    public BlogResult getBlogs(Integer page, Integer pageSize, Integer userId) {
        try {
            List<Blog> blogs = blogDao.getBlogs(page, pageSize, userId);
            int total = blogDao.getBlogCount(userId);
            int totalPage = (int) Math.ceil(total * 1.0 / pageSize);
            return BlogResult.success("获取成功", blogs, total, page, totalPage);
        } catch (Exception e) {
            return BlogResult.failure("系统异常");
        }
    }

    public Blog getBlogInfoById(Integer blogId) {
        Blog blog = blogDao.getBlogInfoById(blogId);
        if (blog == null) {
            return null;
        }
        User user = userDao.getUserById(blog.getUserId());
        blog.setUser(user);
        return blog;
    }

    public Blog createBlog(Blog blog) {
        Integer blogId = blogDao.createBlog(blog);
        return getBlogInfoById(blogId);
    }

    public Result<Blog> updateBlog(Blog blog) {
        Blog blogInfoById = getBlogInfoById(blog.getId());
        if (blogInfoById == null) {
            return Result.failure("博客不存在");
        }
        if (!blogInfoById.getUserId().equals(blog.getUserId())) {
            return Result.failure("无法修改别人的博客");
        }

        blogDao.updateBlog(blog);
        Blog updatedBlog = getBlogInfoById(blog.getId());
        return Result.success("修改成功", updatedBlog);
    }

    public Result<Blog> deleteBlog(Integer blogId, User loggedInUser) {
        Blog blogInfoById = getBlogInfoById(blogId);
        if (blogInfoById == null) {
            return Result.failure("博客不存在");
        }
        if (!blogInfoById.getUserId().equals(loggedInUser.getId())) {
            return Result.failure("无法删除别人的博客");
        }

        blogDao.deleteBlog(blogId);
        return Result.success("删除成功", null);
    }
}
