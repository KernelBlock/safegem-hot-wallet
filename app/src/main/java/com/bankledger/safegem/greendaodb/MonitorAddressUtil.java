package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.dao.MonitorAddressTbDao;
import com.bankledger.safegem.greendaodb.entity.MonitorAddressTb;
import com.bankledger.safegem.utils.Constants;
import com.bankledger.safegem.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/29
 * Author: bankledger
 */
public class MonitorAddressUtil {

    private DaoManager mManager;

    public MonitorAddressUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 添加erctoken
     */
    public boolean insertMonitorAddress(MonitorAddressTb monitorAddressTb) {
        boolean flag;
        try {
            mManager.getDaoSession().getMonitorAddressTbDao().insertOrReplace(monitorAddressTb);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 添加erctoken
     */
    public boolean deleteERCToken(MonitorAddressTb monitorAddressTb) {
        boolean flag;
        try {
            String sql = "delete from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and cold_unique_id = ? and address = ? and contract_address = ? and coin_type = ?";
            mManager.getDaoSession().getDatabase().execSQL(sql, new String[]{monitorAddressTb.getUserId() + "", monitorAddressTb.getColdUniqueId(), monitorAddressTb.getAddress(), monitorAddressTb.getContractAddress(), monitorAddressTb.getCoinType() + ""});
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 添加多个监控地址
     *
     * @return
     */
    public boolean insertMonitorAddress(List<MonitorAddressTb> monitorAddressTbList) {
        final boolean[] flag = {false};
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MonitorAddressTb monitorAddressTb : monitorAddressTbList) {
                        flag[0] = mManager.getDaoSession().insertOrReplace(monitorAddressTb) != -1;
                    }
                }
            });
            flag[0] = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag[0];
    }

    public List<String> queryWalletCoin(Integer coin_type) {
        String userId = SafeGemApplication.getInstance().getUserId();
        ArrayList<String> result = new ArrayList<>();
        String sql = "select distinct coin from monitor_address where user_id = ? and coin_type = ?";
        Cursor c = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{userId, String.valueOf(coin_type)});
        try {
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public List<String> queryWalletCoin(Integer coin_type, String coldUniqueId) {
        String userId = SafeGemApplication.getInstance().getUserId();
        ArrayList<String> result = new ArrayList<>();
        String sql = "select distinct coin from monitor_address where user_id = ? and coin_type = ? and cold_unique_id = ?";
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Cursor c = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{userId, String.valueOf(coin_type), coldUniqueId});
                try {
                    if (c.moveToFirst()) {
                        do {
                            result.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        });
        return result;
    }

    public List<String> queryWalletCoinNoEthToken() {
        String userId = SafeGemApplication.getInstance().getUserId();
        ArrayList<String> result = new ArrayList<>();
        String sql = "select distinct coin from monitor_address where temp_flag = ? and user_id = ? and coin_type != ? and cold_unique_id = ? order by coin ASC";
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Cursor c = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{"0", userId, String.valueOf(Constants.COIN_ETH_TOKEN), SafeGemApplication.getInstance().getColdUniqueId()});
                try {
                    if (c.moveToFirst()) {
                        do {
                            result.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        });
        return result;
    }


    public List<MonitorAddressTb> queryCurrentMonitorNoEthToken(String userId, String coldUniqueId) {
        ArrayList<MonitorAddressTb> result = new ArrayList<>();
        String sql = "select distinct coin,coin_type from monitor_address where user_id = ? and coin_type != ? and cold_unique_id = ? and temp_flag = ? order by coin ASC";
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Cursor c = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{userId, String.valueOf(Constants.COIN_ETH_TOKEN), coldUniqueId, "0"});
                try {
                    if (c.moveToFirst()) {
                        do {
                            MonitorAddressTb item = new MonitorAddressTb();
                            item.setCoin(c.getString(0));
                            item.setCoinType(c.getInt(1));
                            result.add(item);
                        } while (c.moveToNext());
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        });
        return result;
    }

