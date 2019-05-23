package com.vmloft.develop.library.im.bean;

/**
 * Create by lzan13 on 2019/5/23 09:50
 *
 * 定义 IM 内部联系人实体类，用来获取头像昵称等简单属性进行展示
 */
public class IMContact {
    // 账户 id
    public String mId;
    // 账户用户名
    public String mUsername;
    // 账户昵称
    public String mNickname;
    // 账户头像
    public String mAvatar;

    public IMContact(String id) {
        mId = id;
    }
}
