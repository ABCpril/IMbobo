package com.example.administrator.imbobo.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.imbobo.controller.activity.AddContactActivity;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.Constant;
import com.example.administrator.imbobo.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.controller.activity.InviteActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.example.administrator.imbobo.controller.activity.ChatActivity;
import com.example.administrator.imbobo.controller.activity.GroupListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leon on 2018/10/6
 * Functions: 联系人列表页面  EaseContactListFragment 环信集成的
 */
public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private LinearLayout ll_contact_invite;
    private String mHxid;

    //接收到广播的处理
    private BroadcastReceiver contactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refreshContact();
        }
    };

    //接收到广播的处理
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
        }
    };

    //接收到广播的处理
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

        //设置listview条目的点击事件easeUI中的方法 方法2：listView.setOnItemClickListener();
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                if (user == null){
                    return;
                }

                Intent intent = new Intent(getActivity(),ChatActivity.class);

                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                startActivity(intent);
            }
        });

        //跳转到群主列表页面
        LinearLayout ll_contact_group = (LinearLayout)headerView.findViewById(R.id.ll_contact_group);
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GroupListActivity.class);

                //

                //跳转到群组activity
                startActivity(intent);
            }
        });
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
        mLBM.registerReceiver(contactChangeReceiver,new IntentFilter(Constant.CONTACT_CHANGED));

        //从环信服务器上获取所有的联系人信息
        getContactFromHxServer();

        //绑定listView和contextmenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //获取环信id先获取postion
        int postion = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(postion);
        //username 就是 环信id
        mHxid = easeUser.getUsername();
        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //R.id.contact_delete 之前写了R.menu.delete 产生了bug
        if (item.getItemId() == R.id.contact_delete){
            //删除选中的联系人操作
            deleteContact();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    //删除选中的联系人操作
    private void deleteContact(){

        //开辟子线程进行网络请求
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器中删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    //本地数据库的更新
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    //避免空指针异常
                    if ( getActivity() == null){ return; }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toast提示
                            Toast.makeText(getActivity(),"删除"+mHxid+"成功",Toast.LENGTH_SHORT).show();

                            //刷新页面
                            refreshContact();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //避免空指针异常
                    if ( getActivity() == null){ return; }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toast提示
                            Toast.makeText(getActivity(),"删除失败: "+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    //从环信服务器上获取所有的联系人信息
    private void  getContactFromHxServer(){
        //开辟子线程做联网操作
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取到所有的好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    //校验- 注意这里用的是>= 0将来要注意异常
                    if (hxids != null && hxids.size() >= 0){

                        //创建一个UserInfo的集合
                        List<UserInfo> contacts = new ArrayList<>();

                        //转换集合
                        for (String hxid : hxids){
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }

                        //保存好友信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts,
                                true);

                        //避免下面空指针异常（刷新页面）
                        if (getActivity() == null){ return; }
                        //刷新页面-回到主线程操作
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新页面的方法
                                refreshContact();
                            }
                        });

                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //刷新页面的方法
    private void  refreshContact(){

        //获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getCountacts();

        //校验获取到的数据 注意这个地方用的是>= 0 可能会有异常
        if (contacts != null && contacts.size() >= 0){

            // 设置数据 Map<String, EaseUser> contactsMap
            Map<String,EaseUser> contactsMap = new HashMap<>();

            //数据转换
            for (UserInfo contact : contacts){
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactsMap.put(contact.getHxid(),easeUser);
            }

            //设置联系人Map，键是hyphenate id。
            setContactsMap(contactsMap);

            //刷新页面
            refresh();

        }
    }

    @Override
    public void onDestroy() {

        //注册的广播一定要关闭掉
        mLBM.unregisterReceiver(receiver);
        mLBM.unregisterReceiver(leReceiver);
        mLBM.unregisterReceiver(contactChangeReceiver);
        super.onDestroy();
    }
}
