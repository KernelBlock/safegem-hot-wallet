package com.bankledger.safegem.greendaodb

import android.content.Context
import com.bankledger.safegem.greendaodb.dao.EosBalanceTbDao
import com.bankledger.safegem.greendaodb.entity.EosBalanceTb
import com.bankledger.safegem.utils.Constants

class EosBalanceUtil(mContext: Context) {

    private var mManager: DaoManager = DaoManager.getInstance()

    init {
        mManager.init(mContext)
    }

    fun insertEosBalanceTb(eosBalanceTb: EosBalanceTb): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.eosBalanceTbDao.insertOrReplaceInTx(eosBalanceTb)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }


    fun insertEosBalanceTb(eosBalanceTbs: List<EosBalanceTb>): Boolean {
        var flag: Boolean
        flag = try {
            mManager.daoSession.eosBalanceTbDao.insertOrReplaceInTx(eosBalanceTbs)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return flag
    }

    fun queryEosBalanceTb(account: String): List<EosBalanceTb> {
        return mManager.daoSession.queryBuilder(EosBalanceTb::class.java).where(EosBalanceTbDao.Properties.Account.eq(account)).list()
    }

    fun queryEosTokenBalanceTb(account: String): List<EosBalanceTb> {
        return mManager.daoSession.queryBuilder(EosBalanceTb::class.java).where(EosBalanceTbDao.Properties.Account.eq(account), EosBalanceTbDao.Properties.TokenName.notEq(Constants.EOS)).list()
    }

    fun getAmountWithAddress(account: String, tokenName: String): String {
        val sql = "select balance from " + EosBalanceTbDao.TABLENAME + " where account = ? and coin_type = ? and token_name = ?"
        val cursor = mManager.daoSession.database.rawQuery(sql, arrayOf(account, Integer.toString(EosBalanceTb.TYPE_EOS), tokenName))
        var amount_str = "0"
        if (cursor.moveToFirst()) {
            amount_str = cursor.getString(0)
        }
        cursor.close()
        return amount_str
    }

    fun getAmountWithUsdt(account: String): String {
        val sql = "select balance from " + EosBalanceTbDao.TABLENAME + " where account = ? and coin_type = ? and token_name = ?"
        val cursor = mManager.daoSession.database.rawQuery(sql, arrayOf(account, Integer.toString(EosBalanceTb.TYPE_USDT), Constants.USDT))
        var amount_str = "0"
        if (cursor.moveToFirst()) {
            amount_str = cursor.getString(0)
        }
        cursor.close()
        return amount_str
    }

    fun removeEOS(account: String) {
        val sql = "DELETE FROM " + EosBalanceTbDao.TABLENAME + " where account = ?"
        val condition = arrayOf(account)
        try {
            mManager.daoSession.database.execSQL(sql, condition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}