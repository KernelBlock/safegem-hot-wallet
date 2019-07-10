package com.bankledger.safegem.net.model.request;

import com.bankledger.safegem.utils.GsonUtils;

import java.util.List;

/**
 * Date：2018/9/1
 * Author: bankledger
 */
public class TransactionListRequest {

    public String coin;   //币名称 如：SAFE
    public int reqTxCount; //请求交易数
    public List<AddressMessage> addressList;

    public TransactionListRequest(String coin, int reqTxCount, List<AddressMessage> addressList) {
        this.coin = coin;
        this.reqTxCount = reqTxCount;
        this.addressList = addressList;
    }

    public static class AddressMessage {
        public AddressMessage(String address, String lastTxId) {
            this.address = address;
            this.lastTxId = lastTxId;
        }

        public String address; //地址
        public String lastTxId;//热端地址接收的最后一个交易ID

        @Override
        public String toString() {
            return GsonUtils.toString(this);
        }
    }
}
