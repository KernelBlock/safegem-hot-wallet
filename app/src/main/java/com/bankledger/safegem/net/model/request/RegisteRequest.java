package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class RegisteRequest {
    public String phone; //手机号码
    public String mobileAuthCode; //短信验证码
    public String password; //用户输入密码

    public RegisteRequest(String phone, String mobileAuthCode, String password) {
        this.phone = phone;
        this.mobileAuthCode = mobileAuthCode;
        this.password = password;
    }


}
