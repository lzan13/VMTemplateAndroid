package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.bean.User

/**
 * Create by lzan13 on 2022/04/07 09:08
 * 描述：黑名单相关请求
 */
class BlacklistRepository : BaseRepository() {

    /**
     * 拉黑
     */
    suspend fun blacklist(id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.blacklistAPI.blacklist(id)) }
    }

    /**
     * 取消拉黑
     */
    suspend fun cancel(id: String): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.blacklistAPI.cancel(id)) }
    }

    /**
     * 获取拉黑列表
     */
    suspend fun getBlacklist(type: Int, page: Int, limit: Int): RResult<RPaging<User>> {
        return safeRequest { executeResponse(APIRequest.blacklistAPI.getBlacklist(type, page, limit)) }
    }

}