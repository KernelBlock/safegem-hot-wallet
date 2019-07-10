package com.bankledger.safegem.net.model.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/14
 * Author: bankledger
 */
public class GetEthAddressInfoRequest {
    public String address;
    public List<String> contractAddressList = new ArrayList<>();
}
