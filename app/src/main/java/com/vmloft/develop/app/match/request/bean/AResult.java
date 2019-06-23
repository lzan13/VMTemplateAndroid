package com.vmloft.develop.app.match.request.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lzan13 on 2018/4/16.
 * 对数据解析的封装，这里主要解析公共参数
 */
public class AResult<T> {
    // 请求结果错误码 为 0 则无错误
    private int code;
    // 请求结果描述
    private String message;
    // 请求符合条件的总条数
    @SerializedName("total_count") private int totalCount;
    // 当前返回的条数
    @SerializedName("result_count") private int resultCount;
    // 请求数据，泛型，根据传入的类型进行解析
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n\tcode:" + code);
        buffer.append("\n\tmessage:" + message);
        buffer.append("\n\ttotalCount:" + totalCount);
        buffer.append("\n\tresultCount:" + resultCount);
        if (data != null) {
            buffer.append("\n\n\tdata:" + data.toString());
        }
        return buffer.toString();
    }
}
