package com.vmloft.develop.library.data.api

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResponse

import retrofit2.http.*

/**
 * Create by lzan13 on 2020/04/07 17:35
 * 描述：程序相关 API 接口
 */
interface AppletAPI {

    /**
     * 获取列表
     * @param page
     * @param limit
     */
    @GET("v1/applet")
    suspend fun appletList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): RResponse<RPaging<Applet>>

}