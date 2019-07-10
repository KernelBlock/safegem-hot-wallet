package com.bankledger.safegem.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * $desc 数字和字母
 * @author bankledger
 * @time 2018/10/30 09:08
 */
public class AddressInputFilter implements InputFilter {

    private final String digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!digits.contains(String.valueOf(source.charAt(i)))) {
                return "";
            }
        }
        return null;
    }
}
