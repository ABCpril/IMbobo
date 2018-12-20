package com.example.administrator.imbobo.wxapi;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.imbobo.R;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Leon on 2018/12/11.
 * Functions:  分享到微信朋友圈的activity
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler, View.OnClickListener {

    private TextView return_text;

    private IWXAPI api;

    private ImageView return_logo;

    /**imageview 动画的处理*/
    private static final int RETURNANIMATION = 98;

    /**对titleBar点击事件的处理*/
    private EaseTitleBar titlebar_share;


    //动画的处理
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case RETURNANIMATION:
                    Animation tipAnim1;//正/倒转动画
                    Animation tipAnim2;//正/倒转动画
                    if ((Math.random()>0.5?1:0) == 0){
                        tipAnim1 = AnimationUtils.loadAnimation(WXEntryActivity.this,R.anim.big_tip_animation);
                        return_logo.startAnimation(tipAnim1);
                    }else {
                        tipAnim2 = AnimationUtils.loadAnimation(WXEntryActivity.this,R.anim.big_back_animation);
                        return_logo.startAnimation(tipAnim2);
                    }
                    sendEmptyMessageDelayed(RETURNANIMATION,8000);
                    break;

            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        //放在这里是因为放在onCreate中不回调onResp方法
        api = WXAPIFactory.createWXAPI(this,"wxf6ef3c3deda3c461",false);
        //api.registerApp("wx469c2fa6cc3517f0");在AiGameView中已经注册过了
        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        return_text = (TextView)findViewById(R.id.return_text);
        return_logo = (ImageView)findViewById(R.id.return_logo);
        titlebar_share = (EaseTitleBar)findViewById(R.id.titlebar_share);

        return_logo.setOnClickListener(this);
        titlebar_share.setLeftImageResource(R.drawable.back_button_selecter);
        titlebar_share.setLeftLayoutClickListener(this);

        //設置自定義字體 字體包在assets/font/test.ttf
        return_text.setTypeface(Typeface.createFromAsset(getAssets(),"font/test.ttf"));

        //开始动画
        mHandler.sendEmptyMessage(RETURNANIMATION);
    }

    /**
     * return_imageview 点击事件的处理-返回游戏
     */
    @Override
    public void onClick(View view) {
        if (view == return_logo){
            finish();
        } else if (view == titlebar_share.getLeftLayout()){
            finish();
        }
    }


    @Override
    public void onResp(BaseResp resp) {

        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_OK:
                return_text.setText("欢迎回来");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                return_text.setText("分享取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                return_text.setText("分享被拒绝");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                return_text.setText("不支持错误");
                break;
            default:
                return_text.setText("分享返回");
                break;
        }


    }

    //这个方法目前什么都不用写能满足需求，没有这个方法会报错
    @Override
    public void onReq(BaseReq baseReq) {

    }



    //控制器销毁的时候消除handler 合理管理内存
    @Override
    protected void onDestroy() {

        if (mHandler != null){
            mHandler.removeMessages(RETURNANIMATION);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        super.onDestroy();
    }


}
