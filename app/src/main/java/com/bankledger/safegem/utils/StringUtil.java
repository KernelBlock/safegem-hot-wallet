package com.bankledger.safegem.utils;

import android.text.TextUtils;

import org.w3c.dom.Text;

public class StringUtil {

    public static String subZeroAndDot(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return "0";
        }
        if (amount.indexOf(".") > 0) {
            amount = amount.replaceAll("0+?$", "");//去掉多余的0
            amount = amount.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return amount;
    }

    public static String getDisplayName(String coin) {
        if (Constants.BCH.equalsIgnoreCase(coin)) {
            return Constants.BCHABC;
        } else if (Constants.BSV.equalsIgnoreCase(coin)) {
            return Constants.BCHSV;
        } else {
            return coin;
        }
    }

    public static String formatKb(Long value) {
        return BigDecimalUtils.div(Long.toString(value), "1024", 2);
    }

    public static String formatMs(Long value) {
        return BigDecimalUtils.div(Long.toString(value), "1000", 2);
    }

}
