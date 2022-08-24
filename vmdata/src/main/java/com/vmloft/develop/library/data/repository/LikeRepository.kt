package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.Post
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
    suspend fun likePostList(owner: String, page: Int, limit: Int, type: Int, id: String): RResult<RPaging<Post>> {
        return safeRequest { executeResponse(APIRequest.likeAPI.likePostList(type, id, owner, page, limit)) }
    }

}