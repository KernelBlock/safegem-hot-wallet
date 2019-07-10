package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
@Entity(nameInDb = "user_tb")
public class UserTb {

    @Id(autoincrement = true)
    private Long id;
    @Unique
    @Property(nameInDb = "user_id")
    private Long userId; // 用户ID
    @Property(nameInDb = "token_id")
    private String tokenId; // 令牌ID
    @Property(nameInDb = "increment_id")
    private Long incrementId;// 界面显示ID
    @Property(nameInDb = "phone")
    private String phone;    //手机号码
    @Property(nameInDb = "nick_name")
    private String nickName; // 昵称
    @Property(nameInDb = "pic_url")
    private String picUrl; // 图片URL
    @Property(nameInDb = "is_login")
    private Boolean isLogin; // 是否登录

    @Generated(hash = 1137851137)
    public UserTb(Long id, Long userId, String tokenId, Long incrementId,
            String phone, String nickName, String picUrl, Boolean isLogin) {
        this.id = id;
        this.userId = userId;
        this.tokenId = tokenId;
        this.incrementId = incrementId;
        this.phone = phone;
        this.nickName = nickName;
        this.picUrl = picUrl;
        this.isLogin = isLogin;
    }

    @Generated(hash = 353326012)
    public UserTb() {
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Long getIncrementId() {
        return this.incrementId;
    }

    public void setIncrementId(Long incrementId) {
        this.incrementId = incrementId;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Boolean getIsLogin() {
        return this.isLogin;
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }

}
