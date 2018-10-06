package com.example.administrator.imbobo;

import android.app.Application;

import com.example.administrator.imbobo.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

/**
 * Created by Leon on 2018/9/3.
 * Functions: Application
 */
public class IMApplication extends Application {

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
    }
}
