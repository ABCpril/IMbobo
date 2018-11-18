package com.example.administrator.imbobo.model.bean;

/**
 * Created by Leon on 2018/11/17.
 * Functions: 创建群组选择联系人的模型
 */
public class PickContactInfo {

    /**用户模型*/
    private UserInfo user;

    /**是否选中*/
    private boolean isChecked;

    public PickContactInfo(UserInfo user, boolean isChecked) {
        this.user = user;
        this.isChecked = isChecked;
    }

    public PickContactInfo() {
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
