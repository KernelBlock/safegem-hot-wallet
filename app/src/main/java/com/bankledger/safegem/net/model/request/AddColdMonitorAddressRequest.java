package com.bankledger.safegem.net.model.request;

import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class AddColdMonitorAddressRequest {
    public AddColdMonitorAddressRequest(String coldUniqueId, List<Monitor> monitorList) {
        this.coldUniqueId = coldUniqueId;
        this.monitorList = monitorList;
    }

    public String coldUniqueId;
    public List<Monitor> monitorList;

   public static class Monitor {

        public String coin;  //币名称 如：SAFE

        public Monitor(String coin, String name, String address) {
            this.coin = coin;
            this.name = name;
            this.address = address;
        }

        public String name;  //地址名称
        public String address; //地址
    }
}
