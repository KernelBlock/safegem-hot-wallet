package com.bankledger.safegem.entity;

import com.bankledger.safegem.greendaodb.entity.TxTb;

/**
 * @author bankledger
 * @time 2018/9/3 17:45
 */
public class TransactionDetailBean extends TxTb {

    private String amount;
    private String receiveAddress;
    private String minerFee;
    private boolean isSend;

    public TransactionDetailBean() {
    }

    public TransactionDetailBean(TxTb tx, String amount, String receiveAddress, String minerFee, boolean isSend) {
        super(tx);
        this.amount = amount;
        this.receiveAddress = receiveAddress;
        this.minerFee = minerFee;
        this.isSend = isSend;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getMinerFee() {
        return minerFee;
    }

    public void setMinerFee(String minerFee) {
        this.minerFee = minerFee;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
