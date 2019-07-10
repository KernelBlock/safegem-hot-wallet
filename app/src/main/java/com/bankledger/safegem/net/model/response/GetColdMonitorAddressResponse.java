package com.bankledger.safegem.net.model.response;

/**
 * Date：2018/9/3
 * Author: bankledger
 */
public class GetColdMonitorAddressResponse {

    public String coin;  //币名称 如：SAFE
    public String name; //地址名称
    public String address; //地址
    public String coldUniqueId; //冷端唯一ID

}
