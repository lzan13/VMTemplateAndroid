package com.vmloft.develop.app.match.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVUser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by lzan13 on 2019/5/21 19:33
 */
@AVClassName("AUser")
public class AUser extends AVUser {

    /**
     * 用户昵称
     */
    public String getNickname() {
        return getString("nickname");
    }

    /**
     * 设置昵称
     */
    public void setNickname(String nickname) {
        put("nickname", nickname);
    }

    /**
     * 用户头像
     */
    public String getAvatarUrl() {
        return getString("avatarUrl");
    }

    /**
     * 用户签名
     */
    public String getSignature() {
        return getString("signature");
    }

    /**
     * 用户信息转为 json 串
     */
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("username", getUsername());
            object.put("email", getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
