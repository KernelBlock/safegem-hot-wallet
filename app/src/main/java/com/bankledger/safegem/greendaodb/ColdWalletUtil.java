package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.dao.ColdWalletTbDao;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/24
 * Author: bankledger
 */
public class ColdWalletUtil {
    private DaoManager mManager;

    public ColdWalletUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 添加冷钱包
     *
     * @return
     */
    public boolean insertColdWallet(ColdWalletTb coldWallet) {
        boolean flag;
        try {
            flag = mManager.getDaoSession().getColdWalletTbDao().insert(coldWallet) != -1;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 添加多个冷钱包
     *
     * @return
     */
    public boolean insertColdWallets(List<ColdWalletTb> coldWalletList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ColdWalletTb coldWallet : coldWalletList) {
                        mManager.getDaoSession().insertOrReplace(coldWallet);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<ColdWalletTb> queryAllWallet() {
        String sql = "where user_id = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId()};
        return mManager.getDaoSession().queryRaw(ColdWalletTb.class, sql, condition);
    }

    public int queryWalletCount(String cold_unique_id) {
        int count = 0;
        String sql = "select count(*) from " + ColdWalletTbDao.TABLENAME + " where user_id = ? and cold_unique_id = ?";
        String[] condition = new String[]{SafeGemApplication.getInstance().getUserId(), cold_unique_id};
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, condition);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void updateColdWalletName(String cold_wallet_name, String cold_unique_id) {
        String sql = "update " + ColdWalletTbDao.TABLENAME + " set cold_wallet_name = ? where user_id = ? and cold_unique_id = ?";
        mManager.getDaoSession().getDatabase().execSQL(sql, new String[]{cold_wallet_name, SafeGemApplication.getInstance().getUserId(), cold_unique_id});
    }

    /**
     * 查询当前用户钱包的ETH监控地址名称
     */
    public List<String> queryUserWalletId() {
        List<String> list = new ArrayList<>();
        String sql = "select cold_unique_id from " + ColdWalletTbDao.TABLENAME + " where user_id = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{String.valueOf(SafeGemApplication.getInstance().getUserId())});
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    public boolean removeColdWallet(String cold_unique_id) {
        boolean flag;
        String sql = "DELETE FROM " + ColdWalletTbDao.TABLENAME + " where cold_unique_id = ?";
        String[] condition = new String[]{cold_unique_id};
        try {
            mManager.getDaoSession().getDatabase().execSQL(sql, condition);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

}
