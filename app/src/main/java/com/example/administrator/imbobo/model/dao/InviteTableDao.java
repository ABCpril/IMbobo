package com.example.administrator.imbobo.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.imbobo.model.bean.GroupInfo;
import com.example.administrator.imbobo.model.bean.InvationInfo;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/3.
 * Functions: 邀请信息表（InviteTable）的操作类
 */
public class InviteTableDao {

    private DBHelper mHelper;

    public InviteTableDao(DBHelper helper) {
        this.mHelper = helper;
    }

    /**添加邀请信息*/
    public void addInvitation(InvationInfo invationInfo){

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行添加语句
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON,invationInfo.getReason());//邀请的原因
        /**ordinal() 枚举转 integer 按照枚举的序号从0开始的*/
        values.put(InviteTable.COL_STATUS,invationInfo.getStatus().ordinal());//邀请的状态

        //为了排序倒顺序----------Leon新增加---------------------------------------
        values.put(InviteTable.CURRENTTIME,System.currentTimeMillis());//邀请的时间

        UserInfo user = invationInfo.getUser();
        if (user != null){//联系人
            values.put(InviteTable.COL_USER_HXID,invationInfo.getUser().getHxid());
            values.put(InviteTable.COL_USER_NAME,invationInfo.getUser().getName());
        }else {//群组
            values.put(InviteTable.COL_GROUP_HXID,invationInfo.getGroup().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME,invationInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_HXID,invationInfo.getGroup().getInvatePerson());
        }

        //Leon修改bug
        //db.insert(InviteTable.TABLE_NAME,null,values);.//這個方法插入有問題 primary key 不可重複
        db.replace(InviteTable.TABLE_NAME,null,values);//这是原来的方法
    }

    /**获取邀请信息*/
    public List<InvationInfo> getInvittations(){
        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行查询语句 ↓可以正常使用
        //String sql = "select * from "+InviteTable.TABLE_NAME;
        //2.执行查询语句 ↓倒序 根据后来添加的自增长id字段
        String sql = "select * from "+InviteTable.TABLE_NAME + " order by currentTime desc";

        Cursor cursor = db.rawQuery(sql,null);

        List<InvationInfo> invationInfos = new ArrayList<>();
        while (cursor.moveToNext()){
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(
                    InviteTable.COL_STATUS))));
            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));

            if (groupId == null){//联系人的邀请信息
                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                //这里的昵称和用户名称设置的一样的
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                invationInfo.setUser(userInfo);
            }else {//群组的邀请信息
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                invationInfo.setGroup(groupInfo);
            }

            //添加遍历的邀请信息
            invationInfos.add(invationInfo);
        }

        //3.关闭资源
        cursor.close();

        //4.返回数据
        return invationInfos;
    }

    /**将int类型状态转换为邀请的状态*/
    private InvationInfo.InvitationStatus int2InviteStatus(int intStatus){

        /**新邀请 NEW_INVITE,*/
        if (intStatus == InvationInfo.InvitationStatus.NEW_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.NEW_INVITE;
        }

        /**接受邀请INVITE_ACCEPT*/
        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()){
            return InvationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        /**邀请被接受 INVITE_ACCEPT_BY_PEER*/
        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()){
            return InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        //-----以下是群组邀请信息状态-----

        /**收到邀请去加入群组*/
        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        /**收到申请加入*/
        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        /**群邀请已经被对方接受*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        /**群申请已经被批准*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        /**接受了群邀请*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        /**批准的群加入申请*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        /**拒绝了群邀请*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        /**拒绝了群申请加入*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        /**群邀请被对方拒绝*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        /**群申请被拒绝*/
        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()){
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        /**Leon 别人拒绝了你的邀请*/
        if (intStatus == InvationInfo.InvitationStatus.REFUSE_AN_INVITATION.ordinal()){
            return InvationInfo.InvitationStatus.REFUSE_AN_INVITATION;
        }

        return null;
    }

    /**删除邀请*/
    public void removeInvitation(String hxId){
        //避免空指针
        if (hxId == null){ return; }

        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行删除语句
        db.delete(InviteTable.TABLE_NAME,InviteTable.COL_USER_HXID+"=?;",new String[]{hxId});
    }

    /**更新邀请状态*/
    public void updateInvitationStatus(InvationInfo.InvitationStatus invitationStatus,String hxId){
        //避免空指针
        if (hxId == null){ return; }

        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行更新操作
        ContentValues values = new ContentValues();
        //更新状态
        values.put(InviteTable.COL_STATUS,invitationStatus.ordinal());
        db.update(InviteTable.TABLE_NAME,values,InviteTable.COL_USER_HXID+"=?",new String[]{hxId});
    }

    /**新删除-删除时间最大的一行 即最上面的一行*/
    public void deleteFirstLineInvitation(){
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "select " +InviteTable.COL_USER_HXID+ " from "+InviteTable.TABLE_NAME + " order by currentTime desc";

        Cursor cursor = db.rawQuery(sql,null);

        //定义变量每次只删除一条 最新的数据
        int i = 1;

        while (cursor.moveToNext() && i > 0){

            /**
             *  Couldn't read row 0, col -1 from CursorWindow.
             *  Make sure the Cursor is initialized correctly before accessing data from it.
             *  at android.database.CursorWindow.nativeGetString(Native Method)
             *
             *  获取 COL_USER_HXID  SQL 语句就些获取COL_USER_HXID 不然会报上面的错
             */
            String hxId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID));

            if (hxId != null){
                //执行删除语句  " order by currentTime desc"
                db.delete(InviteTable.TABLE_NAME,InviteTable.COL_USER_HXID+"=?;",new String[]{hxId});
            }
            i--;
        }
    }
}
