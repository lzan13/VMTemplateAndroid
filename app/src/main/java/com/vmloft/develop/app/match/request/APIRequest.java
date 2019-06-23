package com.vmloft.develop.app.match.request;

import android.text.TextUtils;

import com.vmloft.develop.app.match.BuildConfig;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.common.ASignManager;

/**
 * Created by lzan13 on 2017/11/24.
 * 网络请求接口管理类
 */
public class APIRequest {


    private OkHttpClient client;
    private Retrofit retrofit;
    private APIService apiService;

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static APIRequest INSTANCE = new APIRequest();
    }

    /**
     * 获取单例类实例
     */
    public static APIRequest getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 私有化实例方法
     */
    APIRequest() {
        // 实例化 OkHttpClient，如果不自己创建 Retrofit 也会创建一个默认的
        client = new OkHttpClient.Builder().retryOnConnectionFailure(true)
                .addInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new RequestInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        // 实例化 Retrofit
        retrofit = new Retrofit.Builder().client(client)
                .baseUrl(BuildConfig.baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 创建 Retrofit 接口实例
        apiService = createApi(APIService.class);
    }

    /**
     * 为 POST 请求添加公共参数
     */
    private Request addPostFromParams(Request request) {
        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = (FormBody) request.body();
        for (int i = 0; i < formBody.size(); i++) {
            builder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
        }
        builder.addEncoded("device", "lz_mi5").build();
        return request.newBuilder().post(builder.build()).build();
    }

    /**
     * 为 POST 请求添加公共参数，多参数情况（参数包含文件时）
     */
    private Request addPostMultipartParams(Request request) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("device", "lz_mi5");
        MultipartBody body = (MultipartBody) request.body();
        for (int i = 0; i < body.size(); i++) {
            builder.addPart(body.part(i));
        }
        return request.newBuilder().post(builder.build()).build();
    }

    /**
     * 为 GET 请求添加公共参数
     */
    private Request addGetParams(Request request) {
        HttpUrl.Builder builder = request.url().newBuilder();
        builder.addQueryParameter("device", "lz_mi5");
        return request.newBuilder().url(builder.build()).build();
    }

    /**
     * 自定义拦截器，用户添加公共参数操作
     */
    private class RequestInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (request.method().equals("POST")) {
                // POST 请求有两种请求体形式 1.FormBody:表单形式，2.MultipartBody:多参数表单（包含文件）
                if (request.body() instanceof FormBody) {
                    addPostFromParams(request);
                } else if (request.body() instanceof MultipartBody) {
                    addPostMultipartParams(request);
                }
            } else if (request.method().equals("GET")) {
                addGetParams(request);
            }
            // 通过拦截器添加 token
            AAccount account = ASignManager.getInstance().getCurrentAccount();
            if (account != null && !TextUtils.isEmpty(account.getToken())) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + account.getToken()).build();
            }
            return chain.proceed(request);
        }
    }

    /**
     * 根据 API 接口类名创建指定的 Retrofit
     */
    public <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }
}

