package com.example.administrator.imbobo;

import android.app.Application;
import android.content.Context;

import com.example.administrator.imbobo.model.Model;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

/**
 * Created by Leon on 2018/9/3.
 * Functions: Application
 */
public class IMApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化EaseUI
        EMOptions options = new EMOptions();

        //设置需要同意后才能接受好友邀请
        options.setAcceptInvitationAlways(false);
        //需要用户同意之后才能接受群邀请
        options.setAutoAcceptGroupInvitation(false);

        EaseUI.getInstance().init(this,options);

        //初始化数据模型层类
        Model.getInstance().init(this);


        /**
         * easeUi 官方文档地址：
         *  http://docs.easemob.com/start/200androidcleintintegration/135easeuiuseguide
         */
        //-----------------------------------------------leon----------------------------------------------
        EaseUI easeUI = EaseUI.getInstance();
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            //允许MSG通知吗？
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                return true;
            }

            //允许MSG声音吗？
            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
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
        //---------------------------------------------------leon------------------------------------------

        //初始化全局上下文
        mContext = this;
    }

    /**获取全局上下文对象*/
    public static Context getGlobalApplication(){
        return mContext;
    }
}
