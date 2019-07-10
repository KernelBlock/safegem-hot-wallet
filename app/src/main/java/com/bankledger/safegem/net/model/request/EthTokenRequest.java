package com.bankledger.safegem.net.model.request;

/**
 * Created by zm on 2018/9/11.
 */

public class EthTokenRequest {

    public String address;

    public String contractAddress;

    public EthTokenRequest(String address) {
        this.address = address;
    }

    public EthTokenRequest(String address, String contractAddress) {
        this.address = address;
        this.contractAddress = contractAddress;
    }

}
