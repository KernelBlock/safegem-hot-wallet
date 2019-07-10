package com.bankledger.safegem.net.model.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/6
 * Author: bankledger
 */
public class AddAddressBookRequest {
    public List<AddressBook> addressList = new ArrayList<>();

    public static class AddressBook {
        public String coin;   //币名称 如：SAFE

        public AddressBook(String coin, String name, String address) {
            this.coin = coin;
            this.name = name;
            this.address = address;
        }

        public String name; //地址名称
        public String address;  //地址
        public String addressType = "2";  //1、我的地址、2联系人
    }
}
