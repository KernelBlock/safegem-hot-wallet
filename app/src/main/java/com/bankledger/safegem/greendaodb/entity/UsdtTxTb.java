package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.DateTimeUtil;
import com.bankledger.safegem.utils.StringUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

@Entity(
        indexes = {
                @Index(value = "txid DESC", unique = true)
        }, nameInDb = "usdt_tx")
public class UsdtTxTb {

    @Property(nameInDb = "cold_unique_id")
    private String cold_unique_id;

    @Property(nameInDb = "version")
    private String version;

    @Property(nameInDb = "txid")
    private String txid;

    @Property(nameInDb = "amount")
    private String amount;

    @Property(nameInDb = "referenceaddress")
    private String referenceaddress;

    @Property(nameInDb = "sendingaddress")
    private String sendingaddress;

    @Property(nameInDb = "fee")
    private String fee;

    @Property(nameInDb = "blocktime")
    private Long blocktime;

    @Property(nameInDb = "confirmations")
    private String confirmations;

    @Property(nameInDb = "blockhash")
    private String blockhash;

    @Property(nameInDb = "positioninblock")
    private String positioninblock;

    @Property(nameInDb = "block")
    private String block;

    @Property(nameInDb = "propertyid")
    private String propertyid;

    @Generated(hash = 1761416397)
    public UsdtTxTb(String cold_unique_id, String version, String txid,
            String amount, String referenceaddress, String sendingaddress,
            String fee, Long blocktime, String confirmations, String blockhash,
            String positioninblock, String block, String propertyid) {
        this.cold_unique_id = cold_unique_id;
        this.version = version;
        this.txid = txid;
        this.amount = amount;
        this.referenceaddress = referenceaddress;
        this.sendingaddress = sendingaddress;
        this.fee = fee;
        this.blocktime = blocktime;
        this.confirmations = confirmations;
        this.blockhash = blockhash;
        this.positioninblock = positioninblock;
        this.block = block;
        this.propertyid = propertyid;
    }

    @Generated(hash = 375673531)
    public UsdtTxTb() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getAmount() {
        return StringUtil.subZeroAndDot(this.amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReferenceaddress() {
        return referenceaddress;
    }

    public void setReferenceaddress(String referenceaddress) {
        this.referenceaddress = referenceaddress;
    }

    public String getSendingaddress() {
        return sendingaddress;
    }

    public void setSendingaddress(String sendingaddress) {
        this.sendingaddress = sendingaddress;
    }

    public String getFee() {
        return StringUtil.subZeroAndDot(this.fee);
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDate(){
        return DateTimeUtil.getShortDateTimeString0(blocktime * 1000);
    }

    public String getFormatTime(){
        return DateTimeUtil.getMontnDayHM(blocktime * 1000);
    }

    public String getFormatDateTime(){
        return DateTimeUtil.getDateTimeString(blocktime * 1000);
    }

    public Long getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(Long blocktime) {
        this.blocktime = blocktime;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public String getPositioninblock() {
        return positioninblock;
    }

    public void setPositioninblock(String positioninblock) {
        this.positioninblock = positioninblock;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(String propertyid) {
        this.propertyid = propertyid;
    }


    public String getCold_unique_id() {
        return this.cold_unique_id;
    }

    public void setCold_unique_id(String cold_unique_id) {
        this.cold_unique_id = cold_unique_id;
    }

    @Override
    public String toString() {
        return "UsdtTxTb{" +
                "cold_unique_id='" + cold_unique_id + '\'' +
                ", version='" + version + '\'' +
                ", txid='" + txid + '\'' +
                ", amount='" + amount + '\'' +
                ", referenceaddress='" + referenceaddress + '\'' +
                ", sendingaddress='" + sendingaddress + '\'' +
                ", fee='" + fee + '\'' +
                ", blocktime='" + blocktime + '\'' +
                ", confirmations='" + confirmations + '\'' +
                ", blockhash='" + blockhash + '\'' +
                ", positioninblock='" + positioninblock + '\'' +
                ", block='" + block + '\'' +
                ", propertyid='" + propertyid + '\'' +
                '}';
    }
}
