package com.example.administrator.imbobo.controller.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.imbobo.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/10/13
 * Functions: 群组页面listview 适配器
 */
public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> mGroups = new ArrayList<>();

    public GroupListAdapter(Context context) {
        this.mContext = context;
    }

    //刷新方法
    public void refresh(List<EMGroup> groups){
        //数据校验 >=0 时也要刷新（比如原来有群后面没有了 0 的时候也要刷新）
        if (groups != null && groups.size() >= 0){
            //先清空数据再添加数据
            mGroups.clear();

            mGroups.addAll(groups);

            //刷新页面安卓的方法
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1.创建或获取viewholder
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grouplist,null);

            holder.name = (TextView) convertView.findViewById(R.id.tv_grouplist_name);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //2.获取当前item数据
        EMGroup emGroup = mGroups.get(position);

        //3.展示数据
        holder.name.setText(emGroup.getGroupName());

        //4.返回数据
        return convertView;
    }

    private class ViewHolder{
        TextView name;
    }
}
