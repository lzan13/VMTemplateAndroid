package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.Match
import com.vmloft.develop.library.data.db.AppDatabase
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RResult


/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：首页相关请求
 */
class MatchRepository : BaseRepository() {

    /**
     * 获取匹配列表数据
     */
    suspend fun submitMatch(match: Match): RResult<Match> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.matchAPI.submitMatch(match.content, match.emotion, match.gender, match.type)) }
    }

    /**
     * 删除一条匹配数据
     */
    suspend fun removeMatch(id: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.matchAPI.removeMatch(id)) }
    }

    /**
     * 获取匹配列表数据
     */
    suspend fun matchList(gender: Int, type: Int, page: Int, limit: Int): RResult<RPaging<Match>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.matchAPI.matchList(gender, type, page, limit)) }
    }

    /**
     * 获取自己的匹配数据
     */
    suspend fun getSelfMatch(): RResult<Match> {
        val match = AppDatabase.getInstance().matchDao().query("selfMatch")
        return RResult.Success("", match)
    }

    /**
     * 更新自己的匹配数据
     */
    suspend fun setSelfMatch(match: Match) {
        // 先清空原来的数据
        AppDatabase.getInstance().matchDao().delete()
        // 重新插入
        AppDatabase.getInstance().matchDao().insert(match)
    }

    /**
     * 随机获取一条匹配数据
     */
    suspend fun randomMatch(gender: Int, type: Int): RResult<Match> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.matchAPI.randomMatch(gender, type)) }
    }

}