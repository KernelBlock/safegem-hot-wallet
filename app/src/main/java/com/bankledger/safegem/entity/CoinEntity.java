package com.bankledger.safegem.entity;


import android.support.annotation.NonNull;

import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.Utils;

import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class CoinEntity {

    public String coin;
    public int coinType;

    public String coinImg = ""; //coinType == COIN_ETH_TOKEN 存在
    public String assetId = ""; //coinType == COIN_SAFE_ASSET 存在
    public String assetName = ""; //coinType == COIN_SAFE_ASSET 存在


    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
