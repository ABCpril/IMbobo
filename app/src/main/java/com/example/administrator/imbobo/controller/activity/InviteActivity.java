package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.imbobo.controller.adapter.InviteAdapter;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.InvationInfo;
import com.example.administrator.imbobo.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;


/**
 * Created by Leon on 2018/10/6
 * Functions: 邀请信息
 */
public class InviteActivity extends Activity {

    private ListView iv_invite;
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;


    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(final InvationInfo invationInfo) {//同意添加为好友
            //开启子线程做网络请求-通知环信服务器点击了接受按钮
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());

                        //数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.
                                InvitationStatus.INVITE_ACCEPT,invationInfo.getUser().getHxid());

                       //回到主线程处理
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面发生变化
                                Toast.makeText(InviteActivity.this,"接受了邀请",Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        //接受邀请失败的处理
                        //回到主线程处理
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面发生变化
                                Toast.makeText(InviteActivity.this,"接受邀请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onReject(final InvationInfo invationInfo) {//拒绝添加为好友
            //开启子线程做网络请求-通知环信服务器点击了拒绝按钮
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());

                        //本地数据库变化
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(
                                invationInfo.getUser().getHxid());
                        //页面变化-到主线程中处理
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝了邀请",Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        //拒绝失败-页面上提醒用户
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //邀请信息变化刷新页面
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData(){
        //初始化ListView
        inviteAdapter = new InviteAdapter(InviteActivity.this,mOnInviteListener);
        iv_invite.setAdapter(inviteAdapter);

        //刷新方法
        refresh();

        //注册邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(InviteActivity.this);
        mLBM.registerReceiver(receiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
    }

    private void refresh(){
        //获取数据库中的所有邀请信息
        List<InvationInfo> invationInfos = Model.getInstance().getDbManager().getInviteTableDao().getInvittations();

        //刷新适配器
        inviteAdapter.refresh(invationInfos);
    }

    private void initView(){
        iv_invite = (ListView)findViewById(R.id.iv_invite);
    }

    @Override
    protected void onDestroy() {
        //注册了广播一定要记得关闭
        mLBM.unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //发送刚刚返出InviteActivity 红点不要再显示了的广播
        mLBM.sendBroadcast(new Intent(Constant.LEONBROADCAST));
    }
}


