package com.example.administrator.imbobo.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.administrator.imbobo.model.bean.InvationInfo;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.example.administrator.imbobo.utils.Constant;

/**
 * Created by Leon on 2018/11/4.
 * Functions: 全局事件监听类
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
    }

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
        public void onFriendRequestDeclined(String s) {
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };


}