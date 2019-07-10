package com.bankledger.safegem.net.model.response;

import com.bankledger.safegem.message.FormatIn;
import com.bankledger.safegem.message.FormatOut;
import com.bankledger.safegem.utils.GsonUtils;

import java.util.List;

/**
 * Date：2018/9/26
 * Author: bankledger
 */
public class BtcSendTransactionResponse {

    public TransactionBean transaction;
    public String txId;

    public static class TransactionBean {

        public String hex;
        public int locktime;
        public int size;
        public String txid;
        public int version;
        public List<FormatIn> vin;
        public List<FormatOut> vout;

        @Override
        public String toString() {
            return GsonUtils.toString(this);
        }
    }

}
