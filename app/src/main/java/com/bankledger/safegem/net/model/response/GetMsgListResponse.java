package com.bankledger.safegem.net.model.response;

/**
 * Date：2018/9/11
 * Author: bankledger
 */
public class GetMsgListResponse {
    public String msgId; // 消息ID
    public String title; //消息主题
    public String content; //消息内容
    public String msgType;  //消息类型 1：平台消息
    public String source; //消息来源
    public String date;  // 消息时间
    public String msgUrl;   //是否有网页
    public String icon;
}
