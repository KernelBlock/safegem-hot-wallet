package com.bankledger.safegem.utils;

import java.math.BigDecimal;

/**
 * Date：2018/9/20
 * Author: bankledger
 */
public class EthUtil {

    public static String formatEthTokenAmountUnit(String balancestr, int decimals, String symbol) {
        BigDecimal balance = new BigDecimal(balancestr);
        balance = balance.divide(BigDecimal.TEN.pow(decimals));
        BigDecimal decimal = Convert.fromWei(balance, Convert.Unit.ETHER);
        return decimal.toPlainString() + " " + symbol;
    }

    public static String formatETHTokenAmount( String balancestr, int decimals) {
            BigDecimal balance = new BigDecimal(balancestr);
            balance = balance.divide(BigDecimal.TEN.pow(decimals));
            return balance.toPlainString();
    }


    public static String formatETHAmount(String balancestr) {
        BigDecimal decimal = Convert.fromWei(new BigDecimal(balancestr), Convert.Unit.ETHER);
        return decimal.toPlainString();
    }

}
