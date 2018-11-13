package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.example.administrator.imbobo.controller.adapter.GroupListAdapter;

import com.example.administrator.imbobo.R;


/**
 * Created by Leon on 2018/10/13
 * Functions: 群主列表页面
 */
public class GroupListActivity extends Activity {

    private ListView lv_grouplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();
    }

    private void initData(){

        GroupListAdapter groupListAdapter = new GroupListAdapter(this);

        lv_grouplist.setAdapter(groupListAdapter);
    }

    /**初始化view*/
    private void initView(){
        lv_grouplist = (ListView)findViewById(R.id.lv_grouplist);

        //添加listview的头布局  ll_grouplist
        View headerView = View.inflate(this,R.layout.header_grouplist,null);
        lv_grouplist.addHeaderView(headerView);
    }

}
