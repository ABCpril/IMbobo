package com.example.administrator.imbobo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Leon on 2018/10/5
 * Functions: 登陆注册用的正则表达式
 */
public class TestRegUtils {

    /**
     * 题目: 注册的时候, 验证输入手机号码的有效性
     要求:
     必须是1开头的
     长度为11位
     手机号码必须是数字
     */
    public static boolean testTelephone(String telephone,Context context) {
        //是否是1开头的
        if(!telephone.startsWith("1"))
        {
            Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return false;
        }else if(telephone.length()!=11)//判断电话号码是否 11 位
        {
            Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return false;
        }else
        {
            //手机号码是否为数字
            String num="0123456789";
            char[] chars = telephone.toCharArray();
            boolean isValid=true;
            for(int i=0;i<chars.length;i++)
            {
                String s=String.valueOf(chars[i]);
                if(num.indexOf(s)<0)
                {
                    isValid=false;
                }
            }
            if(isValid)
            {
                //Toast.makeText(context,"手机号格式正确",Toast.LENGTH_SHORT).show();
                return true;
            }
            else
            {
                Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    /**
     * 判断密码的格式是否正确
     * 长度至少6位
     */
    public static boolean testPwd(String pwd,Context context) {
        if (pwd.matches("^.{6,}$")){
            return pwd.matches("^.{6,}$");
        }else {
            Toast.makeText(context,"密码长度至少6位",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}
