package com.blog.entity;

public class Result<T> {
    ResultStatus status;
    String msg;
    T data;


    public static <R> Result<R> success(String msg, R data) {
        return new Result<>(ResultStatus.ok, msg, data);
    }

    public static <R> Result<R> failure(String msg) {
        return new Result<>(ResultStatus.fail, msg, null);
    }

    protected Result(ResultStatus status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
