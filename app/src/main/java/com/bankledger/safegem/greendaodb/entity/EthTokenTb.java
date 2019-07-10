package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

/**
 * ETH_TOKEN地址表
 * Author: zm
 */
@Entity(
        indexes = {
                @Index(value = "address DESC,contractAddress DESC", unique = true)
        }, nameInDb = "eth_token")
public class EthTokenTb {

    @Property(nameInDb = "address")
    private String address;

    @Property(nameInDb = "contract_address")
    private String contractAddress;

    @Property(nameInDb = "balance")
    private String balance;

    @Property(nameInDb = "transaction_count")
    private String transactionCount;

    @Generated(hash = 587701750)
    public EthTokenTb(String address, String contractAddress, String balance,
                      String transactionCount) {
        this.address = address;
        this.contractAddress = contractAddress;
        this.balance = balance;
        this.transactionCount = transactionCount;
    }

    @Generated(hash = 371093271)
    public EthTokenTb() {
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContractAddress() {
        return this.contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTransactionCount() {
        return this.transactionCount;
    }

    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }


}
