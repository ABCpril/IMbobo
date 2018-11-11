package com.example.administrator.imbobo.model.dao;

/**
 * Created by Leon on 2018/11/3.
 * Functions: 邀请信息表
 */
public class InviteTable {

    /**表名*/
    public static final String TABLE_NAME = "tab_invite";

    /**用户的环信id String*/
    public static final String COL_USER_HXID = "user_hxid";

    /**用户的名称 String*/
    public static final String COL_USER_NAME = "user_name";

    /**群组名称 String*/
    public static final String COL_GROUP_NAME = "group_name";

    /**群组的hxid String*/
    public static final String COL_GROUP_HXID = "group_hxid";

    /**邀请的原因 String*/
    public static final String COL_REASON = "reason";

    /**邀请的状态 integer*/
    public static final String COL_STATUS = "status";

    /**建表的语句-"create table "后面少了空格产生bug*/
    public static final String CREATE_TAB = "create table "
            + TABLE_NAME +" ("
            + "id int identity(1,1) primary key,"// 自增主键创建语法
            //+ COL_USER_HXID +" text primary key,"
            + COL_USER_HXID +" text,"
            + COL_USER_NAME +" text,"
            + COL_GROUP_HXID +" text,"
            + COL_GROUP_NAME +" text,"
            + COL_REASON +" text,"
            + COL_STATUS +" integer);";

}
