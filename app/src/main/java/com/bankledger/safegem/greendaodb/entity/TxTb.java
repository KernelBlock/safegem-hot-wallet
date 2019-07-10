package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Date：2018/8/24
 * Author: bankledger
 * 交易表
 */
@Entity(nameInDb = "tx_tb")
public class TxTb {

    @Id(autoincrement = true)
    private Long id;

    @Unique()
    @Property(nameInDb = "tx_hash")
    private String txHash; //	交易ID

    @Property(nameInDb = "height")
    private Long height; // 高度

    @Property(nameInDb = "block_hash")
    private String blockHash; // 区块hash

    @Property(nameInDb = "block_time")
    private Long blockTime; // 区块时间

    @Property(nameInDb = "lock_time")
    private Long lockTime; // 交易锁定时长

    @Property(nameInDb = "time")
    private Long time; // 交易时间

    @Property(nameInDb = "version")
    private Long version; // 版本

    @Property(nameInDb = "coin")
    private String coin; //	币种

    @Property(nameInDb = "confirm")
    private int confirm = 0;

    @Property(nameInDb = "btc_asset_type")
    private int btcAsssetType = 0;


    public TxTb(TxTb tx) {
        this.id = tx.getId();
        this.txHash = tx.getTxHash();
        this.height = tx.getHeight();
        this.blockHash = tx.getBlockHash();
        this.blockTime = tx.getBlockTime();
        this.lockTime = tx.getLockTime();
        this.time = tx.getTime();
        this.version = tx.getVersion();
        this.coin = tx.getCoin();
    }

    @Generated(hash = 566500227)
    public TxTb(Long id, String txHash, Long height, String blockHash,
            Long blockTime, Long lockTime, Long time, Long version, String coin,
            int confirm, int btcAsssetType) {
        this.id = id;
        this.txHash = txHash;
        this.height = height;
        this.blockHash = blockHash;
        this.blockTime = blockTime;
        this.lockTime = lockTime;
        this.time = time;
        this.version = version;
        this.coin = coin;
        this.confirm = confirm;
        this.btcAsssetType = btcAsssetType;
    }

    @Generated(hash = 1893559806)
    public TxTb() {
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


    public Long getHeight() {
        return this.height;
    }


    public void setHeight(Long height) {
        this.height = height;
    }


    public String getBlockHash() {
        return this.blockHash;
    }


    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }


    public Long getBlockTime() {
        return this.blockTime;
    }


    public void setBlockTime(Long blockTime) {
        this.blockTime = blockTime;
    }


    public Long getLockTime() {
        return this.lockTime;
    }


    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }


    public Long getTime() {
        return this.time;
    }


    public void setTime(Long time) {
        this.time = time;
    }


    public Long getVersion() {
        return this.version;
    }


    public void setVersion(Long version) {
        this.version = version;
    }


    public String getCoin() {
        return this.coin;
    }


    public void setCoin(String coin) {
        this.coin = coin;
    }

    public int getConfirm() {
        return this.confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public int getBtcAsssetType() {
        return this.btcAsssetType;
    }

    public void setBtcAsssetType(int btcAsssetType) {
        this.btcAsssetType = btcAsssetType;
    }


    
}
