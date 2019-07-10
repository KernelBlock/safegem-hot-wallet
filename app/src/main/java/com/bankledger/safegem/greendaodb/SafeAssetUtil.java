package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.Cursor;

import com.bankledger.safegem.app.SafeGemApplication;
import com.bankledger.safegem.greendaodb.dao.MonitorAddressTbDao;
import com.bankledger.safegem.greendaodb.dao.SafeAssetTbDao;
import com.bankledger.safegem.greendaodb.dao.TxOutsTbDao;
import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2019/1/3
 * Author: bankledger
 */
public class SafeAssetUtil {

    private DaoManager mManager;

    public SafeAssetUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public void insertSafeAsset(List<SafeAssetTb> safeAssetList) {
        try {
            mManager.getDaoSession().getSafeAssetTbDao().insertOrReplaceInTx(safeAssetList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SafeAssetTb querySafeAssetWithTxHash(String txHash) {
        String sql = "select * from safe_asset_tb where asset_id = (select distinct asset_id from " + TxOutsTbDao.TABLENAME + " where tx_hash = ? and asset_id <> ?) limit 1";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{txHash, ""});
        cursor.moveToFirst();
        return mManager.getDaoSession().getSafeAssetTbDao().readEntity(cursor, 0);

    }

    public SafeAssetTb querySafeAssetTb(String assetId) {
        return mManager.getDaoSession().queryBuilder(SafeAssetTb.class).where(SafeAssetTbDao.Properties.AssetId.eq(assetId)).unique();
    }

    public List<SafeAssetTb> getSafeAssetList(String coin) {
        List<SafeAssetTb> outList = new ArrayList<>();
        String sql = "select * from safe_asset_tb where asset_id in (select distinct asset_id from " + TxOutsTbDao.TABLENAME + " where coin = ? and asset_id != ? and out_address in(select address from " + MonitorAddressTbDao.TABLENAME + " where cold_unique_id = ? and coin = ?))";
        Cursor cursor = mManager.getDaoSession().getDatabase().rawQuery(sql, new String[]{coin, coin, SafeGemApplication.getInstance().getColdUniqueId(), coin});
        while (cursor.moveToNext()) {
            outList.add(mManager.getDaoSession().getSafeAssetTbDao().readEntity(cursor, 0));
        }
        cursor.close();
        return outList;
    }
}
