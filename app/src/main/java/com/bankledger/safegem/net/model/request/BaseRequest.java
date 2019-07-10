package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/7/31
 * Author: bankledger
 */
public class BaseRequest {
    private String jsonrpc="1.0";
    private String id="curltest";

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    protected String method;
}
