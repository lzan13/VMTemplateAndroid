package com.vmloft.develop.app.match.request.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lzan13 on 2017/11/24.
 * 账户实体类
 */
public class AAccount {
    @SerializedName("_id") private String id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String avatar;
    private String cover;
    private Integer gender;
    private String nickname;
    private String signature;
    private String address;
    private String token;
    private String code;
    private boolean activated;
    private boolean deleted;
    private boolean admin;
    @SerializedName("create_at") private String createAt;
    @SerializedName("update_at") private String updateAt;

    public AAccount() {
    }

    public AAccount(String account, String password) {
        this.email = account;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\tid:" + id);
        builder.append("\n\tusername:" + username);
        builder.append("\n\temail:" + email);
        builder.append("\n\tphones:" + phone);
        builder.append("\n\tavatar:" + avatar);
        builder.append("\n\tcover:" + cover);
        builder.append("\n\tgender:" + gender);
        builder.append("\n\taddress:" + address);
        builder.append("\n\tnickname:" + nickname);
        builder.append("\n\tsignature:" + signature);
        builder.append("\n\tcreateAt:" + createAt);
        builder.append("\n\tupdateAt:" + updateAt);
        builder.append("\n\ttoken:" + token);
        builder.append("\n\tcode:" + code);
        builder.append("\n\tactivated:" + activated);
        builder.append("\n\tdeleted:" + deleted);
        builder.append("\n\tadmin:" + admin);
        return builder.toString();
    }
}
