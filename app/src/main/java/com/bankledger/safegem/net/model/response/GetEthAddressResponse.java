package com.bankledger.safegem.net.model.response;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/14
 * Author: bankledger
 */
public class GetEthAddressResponse {

    public String transactionCount;
    public List<EthAddress> list;
    public String balance;

    public static class EthAddress {
        public String contractAddress = "";            //ETH 为空
        public String balance;
        public String symbol;
        public String name;
        public String decimals;
        public String totalSupply;
        public String icon;
    }

}
