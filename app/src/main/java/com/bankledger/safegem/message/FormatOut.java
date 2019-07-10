package com.bankledger.safegem.message;

import com.bankledger.safegem.utils.GsonUtils;

/**
 * Created by zm on 2018/7/3.
 */

public class FormatOut {

    public Long n;
    public String value;
    public FormatPubKey scriptPubKey;
    public int unlockedHeight;
    public String reserve;
    public int spentIndex;
    public String spentTxId;
    public String assetId = "";

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
