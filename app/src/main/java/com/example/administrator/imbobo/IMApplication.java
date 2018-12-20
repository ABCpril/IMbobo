package com.example.administrator.imbobo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.service.MediaService;
import com.example.administrator.imbobo.utils.Constant;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Leon on 2018/9/3.
 * Functions: Application
 */
public class IMApplication extends Application {

    private static Context mContext;

    /**注册广播为了会话的小红点*/
    private  LocalBroadcastManager mLBM;

    @Override
    public void onCreate() {
        super.onCreate();

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(this);

        //初始化EaseUI
        EMOptions options = new EMOptions();

        //设置需要同意后才能接受好友邀请
        options.setAcceptInvitationAlways(false);

        //需要用户同意之后才能接受群邀请
        options.setAutoAcceptGroupInvitation(false);

        EaseUI.getInstance().init(this,options);

        //初始化数据模型层类
        Model.getInstance().init(this);

        /**设置其他关于easeUI的方法*/
        setOtherEaseUI();

        //初始化全局上下文
        mContext = this;
    }

    /**获取全局上下文对象*/
    public static Context getGlobalApplication(){
        return mContext;
    }

    /**
     * easeUi 官方文档地址：
     *  http://docs.easemob.com/start/200androidcleintintegration/135easeuiuseguide
     */
    private void setOtherEaseUI(){

        EaseUI easeUI = EaseUI.getInstance();

        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            //允许MSG通知吗？
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                //新增加播放音乐
                //MediaService.play(mContext, R.raw.level_up);
                //发送广播
                mLBM.sendBroadcast(new Intent(Constant.MESSAGE_RECEIVED));
                return false;
            }

            //允许MSG声音吗？
            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                //新增加播放音乐
                // MediaService.play(mContext, R.raw.level_up);
                return false;
            }

            //允许MSG振动吗？
            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return false;
            }

            //IS扬声器打开
            @Override
            public boolean isSpeakerOpened() {
                return false;
            }
        });
    }


    /**
     * GitHub上找到的设置角标的方法 不能用
     *  获取当前用户的方法 ： EMClient.getInstance().getCurrentUser()
     *  获取未读信息数的方法：EMClient.getInstance().chatManager().getConversation.(user).getUnreadMsgCount()
     */
    //ShortcutBadger.applyCount(this, EMClient.getInstance().chatManager().getConversation
     //(EMClient.getInstance().getCurrentUser()).getUnreadMsgCount()); //for 1.1.4+


}
