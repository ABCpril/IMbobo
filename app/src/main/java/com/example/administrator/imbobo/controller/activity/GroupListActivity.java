package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.imbobo.controller.adapter.GroupListAdapter;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;


/**
 * Created by Leon on 2018/10/13
 * Functions: 群主列表页面
 */
public class GroupListActivity extends Activity {

    private ListView lv_grouplist;
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initListener(){
        //listview条目（item）的点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupListActivity.this,ChatActivity.class);

                //listview中添加了头布局 从1开始 避免数组越界
                if (position == 0){ return;}

                //传递会话类型(群聊)
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);

                //传群id
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

                startActivity(intent);
            }
        });

        //跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this,NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData(){

        groupListAdapter = new GroupListAdapter(this);

        lv_grouplist.setAdapter(groupListAdapter);

        //从环信服务器获取所有群的信息
        getGroupFromServer();
    }

    //从环信服务器获取所有群的信息
    private void getGroupFromServer(){
        //开辟子线程进行网络请求
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                    final List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this,"加载群信息成功",
                                    Toast.LENGTH_SHORT).show();

                        //groupListAdapter.refresh(mGroups); ←方法一
                            //刷新页面
                            refresh();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this,"加载群信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    //刷新
    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    /**初始化view*/
    private void initView(){
        lv_grouplist = (ListView)findViewById(R.id.lv_grouplist);

        //添加listview的头布局  ll_grouplist
        View headerView = View.inflate(this,R.layout.header_grouplist,null);
        lv_grouplist.addHeaderView(headerView);

        ll_grouplist = (LinearLayout)headerView.findViewById(R.id.ll_grouplist);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //activity 即将显示的时候（比如从建群的activity回来） 刷新页面
        refresh();
    }
}
