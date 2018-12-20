package com.example.administrator.imbobo.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.utils.LeAlertDialog;
import com.example.administrator.imbobo.utils.TestRegUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import me.leolin.shortcutbadger.ShortcutBadger;

public class RegistActivity extends Activity {

    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button bt_login_regist;

    /**应要求添加左上角返回键*/
    private EaseTitleBar titlebar_regist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //初始化子控件
        initView();

        //初始化监听
        initListener();
    }

    //初始化监听
    private void initListener(){
        //点击注册按钮的业务逻辑处理
        bt_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        //点击左上角的返回键的业务逻辑的处理
        titlebar_regist.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //注册的业务逻辑处理
    private void regist() {

        //1获取输入的用户名和密码
        final String registName = et_login_name.getText().toString();
        final String registPwd = et_login_pwd.getText().toString();

        //2校验输入的用户名和密码
        if (TestRegUtils.testTelephone(registName,RegistActivity.this) && TestRegUtils.testPwd(registPwd,
                RegistActivity.this)){

            //3去服务器注册账号
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器注册账号
                        EMClient.getInstance().createAccount(registName,registPwd);

                        //注册成功更新页面显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                successfulRegistration();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        //注册失败更新页面显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (e.toString().equals("com.hyphenate.exceptions.HyphenateException: User already exist")){
                                    Toast.makeText(RegistActivity.this,"该账号已注册",Toast.LENGTH_SHORT).show();
                                }else if (e.toString().equals("com.hyphenate.exceptions.HyphenateException: Registration failed.")){
                                    Toast.makeText(RegistActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(RegistActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                                }

                                Log.e("leon",String.valueOf(e.toString()));
                            }
                        });
                    }
                }
            });
        }

    }


    /**注册成功后的业务逻辑处理*/
    private void successfulRegistration(){

        final LeAlertDialog leAlertDialog = new LeAlertDialog(RegistActivity.this, R.style.dialog,
                "注册成功","去登陆",null,false);
                leAlertDialog.show();
        leAlertDialog.setCanceledOnTouchOutside(false);
        leAlertDialog.setClicklistener(new LeAlertDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {

                //跳转到登陆页面并且把用户的账号密码也带回去

                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("name",et_login_name.getText().toString());
                intent.putExtra("password",et_login_pwd.getText().toString());
                //设置结果码因为要返回参数
                setResult(RESULT_OK,intent);

                leAlertDialog.dismiss();

                //结束当前页面
                finish();
            }

            @Override
            public void doCancel() {
                leAlertDialog.dismiss();
            }
        });
    }


    /**初始化控件*/
    private void initView(){
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        //设置密码暗文显示
        et_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        bt_login_regist = (Button) findViewById(R.id.bt_login_regist);

        titlebar_regist = (EaseTitleBar)findViewById(R.id.titlebar_regist);
        titlebar_regist.setLeftImageResource(R.drawable.back_button_selecter);
    }
}
