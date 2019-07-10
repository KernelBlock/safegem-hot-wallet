package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class LoginRequest {
    public String phone; //手机号码

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String password; //登录密码
    public String imgCode = ""; //预留（图片验证码）
    public String randomCode = ""; //预留（获取图片验证码相同）
}
