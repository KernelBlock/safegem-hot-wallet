package com.bankledger.safegem.net.model.request;

/**
 * Date：2018/9/6
 * Author: bankledger
 */
public class GetAppVersionRequest {
    public String version;  //当前版本 V1.0
    public String platform="android"; // “ios” , ”android”

    public GetAppVersionRequest(String version, String productType) {
        this.version = version;
        this.productType = productType;
    }

    public String productType;  // 1 热端，2 冷端
}
