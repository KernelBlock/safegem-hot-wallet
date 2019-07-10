package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.dao.MonitorAddressTbDao;
import com.bankledger.safegem.greendaodb.dao.SafeAssetTbDao;
import com.bankledger.safegem.greendaodb.dao.TxInsTbDao;
import com.bankledger.safegem.greendaodb.dao.TxOutsTbDao;
import com.bankledger.safegem.greendaodb.dao.TxTbDao;
import com.bankledger.safegem.greendaodb.entity.TxInsTb;
import com.bankledger.safegem.greendaodb.entity.TxOutsTb;
import com.bankledger.safegem.greendaodb.entity.TxTb;
import com.bankledger.safegem.message.FormatIn;
import com.bankledger.safegem.message.FormatOut;
import com.bankledger.safegem.message.FormatTx;
import com.bankledger.safegem.net.model.response.BtcSendTransactionResponse;
import com.bankledger.safegem.utils.BigDecimalUtils;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.LogUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/24
 * Author: bankledger
 */
public class TxUtil {

    private DaoManager mManager;

    public TxUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public List<TxTb> insertTx(List<FormatTx> formatTxList, String coin) {
        List<TxTb> txTbList = new ArrayList<>();
        List<TxInsTb> txInsTbList = new ArrayList<>();
        List<TxOutsTb> txOutsTbList = new ArrayList<>();
        for (FormatTx formatTx : formatTxList) {
            TxTb txTb = new TxTb();
            txTb.setTxHash(formatTx.txid);
            txTb.setHeight(formatTx.height);
            txTb.setBlockHash(formatTx.blockhash);
            txTb.setBlockTime(formatTx.blocktime);
            txTb.setLockTime(formatTx.locktime);
            txTb.setTime(formatTx.time);
            txTb.setVersion(formatTx.version);
            txTb.setCoin(coin);
            txTb.setConfirm(Constants.CONFIRMED);
            txTb.setBtcAsssetType(Constants.BTC_DEFAULT);
            //插入交易表
            txTbList.add(txTb);

            /**
             * 插入交易输入表
             */
            for (FormatIn formatIn : formatTx.vin) {

                TxInsTb txInsTb = new TxInsTb();
                txInsTb.setTxHash(formatTx.txid);
                txInsTb.setPrevTxHash(formatIn.txid);
                txInsTb.setPrevOutSn(formatIn.vout);
                txInsTb.setValueSat(formatIn.value == null ? "0" : formatIn.value);
                txInsTb.setSequence(formatIn.sequence);
                //插入交易表
                txInsTbList.add(txInsTb);
            }
            /**
             * 插入交易输出表
             */
            for (FormatOut formatOut : formatTx.vout) {
                TxOutsTb txOutsTb = new TxOutsTb();
                txOutsTb.setTxHash(formatTx.txid);
                txOutsTb.setOutSn(formatOut.n);
                txOutsTb.setCoin(coin);
                txOutsTb.setOutScript(formatOut.scriptPubKey.hex);
                txOutsTb.setOutValue(formatOut.value == null ? "0" : formatOut.value);
                if (formatOut.scriptPubKey.addresses == null || formatOut.scriptPubKey.addresses.size() == 0) { //USDT交易过滤
                    if (formatOut.scriptPubKey.hex.startsWith(Constants.BTC_ASSET_BEGIN)) {
                        String assetFlag;
                        if (Constants.isDebug) {
                            assetFlag = Constants.BTC_ASSET_USDT_FLAG_TEST;
                        } else {
                            assetFlag = Constants.BTC_ASSET_USDT_FLAG_RELEASE;
                        }
                        if (formatOut.scriptPubKey.hex.contains(assetFlag)) {
                            txTb.setBtcAsssetType(Constants.BTC_ASSET_USDT);
                            continue;
                        }
                    }
                }
                txOutsTb.setMulType(formatOut.scriptPubKey.addresses.size() == 1 ? 0 : 1);
                StringBuilder stringBuilder = new StringBuilder();
                for (String address : formatOut.scriptPubKey.addresses) {
                    stringBuilder.append(address);
                    stringBuilder.append(",");
                }
                if (stringBuilder.lastIndexOf(",") != -1) {
                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
                }
                txOutsTb.setOutAddress(stringBuilder.toString());
                txOutsTb.setUnLockHeight(formatOut.unlockedHeight);
                txOutsTb.setReserve(formatOut.reserve);
                txOutsTb.setUnLockHeight(0);
                if (coin.equals(Constants.SAFE)) {
                    txOutsTb.setUnLockHeight(formatOut.unlockedHeight);
                    txOutsTb.setReserve(formatOut.reserve);
                    if (!TextUtils.isEmpty(formatOut.assetId)) {
                        txOutsTb.setAssetId(formatOut.assetId);
                    } else {
                        txOutsTb.setAssetId("");
                    }
                } else {
                    txOutsTb.setAssetId("");
                }
                txOutsTb.setOutStatus(0);
                //插入交易表
                txOutsTbList.add(txOutsTb);
            }
        }
        try {
            mManager.getDaoSession().getTxTbDao().insertOrReplaceInTx(txTbList);
            mManager.getDaoSession().getTxInsTbDao().insertOrReplaceInTx(txInsTbList);
            mManager.getDaoSession().getTxOutsTbDao().insertOrReplaceInTx(txOutsTbList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txTbList;
    }


    public void insertSendTx(BtcSendTransactionResponse btcSendTransactionResponse, String
            coin) {
        BtcSendTransactionResponse.TransactionBean formatTx = btcSendTransactionResponse.transaction;
        TxTb txTb = new TxTb();
        txTb.setTxHash(btcSendTransactionResponse.txId);
        txTb.setHeight(new Long((long) 0));
        txTb.setBlockHash("");
        txTb.setBlockTime(System.currentTimeMillis() / 1000);
        txTb.setLockTime(new Long((long) formatTx.locktime));
        txTb.setTime(System.currentTimeMillis() / 1000);
        txTb.setVersion(new Long((long) formatTx.version));
        txTb.setCoin(coin);
        txTb.setConfirm(Constants.UN_CONFIRM);
        txTb.setBtcAsssetType(Constants.BTC_DEFAULT);

        List<TxInsTb> txInsTbList = new ArrayList<>();
        List<TxOutsTb> txOutsTbList = new ArrayList<>();
        for (FormatIn formatIn : formatTx.vin) {
            TxInsTb txInsTb = new TxInsTb();
            txInsTb.setTxHash(btcSendTransactionResponse.txId);
            txInsTb.setPrevTxHash(formatIn.txid);
            txInsTb.setPrevOutSn(formatIn.vout);
            txInsTb.setValueSat(formatIn.value == null ? "0" : formatIn.value);
            txInsTb.setSequence(formatIn.sequence);
            txInsTbList.add(txInsTb);
        }

        /**
         * 插入交易输出表
         */
        for (FormatOut formatOut : formatTx.vout) {
            TxOutsTb txOutsTb = new TxOutsTb();
            txOutsTb.setTxHash(btcSendTransactionResponse.txId);
            txOutsTb.setOutSn(formatOut.n);
            txOutsTb.setCoin(coin);
            txOutsTb.setOutScript(formatOut.scriptPubKey.hex);
            txOutsTb.setOutValue(formatOut.value == null ? "0" : formatOut.value);
            if (formatOut.scriptPubKey.addresses == null || formatOut.scriptPubKey.addresses.size() == 0) { //USDT交易过滤
                if (formatOut.scriptPubKey.hex.startsWith(Constants.BTC_ASSET_BEGIN)) {
                    String assetFlag;
                    if (Constants.isDebug) {
                        assetFlag = Constants.BTC_ASSET_USDT_FLAG_TEST;
                    } else {
                        assetFlag = Constants.BTC_ASSET_USDT_FLAG_RELEASE;
                    }
                    if (formatOut.scriptPubKey.hex.contains(assetFlag)) {
                        txTb.setBtcAsssetType(Constants.BTC_ASSET_USDT);
                        continue;
                    }
                }
            }
            txOutsTb.setMulType(formatOut.scriptPubKey.addresses.size() == 1 ? 0 : 1);
            StringBuilder stringBuilder = new StringBuilder();
            for (String address : formatOut.scriptPubKey.addresses) {
                stringBuilder.append(address);
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
            txOutsTb.setOutAddress(stringBuilder.toString());
            txOutsTb.setUnLockHeight(formatOut.unlockedHeight);
            txOutsTb.setReserve(formatOut.reserve);
            txOutsTb.setUnLockHeight(0);
            if (coin.equals(Constants.SAFE)) {
                txOutsTb.setUnLockHeight(formatOut.unlockedHeight);
                txOutsTb.setReserve(formatOut.reserve);
                if (!TextUtils.isEmpty(formatOut.assetId)) {
                    txOutsTb.setAssetId(formatOut.assetId);
                } else {
                    txOutsTb.setAssetId("");
                }
            } else {
                txOutsTb.setAssetId("");
            }
            txOutsTb.setOutStatus(0);
            txOutsTbList.add(txOutsTb);
        }
        try {
            mManager.getDaoSession().getTxTbDao().insertOrReplaceInTx(txTb);
            mManager.getDaoSession().getTxInsTbDao().insertOrReplaceInTx(txInsTbList);
            mManager.getDaoSession().getTxOutsTbDao().insertOrReplaceInTx(txOutsTbList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TxTb queryTxWithTxHash(String txHash) {
        List<TxTb> txList = mManager.getDaoSession().getTxTbDao().queryRaw("where tx_hash = ?", txHash);
        if (txList != null && txList.size() > 0) {
            return txList.get(0);
        } else {
            return null;
        }
    }

    public void updateTxOuts() {
        String sql = "update " + TxOutsTbDao.TABLENAME + " set out_status = 1 where exists ( select 1 from " + TxInsTbDao.TABLENAME + " where prev_out_sn = " + TxOutsTbDao.TABLENAME + ".out_sn and prev_tx_hash = " + TxOutsTbDao.TABLENAME + ".tx_hash ) and " + TxOutsTbDao.TABLENAME + ".out_status = 0";
        mManager.getDaoSession().getDatabase().execSQL(sql);
    }

    public List<TxTb> queryMonitorTx(String coin, String assetId, int offset, int limit) {
        List<TxTb> txList = new ArrayList<>();
        Cursor cursor;
        if (coin.equals(Constants.SAFE) && TextUtils.isEmpty(assetId)) {
            String sql = "select x.* from " + TxTbDao.TABLENAME + " as x " +
                    "where x.coin = ? and x.btc_asset_type = ? and x.tx_hash in " +
                    "(select a.tx_hash from " + TxOutsTbDao.TABLENAME + " as a,  " + MonitorAddressTbDao.TABLENAME + " as b " +
                    "where a.coin = ? and b.coin = ? and a.out_address = b.address and b.temp_flag = ? and b.cold_unique_id = ? and a.asset_id = ? " +
                    "and 0 == (select count(*) from " + TxOutsTbDao.TABLENAME + " where tx_hash = a.tx_hash and asset_id <> ?) " +
                    "union select a.tx_hash from " + TxInsTbDao.TABLENAME + " as a, " + TxOutsTbDao.TABLENAME + " as b, " + MonitorAddressTbDao.TABLENAME + " as c " +
                    "where a.prev_tx_hash = b.tx_hash and a.prev_out_sn = b.out_sn and b.out_address = c.address and c.cold_unique_id = ? and b.asset_id = ? " +
                    "and 0 == (select count(*) from " + TxOutsTbDao.TABLENAME + " where tx_hash = a.tx_hash and asset_id <> ?) " +
                    ") order by x.time desc limit ?, ?";
            cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{coin, Integer.toString(Constants.BTC_DEFAULT), coin, coin, "0", getColdUniqueId(), assetId, "", getColdUniqueId(), assetId, "", Integer.toString(offset), Integer.toString(limit)});
            while (cursor.moveToNext()) {
                txList.add(mManager.getDaoSession().getTxTbDao().readEntity(cursor, 0));
            }
        } else {
            String sql = "select x.* from " + TxTbDao.TABLENAME + " as x " +
                    "where x.coin = ? and x.btc_asset_type = ? and x.tx_hash in " +
                    "(select a.tx_hash from " + TxOutsTbDao.TABLENAME + " as a,  " + MonitorAddressTbDao.TABLENAME + " as b " +
                    "where a.coin = ? and b.coin = ? and a.out_address = b.address and b.temp_flag = ? and b.cold_unique_id = ? and a.asset_id = ?" +
                    "union select a.tx_hash from " + TxInsTbDao.TABLENAME + " as a, " + TxOutsTbDao.TABLENAME + " as b, " + MonitorAddressTbDao.TABLENAME + " as c " +
                    "where a.prev_tx_hash = b.tx_hash and a.prev_out_sn = b.out_sn and b.out_address = c.address and c.cold_unique_id = ? and b.asset_id = ?) order by x.time desc limit ?, ?";
            cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{coin, Integer.toString(Constants.BTC_DEFAULT), coin, coin, "0", getColdUniqueId(), assetId, getColdUniqueId(), assetId, Integer.toString(offset), Integer.toString(limit)});
            while (cursor.moveToNext()) {
                txList.add(mManager.getDaoSession().getTxTbDao().readEntity(cursor, 0));
            }
        }
        cursor.close();
        return txList;
    }

    public boolean txIsSend(String txHash, String coldUniqueId) {
        String sql = "select c.* from (select b.* from " + TxInsTbDao.TABLENAME + " as a, " + TxOutsTbDao.TABLENAME + " as b " +
                "where a.tx_hash = ? and a.prev_tx_hash = b.tx_hash and a.prev_out_sn = b.out_sn) as c, " + MonitorAddressTbDao.TABLENAME + " as d " +
                "where d.temp_flag = 0 and d.cold_unique_id = ? and c.out_address = d.address";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{txHash, coldUniqueId});
        boolean isSend = cursor.moveToNext();
        cursor.close();
        return isSend;
    }

    public String getTxAmount(String txHash, String coldUniqueId, boolean isSend) {
        Cursor cursor;
        String valueSql;
        String amount;
        if (isSend) {
            valueSql = "select out_value from " + TxOutsTbDao.TABLENAME + " where tx_hash = ? and out_address not in(select address from " + MonitorAddressTbDao.TABLENAME + " where temp_flag = ? and cold_unique_id = ?)";
            cursor = mManager.getDaoSession().getDatabase().rawQuery(valueSql, new String[]{txHash, "0", coldUniqueId});
            String totalAmount = "0";
            while (cursor.moveToNext()) {
                totalAmount = BigDecimalUtils.add(totalAmount, cursor.getString(0));
            }
            amount = "-" + totalAmount;
        } else {
            valueSql = "select out_value from " + TxOutsTbDao.TABLENAME + " where tx_hash = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where temp_flag = ? and cold_unique_id = ?)";
            cursor = mManager.getDaoSession().getDatabase().rawQuery(valueSql, new String[]{txHash, "0", coldUniqueId});
            String totalAmount = "0";
            while (cursor.moveToNext()) {
                totalAmount = BigDecimalUtils.add(totalAmount, cursor.getString(0));
            }
            amount = totalAmount;
        }
        cursor.close();
        return amount;
    }

    public String getTxAddress(String txHash, String coldUniqueId, boolean isSend) {
        String valueSql;
        if (isSend) {
            valueSql = "select out_address from " + TxOutsTbDao.TABLENAME + " where tx_hash = ? and out_address not in(select address from " + MonitorAddressTbDao.TABLENAME + " where temp_flag = ? and cold_unique_id = ?)";
        } else {
            valueSql = "select out_address from " + TxOutsTbDao.TABLENAME + " where tx_hash = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where temp_flag = ? and cold_unique_id = ?)";
        }
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(valueSql, new String[]{txHash, "0", coldUniqueId});
        String address = "";
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    public String getMineFee(String txHash) {
        String sql = "select b.out_value as out_value from " + TxInsTbDao.TABLENAME + " as a," + TxOutsTbDao.TABLENAME + " as b where a.tx_hash = ? and a.prev_tx_hash = b.tx_hash and a.prev_out_sn = b.out_sn";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{txHash});
        String totalIns = "0", totalOuts = "0";
        while (cursor.moveToNext()) {
            totalIns = BigDecimalUtils.add(totalIns, cursor.getString(0));
        }
        cursor.close();
        sql = "select out_value from " + TxOutsTbDao.TABLENAME + " where tx_hash = ?";
        cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{txHash});
        while (cursor.moveToNext()) {
            totalOuts = BigDecimalUtils.add(totalOuts, cursor.getString(0));
        }
        cursor.close();
        return BigDecimalUtils.sub(totalIns, totalOuts);
    }

    public String getAmountWithCoin(String coin, String assetId) {
        String sql = "select out_value from " + TxOutsTbDao.TABLENAME + " where out_status = ? and asset_id = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ? and temp_flag = ? and coin = ?)";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", assetId, getColdUniqueId(), "0", coin});
        String totalAmount = "0";
        while (cursor.moveToNext()) {
            totalAmount = BigDecimalUtils.add(totalAmount, cursor.getString(0));
        }
        cursor.close();
        return totalAmount;
    }


    public String getAmountWithAddress(String address, String coin, String assetId) {
        String sql = "select out_value from " + TxOutsTbDao.TABLENAME + " where out_status = ? and coin = ? and asset_id = ? and out_address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", coin, assetId, address});
        String totalAmount = "0";
        while (cursor.moveToNext()) {
            totalAmount = BigDecimalUtils.add(totalAmount, cursor.getString(0));
        }
        cursor.close();
        return totalAmount;
    }

