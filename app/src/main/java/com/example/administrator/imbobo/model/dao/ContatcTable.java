package com.example.administrator.imbobo.model.dao;

/**
 * Created by Leon on 2018/11/3.
 * Functions: 联系人表 - 建表语句辅助类
 */
public class ContatcTable {

    /**表名*/
    public static final String TAB_NAME = "tab_contact";

    /**环信id string*/
    public static final String COL_HXID = "hxid";

    /**姓名 string*/
    public static final String COL_NAME = "name";

    /**用户昵称 string*/
    public static final String COL_NICK = "nick";

    /**头像 string*/
    public static final String COL_PHOTO = "photo";

    /**是否是联系人-例如：群里的人不一定都是你的好友 integer*/
    public static final String COL_IS_CONTACT = "is_contact";

    /**建表语句*/
    public static final String CREATE_TAB = "create table "
            +TAB_NAME+" ("
            +COL_HXID+" text primary key,"
            +COL_NAME+" text,"
            +COL_NICK+" text,"
            +COL_PHOTO+" text,"
            +COL_IS_CONTACT+" integer);";
}
