package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Date：2018/9/11
 * Author: bankledger
 */
@Entity(
        indexes = {
                @Index(value = "msgId DESC,coldUniqueId DESC", unique = true)
        }, nameInDb = "message_tb"
)

public class MessageTb {

    @Property(nameInDb = "msg_id")
    public String msgId; //	消息ID

    @Property(nameInDb = "user_id")
    public long userId; // 用户ID

    @Property(nameInDb = "title")
    public String title; //	消息主题

    @Property(nameInDb = "content")
    public String content; // 消息内容

    @Property(nameInDb = "msg_type")
    public int msgType; // 消息类型 1、平台， 2、接收消息， 3，发送消息

    @Property(nameInDb = "source")
    public String source; // 手机号码

    @Property(nameInDb = "date")
    public String date;  //	时间

    @Property(nameInDb = "msg_url")
    public String msgUrl; // 消息URL

    @Property(nameInDb = "tx_hash")
    public String txHash; // msgType == 2，3 存在

    @Property(nameInDb = "coin_type")
    public int coinType; // msgType == 2，3 存在（ -1:默认 BTC 1: ETH 2: ETH_TOKEN 3:ETC 4:EOS, 5:EOS_TOKEN, 6:USDT）

    @Property(nameInDb = "icon")
    public String icon; //	图片URL

    @Property(nameInDb = "cold_unique_id")
    public String coldUniqueId; //	冷端序列号


    @Generated(hash = 416539123)
    public MessageTb(String msgId, long userId, String title, String content, int msgType, String source,
            String date, String msgUrl, String txHash, int coinType, String icon, String coldUniqueId) {
        this.msgId = msgId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.msgType = msgType;
        this.source = source;
        this.date = date;
        this.msgUrl = msgUrl;
        this.txHash = txHash;
        this.coinType = coinType;
        this.icon = icon;
        this.coldUniqueId = coldUniqueId;
    }

    @Generated(hash = 1663190291)
    public MessageTb() {
    }


    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsgUrl() {
        return this.msgUrl;
    }

    public void setMsgUrl(String msgUrl) {
        this.msgUrl = msgUrl;
    }

    public String getTxHash() {
        return this.txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getCoinType() {
        return this.coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColdUniqueId() {
        return this.coldUniqueId;
    }

    public void setColdUniqueId(String coldUniqueId) {
        this.coldUniqueId = coldUniqueId;
    }

}
