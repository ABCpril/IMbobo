package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.administrator.imbobo.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class AboutusActivity extends Activity implements View.OnClickListener{

    private WebView webview_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        EaseTitleBar titlebar_adbutus = (EaseTitleBar)findViewById(R.id.titlebar_adbutus);
        titlebar_adbutus.setLeftImageResource(R.drawable.back_button_selecter);
        titlebar_adbutus.setLeftLayoutClickListener(this);

        initView();
    }

    private void initView(){
        webview_about = findViewById(R.id.webview_about);


        //允许js运行
        WebSettings webSettings = webview_about.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //加载本地assets文件夹中自己写的H5页面
        webview_about.loadUrl("file:///android_asset/bootstrap/index.html");
    }

    @Override
    public void onClick(View v) {
        //这里只有一个返回按钮没有做判断以后功能增加一定要判断  if (v == titlebar_adbutus)
         finish();
    }
}
