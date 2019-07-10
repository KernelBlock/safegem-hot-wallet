package com.bankledger.safegem.greendaodb

import android.content.Context
import com.bankledger.safegem.greendaodb.dao.EosRamTbDao
import com.bankledger.safegem.greendaodb.entity.EosRamTb

class EosRamUtil(mContext: Context) {

    private var mManager: DaoManager = DaoManager.getInstance()

    init {
        mManager.init(mContext)
    }

    fun insertEosRamTb(eosRamList: List<EosRamTb>): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.eosRamTbDao.insertOrReplaceInTx(eosRamList);
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }

    fun queryEosRamTbForTxId(txId: String): EosRamTb {
        return mManager.daoSession.queryBuilder(EosRamTb::class.java).where(EosRamTbDao.Properties.TxId.eq(txId)).unique();
    }

}