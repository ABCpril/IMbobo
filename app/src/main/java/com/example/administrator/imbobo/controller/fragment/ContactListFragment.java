package com.example.administrator.imbobo.controller.fragment;

import android.content.Intent;
import android.view.View;

import com.example.administrator.imbobo.controller.activity.AddContactActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.example.administrator.imbobo.R;

/**
 * Created by Leon on 2018/10/6
 * Functions: 联系人列表页面  EaseContactListFragment 环信集成的
 */
public class ContactListFragment extends EaseContactListFragment {

    //initView() 实现EaseContactListFragment中的抽象方法
    @Override
    protected void initView() {
        super.initView();

        //布局显示右上角+
        titleBar.setRightImageResource(R.drawable.em_add);

        //头布局添加
        View headerView = View.inflate(getActivity(),R.layout.header_fragment_contact,null);
        listView.addHeaderView(headerView);
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
    }
}
