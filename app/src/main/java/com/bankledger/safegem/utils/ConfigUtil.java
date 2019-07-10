package com.bankledger.safegem.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Date：2018/8/20
 * Author: bankledger
 */
public class ConfigUtil {

    private Map<String, String> testUrlMap = new HashMap<String, String>();
    private Map<String, String> releaseUrlMap = new HashMap<String, String>();
    private Map<String, String> coinMap = new HashMap<String, String>();

    private final static ConfigUtil INSTANCE = new ConfigUtil();

    private ConfigUtil() {
        testUrlMap.put(Constants.BCH, "https://www.blocktrail.com/tBCC/tx/");
        testUrlMap.put(Constants.BSV, "");
        testUrlMap.put(Constants.BTC, "https://www.blocktrail.com/tBTC/tx/");
        testUrlMap.put(Constants.BTG, "https://test-explorer.bitcoingold.org/insight/tx/");
        testUrlMap.put(Constants.DASH, "https://testnet-insight.dashevo.org/insight/tx/");
        testUrlMap.put(Constants.EOS, "https://jungle.bloks.io/transaction/");
        testUrlMap.put(Constants.ETC, "");
        testUrlMap.put(Constants.ETH, "");
        testUrlMap.put(Constants.FTO, "");
        testUrlMap.put(Constants.LTC, "https://chain.so/tx/LTCTEST/");
        testUrlMap.put(Constants.QTUM, "https://testnet.qtum.info/tx/");
        testUrlMap.put(Constants.SAFE, "http://10.0.0.77/tx/");
        testUrlMap.put(Constants.USDT, "");

        releaseUrlMap.put(Constants.BCH, "https://blockchair.com/bitcoin-cash/transaction/");
        releaseUrlMap.put(Constants.BSV, "https://blockchair.com/bitcoin-sv/transaction/");
        releaseUrlMap.put(Constants.BTC, "https://blockchair.com/bitcoin/transaction/");
        releaseUrlMap.put(Constants.BTG, "https://explorer.bitcoingold.org/insight/tx/");
        releaseUrlMap.put(Constants.DASH, "https://dashblockexplorer.com/tx/");
        releaseUrlMap.put(Constants.EOS, "https://eostracker.io/transactions/");
        releaseUrlMap.put(Constants.ETC, "http://gastracker.io/tx/");
        releaseUrlMap.put(Constants.ETH, "https://www.etherchain.org/tx/");
        releaseUrlMap.put(Constants.FTO, "https://explorer.futurocoin.com/insight/tx/");
        releaseUrlMap.put(Constants.LTC, "https://blockchair.com/litecoin/transaction/");
        releaseUrlMap.put(Constants.QTUM, "https://qtum.info/tx/");
        releaseUrlMap.put(Constants.SAFE, "https://chain.anwang.com/tx/");
        releaseUrlMap.put(Constants.USDT, "https://www.omniexplorer.info/tx/");

        coinMap.put(Constants.BTC, Constants.BTC_FULL_NAME);
        coinMap.put(Constants.LTC, Constants.LTC_FULL_NAME);
        coinMap.put(Constants.SAFE, Constants.SAFE_FULL_NAME);
        coinMap.put(Constants.DASH, Constants.DASH_FULL_NAME);
        coinMap.put(Constants.QTUM, Constants.QTUM_FULL_NAME);
        coinMap.put(Constants.BCH, Constants.BCH_FULL_NAME);
        coinMap.put(Constants.BSV, Constants.BSV_FULL_NAME);
        coinMap.put(Constants.BTG, Constants.BTG_FULL_NAME);
        coinMap.put(Constants.FTO, Constants.FTO_FULL_NAME);
        coinMap.put(Constants.USDT, Constants.USDT_FULL_NAME);
        coinMap.put(Constants.EOS, Constants.EOS_FULL_NAME);
        coinMap.put(Constants.ETH, Constants.ETH_FULL_NAME);
        coinMap.put(Constants.ETC, Constants.ETC_FULL_NAME);
    }

    public static ConfigUtil getInstance() {
        return INSTANCE;
    }

    public String getBrowserUrl(String key) {
        if (Constants.isDebug) {
            return testUrlMap.get(key);
        } else {
            return releaseUrlMap.get(key);
        }
    }

    public String getCoinFullName(String coin){
        return coinMap.get(coin);
    }

}
