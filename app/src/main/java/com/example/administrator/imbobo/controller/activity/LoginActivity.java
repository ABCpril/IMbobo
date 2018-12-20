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
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.service.MediaService;
import com.example.administrator.imbobo.utils.LECustomProgressDialog;
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
    private TextView to_regist;
    private Button bt_login_login;
    private LECustomProgressDialog progressDialog;

    /**产生加载中动画的全局变量……*/
    private int i = 0;

    //自造加载中的动画
    String[] lodingStrs = {"加载中.","加载中..","加载中...","加载中....",
            "加载中......","加载中........","加载中"};



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
        to_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);

                /**跳转activity回带参数的跳转 需要重写onActivityResult*/
                startActivityForResult(intent,2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //成功获取到联系人
        if (resultCode == RESULT_OK){
            //自动填写用户在注册页面填写的账号密码
            et_login_name.setText(data.getStringExtra("name"));
            et_login_pwd.setText(data.getStringExtra("password"));
        }
    }

    //登陆按钮的业务逻辑处理
    private void login(){

        //1获取输入的用户名和密码
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();


        //2校验输入的用户名和密码
        if (TestRegUtils.testTelephone(loginName,LoginActivity.this) && TestRegUtils.
                testPwd(loginPwd, LoginActivity.this)) {

            progressDialog.show();

            progressDialog.setMessage(lodingStrs[++i % 8]);


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
                            Model.getInstance().loginSussess(new UserInfo(loginName));

                            //保存用户账号信息到本地数据库
                            Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                            //切换到UI线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //提示登陆成功
                                    progressDialog.setMessage("登陆成功");
                                    progressDialog.dismiss();
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

                                    progressDialog.setMessage("登陆失败");
                                    progressDialog.dismiss();

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
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"登陆失败"+s,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        //登陆过程中的处理
                        @Override
                        public void onProgress(int i, String s) {
                            progressDialog.show();
                            progressDialog.setProgress(i);
                            progressDialog.setMessage(s);
                        }
                    });
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
        to_regist = (TextView) findViewById(R.id.to_regist);
        bt_login_login = (Button) findViewById(R.id.bt_login_login);

        //用户点击登陆后的loading页面
        progressDialog = new LECustomProgressDialog(this);
    }

}
