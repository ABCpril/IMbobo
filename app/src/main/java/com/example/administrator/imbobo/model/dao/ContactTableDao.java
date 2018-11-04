package com.example.administrator.imbobo.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/3.
 * Functions:联系人表（ContatcTable）的操作类
 *
 * 数据库的四大类操作方法 增删改查
 */
public class ContactTableDao {

    private DBHelper mHelper;

    public ContactTableDao(DBHelper helper){
        this.mHelper = helper;
    }

    //获取所有联系人
    public List<UserInfo> getCountacts(){

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行查询语句
        String sql = "select * from "+ContatcTable.TAB_NAME+" where "+ContatcTable.COL_IS_CONTACT
                +" = 1;";
        Cursor cursor = db.rawQuery(sql,null);

        List<UserInfo> users = new ArrayList<>();
        while (cursor.moveToNext()){
           UserInfo userInfo = new UserInfo();

           userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_HXID)));
           userInfo.setName(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_NAME)));
           userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_NICK)));
           userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_PHOTO)));

            users.add(userInfo);
;        }

        //3.关闭资源
        cursor.close();

        //4.返回数据
        return users;
    }

    //通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId){

        //避免空指针异常
        if (hxId == null){ return null; }

        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from "+ContatcTable.TAB_NAME+" where "+ContatcTable.COL_HXID+" = ?;";
        Cursor cursor = db.rawQuery(sql,new String[]{hxId});

        //这里用SQL语句查到的只有一条结果就用了if语句没有用循环
        UserInfo userInfo = null;
        if (cursor.moveToNext()){
           userInfo =  new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContatcTable.COL_PHOTO)));
        }


        //关闭资源
        cursor.close();

        //返回数据
        return userInfo;
    }

    //通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds){

        //避免空指针异常
        if (hxIds == null || hxIds.size() <= 0){ return null;}

        List<UserInfo> contacts = new ArrayList<>();

        //遍历hxIds
        for (String hxid : hxIds){

            //通过环信id获取联系人单个信息
            UserInfo contact = getContactByHx(hxid);

            contacts.add(contact);
        }

        //返回数据
        return contacts;
    }

    //保存单个联系人
    public void saveContact(UserInfo user,boolean isMyContact){

        //避免空指针异常
        if (user == null){ return;}

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行保存语句  insert() 插入会导致重复  replace()添加不会导致重复
        ContentValues values = new ContentValues();
        values.put(ContatcTable.COL_HXID,user.getHxid());
        values.put(ContatcTable.COL_NAME,user.getName());
        values.put(ContatcTable.COL_NICK,user.getNick());
        values.put(ContatcTable.COL_PHOTO,user.getPhoto());
        //boolean 转整型 isMyContact?1:0
        values.put(ContatcTable.COL_IS_CONTACT,isMyContact?1:0);
        db.replace(ContatcTable.TAB_NAME,null,values);
    }

    //保存联系人信息
    public void saveContacts(List<UserInfo> contacts,boolean isMyContact){
        //避免空指针异常
        if (contacts == null || contacts.size() <= 0){ return;}

        //遍历集合
        for (UserInfo contact : contacts){
            //保存遍历到的单个联系人
            saveContact(contact,isMyContact);
        }
    }

    //删除联系人信息
    public void deleteContactByHxId(String hxId){
        if (hxId == null){ return;}

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行SQL（删除）语句
        db.delete(ContatcTable.TAB_NAME,ContatcTable.COL_HXID+"=?;",new String[]{hxId});
    }

}
