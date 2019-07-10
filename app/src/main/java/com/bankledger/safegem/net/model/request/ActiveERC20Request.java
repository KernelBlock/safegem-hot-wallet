package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/9/13
 * Author: bankledger
 */
public class ActiveERC20Request {
    public String coldUniqueId; //冷端唯一ID

    public ActiveERC20Request(String coldUniqueId, String contractAddress, String activation) {
        this.coldUniqueId = coldUniqueId;
        this.contractAddress = contractAddress;
        this.activation = activation;
    }

    public String contractAddress; // 合约地址
    public String activation; //激活，0：去掉激活TOKEN， 1：激活TOKEN
}
