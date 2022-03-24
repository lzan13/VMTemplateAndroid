package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.app.template.request.bean.Post
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：喜欢相关请求
 */
class LikeRepository : BaseRepository() {

    /**
     * 喜欢
     */
    suspend fun like(type: Int, id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.likeAPI.like(type, id)) }
    }

    /**
     * 取消关注
     */
    suspend fun cancelLike(type: Int, id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.likeAPI.cancelLike(type, id)) }
    }

    /**
     * 获取喜欢列表
     */
    suspend fun getLikePostList(owner: String, page: Int, limit: Int, type: Int, id: String): RResult<RPaging<Post>> {
        return safeRequest { executeResponse(APIRequest.likeAPI.getLikePostList(type, id, owner, page, limit)) }
    }

}