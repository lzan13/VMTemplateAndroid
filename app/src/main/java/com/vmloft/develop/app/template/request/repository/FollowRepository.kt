package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResult
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
    suspend fun cancelFollow(id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.followAPI.cancelFollow(id)) }
    }

    /**
     * 获取关注列表
     */
    suspend fun getFollowList(userId: String, type: Int, page: Int, limit: Int): RResult<RPaging<User>> {
        return safeRequest { executeResponse(APIRequest.followAPI.getFollowList(userId, type, page, limit)) }
    }

}