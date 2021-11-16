package com.blog.entity;

public class Response {
    ResponseStatus status;
    String msg;

    public static Response failure(String msg) {
        return new Response(ResponseStatus.fail, msg);
    }

    public static Response success(String msg) {
        return new Response(ResponseStatus.ok, msg);
    }

    public Response(ResponseStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
