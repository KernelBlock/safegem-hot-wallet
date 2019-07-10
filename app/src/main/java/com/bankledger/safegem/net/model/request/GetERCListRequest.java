package com.bankledger.safegem.net.model.request;

import com.bankledger.safegem.app.SafeGemApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/12
 * Author: bankledger
 */
public class GetERCListRequest {
    public String keyWord = "";
    public String contractAddress = "";
    public List<String> coldUniqueIdList = new ArrayList<>();
}