    public List<TxOutsTb> getUnspendOuts(String coin, String assetId) {
        List<TxOutsTb> outList = new ArrayList<>();
        String sql = "select * from " + TxOutsTbDao.TABLENAME + " where out_status = ? and coin = ? and asset_id = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ? and coin = ?)";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", coin, assetId, getColdUniqueId(), coin});
        while (cursor.moveToNext()) {
            outList.add(mManager.getDaoSession().getTxOutsTbDao().readEntity(cursor, 0));
        }
        cursor.close();
        return outList;
    }

    public List<TxOutsTb> getUnspendOuts() {
        List<TxOutsTb> outList = new ArrayList<>();
        String sql = "select * from " + TxOutsTbDao.TABLENAME + " where out_status = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ?)";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", getColdUniqueId()});
        while (cursor.moveToNext()) {
            outList.add(mManager.getDaoSession().getTxOutsTbDao().readEntity(cursor, 0));
        }
        cursor.close();
        return outList;
    }

    public List<TxOutsTb> queryAddressOuts(String address, String coin) {
        List<TxOutsTb> outList = new ArrayList<>();
        String sql = "select * from " + TxOutsTbDao.TABLENAME + " where out_status = ? and coin = ? and out_address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", coin, address});
        while (cursor.moveToNext()) {
            outList.add(mManager.getDaoSession().getTxOutsTbDao().readEntity(cursor, 0));
        }
        cursor.close();
        return outList;
    }

    public List<TxOutsTb> queryCoinOuts(String coin) {
        List<TxOutsTb> outList = new ArrayList<>();
        String sql = "select * from " + TxOutsTbDao.TABLENAME + " where out_status = ? and coin = ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ? and coin = ?)";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", coin, getColdUniqueId(), coin});
        while (cursor.moveToNext()) {
            outList.add(mManager.getDaoSession().getTxOutsTbDao().readEntity(cursor, 0));
        }
        cursor.close();
        return outList;
    }

    public TxTb getTxTbByTxHash(String txHash) {
        String sql = "select * from " + TxTbDao.TABLENAME + " where tx_hash = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{txHash});
        while (cursor.moveToNext()) {
            return mManager.getDaoSession().getTxTbDao().readEntity(cursor, 0);
        }
        cursor.close();
        return null;
    }

    public List<String> getNoSyncSafeAsset(String coin) {
        List<String> assetList = new ArrayList<>();
        String sql = "select distinct asset_id from " + TxOutsTbDao.TABLENAME + " as a where a.coin = ? and a.asset_id <> ? and not exists (select * from " + SafeAssetTbDao.TABLENAME + " as b where a.asset_id = b.asset_id)";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{coin, ""});
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                while (cursor.moveToNext()) {
                    assetList.add(cursor.getString(0));
                }
                cursor.close();
            }
        });
        cursor.close();
        return assetList;
    }

    /**
     * 获取目前冷端
     * @return
     */
    private String getColdUniqueId() {
        return SafeGemApplication.getInstance().getColdUniqueId();
    }

    /**
     * 查找最后一个交易
     */
    public String getLastTransationTxHash(String address) {
        String sql = "select x.tx_hash from " + TxTbDao.TABLENAME + " as x where confirm = ? and x.tx_hash in " +
                "(select a.tx_hash from " + TxOutsTbDao.TABLENAME + " as a where a.out_address = ? union " +
                "select a.tx_hash from " + TxInsTbDao.TABLENAME + " AS a, " + TxOutsTbDao.TABLENAME + " as b " +
                "where a.prev_tx_hash = b.tx_hash and a.prev_out_sn = b.out_sn and b.out_address = ?) order by x.time desc, x._id desc limit 1";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{Integer.toString(Constants.CONFIRMED), address, address});
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }
}
