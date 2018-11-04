package com.example.administrator.imbobo.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.controller.activity.LoginActivity;
import com.example.administrator.imbobo.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Leon on 2018/10/6
 * Functions: 设置页面
 */
public class SettingFragment extends Fragment {

    private Button bt_setting_out;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Fragment中获取上下文getActivity()
        View view = View.inflate(getActivity(),R.layout.fragment_setting,null);

        initView(view);

        return view;
    }

    private void initView(View view){
        bt_setting_out = (Button)view.findViewById(R.id.bt_setting_out);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //当activity创建好的时候处理业务逻辑
        initData();
    }

    private void initData(){
        //在button上显示当前用户昵称
        bt_setting_out.setText("退出登陆（"+ EMClient.getInstance().getCurrentUser()+"）");

        //退出登陆的逻辑处理
        bt_setting_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //在子线程中处理网络 退出发送请求给服务器
                Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //请求环信服务器退出登陆
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {//退出成功-发现没有网络也能退出成功

                                //关闭DBHelper
                                Model.getInstance().getDbManager().close();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //回到登陆页面
                                        Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_SHORT).show();
                                        //更新UI显示
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i,final String s) {//退出失败
                                //回到主线程更新UI
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //退出失败
                                        Toast.makeText(getActivity(),"退出失败:"+s,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {//退出中

                            }
                        });
                    }
                });
            }
        });
    }
}
