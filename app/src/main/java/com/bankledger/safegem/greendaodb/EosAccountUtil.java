package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;

import com.bankledger.safegem.greendaodb.dao.EosAccountTbDao;
import com.bankledger.safegem.greendaodb.dao.EthTokenTbDao;
import com.bankledger.safegem.greendaodb.entity.ColdWalletTb;
import com.bankledger.safegem.greendaodb.entity.EosAccountTb;
import com.bankledger.safegem.greendaodb.entity.EthTokenTb;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/21
 * Author: bankledger
 */
public class EosAccountUtil {

    private DaoManager mManager;

    public EosAccountUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 添加冷钱包
     *
     * @return
     */
    public boolean insertEosAccountTb(EosAccountTb eosAccountTb) {
        boolean flag;
        try {
            flag = mManager.getDaoSession().getEosAccountTbDao().insert(eosAccountTb) != -1;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    public EosAccountTb queryCurrentEosAccountTb(String coldUniqueId, String account) {
        List<EosAccountTb> eosAccountList = mManager.getDaoSession().queryBuilder(EosAccountTb.class).where(EosAccountTbDao.Properties.ColdUniqueId.eq(coldUniqueId), EosAccountTbDao.Properties.Account.eq(account)).list();
        if (eosAccountList != null && eosAccountList.size() > 0) {
            return eosAccountList.get(0);
        } else {
            return null;
        }
    }

}
