package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class SmsRequest {
    public String phone;//手机号码
    public String imgCode;//图片验证码
    public String randomCode;//获取图片验证码相同
    public SmsRequest(String phone, String imgCode, String randomCode) {
        this.phone = phone;
        this.imgCode = imgCode;
        this.randomCode = randomCode;
    }

}
