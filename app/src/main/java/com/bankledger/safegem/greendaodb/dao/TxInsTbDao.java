package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.TxInsTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "tx_ins_tb".
*/
public class TxInsTbDao extends AbstractDao<TxInsTb, Long> {

    public static final String TABLENAME = "tx_ins_tb";

    /**
     * Properties of entity TxInsTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TxHash = new Property(1, String.class, "txHash", false, "tx_hash");
        public final static Property PrevTxHash = new Property(2, String.class, "prevTxHash", false, "prev_tx_hash");
        public final static Property PrevOutSn = new Property(3, Long.class, "prevOutSn", false, "prev_out_sn");
        public final static Property ValueSat = new Property(4, String.class, "valueSat", false, "value_sat");
        public final static Property Sequence = new Property(5, Long.class, "sequence", false, "sequence");
    }


    public TxInsTbDao(DaoConfig config) {
        super(config);
    }
    
    public TxInsTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"tx_ins_tb\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"tx_hash\" TEXT," + // 1: txHash
                "\"prev_tx_hash\" TEXT," + // 2: prevTxHash
                "\"prev_out_sn\" INTEGER," + // 3: prevOutSn
                "\"value_sat\" TEXT," + // 4: valueSat
                "\"sequence\" INTEGER);"); // 5: sequence
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_tx_ins_tb_tx_hash_DESC_prev_tx_hash_DESC_prev_out_sn_DESC ON \"tx_ins_tb\"" +
                " (\"tx_hash\" DESC,\"prev_tx_hash\" DESC,\"prev_out_sn\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"tx_ins_tb\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TxInsTb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(2, txHash);
        }
 
        String prevTxHash = entity.getPrevTxHash();
        if (prevTxHash != null) {
            stmt.bindString(3, prevTxHash);
        }
 
        Long prevOutSn = entity.getPrevOutSn();
        if (prevOutSn != null) {
            stmt.bindLong(4, prevOutSn);
        }
 
        String valueSat = entity.getValueSat();
        if (valueSat != null) {
            stmt.bindString(5, valueSat);
        }
 
        Long sequence = entity.getSequence();
        if (sequence != null) {
            stmt.bindLong(6, sequence);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TxInsTb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(2, txHash);
        }
 
        String prevTxHash = entity.getPrevTxHash();
        if (prevTxHash != null) {
            stmt.bindString(3, prevTxHash);
        }
 
        Long prevOutSn = entity.getPrevOutSn();
        if (prevOutSn != null) {
            stmt.bindLong(4, prevOutSn);
        }
 
        String valueSat = entity.getValueSat();
        if (valueSat != null) {
            stmt.bindString(5, valueSat);
        }
 
        Long sequence = entity.getSequence();
        if (sequence != null) {
            stmt.bindLong(6, sequence);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TxInsTb readEntity(Cursor cursor, int offset) {
        TxInsTb entity = new TxInsTb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // txHash
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // prevTxHash
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // prevOutSn
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // valueSat
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5) // sequence
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TxInsTb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTxHash(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPrevTxHash(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPrevOutSn(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setValueSat(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSequence(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TxInsTb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TxInsTb entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TxInsTb entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
