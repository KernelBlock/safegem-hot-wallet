package com.bankledger.safegem.greendaodb

import android.content.Context
import com.bankledger.safegem.greendaodb.dao.EosTxTbDao
import com.bankledger.safegem.greendaodb.entity.EosTxTb
import java.util.ArrayList

class EosTxUtil(mContext: Context) {

    private var mManager: DaoManager = DaoManager.getInstance()

    init {
        mManager.init(mContext)
    }

    fun insertEosTxTb(eosTxList: List<EosTxTb>): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.eosTxTbDao.insertOrReplaceInTx(eosTxList);
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }

    fun queryEosTxTbForTxId(txId: String): EosTxTb {
        return mManager.daoSession.queryBuilder(EosTxTb::class.java).where(EosTxTbDao.Properties.TxId.eq(txId)).unique();
    }

    fun queryEosTxTb(account: String, offset: Int, limit: Int): List<EosTxTb> {
        return mManager.daoSession.queryBuilder(EosTxTb::class.java).where(EosTxTbDao.Properties.Account.eq(account)).orderDesc(EosTxTbDao.Properties.Time).offset(offset).limit(limit).list()
    }

    fun queryEosTxTb(account: String, tokenName: String, offset: Int, limit: Int): List<EosTxTb> {
        return mManager.daoSession.queryBuilder(EosTxTb::class.java).where(EosTxTbDao.Properties.Account.eq(account), EosTxTbDao.Properties.Coin.eq(tokenName)).orderDesc(EosTxTbDao.Properties.Time).offset(offset).limit(limit).list()
    }

    fun queryEosTxCount(account: String): Long {
        return mManager.daoSession.queryBuilder(EosTxTb::class.java).where(EosTxTbDao.Properties.Account.eq(account)).count();
    }

    fun updateEosTx() {
        val sql = "select a.tx_id,(case when b.tx_id !='' then 1 when c.tx_id !='' then 2 else 0 end) as type from eos_tx as a left join eos_ram as b on a.tx_id = b.tx_id left join eos_delegate as c on a.tx_id = c.tx_id"
        val cursor = mManager.daoSession.database.rawQuery(sql, arrayOf())
        val list = ArrayList<HashMap<String, String>>();
        while (cursor.moveToNext()) {
            val txId = cursor.getString(0)
            val type = cursor.getString(1)
            val map = HashMap<String, String>();
            map.put("txId", txId);
            map.put("type", type);
            list.add(map)
        }
        cursor.close()
        for (i in list.indices) {
            val map = list.get(i);
            val sql = "UPDATE " + EosTxTbDao.TABLENAME + " SET type = ? where tx_id = ?"
            mManager.daoSession.database.execSQL(sql, arrayOf(map.get("type"), map.get("txId")))
        }
    }
}