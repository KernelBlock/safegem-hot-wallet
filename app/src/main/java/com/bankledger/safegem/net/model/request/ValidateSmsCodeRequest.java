package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/8/22
 * Author: bankledger
 */
public class ValidateSmsCodeRequest {

    public String phone;//手机号码
    public String sms;//图片验证码

    public ValidateSmsCodeRequest(String phone, String sms) {
        this.phone = phone;
        this.sms = sms;
    }
}
