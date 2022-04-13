package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.bean.User

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：关注相关请求
 */
class FollowRepository : BaseRepository() {

    /**
     * 关注
     */
    suspend fun follow(id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.followAPI.follow(id)) }
    }

    /**
     * 取消关注
     */
    suspend fun cancel(id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.followAPI.cancel(id)) }
    }

    /**
     * 获取关注列表
     */
    suspend fun followList(type: Int, page: Int, limit: Int): RResult<RPaging<User>> {
        return safeRequest { executeResponse(APIRequest.followAPI.followList(type, page, limit)) }
    }

}