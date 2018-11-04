package com.example.administrator.imbobo.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.imbobo.controller.activity.AddContactActivity;
import com.example.administrator.imbobo.utils.Constant;
import com.example.administrator.imbobo.utils.SpUtils;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.controller.activity.InviteActivity;

/**
 * Created by Leon on 2018/10/6
 * Functions: 联系人列表页面  EaseContactListFragment 环信集成的
 */
public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private LinearLayout ll_contact_invite;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
        }
    };

    private BroadcastReceiver leReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //不要红点显示刚从InviteActivity（邀请信息列表）回来不要显示
            iv_contact_red.setVisibility(View.GONE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,false);
        }
    };

    //initView() 实现EaseContactListFragment中的抽象方法
    @Override
    protected void initView() {
        super.initView();

        //布局显示右上角+
        titleBar.setRightImageResource(R.drawable.em_add);

        //头布局添加
        View headerView = View.inflate(getActivity(),R.layout.header_fragment_contact,null);
        listView.addHeaderView(headerView);

        //获取红点对象（imageview）
        iv_contact_red = (ImageView)headerView.findViewById(R.id.iv_contact_red);

        //获取邀请信息条目的点击事件
        ll_contact_invite = headerView.findViewById(R.id.ll_contact_invite);

    }

    //setUpView() 实现EaseContactListFragment中的抽象方法
    @Override
    protected void setUpView() {
        super.setUpView();

        //添加按钮的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddContactActivity.class);
                startActivity(intent);
            }
        });

        //初始化红点的显示
        boolean isNewInvite = SpUtils.getInstance().getBooleanPre(SpUtils.IS_NEW_INVITE,false);
        iv_contact_red.setVisibility(isNewInvite?View.VISIBLE:View.GONE);

        //邀请信息条目的点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //红点处理（隐藏掉）
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,false);

                //跳转到邀请信息处理列表页面
                Intent intent = new Intent(getActivity(),InviteActivity.class);
                startActivity(intent);
            }
        });

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        //ContactInviteChangeReceiver - receiver
        mLBM.registerReceiver(receiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(leReceiver,new IntentFilter(Constant.LEONBROADCAST));
    }

    @Override
    public void onDestroy() {

        //注册的广播一定要关闭掉
        mLBM.unregisterReceiver(receiver);
        mLBM.unregisterReceiver(leReceiver);
        super.onDestroy();
    }
}
