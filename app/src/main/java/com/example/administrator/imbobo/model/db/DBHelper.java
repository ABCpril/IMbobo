package com.example.administrator.imbobo.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.imbobo.model.dao.ContatcTable;
import com.example.administrator.imbobo.model.dao.InviteTable;

/**
 * Created by Leon on 2018/11/3.
 * Functions:
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建联系人表
        db.execSQL(ContatcTable.CREATE_TAB);

        //创建邀请信息表
        db.execSQL(InviteTable.CREATE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
