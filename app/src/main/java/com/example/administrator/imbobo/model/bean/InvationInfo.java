package com.example.administrator.imbobo.model.bean;

/**
 * Created by Leon on 2018/11/3.
 * Functions: 邀请信息的模型
 */
public class InvationInfo {

    /**用户模型类*/
    private UserInfo user;

    /**群组模型类*/
    private GroupInfo group;

    /**邀请原因*/
    private String reason;

    /**邀请的状态 枚举*/
    private InvitationStatus status;

    public InvationInfo() {

    }

    public InvationInfo(UserInfo userInfo, GroupInfo groupInfo, String reason, InvitationStatus status) {
        this.user = userInfo;
        this.group = groupInfo;
        this.reason = reason;
        this.status = status;
    }

    public enum  InvitationStatus{

        /**新邀请*/
        NEW_INVITE,

        /**接受邀请*/
        INVITE_ACCEPT,

        /**邀请被接受*/
        INVITE_ACCEPT_BY_PEER,

        //-----以下是群组邀请信息状态-----

        /**收到邀请去加入群组*/
        NEW_GROUP_INVITE,

        /**收到申请加入*/
        NEW_GROUP_APPLICATION,

        /**群邀请已经被对方接受*/
        GROUP_INVITE_ACCEPTED,

        /**群申请已经被批准*/
        GROUP_APPLICATION_ACCEPTED,

        /**接受了群邀请*/
        GROUP__ACCEPT_INVITE,

        /**批准的群加入申请*/
        GROUP_ACCEPT_APPLICATION,

        /**拒绝了群邀请*/
        GROUP_REJECT_INVITE,

        /**拒绝了群申请加入*/
        GROUP_REJECT_APPLICATION,

        /**群邀请被对方拒绝*/
        GROUP_INVITE_DECLINED,

        /**群申请被拒绝*/
        GROUP_APPLICATION_DECLINED
    }

    @Override
    public String toString() {
        return "InvationInfo{" +
                "userInfo=" + user +
                ", groupInfo=" + group +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo userInfo) {
        this.user = userInfo;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }
}
