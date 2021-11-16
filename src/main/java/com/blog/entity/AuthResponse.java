package com.blog.entity;

public class AuthResponse extends Response {
    boolean isLogin;
    User data;

    public static AuthResponse success(String msg, boolean isLogin, User user) {
        return new AuthResponse(ResponseStatus.ok, msg, isLogin, user);
    }

    public AuthResponse(ResponseStatus status, String msg, boolean isLogin, User data) {
        super(status, msg);
        this.isLogin = isLogin;
        this.data = data;
    }

    public boolean isIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean login) {
        isLogin = login;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
