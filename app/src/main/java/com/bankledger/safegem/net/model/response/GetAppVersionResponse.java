package com.bankledger.safegem.net.model.response;

import java.io.Serializable;

/**
 * Date：2018/9/6
 * Author: bankledger
 */
public class GetAppVersionResponse  implements Serializable{
    public GetAppVersionResponse() {
    }

    public GetAppVersionResponse(int lastVer, String downUrl, String updateDesc, String forceUpdate, String md5, String version) {
        this.lastVer = lastVer;
        this.downUrl = downUrl;
        this.updateDesc = updateDesc;
        this.forceUpdate = forceUpdate;
        this.md5 = md5;
        this.version = version;
    }

    public int lastVer;  //  0--没有最新版本，1--有最新版本
    public String downUrl; // 下载最新版本URL
    public String updateDesc;  // 更新说明
    public String forceUpdate;//  0不强制 , 1 强制
    public String md5;
    public String version;
}
