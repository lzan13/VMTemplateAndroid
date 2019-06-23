package com.vmloft.develop.app.match.request;

import com.vmloft.develop.app.match.request.bean.AMatch;
import io.reactivex.Observable;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.request.bean.AResult;
import retrofit2.http.Query;

/**
 * Created by lzan13 on 2017/11/24.
 * 项目 Api 接口服务类
 */
public interface APIService {

    /**
     * --------------------------------- 账户接口 ---------------------------------
     */
    /**
     * 注册账户
     */
    @FormUrlEncoded
    @POST("accounts/create")
    Observable<AResult<AAccount>> createAccount(@Field("email") String account, @Field("password") String password);

    /**
     * 认证账户
     */
    @FormUrlEncoded
    @POST("accounts/login")
    Observable<AResult<AAccount>> loginAccount(@Field("account") String account, @Field("password") String password);

    /**
     * 更新账户信息
     */
    @FormUrlEncoded
    @PUT("accounts/detail")
    Observable<AResult<AAccount>> updateAccountDetail(@Field("gender") int gender, @Field("nickname") String nickname,
        @Field("signature") String signature, @Field("address") String address);

    /**
     * 更新账户密码
     */
    @FormUrlEncoded
    @PUT("accounts/password")
    Observable<AResult<AAccount>> updateAccountPassword(@Field("oldPassword") String oldPassword, @Field("password") String password);

    /**
     * 更新账户头像
     */
    @Multipart
    @PUT("accounts/avatar")
    Observable<AResult<AAccount>> updateAccountAvatar(@Part MultipartBody.Part file);

    /**
     * 获取账户信息
     */
    @GET("accounts/detail/{id}")
    Observable<AResult<AAccount>> getAccount(@Path("id") String id);

    /**
     * 获取账户信息
     */
    @GET("accounts/all")
    Observable<AResult<List<AAccount>>> getAccountAll(@Query("page") int page, @Query("limit") int limit);

    /**
     * --------------------------------- 匹配接口 ---------------------------------
     */
    /**
     * 提交匹配
     */
    @POST("matchs/create")
    Observable<AResult<AMatch>> createMatch();

    /**
     * 移除匹配信息
     */
    @DELETE("matchs/remove/{id}")
    Observable<AResult<AMatch>> removeMatch(@Path("id") String id);

    /**
     * 获取匹配信息
     */
    @GET("matchs/all")
    Observable<AResult<List<AMatch>>> getMatchAll(@Query("page") int page, @Query("limit") int limit);
}

