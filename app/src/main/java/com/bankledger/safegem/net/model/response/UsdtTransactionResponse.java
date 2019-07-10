package com.bankledger.safegem.net.model.response;

import com.bankledger.safegem.message.FormatTx;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/1
 * Author: bankledger
 */
public class UsdtTransactionResponse {
    public String address;
    public String coin;
    public int restCount;
    public String currentLastTxId;
    public List<JsonObject> txList=new ArrayList<>();
}
