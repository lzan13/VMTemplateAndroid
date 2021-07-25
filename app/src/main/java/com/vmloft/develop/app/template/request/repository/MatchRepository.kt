package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.app.template.request.db.AppDatabase
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RResult


/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：首页相关请求
 */
class MatchRepository : BaseRepository() {

    /**
     * 获取匹配列表数据
     */
    suspend fun submitMatch(match: Match): RResult<Match> {
        return safeRequest(call = { requestSubmitMatch(match.content, match.emotion, match.gender) })
    }

    private suspend fun requestSubmitMatch(content: String, emotion: Int, gender: Int): RResult<Match> =
        executeResponse(APIRequest.matchAPI.submitMatch(content, emotion, gender))

    /**
     * 删除一条匹配数据
     */
    suspend fun removeMatch(id: String): RResult<Any> {
        return safeRequest(call = { requestRemoveMatch(id) })
    }

    private suspend fun requestRemoveMatch(id: String): RResult<Any> =
        executeResponse(APIRequest.matchAPI.removeMatch(id))

    /**
     * 获取匹配列表数据
     */
    suspend fun getMatchList(gender: Int, page: Int, limit: Int): RResult<RPaging<Match>> {
        return safeRequest(call = { requestMatchList(gender, page, limit) })
    }

    private suspend fun requestMatchList(gender: Int, page: Int, limit: Int): RResult<RPaging<Match>> =
        executeResponse(APIRequest.matchAPI.getMatchList(gender, page, limit))

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
    suspend fun getOneMatch(gender: Int): RResult<Match> {
        return safeRequest(call = { requestMatchOne(gender) })
    }

    private suspend fun requestMatchOne(gender: Int): RResult<Match> =
        executeResponse(APIRequest.matchAPI.getMatchOne(gender))

}