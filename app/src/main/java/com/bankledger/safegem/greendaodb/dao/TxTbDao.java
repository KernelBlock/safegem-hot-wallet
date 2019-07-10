package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.TxTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "tx_tb".
*/
public class TxTbDao extends AbstractDao<TxTb, Long> {

    public static final String TABLENAME = "tx_tb";

    /**
     * Properties of entity TxTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TxHash = new Property(1, String.class, "txHash", false, "tx_hash");
        public final static Property Height = new Property(2, Long.class, "height", false, "height");
        public final static Property BlockHash = new Property(3, String.class, "blockHash", false, "block_hash");
        public final static Property BlockTime = new Property(4, Long.class, "blockTime", false, "block_time");
        public final static Property LockTime = new Property(5, Long.class, "lockTime", false, "lock_time");
        public final static Property Time = new Property(6, Long.class, "time", false, "time");
        public final static Property Version = new Property(7, Long.class, "version", false, "version");
        public final static Property Coin = new Property(8, String.class, "coin", false, "coin");
        public final static Property Confirm = new Property(9, int.class, "confirm", false, "confirm");
        public final static Property BtcAsssetType = new Property(10, int.class, "btcAsssetType", false, "btc_asset_type");
    }


    public TxTbDao(DaoConfig config) {
        super(config);
    }
    
    public TxTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"tx_tb\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"tx_hash\" TEXT UNIQUE ," + // 1: txHash
                "\"height\" INTEGER," + // 2: height
                "\"block_hash\" TEXT," + // 3: blockHash
                "\"block_time\" INTEGER," + // 4: blockTime
                "\"lock_time\" INTEGER," + // 5: lockTime
                "\"time\" INTEGER," + // 6: time
                "\"version\" INTEGER," + // 7: version
                "\"coin\" TEXT," + // 8: coin
                "\"confirm\" INTEGER NOT NULL ," + // 9: confirm
                "\"btc_asset_type\" INTEGER NOT NULL );"); // 10: btcAsssetType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"tx_tb\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TxTb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(2, txHash);
        }
 
        Long height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(3, height);
        }
 
        String blockHash = entity.getBlockHash();
        if (blockHash != null) {
            stmt.bindString(4, blockHash);
        }
 
        Long blockTime = entity.getBlockTime();
        if (blockTime != null) {
            stmt.bindLong(5, blockTime);
        }
 
        Long lockTime = entity.getLockTime();
        if (lockTime != null) {
            stmt.bindLong(6, lockTime);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(7, time);
        }
 
        Long version = entity.getVersion();
        if (version != null) {
            stmt.bindLong(8, version);
        }
 
        String coin = entity.getCoin();
        if (coin != null) {
            stmt.bindString(9, coin);
        }
        stmt.bindLong(10, entity.getConfirm());
        stmt.bindLong(11, entity.getBtcAsssetType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TxTb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(2, txHash);
        }
 
        Long height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(3, height);
        }
 
        String blockHash = entity.getBlockHash();
        if (blockHash != null) {
            stmt.bindString(4, blockHash);
        }
 
        Long blockTime = entity.getBlockTime();
        if (blockTime != null) {
            stmt.bindLong(5, blockTime);
        }
 
        Long lockTime = entity.getLockTime();
        if (lockTime != null) {
            stmt.bindLong(6, lockTime);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(7, time);
        }
 
        Long version = entity.getVersion();
        if (version != null) {
            stmt.bindLong(8, version);
        }
 
        String coin = entity.getCoin();
        if (coin != null) {
            stmt.bindString(9, coin);
        }
        stmt.bindLong(10, entity.getConfirm());
        stmt.bindLong(11, entity.getBtcAsssetType());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TxTb readEntity(Cursor cursor, int offset) {
        TxTb entity = new TxTb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // txHash
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // height
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // blockHash
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // blockTime
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // lockTime
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // time
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // version
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // coin
            cursor.getInt(offset + 9), // confirm
            cursor.getInt(offset + 10) // btcAsssetType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TxTb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTxHash(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHeight(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setBlockHash(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBlockTime(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setLockTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setVersion(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setCoin(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setConfirm(cursor.getInt(offset + 9));
        entity.setBtcAsssetType(cursor.getInt(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TxTb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TxTb entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TxTb entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
