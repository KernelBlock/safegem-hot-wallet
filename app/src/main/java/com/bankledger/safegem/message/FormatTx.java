package com.bankledger.safegem.message;

import android.text.TextUtils;

import com.bankledger.safegem.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2018/7/3.
 */

public class FormatTx {

    public String hash;
    public String blockhash;
    public long blocktime;
    public long confirmations;
    public long height;
    public String hex;
    public long locktime;
    public long size;
    public long time;
    public String txid;
    public long version;
    public List<FormatIn> vin = new ArrayList<>();
    public List<FormatOut> vout = new ArrayList<>();

    public boolean isSafeAsset() {
        for (FormatOut out : vout) {
            if (!TextUtils.isEmpty(out.assetId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
