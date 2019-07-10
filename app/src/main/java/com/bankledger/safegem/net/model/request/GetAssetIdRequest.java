package com.bankledger.safegem.net.model.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/11
 * Author: bankledger
 */
public class GetAssetIdRequest {

    public List<String> assetIds = new ArrayList<>();

    public GetAssetIdRequest(List<String> assetIds){
        this.assetIds = assetIds;
    }

}
