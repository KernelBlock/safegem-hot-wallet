package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.EosBalanceTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "eos_balance_tb".
*/
public class EosBalanceTbDao extends AbstractDao<EosBalanceTb, Void> {

    public static final String TABLENAME = "eos_balance_tb";

    /**
     * Properties of entity EosBalanceTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Account = new Property(0, String.class, "account", false, "account");
        public final static Property Balance = new Property(1, String.class, "balance", false, "balance");
        public final static Property TokenName = new Property(2, String.class, "tokenName", false, "token_name");
        public final static Property CoinType = new Property(3, int.class, "coinType", false, "coin_type");
    }


    public EosBalanceTbDao(DaoConfig config) {
        super(config);
    }
    
    public EosBalanceTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"eos_balance_tb\" (" + //
                "\"account\" TEXT," + // 0: account
                "\"balance\" TEXT," + // 1: balance
                "\"token_name\" TEXT," + // 2: tokenName
                "\"coin_type\" INTEGER NOT NULL );"); // 3: coinType
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_eos_balance_tb_account_DESC_token_name_DESC ON \"eos_balance_tb\"" +
                " (\"account\" DESC,\"token_name\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"eos_balance_tb\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, EosBalanceTb entity) {
        stmt.clearBindings();
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(1, account);
        }
 
        String balance = entity.getBalance();
        if (balance != null) {
            stmt.bindString(2, balance);
        }
 
        String tokenName = entity.getTokenName();
        if (tokenName != null) {
            stmt.bindString(3, tokenName);
        }
        stmt.bindLong(4, entity.getCoinType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EosBalanceTb entity) {
        stmt.clearBindings();
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(1, account);
        }
 
        String balance = entity.getBalance();
        if (balance != null) {
            stmt.bindString(2, balance);
        }
 
        String tokenName = entity.getTokenName();
        if (tokenName != null) {
            stmt.bindString(3, tokenName);
        }
        stmt.bindLong(4, entity.getCoinType());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public EosBalanceTb readEntity(Cursor cursor, int offset) {
        EosBalanceTb entity = new EosBalanceTb( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // account
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // balance
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tokenName
            cursor.getInt(offset + 3) // coinType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EosBalanceTb entity, int offset) {
        entity.setAccount(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setBalance(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTokenName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCoinType(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(EosBalanceTb entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(EosBalanceTb entity) {
        return null;
    }

    @Override
    public boolean hasKey(EosBalanceTb entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
