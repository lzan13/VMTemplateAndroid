package com.vmloft.develop.app.match.bean;

import com.avos.avoscloud.AVUser;
import com.vmloft.develop.library.tools.utils.VMStr;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by lzan13 on 2019/04/16
 *
 * 用户实体类
 */
public class UserBean {

    private AVUser user;

    public UserBean() {
        user = new AVUser();
    }

    public UserBean(AVUser user) {
        this.user = user;
    }

    public AVUser getUser() {
        return user;
    }

    public void setUser(AVUser user) {
        this.user = user;
    }

    /**
     * 用户 Id
     */
    public String getId() {
        return user.getObjectId();
    }

    /**
     * 用户名
     */
    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    /**
     * 用户邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    /**
     * 用户昵称
     */
    public String getNickname() {
        return user.getString("nickname");
    }

    public void setNickname(String nickname) {
        user.put("nickname", nickname);
    }

    /**
     * 用户头像
     */
    public String getAvatarUrl() {
        return user.getString("avatarUrl");
    }

    /**
     * 用户签名
     */
    public String getSignature() {
        return user.getString("signature");
    }

    /**
     * 通过 json 文件解析用户信息，这个主要是保存的登录用户信息
     *
     * @param userStr 用户信息 json 串
     */
    public UserBean parse(String userStr) {
        if (VMStr.isEmpty(userStr)) {
            return null;
        }
        UserBean bean = new UserBean();
        try {
            JSONObject object = new JSONObject(userStr);
            bean.setUsername(object.optString("username"));
            bean.setEmail(object.optString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 用户信息转为 json 串
     */
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("username", user.getUsername());
            object.put("email", user.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
