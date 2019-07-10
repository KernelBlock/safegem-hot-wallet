package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.EosRamTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "eos_ram".
*/
public class EosRamTbDao extends AbstractDao<EosRamTb, Void> {

    public static final String TABLENAME = "eos_ram";

    /**
     * Properties of entity EosRamTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property TxId = new Property(0, String.class, "txId", false, "tx_id");
        public final static Property Bytes = new Property(1, String.class, "bytes", false, "bytes");
        public final static Property From = new Property(2, String.class, "from", false, "from");
        public final static Property To = new Property(3, String.class, "to", false, "to");
        public final static Property Quantity = new Property(4, String.class, "quantity", false, "quantity");
        public final static Property Memo = new Property(5, String.class, "memo", false, "memo");
    }


    public EosRamTbDao(DaoConfig config) {
        super(config);
    }
    
    public EosRamTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"eos_ram\" (" + //
                "\"tx_id\" TEXT," + // 0: txId
                "\"bytes\" TEXT," + // 1: bytes
                "\"from\" TEXT," + // 2: from
                "\"to\" TEXT," + // 3: to
                "\"quantity\" TEXT," + // 4: quantity
                "\"memo\" TEXT);"); // 5: memo
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_eos_ram_tx_id_DESC ON \"eos_ram\"" +
                " (\"tx_id\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"eos_ram\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, EosRamTb entity) {
        stmt.clearBindings();
 
        String txId = entity.getTxId();
        if (txId != null) {
            stmt.bindString(1, txId);
        }
 
        String bytes = entity.getBytes();
        if (bytes != null) {
            stmt.bindString(2, bytes);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(4, to);
        }
 
        String quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindString(5, quantity);
        }
 
        String memo = entity.getMemo();
        if (memo != null) {
            stmt.bindString(6, memo);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EosRamTb entity) {
        stmt.clearBindings();
 
        String txId = entity.getTxId();
        if (txId != null) {
            stmt.bindString(1, txId);
        }
 
        String bytes = entity.getBytes();
        if (bytes != null) {
            stmt.bindString(2, bytes);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(3, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(4, to);
        }
 
        String quantity = entity.getQuantity();
        if (quantity != null) {
            stmt.bindString(5, quantity);
        }
 
        String memo = entity.getMemo();
        if (memo != null) {
            stmt.bindString(6, memo);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public EosRamTb readEntity(Cursor cursor, int offset) {
        EosRamTb entity = new EosRamTb( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // txId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // bytes
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // from
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // to
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // quantity
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // memo
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EosRamTb entity, int offset) {
        entity.setTxId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setBytes(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFrom(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setQuantity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMemo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(EosRamTb entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(EosRamTb entity) {
        return null;
    }

    @Override
    public boolean hasKey(EosRamTb entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
