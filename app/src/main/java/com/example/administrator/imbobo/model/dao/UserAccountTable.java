package com.example.administrator.imbobo.model.dao;

/**
 * Created by Leon on 2018/10/6
 * Functions: sql 语句类
 */
public class UserAccountTable {

    //表的名称
    public static final String TAB_NAME = "tab_account";

    //表中存放的字段
    public static final String COL_NAME = "name";

    //表中存放的字段
    public static final String COL_HXID = "hxid";

    //表中存放的字段
    public static final String COL_NICK = "nick";

    //表中存放的字段
    public static final String COL_PHOTO = "photo";

    //创建表的语句
    public static final String CREATE_TAB = "create table "+TAB_NAME+" ("+COL_HXID+" text primary key,"+COL_NAME+" text,"
            +COL_NICK+" text,"+COL_PHOTO+" text);";

}
