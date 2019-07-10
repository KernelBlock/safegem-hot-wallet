package com.bankledger.safegem.greendaodb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bankledger.safegem.greendaodb.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, true);
    }
}