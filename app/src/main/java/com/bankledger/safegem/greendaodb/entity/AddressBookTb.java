package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Date：2018/9/6
 * Author: bankledger
 */
@Entity( indexes = {
        @Index(value = "userId DESC,coin DESC,address DESC", unique = true)
    },nameInDb = "address_book")
public class AddressBookTb {

    @Id(autoincrement = true)
    @Property(nameInDb = "addr_id")
    private Long addrId;

    @Property(nameInDb = "user_id")
    private Long userId;

    @Property(nameInDb = "coin")
    private String coin;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "address")
    private String address;

    public AddressBookTb(){

    }

    public AddressBookTb(Long userId, String coin, String name,
                         String address) {
        this.userId = userId;
        this.coin = coin;
        this.name = name;
        this.address = address;
    }

    @Generated(hash = 1464818611)
    public AddressBookTb(Long addrId, Long userId, String coin, String name,
            String address) {
        this.addrId = addrId;
        this.userId = userId;
        this.coin = coin;
        this.name = name;
        this.address = address;
    }

    public Long getAddrId() {
        return this.addrId;
    }

    public void setAddrId(Long addrId) {
        this.addrId = addrId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCoin() {
        return this.coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
