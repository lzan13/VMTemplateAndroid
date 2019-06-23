package com.vmloft.develop.app.match.request.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Create by lzan13 on 2019/5/21 19:30
 *
 * 自定义匹配对象实体类
 */
public class AMatch {

    @SerializedName("_id") private String id;
    @SerializedName("account_id") private String accountId;
    @SerializedName("create_at") private String createAt;
    @SerializedName("update_at") private String updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
}
