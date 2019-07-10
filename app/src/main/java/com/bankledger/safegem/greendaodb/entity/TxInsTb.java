package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

/**
 * Date：2018/8/24
 * Author: bankledger
 * 交易输入表
 */
@Entity(
        indexes = {
                @Index(value = "txHash DESC,prevTxHash DESC,prevOutSn DESC", unique = true)
        },
        nameInDb = "tx_ins_tb"
)
public class TxInsTb {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "tx_hash")
    private String txHash; //	交易ID txs ->txHash
    @Property(nameInDb = "prev_tx_hash")
    private String prevTxHash; //	上一个交易txid
    @Property(nameInDb = "prev_out_sn")
    private Long prevOutSn; //	上一个交易索vout
    @Property(nameInDb = "value_sat")
    private String valueSat; //	金额valueSat
    @Property(nameInDb = "sequence")
    private Long sequence; //	序列号

    @Generated(hash = 23551331)
    public TxInsTb(Long id, String txHash, String prevTxHash, Long prevOutSn, String valueSat,
            Long sequence) {
        this.id = id;
        this.txHash = txHash;
        this.prevTxHash = prevTxHash;
        this.prevOutSn = prevOutSn;
        this.valueSat = valueSat;
        this.sequence = sequence;
    }

    @Generated(hash = 27170074)
    public TxInsTb() {
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxHash() {
        return this.txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getPrevTxHash() {
        return this.prevTxHash;
    }

    public void setPrevTxHash(String prevTxHash) {
        this.prevTxHash = prevTxHash;
    }

    public Long getPrevOutSn() {
        return this.prevOutSn;
    }

    public void setPrevOutSn(Long prevOutSn) {
        this.prevOutSn = prevOutSn;
    }

    public String getValueSat() {
        return valueSat;
    }

    public void setValueSat(String valueSat) {
        this.valueSat = valueSat;
    }

    public Long getSequence() {
        return this.sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

}
