package com.blog.entity;

public class AuthResult extends Result<User> {
    boolean isLogin;

    public static AuthResult success(String msg, boolean isLogin) {
        return new AuthResult(ResultStatus.ok, msg, isLogin, null);
    }

    public static AuthResult success(String msg, boolean isLogin, User user) {
        return new AuthResult(ResultStatus.ok, msg, isLogin, user);
    }

    public static AuthResult failure(String msg) {
        return new AuthResult(ResultStatus.fail, msg, false, null);
    }

    protected AuthResult(ResultStatus status, String msg, boolean isLogin, User data) {
        super(status, msg, data);
        this.isLogin = isLogin;
        this.data = data;
    }

    public boolean isIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean login) {
        isLogin = login;
    }
}
