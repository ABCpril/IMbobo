package com.example.administrator.imbobo.model;

import android.content.Context;

import com.example.administrator.imbobo.model.bean.UserInfo;
import com.example.administrator.imbobo.model.dao.UserAccountDao;
import com.example.administrator.imbobo.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Leon on 2018/10/5
 * Functions: 数据模型层全局类 - 单例模式
 */
public class Model {

    //上下文
    private Context mContext;

    //户账号数据库的操作类对象
    private UserAccountDao userAccountDao;

    // 联系人和邀请信息表的操作类的管理类
    private DBManager dbManager;

    //线程池
    private ExecutorService executors = Executors.newCachedThreadPool();

    //创建一个静态的对象
    private static Model model = new Model();

    //创建一个私有的构造函数-单例
    private Model(){

    }

    //获取单例对象
    public static Model getInstance(){
        return model;
    }

    //初始化的方法
    public void init(Context context){
        this.mContext = context;

        //创建用户账号数据库的操作类对象
        userAccountDao = new UserAccountDao(context);

        //开启全局监听
        EventListener eventListener = new EventListener(mContext);
    }

    /**获取全局的线程池*/
    public ExecutorService getGloabalThreadPool() {
        return executors;
    }

    /**用户登陆成功后的处理方法*/
    public void loginSussess(UserInfo account){

        //避免空指针异常
        if (account == null){ return; }

        if (dbManager != null){
            dbManager.close();
        }

        dbManager = new DBManager(mContext,account.getName());
    }

    /**获取数据库的操作类对象*/
    public DBManager getDbManager(){
        return dbManager;
    }

    /**获取用户账号数据库的操作类对象*/
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

}
