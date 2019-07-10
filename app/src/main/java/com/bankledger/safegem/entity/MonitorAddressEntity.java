package com.bankledger.safegem.entity;


import android.support.annotation.NonNull;

import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.utils.Utils;

import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class MonitorAddressEntity implements Comparable<MonitorAddressEntity> {

    public int count;
    public String coin;
    public String name;
    public String ethAmount;
    public int coinType;
    public String coinImg = ""; //coinType == COIN_ETH_TOKEN 存在
    public String assetId = ""; //coinType == COIN_SAFE_ASSET 存在

    public List<MonitorAddressTb> monitorAddressList;

    public MonitorAddressEntity() {
    }

    public MonitorAddressEntity(String coin, String name, int count, String ethAmount, int coinType, String coinImg, List<MonitorAddressTb> monitorAddressList) {
        this.coin = coin;
        this.name = name;
        this.count = count;
        this.ethAmount = ethAmount;
        this.coinType = coinType;
        this.monitorAddressList = monitorAddressList;
        this.coinImg = coinImg;
        this.assetId = name;
    }

    public MonitorAddressEntity(String coin, String name, int count, String ethAmount, int coinType, String coinImg, List<MonitorAddressTb> monitorAddressList, String assetId) {
        this(coin, name, count, ethAmount, coinType, coinImg, monitorAddressList);
        this.assetId = assetId;
    }

    @Override
    public int compareTo(@NonNull MonitorAddressEntity monitorAddressEntity) {
        return name.compareTo(monitorAddressEntity.name);
    }

}
