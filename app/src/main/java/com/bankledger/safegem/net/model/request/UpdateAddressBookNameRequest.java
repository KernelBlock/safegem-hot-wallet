package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/9/10
 * Author: bankledger
 */
public class UpdateAddressBookNameRequest {

    public String coin;

    public UpdateAddressBookNameRequest(String coin, String address, String newName) {
        this.coin = coin;
        this.address = address;
        this.newName = newName;
    }

    public String address;
    public String newName;
}
