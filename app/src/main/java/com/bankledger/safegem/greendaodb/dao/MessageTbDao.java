package com.bankledger.safegem.greendaodb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bankledger.safegem.greendaodb.entity.MessageTb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "message_tb".
*/
public class MessageTbDao extends AbstractDao<MessageTb, Void> {

    public static final String TABLENAME = "message_tb";

    /**
     * Properties of entity MessageTb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property MsgId = new Property(0, String.class, "msgId", false, "msg_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "user_id");
        public final static Property Title = new Property(2, String.class, "title", false, "title");
        public final static Property Content = new Property(3, String.class, "content", false, "content");
        public final static Property MsgType = new Property(4, int.class, "msgType", false, "msg_type");
        public final static Property Source = new Property(5, String.class, "source", false, "source");
        public final static Property Date = new Property(6, String.class, "date", false, "date");
        public final static Property MsgUrl = new Property(7, String.class, "msgUrl", false, "msg_url");
        public final static Property TxHash = new Property(8, String.class, "txHash", false, "tx_hash");
        public final static Property CoinType = new Property(9, int.class, "coinType", false, "coin_type");
        public final static Property Icon = new Property(10, String.class, "icon", false, "icon");
        public final static Property ColdUniqueId = new Property(11, String.class, "coldUniqueId", false, "cold_unique_id");
    }


    public MessageTbDao(DaoConfig config) {
        super(config);
    }
    
    public MessageTbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"message_tb\" (" + //
                "\"msg_id\" TEXT," + // 0: msgId
                "\"user_id\" INTEGER NOT NULL ," + // 1: userId
                "\"title\" TEXT," + // 2: title
                "\"content\" TEXT," + // 3: content
                "\"msg_type\" INTEGER NOT NULL ," + // 4: msgType
                "\"source\" TEXT," + // 5: source
                "\"date\" TEXT," + // 6: date
                "\"msg_url\" TEXT," + // 7: msgUrl
                "\"tx_hash\" TEXT," + // 8: txHash
                "\"coin_type\" INTEGER NOT NULL ," + // 9: coinType
                "\"icon\" TEXT," + // 10: icon
                "\"cold_unique_id\" TEXT);"); // 11: coldUniqueId
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_message_tb_msg_id_DESC_cold_unique_id_DESC ON \"message_tb\"" +
                " (\"msg_id\" DESC,\"cold_unique_id\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"message_tb\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MessageTb entity) {
        stmt.clearBindings();
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(1, msgId);
        }
        stmt.bindLong(2, entity.getUserId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindLong(5, entity.getMsgType());
 
        String source = entity.getSource();
        if (source != null) {
            stmt.bindString(6, source);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(7, date);
        }
 
        String msgUrl = entity.getMsgUrl();
        if (msgUrl != null) {
            stmt.bindString(8, msgUrl);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(9, txHash);
        }
        stmt.bindLong(10, entity.getCoinType());
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(11, icon);
        }
 
        String coldUniqueId = entity.getColdUniqueId();
        if (coldUniqueId != null) {
            stmt.bindString(12, coldUniqueId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MessageTb entity) {
        stmt.clearBindings();
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(1, msgId);
        }
        stmt.bindLong(2, entity.getUserId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
        stmt.bindLong(5, entity.getMsgType());
 
        String source = entity.getSource();
        if (source != null) {
            stmt.bindString(6, source);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(7, date);
        }
 
        String msgUrl = entity.getMsgUrl();
        if (msgUrl != null) {
            stmt.bindString(8, msgUrl);
        }
 
        String txHash = entity.getTxHash();
        if (txHash != null) {
            stmt.bindString(9, txHash);
        }
        stmt.bindLong(10, entity.getCoinType());
 
        String icon = entity.getIcon();
        if (icon != null) {
            stmt.bindString(11, icon);
        }
 
        String coldUniqueId = entity.getColdUniqueId();
        if (coldUniqueId != null) {
            stmt.bindString(12, coldUniqueId);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public MessageTb readEntity(Cursor cursor, int offset) {
        MessageTb entity = new MessageTb( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // msgId
            cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.getInt(offset + 4), // msgType
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // source
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // date
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // msgUrl
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // txHash
            cursor.getInt(offset + 9), // coinType
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // icon
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // coldUniqueId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MessageTb entity, int offset) {
        entity.setMsgId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMsgType(cursor.getInt(offset + 4));
        entity.setSource(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDate(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setMsgUrl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTxHash(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCoinType(cursor.getInt(offset + 9));
        entity.setIcon(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setColdUniqueId(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(MessageTb entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(MessageTb entity) {
        return null;
    }

    @Override
    public boolean hasKey(MessageTb entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
