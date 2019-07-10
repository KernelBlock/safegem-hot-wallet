package com.bankledger.safegem.net.model.request;

import com.bankledger.safegem.utils.Constants;

import java.io.Serializable;

/**
 * Date：2018/9/5
 * Author: bankledger
 */
public class SendTransactionRequest implements Serializable {
    public String coin;//币名称 如：SAFE
    public String hex;//16进制交易原始数据

    public int getCoinType() {
        int type = 0;
        if (coin.equals(Constants.ETH)) {
            type = Constants.COIN_ETH;
        } else if (coin.equals(Constants.ETC)) {
            type = Constants.COIN_ETC;
        } else if (coin.equals(Constants.EOS)) {
            type = Constants.COIN_EOS;
        } else {
            type = Constants.COIN_BTC;
        }
        return type;
    }

    public static class SendETHTransactionRequest {
        public String hex;//16进制交易原始数据
    }
}
