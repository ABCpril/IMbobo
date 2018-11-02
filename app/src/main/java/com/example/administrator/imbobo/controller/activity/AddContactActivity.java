package com.example.administrator.imbobo.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.model.Model;
import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.utils.TestRegUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Leon on 2018/10/6
 * Functions: 添加联系人页面
 */
public class AddContactActivity extends Activity {

    //右上角的加号
    private TextView tv_add_find;

    private EditText et_add_name;

    //需要隐藏/显示的相对布局
    private RelativeLayout rl_add;

    //查找到的用户的名称
    private TextView tv_add_name;

    //添加用户的按钮
    private Button bt_add_add;


    /**用户信息全局变量*/
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //初始化UI控件
        initView();

        //点击事件的处理
        initListener();
    }

    private void initListener(){
        //右上角+ 查找按钮的点击事件的处理
        tv_add_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        //添加按钮的点击事件处理
        bt_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
    }

    /**查找按钮的处理*/
    private void find(){
        //1获取输入框中输入的用户名称
        final String name = et_add_name.getText().toString();

        /**隐藏软键盘**/
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(AddContactActivity.this.
                    INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //2校验输入的名称
        if (TestRegUtils.testTelephone(name,AddContactActivity.this)){
            //3去服务器判断当前用户是否存在
            Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //去服务器判断查找的用户是否存在(去自己的服务器上判断这里没有做这一步)
                    userInfo = new UserInfo(name);

                    //更新UI显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl_add.setVisibility(View.VISIBLE);
                            tv_add_name.setText(userInfo.getName());
                        }
                    });
                }
            });
        }
    }

    /**添加按钮处理*/
    private void add(){
        Model.getInstance().getGloabalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(),"添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl_add.setVisibility(View.GONE);
                            Toast.makeText(AddContactActivity.this,"好友申请已经发出",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl_add.setVisibility(View.GONE);
                            Toast.makeText(AddContactActivity.this,"添加好友失败"+e.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView(){
        tv_add_find = (TextView)findViewById(R.id.tv_add_find);
        et_add_name = (EditText)findViewById(R.id.et_add_name);
        rl_add = (RelativeLayout)findViewById(R.id.rl_add);
        tv_add_name = (TextView)findViewById(R.id.tv_add_name);
        bt_add_add = (Button)findViewById(R.id.bt_add_add);
    }
}
