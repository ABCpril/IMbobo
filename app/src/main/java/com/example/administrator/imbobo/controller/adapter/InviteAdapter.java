package com.example.administrator.imbobo.controller.adapter;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.imbobo.R;

import com.example.administrator.imbobo.model.bean.InvationInfo;
import com.example.administrator.imbobo.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/4.
 * Functions: InviteActivity(邀请信息) listview 适配器
 */
public class InviteAdapter extends BaseAdapter {

    private Context mContext;

    private List<InvationInfo> mInvationInfos = new ArrayList<>();

    private OnInviteListener mOnInviteListener;

    private InvationInfo invationInfo;

    public InviteAdapter(Context context,OnInviteListener onInviteListener){
        this.mContext = context;
        this.mOnInviteListener = onInviteListener;
    }

    //刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos){
        //注意这里要>=0
        if (invationInfos != null && invationInfos.size() >= 0){
            mInvationInfos.clear();//每次进来之前先清空一下

            mInvationInfos.addAll(invationInfos);

            //通知刷新页面 安卓系统方法
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvationInfos == null ? 0 : mInvationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1.获取创建一个ViewHolder
        ViewHodler hodler = null;
        if (convertView == null){
            hodler = new ViewHodler();

            convertView = View.inflate(mContext,R.layout.item_invite,null);

            hodler.name = (TextView)convertView.findViewById(R.id.tv_invite_name);
            hodler.reason = (TextView)convertView.findViewById(R.id.tv_invite_reason);
            hodler.accept = (Button)convertView.findViewById(R.id.bt_invite_accept);
            hodler.reject = (Button)convertView.findViewById(R.id.bt_invite_reject);

            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler)convertView.getTag();
        }

        //2.获取当前item数据
        invationInfo = mInvationInfos.get(position);

        //3.展示数据显示当前item数据
        UserInfo user = invationInfo.getUser();

        if (user != null){//联系人
            //获取到联系人的名称展示
            hodler.name.setText(invationInfo.getUser().getName());

            hodler.accept.setVisibility(View.GONE);
            hodler.reject.setVisibility(View.GONE);

            //（加好友的）原因
            if (invationInfo.getStatus() == InvationInfo.InvitationStatus.NEW_INVITE){
                //新的邀请
                if (invationInfo.getReason() == null){
                    hodler.reason.setText("添加好友");
                }else {
                    hodler.reason.setText(invationInfo.getReason());
                }

                hodler.accept.setVisibility(View.VISIBLE);
                hodler.reject.setVisibility(View.VISIBLE);

            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT){
                //接受邀请
                if (invationInfo.getReason() == null){
                    hodler.reason.setText("接受邀请");
                }else {
                    hodler.reason.setText(invationInfo.getReason());
                }
            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER){
                //邀请被接受
                if (invationInfo.getReason() == null){
                    hodler.reason.setText("邀请被接受");
                }else {
                    hodler.reason.setText(invationInfo.getReason());
                }
            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.REFUSE_AN_INVITATION){
                //邀请被接受
                if (invationInfo.getReason() == null){
                    hodler.reason.setText("邀请被拒绝");
                }else {
                    hodler.reason.setText(invationInfo.getReason());
                }
            }
            //-----------------------------leon----------------------------------

            //(同意)按钮点击事件的处理
            hodler.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invationInfo);
                }
            });
            //(拒绝)按钮点击事件的处理
            hodler.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invationInfo);
                }
            });

        }else {//群组

        }

        //返回View
        return convertView;
    }

    /**内部类 ViewHodler*/
    private class ViewHodler{
        private TextView name;
        private TextView reason;

        private Button accept;
        private Button reject;
    }

    public interface OnInviteListener{
        //联系人接受按钮的点击事件
        void onAccept(InvationInfo invationInfo);

        //联系人拒绝按钮的点击事件
        void onReject(InvationInfo invationInfo);
    }
}
