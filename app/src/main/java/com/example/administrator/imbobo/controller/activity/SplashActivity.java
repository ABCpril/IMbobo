package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

/**
 * Created by Leon on 2018/10/5
 * Functions: 欢迎界面
 */
public class SplashActivity extends Activity {


    private final static int SPLASH = 2018105;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //如果当前的activity 已经退出那么不做任何的处理
            if (isFinishing()){
                return;
            }

            //判断进入主页面还是登陆页面。
            toMainOrLogin();
        }
    };

    /**判断用户是进入主页面还是登陆页面*/
    private void toMainOrLogin() {

        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
            //判断当前账号是否已经登陆
            if (EMClient.getInstance().isLoggedInBefore()){//登陆过

                //获取当前登陆用户的信息
               UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxid(EMClient.getInstance()
                       .getCurrentUser());

               if (account == null){
                   Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                   startActivity(intent);
               }else {
                   //跳转到主页面-相当于登陆成功 也应该调用登陆成功后的方法
                   Model.getInstance().loginSussess(account);

                   //跳转到主页面
                   Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                   startActivity(intent);
               }
            }else {//没有登陆过
                //跳转到登陆页面
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            //结束当前页面
            finish();
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //handle发送一个2秒钟延时的消息
        handler.sendEmptyMessageDelayed(SPLASH, 2000);

    }

    @Override
    protected void onDestroy() {

        //当activity销毁的时候移除掉handle发送的消息合理的管理内存
        handler.removeCallbacksAndMessages(null);


        super.onDestroy();
    }
}
