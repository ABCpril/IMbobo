package com.example.administrator.imbobo.controller.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.administrator.imbobo.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

/**
 * Created by Leon on 2018/10/6
 * Functions: 会话详情页面
 */
public class ChatActivity extends FragmentActivity {

    private String mHxid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();
    }

    private void initData(){
        //创建一个会话的fragment
        EaseChatFragment easeChatFragment = new EaseChatFragment();

        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);

        easeChatFragment.setArguments(getIntent().getExtras());

        //替换fragment-activity继承FragmentActivity才有的方法
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //替换fragment
        transaction.replace(R.id.fl_chat,easeChatFragment).commit();
    }
}
