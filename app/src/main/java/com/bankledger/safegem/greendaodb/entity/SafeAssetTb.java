package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.GsonUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Date：2019/1/3
 * Author: bankledger
 */
@Entity(
        indexes = {
                @Index(value = "assetId DESC", unique = true)
        }, nameInDb = "safe_asset_tb")
public class SafeAssetTb {

    @Property(nameInDb = "asset_id")
    private String assetId; // 资产ID

    @Property(nameInDb = "asset_short_name")
    private String assetShortName; // 资产简称

    @Property(nameInDb = "asset_name")
    private String assetName; // 资产名称

    @Property(nameInDb = "asset_desc")
    private String assetDesc; // 资产描述

    @Property(nameInDb = "asset_unit")
    private String assetUnit; // 资产单位

    @Property(nameInDb = "asset_decimals")
    private long assetDecimals; // 最小单位

    @Property(nameInDb = "issue_time")
    private String issueTime; // 发行时间

    @Property(nameInDb = "asset_avail_amount")
    private long assetAvailAmount; // 资产可用总量

    @Property(nameInDb = "asset_wait_amount")
    private long assetWaitAmount; // 资产等待中总量

    @Property(nameInDb = "asset_lock_amount")
    private long assetLockAmount; // 资产锁定总量

    @Property(nameInDb = "asset_lock_total_amount")
    private long assetLocalTotalAmount; // 本地资产总量

    @Generated(hash = 1067997310)
    public SafeAssetTb(String assetId, String assetShortName, String assetName,
            String assetDesc, String assetUnit, long assetDecimals,
            String issueTime, long assetAvailAmount, long assetWaitAmount,
            long assetLockAmount, long assetLocalTotalAmount) {
        this.assetId = assetId;
        this.assetShortName = assetShortName;
        this.assetName = assetName;
        this.assetDesc = assetDesc;
        this.assetUnit = assetUnit;
        this.assetDecimals = assetDecimals;
        this.issueTime = issueTime;
        this.assetAvailAmount = assetAvailAmount;
        this.assetWaitAmount = assetWaitAmount;
        this.assetLockAmount = assetLockAmount;
        this.assetLocalTotalAmount = assetLocalTotalAmount;
    }

    @Generated(hash = 425599423)
    public SafeAssetTb() {
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetShortName() {
        return assetShortName;
    }

    public void setAssetShortName(String assetShortName) {
        this.assetShortName = assetShortName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetDesc() {
        return assetDesc;
    }

    public void setAssetDesc(String assetDesc) {
        this.assetDesc = assetDesc;
    }

    public String getAssetUnit() {
        return assetUnit;
    }

    public void setAssetUnit(String assetUnit) {
        this.assetUnit = assetUnit;
    }

    public long getAssetDecimals() {
        return assetDecimals;
    }

    public void setAssetDecimals(long assetDecimals) {
        this.assetDecimals = assetDecimals;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public long getAssetAvailAmount() {
        return assetAvailAmount;
    }

    public void setAssetAvailAmount(long assetAvailAmount) {
        this.assetAvailAmount = assetAvailAmount;
    }

    public long getAssetWaitAmount() {
        return assetWaitAmount;
    }

    public void setAssetWaitAmount(long assetWaitAmount) {
        this.assetWaitAmount = assetWaitAmount;
    }

    public long getAssetLockAmount() {
        return assetLockAmount;
    }

    public void setAssetLockAmount(long assetLockAmount) {
        this.assetLockAmount = assetLockAmount;
    }

    public long getAssetLocalTotalAmount() {
        return assetLocalTotalAmount;
    }

    public void setAssetLocalTotalAmount(long assetLocalTotalAmount) {
        this.assetLocalTotalAmount = assetLocalTotalAmount;
    }
}
