package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.AddressBookTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "address_book".
*/
public class AddressBookTbDao extends AbstractDao<AddressBookTb, Long> {

    public static final String TABLENAME = "address_book";

    /**
     * Properties of entity AddressBookTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AddrId = new Property(0, Long.class, "addrId", true, "addr_id");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "user_id");
        public final static Property Coin = new Property(2, String.class, "coin", false, "coin");
        public final static Property Name = new Property(3, String.class, "name", false, "name");
        public final static Property Address = new Property(4, String.class, "address", false, "address");
    }


    public AddressBookTbDao(DaoConfig config) {
        super(config);
    }
    
    public AddressBookTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"address_book\" (" + //
                "\"addr_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: addrId
                "\"user_id\" INTEGER," + // 1: userId
                "\"coin\" TEXT," + // 2: coin
                "\"name\" TEXT," + // 3: name
                "\"address\" TEXT);"); // 4: address
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_address_book_user_id_DESC_coin_DESC_address_DESC ON \"address_book\"" +
                " (\"user_id\" DESC,\"coin\" DESC,\"address\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"address_book\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AddressBookTb entity) {
        stmt.clearBindings();
 
        Long addrId = entity.getAddrId();
        if (addrId != null) {
            stmt.bindLong(1, addrId);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String coin = entity.getCoin();
        if (coin != null) {
            stmt.bindString(3, coin);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(5, address);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AddressBookTb entity) {
        stmt.clearBindings();
 
        Long addrId = entity.getAddrId();
        if (addrId != null) {
            stmt.bindLong(1, addrId);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String coin = entity.getCoin();
        if (coin != null) {
            stmt.bindString(3, coin);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(5, address);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AddressBookTb readEntity(Cursor cursor, int offset) {
        AddressBookTb entity = new AddressBookTb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // addrId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // coin
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // address
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AddressBookTb entity, int offset) {
        entity.setAddrId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setCoin(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAddress(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AddressBookTb entity, long rowId) {
        entity.setAddrId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AddressBookTb entity) {
        if(entity != null) {
            return entity.getAddrId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AddressBookTb entity) {
        return entity.getAddrId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
