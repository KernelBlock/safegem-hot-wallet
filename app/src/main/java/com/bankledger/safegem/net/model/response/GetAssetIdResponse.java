package com.bankledger.safegem.net.model.response;

import com.bankledger.safegem.utils.GsonUtils;

/**
 * Created by zm on 2018/9/11.
 */

public class GetAssetIdResponse {

    public String assetId;               // 资产ID
    public String assetShortName;            // 资产简称
    public String assetName;            // 资产名称
    public String assetDesc;                // 资产描述
    public String assetUnit;                // 资产单位
    public Long assetDecimals;               // 最小单位
    public String issueTime;                   // 发行时间


    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
