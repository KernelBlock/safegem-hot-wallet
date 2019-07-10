package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        indexes = {
                @Index(value = "account DESC,tokenName DESC", unique = true)
        }, nameInDb = "eos_balance_tb")
public class EosBalanceTb {

    @Property(nameInDb = "account")
    private String account;

    @Property(nameInDb = "balance")
    private String balance;

    @Property(nameInDb = "token_name")
    private String tokenName;

    @Property(nameInDb = "coin_type")
    private int coinType;  // 0 EOS，1 USDT

    public static final int TYPE_EOS = 0;

    public static final int TYPE_USDT = 1;


    @Generated(hash = 1411307458)
    public EosBalanceTb(String account, String balance, String tokenName,
            int coinType) {
        this.account = account;
        this.balance = balance;
        this.tokenName = tokenName;
        this.coinType = coinType;
    }

    @Generated(hash = 1531840249)
    public EosBalanceTb() {
    }
    

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTokenName() {
        return this.tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public int getCoinType() {
        return coinType;
    }

    public void setCoinType(int coinType) {
        this.coinType = coinType;
    }
}
