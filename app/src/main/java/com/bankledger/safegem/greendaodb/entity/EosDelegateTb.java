package com.bankledger.safegem.greendaodb.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        indexes = {
                @Index(value = "txId DESC", unique = true)
        }, nameInDb = "eos_delegate")
public class EosDelegateTb {

    @Property(nameInDb = "tx_id")
    private String txId;

    @Property(nameInDb = "from")
    private String from;

    @Property(nameInDb = "receiver")
    private String receiver;

    @Property(nameInDb = "stake_cpu_quantity")
    private String stakeCpuQuantity;

    @Property(nameInDb = "stake_net_quantity")
    private String stakeNetQuantity;

    @Property(nameInDb = "transfer")
    private String transfer;


    @Generated(hash = 1930510722)
    public EosDelegateTb(String txId, String from, String receiver,
            String stakeCpuQuantity, String stakeNetQuantity, String transfer) {
        this.txId = txId;
        this.from = from;
        this.receiver = receiver;
        this.stakeCpuQuantity = stakeCpuQuantity;
        this.stakeNetQuantity = stakeNetQuantity;
        this.transfer = transfer;
    }

    @Generated(hash = 849448465)
    public EosDelegateTb() {
    }


    public String getTxId() {
        return this.txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStakeCpuQuantity() {
        return this.stakeCpuQuantity;
    }

    public void setStakeCpuQuantity(String stakeCpuQuantity) {
        this.stakeCpuQuantity = stakeCpuQuantity;
    }

    public String getStakeNetQuantity() {
        return this.stakeNetQuantity;
    }

    public void setStakeNetQuantity(String stakeNetQuantity) {
        this.stakeNetQuantity = stakeNetQuantity;
    }

    public String getTransfer() {
        return this.transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }


}
