package com.bankledger.safegem.net.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * Date：2018/9/12
 * Author: bankledger
 */
public class ERCResponse implements Serializable {

    public String coldUniqueId;
    public List<ERC> erc20List;

    public static class ERC implements Serializable {

        private String name;
        private String symbol;
        private String decimals;
        private String totalSupply;
        private String contractAddress;

        public int getActivication() {
            return activication;
        }

        public void setActivication(int activication) {
            this.activication = activication;
        }

        private int activication;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        private String icon;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getDecimals() {
            return decimals;
        }

        public void setDecimals(String decimals) {
            this.decimals = decimals;
        }

        public String getTotalSupply() {
            return totalSupply;
        }

        public void setTotalSupply(String totalSupply) {
            this.totalSupply = totalSupply;
        }

        public String getContractAddress() {
            return contractAddress.toLowerCase();
        }

        public void setContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
        }

        public String formatActive() {
            return "[\"" + name + "\"," + "\"" + contractAddress + "\"," + "\"" + symbol + "\"," + "\"" + decimals + "\"," + "\"" + totalSupply + "\"]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ERC) {
                ERC erc = (ERC) obj;
                return this.contractAddress.equals(erc.getContractAddress());
            }
            return false;
        }
    }

}
