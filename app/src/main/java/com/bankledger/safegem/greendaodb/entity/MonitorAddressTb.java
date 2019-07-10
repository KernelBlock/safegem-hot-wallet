package com.bankledger.safegem.greendaodb.entity;

import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.GsonUtils;
import com.bankledger.safegem.utils.Utils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
@Entity(
        indexes = {
                @Index(value = "userId DESC, coldUniqueId DESC, address DESC, coinType DESC, contractAddress DESC", unique = true)
        },
        nameInDb = "monitor_address"
)
public class MonitorAddressTb {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "user_id")
    private Long userId;//	用户ID

    @Property(nameInDb = "cold_unique_id")
    private String coldUniqueId; //	冷端序列号

    @Property(nameInDb = "coin")
    private String coin; //	币种

    @Property(nameInDb = "address")
    private String address; // 地址

    @Property(nameInDb = "temp_flag")
    private Integer tempFlag; // 临时标识  0：监控，1:临时

    @Property(nameInDb = "contract_address")
    private String contractAddress; // 合约地址

    @Property(nameInDb = "coin_type")
    private Integer coinType; // 币种类型 0: BTC 1: ETH 2: ETH_TOKEN 3:ETC 4:EOS, 5:EOS_TOKEN, 6:USDT

    @Property(nameInDb = "symbol")
    private String symbol; // 代币符号

    @Property(nameInDb = "decimals")
    private String decimals; // 精度

    @Property(nameInDb = "total_supply")
    private String totalSupply; // 发行总量

    @Property(nameInDb = "coin_img")
    private String coinImg; // token图片

    public MonitorAddressTb(Long userId, String coldUniqueId, String coin, String address, Integer tempFlag) {
        this.userId = userId;
        this.coldUniqueId = coldUniqueId;
        this.coin = coin;
        this.address = address;
        this.coinType = Utils.coin2Type(coin);
        this.contractAddress = "";
        this.tempFlag = tempFlag;
    }

    public MonitorAddressTb(Long userId, String coldUniqueId, String coin, String address, int coinType, Integer tempFlag) {
        this.userId = userId;
        this.coldUniqueId = coldUniqueId;
        this.coin = coin;
        this.address = address;
        this.coinType = coinType;
        this.contractAddress = "";
        this.tempFlag = tempFlag;
    }

    public MonitorAddressTb(Long userId, String coldUniqueId, String coin, String address, Integer tempFlag,
                            String contractAddress, String symbol, String decimals, String totalSupply, Integer coinType, String coinImg) {
        this.userId = userId;
        this.coldUniqueId = coldUniqueId;
        this.coin = coin;
        this.address = address;
        this.tempFlag = tempFlag;
        this.contractAddress = contractAddress;
        this.symbol = symbol;
        this.decimals = decimals;
        this.totalSupply = totalSupply;
        this.coinType = coinType;
        this.coinImg = coinImg;
    }

    @Generated(hash = 155774958)
    public MonitorAddressTb(Long id, Long userId, String coldUniqueId, String coin, String address, Integer tempFlag, String contractAddress,
            Integer coinType, String symbol, String decimals, String totalSupply, String coinImg) {
        this.id = id;
        this.userId = userId;
        this.coldUniqueId = coldUniqueId;
        this.coin = coin;
        this.address = address;
        this.tempFlag = tempFlag;
        this.contractAddress = contractAddress;
        this.coinType = coinType;
        this.symbol = symbol;
        this.decimals = decimals;
        this.totalSupply = totalSupply;
        this.coinImg = coinImg;
    }

    @Generated(hash = 1743627313)
    public MonitorAddressTb() {
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getColdUniqueId() {
        return this.coldUniqueId;
    }

    public void setColdUniqueId(String coldUniqueId) {
        this.coldUniqueId = coldUniqueId;
    }

    public String getCoin() {
        if (coinType == Constants.COIN_ETH_TOKEN) {
            return symbol;
        } else {
            return this.coin;
        }
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTempFlag() {
        return this.tempFlag;
    }

    public void setTempFlag(Integer tempFlag) {
        this.tempFlag = tempFlag;
    }

    public String getContractAddress() {
        return this.contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Integer getCoinType() {
        return this.coinType;
    }

    public void setCoinType(Integer coinType) {
        this.coinType = coinType;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDecimals() {
        return this.decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getTotalSupply() {
        return this.totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getCoinImg() {
        return this.coinImg;
    }

    public void setCoinImg(String coinImg) {
        this.coinImg = coinImg;
    }

    @Override
    public String toString() {
        return GsonUtils.toString(this);
    }

}
