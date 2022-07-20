package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.bean.User

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：用户关系相关请求
 */
class RelationRepository : BaseRepository() {

    /**
     * 关注
     */
    suspend fun follow(id: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.relationAPI.follow(id)) }
    }

    /**
     * 取消关注
     */
    suspend fun cancel(id: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.relationAPI.cancelFollow(id)) }
    }

    /**
     * 获取关注列表
     */
    suspend fun relationList(type: Int, page: Int, limit: Int): RResult<RPaging<User>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.relationAPI.relationList(type, page, limit)) }
    }

}