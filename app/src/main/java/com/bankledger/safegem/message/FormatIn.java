package com.bankledger.safegem.message;

import com.bankledger.safegem.utils.GsonUtils;

/**
 * Created by zm on 2018/7/3.
 */

public class FormatIn {

    public long sequence;
    public String txid;
    public String value;
    public long vout;
    public ScriptSig scriptSig;

    public class ScriptSig {
        public String asm;
        public String hex;
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

}
