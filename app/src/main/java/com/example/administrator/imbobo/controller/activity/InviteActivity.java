package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.administrator.imbobo.controller.adapter.InviteAdapter;

import com.example.administrator.imbobo.R;


/**
 * Created by Leon on 2018/10/6
 * Functions: 邀请信息
 */
public class InviteActivity extends Activity {

    private ListView iv_invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData(){
        //初始化ListView
        InviteAdapter inviteAdapter = new InviteAdapter(InviteActivity.this);
        iv_invite.setAdapter(inviteAdapter);
    }


    private void initView(){
        iv_invite = (ListView)findViewById(R.id.iv_invite);
    }

}


