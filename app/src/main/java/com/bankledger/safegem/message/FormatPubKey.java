package com.bankledger.safegem.message;

import com.bankledger.safegem.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zm on 2018/7/3.
 */

public class FormatPubKey {

    public List<String> addresses = new ArrayList<>();
    public String asm;
    public String hex;
    public String reqSigs;
    public String type;

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

}
