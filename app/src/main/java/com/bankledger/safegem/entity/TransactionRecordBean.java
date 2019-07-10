package com.bankledger.safegem.entity;

import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;

/**
 * 交易记录实体对象
 * @author bankledger
 * @time 2018/8/31 17:11
 */
public class TransactionRecordBean {

    public String txHash;
    public String coin;
    public int coinType;
    public String amount;
    public String address;
    public String date;
    public String time;

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

}
