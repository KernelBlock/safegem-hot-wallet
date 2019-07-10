package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        indexes = {
                @Index(value = "coldUniqueId DESC", unique = true)
        }, nameInDb = "eos_account_tb")
public class EosAccountTb {

    @Property(nameInDb = "cold_unique_id")
    private String coldUniqueId; //	冷端序列号

    @Property(nameInDb = "account")
    private String account;

    @Property(nameInDb = "owner_pub_key")
    private String ownerPubKey;

    @Property(nameInDb = "active_pub_key")
    private String activePubKey;

    @Generated(hash = 1964997352)
    public EosAccountTb(String coldUniqueId, String account, String ownerPubKey,
            String activePubKey) {
        this.coldUniqueId = coldUniqueId;
        this.account = account;
        this.ownerPubKey = ownerPubKey;
        this.activePubKey = activePubKey;
    }

    @Generated(hash = 1279532145)
    public EosAccountTb() {
    }

    public String getColdUniqueId() {
        return coldUniqueId;
    }

    public void setColdUniqueId(String coldUniqueId) {
        this.coldUniqueId = coldUniqueId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOwnerPubKey() {
        return ownerPubKey;
    }

    public void setOwnerPubKey(String ownerPubKey) {
        this.ownerPubKey = ownerPubKey;
    }

    public String getActivePubKey() {
        return activePubKey;
    }

    public void setActivePubKey(String activePubKey) {
        this.activePubKey = activePubKey;
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
