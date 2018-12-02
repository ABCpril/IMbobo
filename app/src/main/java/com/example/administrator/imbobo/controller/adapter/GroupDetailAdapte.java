package com.example.administrator.imbobo.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2018/11/25.
 * Functions: GroupDetailActivity 中的 GridView 的適配器
 */
public class GroupDetailAdapte extends BaseAdapter {

    private Context mContext;

    /*是否允許添加和刪除群成員*/
    private boolean mIsCanModify;

    /**GridView 的數據源*/
    private List<UserInfo> mUsers = new ArrayList<>();

    /**刪除模式 true：表示可以刪除 false：表示不可以刪除*/
    private boolean mIsDeleteModle;

    /**自定義接口 實現添加群成員和 刪除群成員的業務邏輯*/
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapte(Context context,boolean isCanModify,OnGroupDetailListener onGroupDetailListener) {
            this.mContext = context;
            this.mIsCanModify = isCanModify;
            this.mOnGroupDetailListener = onGroupDetailListener;
    }

    //获取当前的删除模式
    public boolean ismIsDeleteModle() {
        return mIsDeleteModle;
    }

    //设置档期的删除模式
    public void setmIsDeleteModle(boolean mIsDeleteModle) {
        this.mIsDeleteModle = mIsDeleteModle;
    }

    //刷新方法-用於刷新和傳參
    public void refresh(List<UserInfo> users){
        if (users != null && users.size() >= 0){
            //1.清空原有的數據
            mUsers.clear();

            //2.添加加號和減號
            initUsers();

            //3.添加數據 - index：0 是爲了把加號 減號 都擠到後面
            mUsers.addAll(0,users);
        }

        //刷新頁面
        notifyDataSetChanged();
    }

    //添加加號和減號
    private void initUsers(){
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        //添加 加號 和 減號讓它們在數組集合的後邊
        mUsers.add(delete);
        mUsers.add(0,add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1.獲取或創建ViewHolder
        ViewHolder holder = null;

        if (convertView == null){
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_groupdetail,null);

            holder.photo = (ImageView) convertView.findViewById(R.id.iv_group_detail_photo);
            holder.delete = (ImageView)convertView.findViewById(R.id.iv_group_detatil_delete);
            holder.name = (TextView)convertView.findViewById(R.id.tv_group_detail_name);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //2.獲取當前item數據
        final UserInfo userInfo = mUsers.get(position);

        //3.顯示數據
        if (mIsCanModify){//群主 或 公開群權限裏的群成員
            //佈局的處理
            if (position == getCount() - 1){//減號的位置特殊處理
                //刪除模式判斷
                if (mIsDeleteModle){
                    convertView.setVisibility(View.GONE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else if (position == getCount() -2){//加號的位置特殊處理
                //刪除模式判斷（增刪模式）
                if (mIsDeleteModle){
                    convertView.setVisibility(View.GONE);
                }else {
                    convertView.setVisibility(View.VISIBLE);

                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else {//群成員
                convertView.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);

                //名稱 由於我這裏昵稱是手機號太長，所以只顯示后四位
                if (userInfo.getName() != null){
                    String name = userInfo.getName().substring(userInfo.getName().length()-4,userInfo.getName().length());
                    holder.name.setText(name);
                }
                holder.photo.setImageResource(R.drawable.em_default_avatar);

                if (mIsDeleteModle){//刪除（增刪）模式
                    holder.delete.setVisibility(View.VISIBLE);
                }else {//非 刪除（增刪）模式
                    holder.delete.setVisibility(View.GONE);
                }
            }

            //點擊事件的處理
            if (position == getCount() - 1){//減號的位置
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteModle){
                            mIsDeleteModle = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if (position == getCount() - 2){//加號的位置
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });

            }else {//群成員的位置 delete 點擊事件的處理
             holder.delete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                    mOnGroupDetailListener.onDeleteMember(userInfo);
                 }
             });
          }
        }else{//普通的群成員
            if (position == getCount() - 1 || position == getCount() - 2){//減號和加號的位置隱藏掉
                convertView.setVisibility(View.GONE);
            }else{
                convertView.setVisibility(View.VISIBLE);

                //名稱 由於我這裏昵稱是手機號太長，所以只顯示后四位
                if (userInfo.getName() != null){
                    String name = userInfo.getName().substring(userInfo.getName().length()-4,userInfo.getName().length());
                    holder.name.setText(name);
                }

                //頭像
                holder.photo.setImageResource(R.drawable.em_default_avatar);

                //刪除按鈕 - 普通成員沒有權力刪除和邀請群成員
                holder.delete.setVisibility(View.GONE);
            }
        }

        //4.返回View
        return convertView;
    }

    /**内部類 ViewHolder*/
    private class ViewHolder{
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }

    /**自定義接口 誰實現誰處理 刪除群成員 和添加群成員的業務邏輯*/
    public interface OnGroupDetailListener{
        /**添加群成員方法*/
        void onAddMembers();

        /**刪除群成員方法*/
        void onDeleteMember(UserInfo user);
    }
}
