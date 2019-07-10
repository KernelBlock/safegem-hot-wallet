package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.StringUtil;
import com.bankledger.safegem.utils.Utils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        indexes = {
                @Index(value = "txId DESC", unique = true)
        }, nameInDb = "eos_tx")
public class EosTxTb {

    @Property(nameInDb = "tx_id")
    private String txId;

    @Property(nameInDb = "account")
    private String account;

    @Property(nameInDb = "time")
    private String time;

    @Property(nameInDb = "height")
    private String height;

    @Property(nameInDb = "coin")
    private String coin;

    @Property(nameInDb = "amount")
    private String amount;

    @Property(nameInDb = "from")
    private String from;

    @Property(nameInDb = "to")
    private String to;

    @Property(nameInDb = "memo")
    private String memo;

    @Property(nameInDb = "type")
    private int type;

    @Generated(hash = 398487378)
    public EosTxTb(String txId, String account, String time, String height,
            String coin, String amount, String from, String to, String memo,
            int type) {
        this.txId = txId;
        this.account = account;
        this.time = time;
        this.height = height;
        this.coin = coin;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.memo = memo;
        this.type = type;
    }

    @Generated(hash = 1793225281)
    public EosTxTb() {
    }
    

    public String getFormatTime() {
        return time.substring(5, 19).replace("T", " ");
    }

    public String getFormatDateTime() {
        return DateTimeUtil.transformTime(time.substring(0, 19).replace("T", " "));
    }

    public String getDate() {
        return this.time.substring(0, 7);
    }

    public String getTxId() {
        return this.txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCoin() {
        return this.coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getAmount() {
        return StringUtil.subZeroAndDot(this.amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
