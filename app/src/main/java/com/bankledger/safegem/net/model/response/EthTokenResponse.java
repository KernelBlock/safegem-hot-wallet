package com.bankledger.safegem.net.model.response;

import com.bankledger.safegem.utils.GsonUtils;

/**
 * Created by zm on 2018/9/11.
 */

public class EthTokenResponse {

    public String balance;

    public String transactionCount;

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }
}
