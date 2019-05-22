package com.vmloft.develop.app.match.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
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

    public void setNickname(String nickname) {
        put("nickname", nickname);
    }

    /**
     * 用户头像
     */
    public void setAvatar(AVFile file) {
        put("avatar", file);
    }

    public AVFile getAvatar() {
        return getAVFile("avatar");
    }

    /**
     * 用户签名
     */
    public String getSignature() {
        return getString("signature");
    }

    public void setSignature(String signature) {
        put("signature", signature);
    }

    /**
     * 用户性别
     */
    public int getGender() {
        return getInt("gender");
    }

    public void setGender(int gender) {
        put("gender", gender);
    }

    /**
     * 用户地址
     */
    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
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
