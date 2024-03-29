package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.SafeAssetTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "safe_asset_tb".
*/
public class SafeAssetTbDao extends AbstractDao<SafeAssetTb, Void> {

    public static final String TABLENAME = "safe_asset_tb";

    /**
     * Properties of entity SafeAssetTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AssetId = new Property(0, String.class, "assetId", false, "asset_id");
        public final static Property AssetShortName = new Property(1, String.class, "assetShortName", false, "asset_short_name");
        public final static Property AssetName = new Property(2, String.class, "assetName", false, "asset_name");
        public final static Property AssetDesc = new Property(3, String.class, "assetDesc", false, "asset_desc");
        public final static Property AssetUnit = new Property(4, String.class, "assetUnit", false, "asset_unit");
        public final static Property AssetDecimals = new Property(5, long.class, "assetDecimals", false, "asset_decimals");
        public final static Property IssueTime = new Property(6, String.class, "issueTime", false, "issue_time");
        public final static Property AssetAvailAmount = new Property(7, long.class, "assetAvailAmount", false, "asset_avail_amount");
        public final static Property AssetWaitAmount = new Property(8, long.class, "assetWaitAmount", false, "asset_wait_amount");
        public final static Property AssetLockAmount = new Property(9, long.class, "assetLockAmount", false, "asset_lock_amount");
        public final static Property AssetLocalTotalAmount = new Property(10, long.class, "assetLocalTotalAmount", false, "asset_lock_total_amount");
    }


    public SafeAssetTbDao(DaoConfig config) {
        super(config);
    }
    
    public SafeAssetTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"safe_asset_tb\" (" + //
                "\"asset_id\" TEXT," + // 0: assetId
                "\"asset_short_name\" TEXT," + // 1: assetShortName
                "\"asset_name\" TEXT," + // 2: assetName
                "\"asset_desc\" TEXT," + // 3: assetDesc
                "\"asset_unit\" TEXT," + // 4: assetUnit
                "\"asset_decimals\" INTEGER NOT NULL ," + // 5: assetDecimals
                "\"issue_time\" TEXT," + // 6: issueTime
                "\"asset_avail_amount\" INTEGER NOT NULL ," + // 7: assetAvailAmount
                "\"asset_wait_amount\" INTEGER NOT NULL ," + // 8: assetWaitAmount
                "\"asset_lock_amount\" INTEGER NOT NULL ," + // 9: assetLockAmount
                "\"asset_lock_total_amount\" INTEGER NOT NULL );"); // 10: assetLocalTotalAmount
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_safe_asset_tb_asset_id_DESC ON \"safe_asset_tb\"" +
                " (\"asset_id\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"safe_asset_tb\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SafeAssetTb entity) {
        stmt.clearBindings();
 
        String assetId = entity.getAssetId();
        if (assetId != null) {
            stmt.bindString(1, assetId);
        }
 
        String assetShortName = entity.getAssetShortName();
        if (assetShortName != null) {
            stmt.bindString(2, assetShortName);
        }
 
        String assetName = entity.getAssetName();
        if (assetName != null) {
            stmt.bindString(3, assetName);
        }
 
        String assetDesc = entity.getAssetDesc();
        if (assetDesc != null) {
            stmt.bindString(4, assetDesc);
        }
 
        String assetUnit = entity.getAssetUnit();
        if (assetUnit != null) {
            stmt.bindString(5, assetUnit);
        }
        stmt.bindLong(6, entity.getAssetDecimals());
 
        String issueTime = entity.getIssueTime();
        if (issueTime != null) {
            stmt.bindString(7, issueTime);
        }
        stmt.bindLong(8, entity.getAssetAvailAmount());
        stmt.bindLong(9, entity.getAssetWaitAmount());
        stmt.bindLong(10, entity.getAssetLockAmount());
        stmt.bindLong(11, entity.getAssetLocalTotalAmount());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SafeAssetTb entity) {
        stmt.clearBindings();
 
        String assetId = entity.getAssetId();
        if (assetId != null) {
            stmt.bindString(1, assetId);
        }
 
        String assetShortName = entity.getAssetShortName();
        if (assetShortName != null) {
            stmt.bindString(2, assetShortName);
        }
 
        String assetName = entity.getAssetName();
        if (assetName != null) {
            stmt.bindString(3, assetName);
        }
 
        String assetDesc = entity.getAssetDesc();
        if (assetDesc != null) {
            stmt.bindString(4, assetDesc);
        }
 
        String assetUnit = entity.getAssetUnit();
        if (assetUnit != null) {
            stmt.bindString(5, assetUnit);
        }
        stmt.bindLong(6, entity.getAssetDecimals());
 
        String issueTime = entity.getIssueTime();
        if (issueTime != null) {
            stmt.bindString(7, issueTime);
        }
        stmt.bindLong(8, entity.getAssetAvailAmount());
        stmt.bindLong(9, entity.getAssetWaitAmount());
        stmt.bindLong(10, entity.getAssetLockAmount());
        stmt.bindLong(11, entity.getAssetLocalTotalAmount());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public SafeAssetTb readEntity(Cursor cursor, int offset) {
        SafeAssetTb entity = new SafeAssetTb( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // assetId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // assetShortName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // assetName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // assetDesc
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // assetUnit
            cursor.getLong(offset + 5), // assetDecimals
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // issueTime
            cursor.getLong(offset + 7), // assetAvailAmount
            cursor.getLong(offset + 8), // assetWaitAmount
            cursor.getLong(offset + 9), // assetLockAmount
            cursor.getLong(offset + 10) // assetLocalTotalAmount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SafeAssetTb entity, int offset) {
        entity.setAssetId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setAssetShortName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAssetName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAssetDesc(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAssetUnit(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAssetDecimals(cursor.getLong(offset + 5));
        entity.setIssueTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAssetAvailAmount(cursor.getLong(offset + 7));
        entity.setAssetWaitAmount(cursor.getLong(offset + 8));
        entity.setAssetLockAmount(cursor.getLong(offset + 9));
        entity.setAssetLocalTotalAmount(cursor.getLong(offset + 10));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(SafeAssetTb entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(SafeAssetTb entity) {
        return null;
    }

    @Override
    public boolean hasKey(SafeAssetTb entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
