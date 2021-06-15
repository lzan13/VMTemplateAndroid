package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.app.template.request.bean.Match
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
    suspend fun submitMatch(content: String, emotion: Int, type: Int): RResult<Match> {
        return safeRequest(call = { requestSubmitMatch(content, emotion, type) })
    }

    private suspend fun requestSubmitMatch(content: String, emotion: Int, type: Int): RResult<Match> =
        executeResponse(APIRequest.matchAPI.submitMatch(content, emotion, type))

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
    suspend fun getMatchList(page: Int, limit: Int): RResult<RPaging<Match>> {
        return safeRequest(call = { requestMatchList(page, limit) })
    }

    private suspend fun requestMatchList(page: Int, limit: Int): RResult<RPaging<Match>> =
        executeResponse(APIRequest.matchAPI.getMatchList(page, limit))

    /**
     * 随机获取一条匹配数据
     */
    suspend fun getMatchOne(type: Int): RResult<Match> {
        return safeRequest(call = { requestMatchOne(type) })
    }

    private suspend fun requestMatchOne(type: Int): RResult<Match> =
        executeResponse(APIRequest.matchAPI.getMatchOne(type))

}