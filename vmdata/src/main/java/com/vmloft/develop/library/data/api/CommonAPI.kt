package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Create by lzan13 on 2020-02-13 17:35
 * 描述：一些通用相关 API 网络接口
 */
interface CommonAPI {

    /**
     * --------------------------------- 附件件接口 ---------------------------------
     */
    /**
     * 上传单附件
     */
    @POST("v1/attachment")
    suspend fun upload(@Body body: MultipartBody): RResponse<Attachment>

    /**
     * 下载附件，为防止 OOM 大文件下载必须 @treaming 注解
     */
    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody

    /**
     * ------------------------------------ 通用接口  ------------------------------------
     */
    /**
     * 获取分类列表
     */
    @GET("v1/common/category")
    suspend fun categoryList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Category>>

    /**
     * 获取职业列表
     */
    @GET("v1/common/profession")
    suspend fun professionList(
        @Query("page") page: Int = CConstants.defaultPage,
        @Query("limit") limit: Int = CConstants.defaultLimit,
    ): RResponse<RPaging<Profession>>

    /**
     * 获取礼物列表
     */
    @GET("v1/gift")
    suspend fun giftList(): RResponse<RPaging<Gift>>

    /**
     * 检查版本
     */
    @GET("v1/common/checkVersion")
    suspend fun checkVersion(@Query("platform") platform: Int = 0): RResponse<Version>

    /**
     * 获取客户端配置
     */
    @GET("v1/common/clientConfig")
    suspend fun clientConfig(): RResponse<Config>

    /**
     * 获取隐私政策
     */
    @GET("v1/common/privatePolicy")
    suspend fun privatePolicy(): RResponse<Config>

    /**
     * 获取用户协议
     */
    @GET("v1/common/userAgreement")
    suspend fun userAgreement(): RResponse<Config>


    /**
     * 获取用户行为规范
     */
    @GET("v1/common/userNorm")
    suspend fun userNorm(): RResponse<Config>


    /**
     * ------------------------------------ 反馈接口  ------------------------------------
     */
    /**
     * 提交反馈
     * @param contact 联系方式
     * @param content 反馈内容
     * @param type 反馈类型 0-意见建议 1-广告引流 2-政治敏感 3-违法违规 4-色情低俗 5-血腥暴力 6-诱导信息 7-谩骂攻击 8-涉嫌诈骗 9-引人不适 10-其他
     * @param attachments 附件
     * @param user 相关用户
     * @param post 相关帖子
     * @param comment 相关评论
     */
    @FormUrlEncoded
    @POST("v1/common/feedback")
    suspend fun feedback(
        @Field("contact") contact: String,
        @Field("content") content: String,
        @Field("type") type: Int,
        @Field("attachments") attachments: List<String>,
        @Field("user") user: String,
        @Field("post") post: String,
        @Field("comment") comment: String,
    ): RResponse<Any>

    /**
     * 查询我提交的反馈
     */
    @GET("v1/common/feedbackList")
    suspend fun feedbackList(@Query("page") page: Int, @Query("limit") limit: Int): RResponse<RPaging<Feedback>>

    /**
     * 商品相关
     */
    /**
     * 查询虚拟商品列表
     */
    @GET("v1/common/virtualCommodityList")
    suspend fun virtualCommodityList(): RResponse<RPaging<Commodity>>

}