    public List<MonitorAddressTb> queryCurrentMonitorNoEth2Etc(String userId, String coldUniqueId) {
        ArrayList<MonitorAddressTb> result = new ArrayList<>();
        String sql = "select distinct coin,coin_type from monitor_address where user_id = ? and cold_unique_id = ? and temp_flag = ? and coin_type in (?, ?, ?) order by coin ASC";
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                Cursor c = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{userId, coldUniqueId, "0", String.valueOf(Constants.COIN_BTC), String.valueOf(Constants.COIN_EOS), String.valueOf(Constants.COIN_USDT)});
                try {
                    if (c.moveToFirst()) {
                        do {
                            MonitorAddressTb item = new MonitorAddressTb();
                            item.setCoin(c.getString(0));
                            item.setCoinType(c.getInt(1));
                            result.add(item);
                        } while (c.moveToNext());
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        });
        return result;
    }

    public MonitorAddressTb queryFirstMonitorAddress(String userId, String coldUniqueId) {
        String sql = "where user_id = ? and cold_unique_id = ? and coin_type = ? and temp_flag = ? ORDER BY coin ASC limit 1";
        String[] condition = new String[]{userId, coldUniqueId, Integer.toString(Constants.COIN_BTC), "0"};
        List<MonitorAddressTb> list = mManager.getDaoSession().queryRaw(MonitorAddressTb.class, sql, condition);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<MonitorAddressTb> queryUserCoinEthTokenMonitorAddressTb() {
        String sql = "where user_id = ? and cold_unique_id = ? and temp_flag = ? and coin_type = ? and address = ? ORDER BY coin ASC";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), SafeGemApplication.getInstance().getColdUniqueId(), "0", Integer.toString(Constants.COIN_ETH_TOKEN), SafeGemApplication.getInstance().getCurrentETHAddress()};
        return mManager.getDaoSession().queryRaw(MonitorAddressTb.class, sql, condition);
    }

    public List<MonitorAddressTb> queryUserCoinEthTokenMonitorAddressTb(String contractAddress) {
        String sql = "where user_id = ? and cold_unique_id = ? and temp_flag = ? and coin_type = ? and address = ? and contract_address = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), SafeGemApplication.getInstance().getColdUniqueId(), "0", Integer.toString(Constants.COIN_ETH_TOKEN), SafeGemApplication.getInstance().getCurrentETHAddress(), contractAddress};
        return mManager.getDaoSession().queryRaw(MonitorAddressTb.class, sql, condition);
    }

    public List<MonitorAddressTb> queryUserCoinMonitorAddressTbs(String coin) {
        String sql;
        String[] condition;
        if (coin.equals(Constants.ETH)) {
            sql = "where user_id = ? and coin = ? and coin_type = ? and temp_flag = ? and cold_unique_id = ?";
            condition = new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), coin, Integer.toString(Constants.COIN_ETH), "0", SafeGemApplication.getInstance().getColdUniqueId()};
        } else {
            sql = "where user_id = ? and coin = ? and temp_flag = ? and cold_unique_id = ?";
            condition = new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), coin, "0", SafeGemApplication.getInstance().getColdUniqueId()};
        }
        return mManager.getDaoSession().queryRaw(MonitorAddressTb.class, sql, condition);
    }

    public List<MonitorAddressTb> queryUserCoinMonitorAddressTbs(String coin, int coinType) {
        LogUtils.i("------coin = " + coin + " coinType = " + coinType);
        String sql;
        String[] condition;
        if (coinType == Constants.COIN_ETH_TOKEN) {
            sql = "where user_id = ? and symbol = ? and coin_type = ? and temp_flag = ? and cold_unique_id = ?";
        } else {
            sql = "where user_id = ? and coin = ? and coin_type = ? and temp_flag = ? and cold_unique_id = ?";
        }
        condition = new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), coin, String.valueOf(coinType), "0", SafeGemApplication.getInstance().getColdUniqueId()};
        return mManager.getDaoSession().queryRaw(MonitorAddressTb.class, sql, condition);
    }

    public List<String> queryUserCoinMonitorAddress(String coin) {
        List<String> list = new ArrayList<>();
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), coin, "0"});
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
                cursor.close();
            }
        });
        return list;
    }

    public List<String> queryCurrentWalletMonitorAddress(String coin) {
        List<String> list = new ArrayList<>();
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and cold_unique_id = ? and coin = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), SafeGemApplication.getInstance().getColdUniqueId(), coin, "0"});
        mManager.getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
                cursor.close();
            }
        });
        return list;
    }

    public int queryUserCoinMonitorAddressCount() {
        int count = 0;
        String sql = "select count(*) from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and cold_unique_id = ? and temp_flag = ? and coin_type in (?, ?, ?, ?, ?)";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), SafeGemApplication.getInstance().getColdUniqueId(), "0", Integer.toString(Constants.COIN_BTC), Integer.toString(Constants.COIN_ETH), Integer.toString(Constants.COIN_ETC), Integer.toString(Constants.COIN_EOS), Integer.toString(Constants.COIN_USDT)};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 是否是监控地址
     *
     * @return
     */
    public boolean isMinerMonitorAddress(long userId, String address, int coinType) {
        int count = 0;
        String sql = "select count(*) from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and temp_flag = ? and address = ? and coin_type = ?";
        String[] condition = new String[]{Long.toString(userId), "0", address, Integer.toString(coinType)};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    /**
     * 是否是监控地址
     *
     * @return
     */
    public boolean isMinerMonitorAddress(String address, String contractAddress) {
        int count = 0;
        String sql = "select count(*) from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and temp_flag = ? and address = ? and contract_address = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), "0", address, contractAddress};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }


    /**
     * 查询当前用户钱包的ETH监控地址名称
     */
    public String queryUserETHMonitorAddress() {
        String address = "";
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin = ? and cold_unique_id = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), Constants.ETH, SafeGemApplication.getInstance().getColdUniqueId(), "0"});
        while (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    public String queryUserETHMonitorAddress(String coldUniqueId) {
        String address = "";
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin = ? and cold_unique_id = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), Constants.ETH, coldUniqueId, "0"});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    /**
     * 查询当前用户绑定钱包的Token地址名称
     */
    public List<String> queryUserEthTokenMonitorAddress() {
        List<String> list = new ArrayList<>();
        String sql = "select contract_address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin_type = ? and temp_flag = ? and cold_unique_id = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), String.valueOf(Constants.COIN_ETH_TOKEN), "0", SafeGemApplication.getInstance().getColdUniqueId()});
        if (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    /**
     * 查询当前用户钱包的EOS账户
     */
    public String queryUserEOSMonitorAddress() {
        String address = "";
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin_type = ? and cold_unique_id = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), String.valueOf(Constants.COIN_EOS), SafeGemApplication.getInstance().getColdUniqueId(), "0"});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    /**
     * 查询当前用户钱包的Usdt
     */
    public String queryUserUsdtMonitorAddress() {
        String address = "";
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where user_id = ? and coin_type = ? and cold_unique_id = ? and temp_flag = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId()), String.valueOf(Constants.COIN_USDT), SafeGemApplication.getInstance().getColdUniqueId(), "0"});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    public String queryUserETCMonitorAddress() {
        String address = "";
        String sql = "select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ? and coin_type = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{SafeGemApplication.getInstance().getColdUniqueId(), String.valueOf(Constants.COIN_ETC)});
        if (cursor.moveToNext()) {
            address = cursor.getString(0);
            cursor.close();
        }
        return address;
    }

    public String getDecimalsWithAddress(String contract_address) {
        String amount = "0";
        String sql = "select decimals from " + MonitorAddressTbDao.TABLENAME + " where address = ? and contract_address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{SafeGemApplication.getInstance().getCurrentETHAddress(), contract_address});
        if (cursor.moveToFirst()) {
            amount = cursor.getString(0);
        }
        cursor.close();
        return amount;
    }

    public String queryAddressWalletId(String address) {
        String coldUniqueId = "";
        String sql = "select cold_unique_id from " + MonitorAddressTbDao.TABLENAME + " where address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{address});
        if (cursor.moveToNext()) {
            coldUniqueId = cursor.getString(0);
        }
        cursor.close();
        return coldUniqueId;
    }

    public void deleteByColdWallet(String coldUniqueId) {
        String sql = "DELETE FROM " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ?";
        String[] condition = new String[]{coldUniqueId};
        try {
            mManager.getDaoSession().getDatabase().execSQL(sql, condition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
