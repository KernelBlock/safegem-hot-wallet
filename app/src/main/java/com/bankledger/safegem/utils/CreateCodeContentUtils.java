package com.bankledger.safegem.utils;

import android.text.TextUtils;

import com.bankledger.protobuf.bean.CoinBalance;
import com.bankledger.protobuf.bean.EosBalance;
import com.bankledger.protobuf.bean.EthToken;
import com.bankledger.protobuf.bean.UTXO;
import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;
import com.bankledger.safegem.greendaodb.entity.TxOutsTb;
import com.bankledger.safegem.net.model.response.GetEthAddressResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成二维码内容工具类
 */
public class CreateCodeContentUtils {

    /**
     * 生产ETH同步余额二维码内容
     */
    public static List<CoinBalance> createETHUpdateBanlanceContent(String eth_address, GetEthAddressResponse getEthAddressResponse) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        if (getEthAddressResponse.list == null || getEthAddressResponse.list.size() == 0) {
            return coinBalanceList;
        }
        CoinBalance balance = new CoinBalance();
        balance.coin = Constants.ETH;
        EthToken ethBalance = new EthToken();
        ethBalance.isToken = 0;
        ethBalance.ethAddress = eth_address;
        ethBalance.balance = getEthAddressResponse.list.get(0).balance;
        ethBalance.transactionCount = Integer.parseInt(getEthAddressResponse.transactionCount);
        ethBalance.name = Constants.ETH;
        ethBalance.symbol = Constants.ETH;
        ethBalance.decimals = 18;
        ethBalance.totalSupply = String.valueOf(0);
        ethBalance.contractsAddress = eth_address;
        balance.ethToken = ethBalance;
        coinBalanceList.add(balance);
        return coinBalanceList;
    }

    /**
     * 生产ETHToken同步余额二维码内容
     */

    public static List<CoinBalance> createETHTokenUpdateBanlanceContent(String eth_address, GetEthAddressResponse getEthAddressResponse) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        if (getEthAddressResponse.list == null || getEthAddressResponse.list.size() == 0) {
            return coinBalanceList;
        }
        for (GetEthAddressResponse.EthAddress ethAddress : getEthAddressResponse.list) {
            if (ethAddress.contractAddress != null && ethAddress.contractAddress.trim().length() > 0) {
                CoinBalance balance = new CoinBalance();
                balance.coin = Constants.ETH;
                EthToken ethBalance = new EthToken();
                ethBalance.isToken = 1;
                ethBalance.ethAddress = eth_address;
                ethBalance.balance = ethAddress.balance;
                ethBalance.transactionCount = Integer.parseInt(getEthAddressResponse.transactionCount);
                ethBalance.name = ethAddress.name;
                ethBalance.symbol = ethAddress.symbol;
                ethBalance.decimals = Integer.parseInt(ethAddress.decimals);
                ethBalance.totalSupply = ethAddress.totalSupply;
                ethBalance.contractsAddress = ethAddress.contractAddress;
                balance.ethToken = ethBalance;
                coinBalanceList.add(balance);
            }
        }
        return coinBalanceList;
    }

    /**
     * 生产ETC同步余额二维码内容
     */

    public static List<CoinBalance> createETCUpdateBanlanceContent(String etc_address, GetEthAddressResponse getEthAddressResponse) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        CoinBalance balance = new CoinBalance();
        balance.coin = Constants.ETC;
        EthToken ethBalance = new EthToken();
        ethBalance.isToken = 2;
        ethBalance.ethAddress = etc_address;
        ethBalance.balance = getEthAddressResponse.balance;
        ethBalance.transactionCount = Integer.parseInt(getEthAddressResponse.transactionCount);
        ethBalance.name = Constants.ETC;
        ethBalance.symbol = Constants.ETC;
        ethBalance.decimals = 18;
        ethBalance.totalSupply = String.valueOf(0);
        ethBalance.contractsAddress = etc_address;
        balance.ethToken = ethBalance;
        coinBalanceList.add(balance);
        return coinBalanceList;
    }

    public static List<CoinBalance> createEOSUpdateBanlanceContent(EosAccountTb account, List<EosBalanceTb> eosBalanceTbs) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        for (int i = 0; i < eosBalanceTbs.size(); i++) {
            EosBalanceTb eosBalanceTb = eosBalanceTbs.get(i);
            CoinBalance balance = new CoinBalance();
            balance.coin = Constants.EOS;
            EosBalance eosBalance = new EosBalance();
            eosBalance.account = account.getAccount();
            if (i == 0) {
                eosBalance.active = account.getActivePubKey();
                eosBalance.owner = account.getOwnerPubKey();
            }
            eosBalance.balance = eosBalanceTb.getBalance();
            eosBalance.tokenName = eosBalanceTb.getTokenName();
            balance.eosBalance = eosBalance;
            coinBalanceList.add(balance);
        }
        return coinBalanceList;
    }

    public static List<CoinBalance> createUsdtUpdateBanlanceContent(String address, List<EosBalanceTb> eosBalanceTbs) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        for (EosBalanceTb eosBalanceTb : eosBalanceTbs) {
            CoinBalance balance = new CoinBalance();
            balance.coin = Constants.USDT;
            EosBalance eosBalance = new EosBalance();
            eosBalance.account = address;
            eosBalance.balance = eosBalanceTb.getBalance();
            eosBalance.tokenName = eosBalanceTb.getTokenName();
            balance.eosBalance = eosBalance;
            coinBalanceList.add(balance);
        }
        return coinBalanceList;
    }


    /**
     * 生产BTC系列同步余额二维码内容
     */
    public static List<CoinBalance> createBTCUpdateBanlanceContent(List<TxOutsTb> txOutsTbList) {
        List<CoinBalance> coinBalanceList = new ArrayList<CoinBalance>();
        for (TxOutsTb txOutsTb : txOutsTbList) {
            CoinBalance balance = new CoinBalance();
            balance.coin = txOutsTb.getCoin();
            UTXO utxo = new UTXO();
            utxo.txHash = txOutsTb.getTxHash();
            utxo.outSn = new Long(txOutsTb.getOutSn()).intValue();
            utxo.status = txOutsTb.getOutStatus();
            List<String> addressList = new ArrayList<String>();
            addressList.add(txOutsTb.getOutAddress());
            utxo.address = addressList;
            utxo.unlockHeight = txOutsTb.getUnLockHeight();
            if (!TextUtils.isEmpty(txOutsTb.getAssetId())) {
                SafeAssetTb safeAssetTb = SafeGemApplication.getInstance().getSafeAsset(txOutsTb.getAssetId());
                if (safeAssetTb == null) {
                    continue;
                }
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("assetId", safeAssetTb.getAssetId());
                    jsonObj.put("assetShortName", safeAssetTb.getAssetShortName());
                    jsonObj.put("assetName", safeAssetTb.getAssetName());
                    jsonObj.put("assetUnit", safeAssetTb.getAssetUnit());
                    jsonObj.put("assetDecimals", safeAssetTb.getAssetDecimals());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                utxo.reserve = jsonObj.toString();
                utxo.value = BigDecimalUtils.formatAssetAmount(txOutsTb.getOutValue(), safeAssetTb.getAssetDecimals());
            } else {
                utxo.reserve = "";
                utxo.value = BigDecimalUtils.formatRealAmount(txOutsTb.getOutValue());
            }
            balance.utxo = utxo;
            coinBalanceList.add(balance);
        }
        return coinBalanceList;
    }

}
