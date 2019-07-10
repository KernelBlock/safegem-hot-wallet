package com.bankledger.safegem.net.model.response;

import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class AddColdMonitorAddressResponse {

    private List<FailedBean> failed;

    public List<FailedBean> getFailed() {
        return failed;
    }

    public void setFailed(List<FailedBean> failed) {
        this.failed = failed;
    }

    public static class FailedBean {

        private String address;
        private String coin;
        private String name;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
