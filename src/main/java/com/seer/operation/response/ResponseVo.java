package com.seer.operation.response;

import java.io.Serializable;

public class ResponseVo<T> implements Serializable {
    private static final long serialVersionUID = 874200365941306385L;
    private Integer code;
    private String msg;
    private T data;

    public ResponseVo() {
    }

    public static final ResponseVo ResultSuccess() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setCode(200);
        responseVo.setMsg("success");
        return responseVo;
    }

    public static final ResponseVo<String> ResultParamsNull() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setCode(201);
        responseVo.setMsg("params is null!");
        return responseVo;
    }

    public static final ResponseVo<String> ResultFailed(String msg) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setCode(500);
        responseVo.setMsg(msg);
        return responseVo;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
