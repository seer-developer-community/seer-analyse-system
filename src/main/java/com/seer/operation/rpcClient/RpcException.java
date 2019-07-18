package com.seer.operation.rpcClient;

public class RpcException extends RuntimeException {
    private String rpcMethod;
    private String rpcParams;
    private int responseCode;
    private String responseMessage;
    private String response;

    public RpcException(String method, String params, int responseCode, String responseMessage, String response) {
        super("RPC Query Failed (method: " + method + ", params: " + params + ", response code: " + responseCode + " responseMessage " + responseMessage + ", response: " + response);
        this.rpcMethod = method;
        this.rpcParams = params;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.response = response;
    }

    public RpcException(String method, String params, Throwable cause) {
        super("RPC Query Failed (method: " + method + ", params: " + params + ")", cause);
        this.rpcMethod = method;
        this.rpcParams = params;
    }

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getRpcMethod() {
        return rpcMethod;
    }

    public String getRpcParams() {
        return rpcParams;
    }
}
