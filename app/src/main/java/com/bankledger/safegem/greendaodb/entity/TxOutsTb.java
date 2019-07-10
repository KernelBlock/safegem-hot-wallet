package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Date：2018/8/24
 * Author: bankledger
 */
@Entity(
        indexes = {
                @Index(value = "txHash DESC, outSn DESC", unique = true)
        },
        nameInDb = "tx_outs_tb"
)
public class TxOutsTb {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "tx_hash")
    private String txHash; //  	交易ID
    @Property(nameInDb = "out_sn")
    private Long outSn; // 交易索引
    @Property(nameInDb = "coin")
    private String coin; // 币种
    @Property(nameInDb = "out_script")
    private String outScript; // 公钥
    @Property(nameInDb = "out_value")
    private String outValue; // 金额
    @Property(nameInDb = "out_status")
    private Integer outStatus = 0; // 0未花费，1已花费
    @Property(nameInDb = "mul_type")
    private Integer mulType; //	0单地址，1多地址
    @Property(nameInDb = "out_address")
    private String outAddress; //	地址，多地址逗号分割
    @Property(nameInDb = "unlock_height")
    private Integer unLockHeight = 0; // SAFE预留
    @Property(nameInDb = "reserve")
    private String reserve = ""; //	SAFE预留
    @Property(nameInDb = "asset_id")
    private String assetId = ""; //	当币种为SAFE资产，该字段为assetId，否则直接用币种填充


    @Generated(hash = 1799553426)
    public TxOutsTb(Long id, String txHash, Long outSn, String coin, String outScript, String outValue, Integer outStatus, Integer mulType, String outAddress, Integer unLockHeight,
            String reserve, String assetId) {
        this.id = id;
        this.txHash = txHash;
        this.outSn = outSn;
        this.coin = coin;
        this.outScript = outScript;
        this.outValue = outValue;
        this.outStatus = outStatus;
        this.mulType = mulType;
        this.outAddress = outAddress;
        this.unLockHeight = unLockHeight;
        this.reserve = reserve;
        this.assetId = assetId;
    }


    @Generated(hash = 702258826)
    public TxOutsTb() {
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


    public Long getOutSn() {
        return this.outSn;
    }


    public void setOutSn(Long outSn) {
        this.outSn = outSn;
    }


    public String getCoin() {
        return this.coin;
    }


    public void setCoin(String coin) {
        this.coin = coin;
    }


    public String getOutScript() {
        return this.outScript;
    }


    public void setOutScript(String outScript) {
        this.outScript = outScript;
    }

    public String getOutValue() {
        return outValue;
    }

    public void setOutValue(String outValue) {
        this.outValue = outValue;
    }

    public Integer getOutStatus() {
        return this.outStatus;
    }


    public void setOutStatus(Integer outStatus) {
        this.outStatus = outStatus;
    }


    public Integer getMulType() {
        return this.mulType;
    }


    public void setMulType(Integer mulType) {
        this.mulType = mulType;
    }

    public String getOutAddress() {
        return this.outAddress;
    }


    public void setOutAddress(String outAddress) {
        this.outAddress = outAddress;
    }


    public Integer getUnLockHeight() {
        return this.unLockHeight;
    }


    public void setUnLockHeight(Integer unLockHeight) {
        this.unLockHeight = unLockHeight;
    }


    public String getReserve() {
        return this.reserve;
    }


    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getAssetId() {
        return this.assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public String toString() {
        return "[" + coin + "," + txHash + "," + outSn + "," + outValue + "," + outStatus + "," + mulType + "," + outAddress + "," + unLockHeight + "," + reserve + "," + assetId + "]";
    }

}
