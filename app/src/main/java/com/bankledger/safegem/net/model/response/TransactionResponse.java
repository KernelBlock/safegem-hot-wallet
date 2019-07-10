package com.bankledger.safegem.net.model.response;

import android.text.TextUtils;

import com.bankledger.safegem.message.FormatOut;
import com.bankledger.safegem.message.FormatTx;
import com.bankledger.safegem.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/1
 * Author: bankledger
 */
public class TransactionResponse {
    public String address;
    public String coin;
    public int restCount;
    public String currentLastTxId;
    public List<FormatTx> txList = new ArrayList<>();

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
