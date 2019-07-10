package com.bankledger.safegem.net.model.request;

import java.io.Serializable;

/**
 * Date：2018/8/24
 * Author: bankledger
 */
public class AddColdWalletRequest implements Serializable{

    public String coldUniqueId;  //冷端唯一ID

    public String getColdUniqueId() {
        return coldUniqueId;
    }

    public void setColdUniqueId(String coldUniqueId) {
        this.coldUniqueId = coldUniqueId;
    }

    public String getColdWalletName() {
        return coldWalletName;
    }

    public void setColdWalletName(String coldWalletName) {
        this.coldWalletName = coldWalletName;
    }

    public String coldWalletName;  //冷端名称

    public String bluetooth;  //冷端名称
}
