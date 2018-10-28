package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.TestRegUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Leon on 2018/10/5
 * Functions: 登陆界面
*/
public class LoginActivity extends Activity {


    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button bt_login_regist;
    private Button bt_login_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        initView();

        //初始化监听
        initListener();

    }

    private void initListener(){
        //注册按钮的点击事件处理
        bt_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });

        //登陆按钮的点击事件的处理
        bt_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    //登陆按钮的业务逻辑处理
    private void login(){
        //1获取输入的用户名和密码
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();


        //2校验输入的用户名和密码
        if (TestRegUtils.testTelephone(loginName,LoginActivity.this) && TestRegUtils.
                testPwd(loginPwd, LoginActivity.this)) {

            //3登陆逻辑的处理
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去环信服务器登陆
                    EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {

                        //登陆成功后的处理
                        @Override
                        public void onSuccess() {
                            //对模型层数据的处理
                            Model.getInstance().loginSussess();

                            //保存用户账号信息到本地数据库
                            Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                            //切换到UI线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //提示登陆成功
                                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                                    //跳转到主页面
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        //登陆失败的处理
                        @Override
                        public void onError(int i,final String s) {
                            //将线程切换到主线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //提示用户登陆失败
                                   // Log.e("leon",s);
                                    if(s.equals("User dosn't exist")){
                                        Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                                    }else if(s.equals("Username or password is wrong")) {
                                        Toast.makeText(LoginActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                                    }else if (s.equals("Network isn't avaliable")){
                                        Toast.makeText(LoginActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.e("登陆失败:",s);
                                        Toast.makeText(LoginActivity.this,"登陆失败"+s,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        //登陆过程中的处理
                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            });
        }
    }

    //注册的业务逻辑处理
    private void regist() {

        //1获取输入的用户名和密码
        final String registName = et_login_name.getText().toString();
        final String registPwd = et_login_pwd.getText().toString();

        //2校验输入的用户名和密码
        if (TestRegUtils.testTelephone(registName,LoginActivity.this) && TestRegUtils.testPwd(registPwd,
                LoginActivity.this)){

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
                                Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        //注册失败更新页面显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (e.toString().equals("com.hyphenate.exceptions.HyphenateException: User already exist")){
                                    Toast.makeText(LoginActivity.this,"该账号已注册",Toast.LENGTH_SHORT).show();
                                }else if (e.toString().equals("com.hyphenate.exceptions.HyphenateException: Registration failed.")){
                                    Toast.makeText(LoginActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(LoginActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                                }

                                Log.e("leon",String.valueOf(e.toString()));
                            }
                        });
                    }
                }
            });
        }

    }

    /**初始化页面*/
    private void initView() {
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        //设置密码暗文显示
        et_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        bt_login_regist = (Button) findViewById(R.id.bt_login_regist);
        bt_login_login = (Button) findViewById(R.id.bt_login_login);
    }

}
