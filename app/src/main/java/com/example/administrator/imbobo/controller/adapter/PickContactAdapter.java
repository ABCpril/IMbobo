package com.example.administrator.imbobo.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.bean.PickContactInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/17.
 * Functions:（创建群组时）选择人页面 的list view的适配器
 */
public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks; //= new ArrayList<>();

    public PickContactAdapter(Context context,List<PickContactInfo> picks) {
        this.mContext = context;

        this.mPicks = picks;

//        if (picks != null && picks.size() >= 0){
//            this.mPicks.clear();
//            this.mPicks.addAll(picks);
//        }
    }


    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1.创建或获取viewholder
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_pick, null);

            holder.cb = (CheckBox)convertView.findViewById(R.id.cb_pick);
            holder.tv_name = (TextView)convertView.findViewById(R.id.tv_pick_name);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //2.获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        //3.显示数据
        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());

        //4.返回convertView
        return convertView;
    }

    /**获取选择的联系人*/
    public List<String> getPickContacts(){

        List<String> picks = new ArrayList<>();

        for (PickContactInfo pick : mPicks){

            //判断是否选中
            if (pick.isChecked()){
                picks.add(pick.getUser().getName());
            }
        }

        return picks;
    }

    private class ViewHolder{
        private CheckBox cb;
        private TextView tv_name;
    }
}
