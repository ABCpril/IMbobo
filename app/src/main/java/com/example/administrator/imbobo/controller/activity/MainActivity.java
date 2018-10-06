package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.controller.fragment.ChatFragment;
import com.example.administrator.imbobo.controller.fragment.ContactListFragment;
import com.example.administrator.imbobo.controller.fragment.SettingFragment;

/**
 * 应用标识(AppKey)	leon88888888#instantmessaging
 *OrgName	leon88888888
 *AppName	instantmessaging
 *创建时间	2018-10-04 17:37:16
 *最后修改时间	2018-10-04 17:37:16
 *服务类型	社区版
 *用户注册模式	开放注册允许在该应用下自由注册新用户
 *开启强制推送	禁止由系统设置和应用设置控制是否接收推送
 *Client ID:	YXA6FNeU8Me5EeipVc9fOublWg
 *Client Secret:	YXA6MW5Hy_VDkCihpu0ueQmReH5V4T0
 *缩略图大小	170px * 170px(长* 宽)
 *App 状态	Offline
 *申请上线审核通过后，应用将从开发环境切换为生产环境，审核将在工作日9:00-18:00之间1小时内完成，应用数据及设置在上线后不会有任何变化。
 */

public class MainActivity extends FragmentActivity{

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    private void initListener(){
        //radiogroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Fragment fragment = null;

                switch (checkedId){
                    //会话列表页面
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;
                    //联系人页面
                    case R.id.rb_main_contact:
                        fragment = contactListFragment;
                        break;

                    //设置页面
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }

                //实现fragment切换的方法
                switchFragment( fragment);
            }
        });

        //默认选择会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }

    //实现fragment切换的方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }

    private void initData(){
        //创建3个fragment对象
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
    }

    private void initView(){
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
    }



}
