package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;

import com.bankledger.safegem.greendaodb.dao.EthTokenTbDao;
import com.bankledger.safegem.greendaodb.entity.EthTokenTb;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class EthTokenUtil {

    private DaoManager mManager;

    public EthTokenUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public boolean addOrUpdateEth(EthTokenTb ethTb) {
        boolean flag = false;
        try {
            long count = mManager.getDaoSession().getEthTokenTbDao().queryBuilder().where(EthTokenTbDao.Properties.Address.eq(ethTb.getAddress()), EthTokenTbDao.Properties.ContractAddress.eq(ethTb.getContractAddress())).count();
            if (count > 0) {
                mManager.getDaoSession().getEthTokenTbDao().getDatabase().execSQL("update eth_token set balance = ?, transaction_count = ? where address = ? and contract_address = ?",
                        new String[]{ethTb.getBalance(), ethTb.getTransactionCount(), ethTb.getAddress(), ethTb.getContractAddress()});
            } else {
                mManager.getDaoSession().getEthTokenTbDao().insertOrReplace(ethTb);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 查询eth余额
     *
     * @param address
     * @return
     */
    public String getAmountWithAddress(String address, String contract_address) {
        String sql = "select balance from " + EthTokenTbDao.TABLENAME + " where address = ? and contract_address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{address, contract_address});
        String amount_str = "0";
        if (cursor.moveToFirst()) {
            amount_str = cursor.getString(0);
        }
        cursor.close();
        return amount_str;
    }



    public boolean isEthZeroAmout(String address) {
        String sql = "select balance from " + EthTokenTbDao.TABLENAME + " where address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{address});
        String amount_str = "0";
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null) {
                amount_str = cursor.getString(0);
                if (!amount_str.equals("0")) {
                    return false;
                }
            }
        }
        cursor.close();
        return true;
    }

    public EthTokenTb queryEthAndToken(String address, String contract_address) {
        String sql = "select * from " + EthTokenTbDao.TABLENAME + " where address = ? and contract_address = ?";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{address, contract_address});
        EthTokenTb ethTokenTb=null;
        while (cursor.moveToNext()) {
            ethTokenTb= mManager.getDaoSession().getEthTokenTbDao().readEntity(cursor, 0);
        }
        cursor.close();
        return ethTokenTb;
    }

    public List<EthTokenTb> queryEthToken(String address, List<String> contract_address) {
        List<EthTokenTb> list = new ArrayList<>();
        String sql = "where address = ? and contract_address = ?";
        for (String str : contract_address) {
            String[] condition = new String[]{address, str};
            list.addAll(mManager.getDaoSession().queryRaw(EthTokenTb.class, sql, condition));
        }
        return list;
    }

    /**
     * 删除erctoken
     */
    public boolean deleteERCToken(String address, String constraceAddress) {
        boolean flag;
        try {
            String sql = "delete from " + EthTokenTbDao.TABLENAME + " where address = ? and contract_address = ?";
            mManager.getDaoSession().getDatabase().execSQL(sql, new String[]{address, constraceAddress});
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 删除erctoken
     */
    public boolean deleteERCToken(String address) {
        boolean flag;
        try {
            String sql = "delete from " + EthTokenTbDao.TABLENAME + " where address = ?";
            mManager.getDaoSession().getDatabase().execSQL(sql, new String[]{address});
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

}
