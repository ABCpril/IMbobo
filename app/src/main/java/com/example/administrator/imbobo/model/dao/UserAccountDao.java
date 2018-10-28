package com.example.administrator.imbobo.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.model.db.UserAccountDB;

/**
 * Created by Leon on 2018/10/6
 * Functions: 数据库的操作类
 */
public class UserAccountDao {

    private final UserAccountDB mHelpr;

    public UserAccountDao(Context context){
         mHelpr = new UserAccountDB(context);
    }

    //添加用户到数据库
    public void addAccount(UserInfo user){
        //获取数据库对象
        SQLiteDatabase db = mHelpr.getReadableDatabase();

        //执行添加操作 replace():如果数据库中有就替换，没有就插入一条数据。
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_HXID,user.getHxid());
        values.put(UserAccountTable.COL_NAME,user.getName());
        values.put(UserAccountTable.COL_NICK,user.getNick());
        values.put(UserAccountTable.COL_PHOTO,user.getPhoto());
        db.replace(UserAccountTable.TAB_NAME,null,values);
    }

    //根据环信ID获取用户的信息
    public UserInfo getAccountByHxid(String hxId){
        //获取数据库对象
        SQLiteDatabase db = mHelpr.getReadableDatabase();

        //执行查询语句
        String sql = "select *from "+UserAccountTable.TAB_NAME+" where "+UserAccountTable.COL_HXID +" = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{hxId});

        UserInfo userInfo = null;
        if (cursor.moveToNext()){
            userInfo = new UserInfo();

            //封装对象
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }

        //关闭资源
        cursor.close();

        //返回数据
        return userInfo;
    }

}
