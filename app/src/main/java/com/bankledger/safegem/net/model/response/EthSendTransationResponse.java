package com.bankledger.safegem.net.model.response;

/**
 * Date：2018/9/26
 * Author: bankledger
 */
public class EthSendTransationResponse {

    private TransactionBean transaction;
    private String txId;

    public TransactionBean getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionBean transaction) {
        this.transaction = transaction;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public static class TransactionBean {

        private String blockHash;
        private int blockNumber;
        private String blockNumberRaw;
        private int chainId;
        private String creates;
        private String from;
        private int gas;
        private long gasPrice;
        private String gasPriceRaw;
        private String gasRaw;
        private String hash;
        private String input;
        private int nonce;
        private String nonceRaw;
        private String publicKey;
        private String r;
        private String raw;
        private String s;
        private String to;
        private int transactionIndex;
        private String transactionIndexRaw;
        private int v;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;
        private String valueRaw;

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public int getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(int blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getBlockNumberRaw() {
            return blockNumberRaw;
        }

        public void setBlockNumberRaw(String blockNumberRaw) {
            this.blockNumberRaw = blockNumberRaw;
        }

        public int getChainId() {
            return chainId;
        }

        public void setChainId(int chainId) {
            this.chainId = chainId;
        }

        public String getCreates() {
            return creates;
        }

        public void setCreates(String creates) {
            this.creates = creates;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public int getGas() {
            return gas;
        }

        public void setGas(int gas) {
            this.gas = gas;
        }

        public long getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(long gasPrice) {
            this.gasPrice = gasPrice;
        }

        public String getGasPriceRaw() {
            return gasPriceRaw;
        }

        public void setGasPriceRaw(String gasPriceRaw) {
            this.gasPriceRaw = gasPriceRaw;
        }

        public String getGasRaw() {
            return gasRaw;
        }

        public void setGasRaw(String gasRaw) {
            this.gasRaw = gasRaw;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public int getNonce() {
            return nonce;
        }

        public void setNonce(int nonce) {
            this.nonce = nonce;
        }

        public String getNonceRaw() {
            return nonceRaw;
        }

        public void setNonceRaw(String nonceRaw) {
            this.nonceRaw = nonceRaw;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public int getTransactionIndex() {
            return transactionIndex;
        }

        public void setTransactionIndex(int transactionIndex) {
            this.transactionIndex = transactionIndex;
        }

        public String getTransactionIndexRaw() {
            return transactionIndexRaw;
        }

        public void setTransactionIndexRaw(String transactionIndexRaw) {
            this.transactionIndexRaw = transactionIndexRaw;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }



        public String getValueRaw() {
            return valueRaw;
        }

        public void setValueRaw(String valueRaw) {
            this.valueRaw = valueRaw;
        }
    }
}
