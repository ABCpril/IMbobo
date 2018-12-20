package com.example.administrator.imbobo.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Leon on 2018/11/16
 * Functions: 创建新群的activity
 */
public class NewGroupActivity extends Activity {

    /**新建的群名称*/
    private EditText et_newgroup_name;

    /**群组的简介*/
    private EditText et_newgroup_desc;

    /**是否公开*/
    private CheckBox cb_newgroup_public;

    /**是否开放群邀请*/
    private CheckBox cb_newgroup_invite;

    /**创建按钮*/
    private Button bt_newgroup_create;

    /**应要求添加左上角返回键*/
    private EaseTitleBar titlebar_newgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();

        initListener();
    }

    private void initListener(){
        //创建按钮的点击事件的处理
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(et_newgroup_name.getText().toString())){
                    //跳转到选择联系人页面
                    Intent intent = new Intent(NewGroupActivity.this,PickContactActivity.class);

                    //跳转activity回带参数的跳转 需要重写onActivityResult
                    startActivityForResult(intent,1);
                }else {
                    Toast.makeText(NewGroupActivity.this,"请设置群名称",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //应要求添加左上角的返回按钮-点击事件的处理
        titlebar_newgroup.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //成功获取到联系人
        if (resultCode == RESULT_OK){
            //创建群组
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    //创建群组
    private void createGroup(final String[] memberses) {

        //群名称
        final String groupName = et_newgroup_name.getText().toString();


        //群描述
        final String groupDesc = et_newgroup_desc.getText().toString();

        //开辟子线程做网络请求
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                EMGroupOptions options = new EMGroupOptions();

                options.maxUsers = 200;//群最多人容纳多少人
                options.inviteNeedConfirm = true;//拉人要经过同意新SDK默认不需要
                EMGroupManager.EMGroupStyle groupStyle = null;//群样式（风格）

                if (cb_newgroup_public.isChecked()){//是否公开为 是
                    if (cb_newgroup_invite.isChecked()){//开放群邀请为 是
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {//开放群邀请为 否
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else {//是否公开为 否
                    if (cb_newgroup_invite.isChecked()){//开放群邀请为 是
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else {//开放群邀请为 否
                        //EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                options.style = groupStyle;//创建群的类型

                try {
                    /**
                     *  去环信服务器创建群
                     *  参数① String var1, 群名称；
                     *  参数② String var2, 群描述（群简介）
                     *  参数③ String[] var3, 群成员
                     *  参数④ String var4, 创建群的原因
                     *  参数⑤ EMGroupOptions var5 参数设置
                     */
                    EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,memberses,"申请加入群"
                    ,options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //创建群成功提示用户
                            Toast.makeText(NewGroupActivity.this,"创建群成功",Toast.LENGTH_SHORT).show();

                            //结束当前页面
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //创建群失败提示用户
                            Toast.makeText(NewGroupActivity.this,"创建群失败: "+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //初始化视图
    private void initView(){
        et_newgroup_name = (EditText)findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText)findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = (CheckBox)findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = (CheckBox)findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = (Button)findViewById(R.id.bt_newgroup_create);
        titlebar_newgroup = (EaseTitleBar)findViewById(R.id.titlebar_newgroup);
        titlebar_newgroup.setLeftImageResource(R.drawable.back_button_selecter);
    }
}
