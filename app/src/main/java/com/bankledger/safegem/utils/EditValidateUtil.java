package com.bankledger.safegem.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * Date：2018/8/22
 * Author: bankledger
 * EditText验证工具类
 */
public class EditValidateUtil {
    private static volatile EditValidateUtil INSTANCE;

    private EditValidateUtil() {

    }

    public static EditValidateUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (EditValidateUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EditValidateUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 判断字符串是否符合手机号码格式
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
     * 电信号段: 133,149,153,170,173,177,180,181,189
     *
     * @return 待检测的字符串
     */
    public boolean isMobileNO(String mobileNums) {
        String telRegex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 空格限制
     *
     * @param editText
     */
    public void fiterTrim(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return " ".equals(source) ? "" : null;
            }
        };
        //保留原有的exittext的filter
        InputFilter[] oldFilters = editText.getFilters();
        int oldFiltersLength = oldFilters.length;
        InputFilter[] newFilters = new InputFilter[oldFiltersLength + 1];
        if (oldFiltersLength > 0) {
            System.arraycopy(oldFilters, 0, newFilters, 0, oldFiltersLength);
        }
        //添加新的过滤规则
        newFilters[oldFiltersLength] = filter;
        editText.setFilters(newFilters);
    }

}
