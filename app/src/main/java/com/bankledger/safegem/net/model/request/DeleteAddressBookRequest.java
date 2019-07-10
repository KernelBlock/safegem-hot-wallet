package com.bankledger.safegem.net.model.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/9/10
 * Author: bankledger
 */
public class DeleteAddressBookRequest {

    public List<DeleteAddressBook> addressList = new ArrayList<>();

    public static class DeleteAddressBook {
        public DeleteAddressBook(String address) {
            this.address = address;
        }

        public String address;
    }

}
