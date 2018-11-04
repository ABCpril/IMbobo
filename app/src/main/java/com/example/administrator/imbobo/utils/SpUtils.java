package com.example.administrator.imbobo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.imbobo.IMApplication;

/**
 * Created by Leon on 2018/11/4.
 * Functions:  单列
 *
 * 提供 保存数据 和获取数据的方法
 */
public class SpUtils {

    //新的邀请标记
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    //私有的构造方法
    private SpUtils() {

    }

    public static SpUtils getInstance(){
        if (mSp == null){
            //IMApplication.getGlobalApplication() 获取全局上下文
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im",
                    Context.MODE_PRIVATE);
        }

        return instance;
    }

    /**保存数据的方法*/
    public void save(String key,Object values){
        if (values instanceof String){
            mSp.edit().putString(key,(String)values).commit();
        }else if (values instanceof Boolean){
            mSp.edit().putBoolean(key,(Boolean)values).commit();
        }else if (values instanceof Integer){
            mSp.edit().putInt(key,(Integer)values).commit();
        }
    }

    /**获取数据的方法  String*/
    public String getStringPre(String key,String defValue){
        return mSp.getString(key,defValue);
    }

    /**获取Boolean类型数据*/
    public boolean getBooleanPre(String key,boolean defValue){
        return mSp.getBoolean(key,defValue);
    }

    /**获取Int类型的数据*/
    public int getIntegrPre(String key,int defValue){
        return mSp.getInt(key,defValue);
    }

}
