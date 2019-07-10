package com.bankledger.safegem.greendaodb

import android.content.Context
import com.bankledger.safegem.greendaodb.dao.EosDelegateTbDao
import com.bankledger.safegem.greendaodb.entity.EosDelegateTb

class EosDelegateUtil(mContext: Context) {

    private var mManager: DaoManager = DaoManager.getInstance()

    init {
        mManager.init(mContext)
    }

    fun insertEosDelegateTb(eosDelegateList: List<EosDelegateTb>): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.eosDelegateTbDao.insertOrReplaceInTx(eosDelegateList);
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }

    fun queryEosDelegateTbForTxId(txId: String): EosDelegateTb {
        return mManager.daoSession.queryBuilder(EosDelegateTb::class.java).where(EosDelegateTbDao.Properties.TxId.eq(txId)).unique();
    }

}