package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Date：2018/8/24
 * Author: bankledger
 */
@Entity(nameInDb = "cold_wallet")
public class ColdWalletTb {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;
    @Property(nameInDb = "user_id")
    private Long userId; //	用户ID
    @Unique
    @Property(nameInDb = "cold_unique_id")
    private String coldUniqueId; //	冷端序列号
    @Property(nameInDb = "cold_wallet_name")
    private String coldWalletName; //	冷端名称
    @Property(nameInDb = "data")
    private String date; //	添加时间

    @Generated(hash = 1604946513)
    public ColdWalletTb(Long id, Long userId, String coldUniqueId,
            String coldWalletName, String date) {
        this.id = id;
        this.userId = userId;
        this.coldUniqueId = coldUniqueId;
        this.coldWalletName = coldWalletName;
        this.date = date;
    }


    @Generated(hash = 1263357851)
    public ColdWalletTb() {
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


    public String getColdUniqueId() {
        return this.coldUniqueId;
    }


    public void setColdUniqueId(String coldUniqueId) {
        this.coldUniqueId = coldUniqueId;
    }


    public String getColdWalletName() {
        return this.coldWalletName;
    }


    public void setColdWalletName(String coldWalletName) {
        this.coldWalletName = coldWalletName;
    }


    public String getDate() {
        return this.date;
    }


    public void setDate(String date) {
        this.date = date;
    }
}
