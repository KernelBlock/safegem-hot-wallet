package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        indexes = {
                @Index(value = "txId DESC", unique = true)
        }, nameInDb = "eos_ram")
public class EosRamTb {

    @Property(nameInDb = "tx_id")
    private String txId;

    @Property(nameInDb = "bytes")
    private String bytes;

    @Property(nameInDb = "from")
    private String from;

    @Property(nameInDb = "to")
    private String to;

    @Property(nameInDb = "quantity")
    private String quantity;

    @Property(nameInDb = "memo")
    private String memo;


    @Generated(hash = 1791856248)
    public EosRamTb(String txId, String bytes, String from, String to,
                    String quantity, String memo) {
        this.txId = txId;
        this.bytes = bytes;
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.memo = memo;
    }

    @Generated(hash = 1779801059)
    public EosRamTb() {
    }


    public String getTxId() {
        return this.txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getBytes() {
        return this.bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
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

    public String getQuantity() {
        return this.quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


}
