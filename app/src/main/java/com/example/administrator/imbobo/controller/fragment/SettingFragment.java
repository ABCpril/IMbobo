package com.example.administrator.imbobo.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.controller.activity.AboutusActivity;
import com.example.administrator.imbobo.controller.activity.LoginActivity;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.utils.LeAlertDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leon on 2018/10/6
 * Functions: 设置页面
 */
public class SettingFragment extends Fragment {

    /**
     * 分享按钮
     */
    private Button bt_share;

    /**
     * 退出登陆的按钮
     */
    private Button bt_setting_out;

    /**
     * 关于我们按钮
     */
    private Button bt_about;

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    private static final String APP_ID = "wxf6ef3c3deda3c461";

    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Fragment中获取上下文getActivity()
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);

        //初始化ui界面
        initView(view);

        //注册微信-为了分享到朋友圈
        regToWx();

        return view;
    }

    private void initView(View view) {
        bt_share = (Button) view.findViewById(R.id.bt_share);
        bt_setting_out = (Button) view.findViewById(R.id.bt_setting_out);
        bt_about = (Button) view.findViewById(R.id.bt_about);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //当activity创建好的时候处理业务逻辑
        initData();
    }

    private void initData() {
        //在button上显示当前用户昵称
        bt_setting_out.setText("退出登陆（" + EMClient.getInstance().getCurrentUser() + "）");

        //跳转到关于们页面-跳转到关于我们activity
        bt_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutusActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //处理分享到微信朋友圈的逻辑
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LeAlertDialog leAlertDialog = new LeAlertDialog(getContext(), R.style.dialog,
                        "share", "朋友圈", "好友", true);
                leAlertDialog.show();
                leAlertDialog.setCanceledOnTouchOutside(false);

                leAlertDialog.setClicklistener(new LeAlertDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {//分享到朋友圈

                        //开辟子线程做网络请求
                        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                 if (checkUrl("https://geekpark.site/html/pgy.html", 10000)){
                                     //判读getActivity()避免空指针
                                     if (getActivity() != null){
                                         getActivity().runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 //回主线程处理逻辑
                                                 //分享自己的网站地址到朋友圈
                                                 isCheckShere(true, false);
                                                 leAlertDialog.dismiss();
                                             }
                                         });
                                     }
                                 }else {
                                     //判读getActivity()避免空指针
                                     if (getActivity() != null){
                                         getActivity().runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 //回主线程处理逻辑
                                                 //分享github到朋友圈
                                                 isCheckShere(true, true);
                                                 leAlertDialog.dismiss();
                                             }
                                         });
                                     }
                                 }
                            }
                        });
                    }

                    @Override
                    public void doCancel() {//分享给好友

                        //开辟子线程做网络请求
                        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (checkUrl("https://geekpark.site/html/pgy.html", 10000)){
                                    //判读getActivity()避免空指针
                                    if (getActivity() != null){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //回主线程处理逻辑
                                                //分享自己的网站给好友
                                                isCheckShere(false, false);
                                                leAlertDialog.dismiss();
                                            }
                                        });
                                    }
                                }else {
                                    //判读getActivity()避免空指针
                                    if (getActivity() != null){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //回主线程处理逻辑
                                                //分享github给好友
                                                isCheckShere(false, true);
                                                leAlertDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

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
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                        //更新UI显示
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, final String s) {//退出失败
                                //回到主线程更新UI
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //退出失败
                                        Toast.makeText(getActivity(), "退出失败:" + s, Toast.LENGTH_SHORT).show();
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


    /**
     * 处理分享朋友圈的逻辑 这里有判断url是否有效
     */
    private void isCheckShere(boolean isCircleOfFriends, boolean isGithub) {
        //https://github.com/leonInShanghai
        if (isGithub == false) {//分享到github分享给个人和朋友圈
            if (isCircleOfFriends) {//分享给朋友圈
                shareUrl(1, getActivity(), "https://geekpark.site/html/pgy.html", "波波IM",
                        "刘氏祖传即时通讯软件，C/S架构，通过环信服务器、允许两人或多" +
                                "人使用网路即时的传递文字、语音、图片、交流的软件，开源项目不做任何商业用途");
            } else {//分享给个人
                shareUrl(0, getActivity(), "https://geekpark.site/html/pgy.html", "波波IM",
                        "刘氏祖传即时通讯软件，C/S架构，通过环信服务器、允许两人或多" +
                                "人使用网路即时的传递文字、语音、图片、交流的软件，开源项目不做任何商业用途");
            }
        } else {
            if (isCircleOfFriends) {//分享给朋友圈
                shareUrl(1, getActivity(), "https://github.com/leonInShanghai", "波波IM",
                        "刘氏祖传即时通讯软件，C/S架构，通过环信服务器、允许两人或多" +
                                "人使用网路即时的传递文字、语音、图片、交流的软件，开源项目不做任何商业用途");
            } else {//分享给个人
                shareUrl(0, getActivity(), "https://github.com/leonInShanghai", "波波IM",
                        "刘氏祖传即时通讯软件，C/S架构，通过环信服务器、允许两人或多" +
                                "人使用网路即时的传递文字、语音、图片、交流的软件，开源项目不做任何商业用途");
            }
        }
    }


    /**
     * 注册到微信-分享到朋友圈的方法
     */
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(getActivity(), APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(APP_ID);
    }


    //flag用来判断是分享到微信好友还是分享到微信朋友圈，
    //0代表分享到微信好友，1代表分享到朋友圈
    private void shareUrl(int flag, Context context, String url, String title, String descroption) {
        //初始化一个WXWebpageObject填写url
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;
        //用WXWebpageObject对象初始化一个WXMediaMessage，天下标题，描述
        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = descroption;
        //这块需要注意，图片的像素千万不要太大，不然的话会调不起来微信分享，
        //我在做的时候和我们这的UIMM说随便给我一张图，她给了我一张1024*1024的图片
        //当时也不知道什么原因，后来在我的机智之下换了一张像素小一点的图片好了！
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.easemob);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }


    /**
     * 判断url是否可达 - 涉及到网络请求在子线程调用
     */
    public Boolean checkUrl(final String address, final int waitMilliSecond) {

        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(waitMilliSecond);
            conn.setReadTimeout(waitMilliSecond);

            //HTTP connect
            try {
                conn.connect();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            int code = conn.getResponseCode();
            if ((code >= 100) && (code < 400)) {
                return true;
            }

             return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}




