package com.example.administrator.imbobo.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.administrator.imbobo.model.bean.GroupInfo;
import com.example.administrator.imbobo.model.bean.InvationInfo;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.example.administrator.imbobo.utils.Constant;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

/**
 * Created by Leon on 2018/11/4.
 * Functions: 全局事件监听类- 广播在这里创建和发送
 */
public class EventListener {

    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        this.mContext = context;

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }

    //群信息变化的监听
    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {

        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reson) {

            Log.e("leon","接收到群组加入邀请");
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reson);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知  收到的加入请求
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

            Log.e("leon","用户申请加入群");
            //数据更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,applicant));
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受  应要求加入接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            Log.e("leon","加群申请被同意");
            //更新数据
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            //invationInfo.setReason("群申请被接受");//Leon
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        //应要求加入拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

            Log.e("leon","加群申请被拒绝");
            //更新数据
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupName,groupId,decliner));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            Log.e("leon","群组邀请被同意");
            //更新数据
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {

            Log.e("leon","群组邀请被拒绝");
            //更新数据
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(reason);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

            Log.e("leon","群成员被删除");
        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String s, String s1) {
            Log.e("leon","00006");
        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            Log.e("leon","接收邀请时自动加入到群组的通知");
            //更新数据
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setReason(inviteMessage);
            invationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invationInfo.setStatus(InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {
            Log.e("leon","成员禁言的通知");
        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {
            Log.e("leon","成员从禁言列表里移除通知");
        }

        @Override
        public void onAdminAdded(String s, String s1) {
            Log.e("leon","增加管理员的通知");
        }

        @Override
        public void onAdminRemoved(String s, String s1) {
            Log.e("leon","管理员移除的通知");
        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {
            Log.e("leon","群所有者变动通知");
        }

        //群成员加入
        @Override
        public void onMemberJoined(String s, String s1) {
            Log.e("leon","群组加入新成员通知");
        }

        @Override
        public void onMemberExited(String s, String s1) {
            Log.e("leon","群成员退出通知");
        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {
            Log.e("leon","群公告变动通知");
        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {
            Log.e("leon","增加共享文件的通知");
        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {
            Log.e("leon","群共享文件删除通知");
        }
    };

    /**联系人变化的监听*/
    private final EMContactListener emContactListener = new EMContactListener() {

        //联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {
            //本地的数据库更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid),
                    true);

            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }

        //联系人删除后执行的方法
        @Override
        public void onContactDeleted(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);

            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接收到联系人的新邀请（别人邀请你）
        @Override
        public void onContactInvited(String hxid, String reson) {

            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            //设置邀请人
            invationInfo.setUser(new UserInfo(hxid));
            //设置邀请原因
            invationInfo.setReason(reson);
            //设置邀请的状态
            invationInfo.setStatus(InvationInfo.InvitationStatus.NEW_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //联系人同意了邀请后调用的方法
        @Override
        public void onFriendRequestAccepted(String hxid) {
            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            invationInfo.setUser(new UserInfo(hxid));
            //别人同意了你的邀请
            invationInfo.setStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人拒绝了你的好友邀请
        @Override
        public void onFriendRequestDeclined(String hxid) {
            //拒绝邀请的处理-Leon---------------------------------------
            //数据库更新
            InvationInfo invationInfo = new InvationInfo();
            //设置拒绝原因
            invationInfo.setReason("拒绝添加");
            invationInfo.setUser(new UserInfo(hxid));
            //别人拒绝你的邀请
            invationInfo.setStatus(InvationInfo.InvitationStatus.REFUSE_AN_INVITATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
            //拒绝邀请的处理-Leon---------------------------------------


            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };


}
