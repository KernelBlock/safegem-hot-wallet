package com.bankledger.safegem.greendaodb

import android.content.Context
import com.bankledger.safegem.greendaodb.dao.UsdtTxTbDao
import com.bankledger.safegem.greendaodb.entity.UsdtTxTb

class UsdtTxUtil(mContext: Context) {

    private var mManager: DaoManager = DaoManager.getInstance()

    init {
        mManager.init(mContext)
    }

    fun insertUsdtTxTb(UsdtTxList: List<UsdtTxTb>): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.usdtTxTbDao.insertOrReplaceInTx(UsdtTxList);
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }

    fun queryUsdtTxTb(coldUniqueId: String, offset: Int, limit: Int): List<UsdtTxTb> {
        return mManager.daoSession.queryBuilder(UsdtTxTb::class.java).where(UsdtTxTbDao.Properties.Cold_unique_id.eq(coldUniqueId)).orderDesc(UsdtTxTbDao.Properties.Blocktime).orderDesc(UsdtTxTbDao.Properties.Blocktime).offset(offset).limit(limit).list()
    }

    fun queryUsdtTxCount(coldUniqueId: String): Long {
        return mManager.daoSession.queryBuilder(UsdtTxTb::class.java).where(UsdtTxTbDao.Properties.Cold_unique_id.eq(coldUniqueId)).count();
    }

    fun queryTxWithTxHash(txHash: String): UsdtTxTb {
        return mManager.daoSession.queryBuilder(UsdtTxTb::class.java).where(UsdtTxTbDao.Properties.Txid.eq(txHash)).unique();
    }

    fun getLastTransationTx(coldUniqueId: String): String {
        val item = mManager.daoSession.queryBuilder(UsdtTxTb::class.java).where(UsdtTxTbDao.Properties.Cold_unique_id.eq(coldUniqueId)).orderDesc(UsdtTxTbDao.Properties.Blocktime).limit(1).unique();
        if (item != null) {
            return item.txid
        } else {
            return "";
        }
    }
}