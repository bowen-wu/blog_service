package com.blog.entity;

public class Response {
    ResponseStatus status;
    String msg;

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
