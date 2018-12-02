package com.example.administrator.imbobo.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.example.administrator.imbobo.controller.adapter.GroupDetailAdapte;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/24
 * Functions: 群详情页面
 */
public class GroupDetailActivity extends Activity {

    /**网格视图*/
    private GridView gv_groupdetail;

    /**退群的按钮*/
    private Button bt_groupdetail_out;

    /**群组类（环信easeUI定义的类*/
    private EMGroup mGroup;

    //群成員集合
    private List<UserInfo> mUsers;

    /**GridView 適配器*/
    private GroupDetailAdapte groupDetailAdapte;


    /**處理 增加 和刪除群成員的業務邏輯的接口*/
    private GroupDetailAdapte.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapte.
            OnGroupDetailListener() {
        //添加群成員
        @Override
        public void onAddMembers() {
            //跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this,PickContactActivity.class);

            //传递群id
            intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());

            //带返回参数的跳转
            startActivityForResult(intent,2);
        }

        @Override
        public void onDeleteMember(final UserInfo user) {
            //刪除群成員-開闢子綫程做網絡請求
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //從環信服務器中刪除此成員
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(),user.getHxid());

                        //更新頁面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"刪除成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"刪除失敗"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grour_detail);

        initView();

        /**获取传递过来的环信id*/
        getData();

        initData();

        initListener();
    }

    private void initListener(){
//        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//
//                        //判断当前是否是删除模式,如果是删除模式
//                        if (groupDetailAdapte.ismIsDeleteModle()){
//                            //切换为非删除模式
//                            groupDetailAdapte.setmIsDeleteModle(false);
//
//                            //刷新页面-适配器刷新
//                            groupDetailAdapte.notifyDataSetChanged();
//                        }
//
//                        break;
//                }
//                return false;
//            }
//        });

        //----------------------------leon------------------------------
        gv_groupdetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //判断当前是否是删除模式,如果是删除模式
                if (groupDetailAdapte.ismIsDeleteModle()) {
                    //切换为非删除模式
                    groupDetailAdapte.setmIsDeleteModle(false);

                    //开辟子线程做网络请求
                    Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //把username从群聊里删除
                            try {
                                EMClient.getInstance().groupManager().removeUserFromGroup(mGroup
                                        .getGroupId(), mUsers.get(position).getHxid());
                                //刷新页面 删除成功-回到主线程
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //刷新页面-适配器刷新
                                        //groupDetailAdapte.notifyDataSetChanged();

                                        //刷新页面-传递数据刷新-下面这个方法会再到环信服务器上获取群成员
                                        getMembersFromHxServer();

                                        Toast.makeText(GroupDetailActivity.this,"移除成功",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                //删除失败提示用户-回到主线程
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //刷新页面-适配器刷新
                                        Toast.makeText(GroupDetailActivity.this,"移除失败"+e.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        //----------------------------leon------------------------------
    }

    //添加群成员时有一个返回参数的跳转这里要重写这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            //这里和PickContactActivity返回参数时的key一样 获取返回的群成员信息
            final String[] memberses = data.getStringArrayExtra("members");

            //开辟子线程-和环信服务器交互
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器，发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),memberses);

                        //更新页面-切换到主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"发送邀请成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        //更新页面-切换到主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"发送邀请失败"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void initData(){
        //初始化button显示
        initButtonDisplay();

        //初始化gridview
        initGridView();

        //從環信服務器獲取所有的群成員
        getMembersFromHxServer();
    }

    /**從環信服務器獲取所有的群成員*/
    private void getMembersFromHxServer(){
        //開闢子綫程做網絡請求
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                   //從環信服務器獲取所有的群成員信息
                    Log.e("leon",mGroup.getGroupId());
                   EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                   List<String> menbers = emGroup.getMembers();


                   if (menbers != null && menbers.size() >= 0){

                       mUsers = new ArrayList<>();

                       //轉換
                       for (String menber : menbers){
                           UserInfo userInfo = new UserInfo(menber);
                           mUsers.add(userInfo);
                       }
                   }

                   //更新頁面-回到主綫程
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           //刷新適配器
                           groupDetailAdapte.refresh(mUsers);
                       }
                   });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //更新頁面-回到主綫程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           Toast.makeText(GroupDetailActivity.this,"獲取群信息失敗"+e.toString(),
                                   Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //初始化gridview
    private void initGridView(){

        /**
         *  群主有邀請人入群的權限
         *  另外當 群組是公開的時候群成員也有邀請人的權限
         */
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) ||
                mGroup.isPublic();

        /**
         * 第一個參數是上下文
         * 第二個參數是標記 是 群主/群員
         */
        groupDetailAdapte = new GroupDetailAdapte(this,isCanModify,
                mOnGroupDetailListener);

        //GridView 設置 adapte
        gv_groupdetail.setAdapter(groupDetailAdapte);
    }

    //初始化button显示 - 退群/解散群
    private void initButtonDisplay(){

        //判断当前用户是否为群组
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())){//群主
            bt_groupdetail_out.setText("解散群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //开辟子线程-去环信服务器解散群
                    Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送一个退群的广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"解散群成功",
                                                Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });

                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"解散群失败"+e.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }else {//群成员
            bt_groupdetail_out.setText("退群");

            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //开辟子线程做服务器请求操作
                    Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //告诉环信服务器群成员要退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                //发送一个退群的广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"退群成功",
                                                Toast.LENGTH_SHORT).show();

                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"退群失败"+e.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    /**发送退群和解散群广播*/
    private void  exitGroupBroatCast(){
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());

        mLBM.sendBroadcast(intent);
    }

    /**获取传递过来的数据最终得到一个 mGroup*/
    private void getData(){
       String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

       if (groupId == null){
           return;
       }else {
           mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
       }
    }

    private void initView(){
        gv_groupdetail = (GridView)findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = (Button)findViewById(R.id.bt_groupdetail_out);
    }
}
