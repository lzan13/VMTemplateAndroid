package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.Applet
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult

/**
 * Create by lzan13 on 2022/05/25 09:08
 * 描述：程序相关请求
 */
class AppletRepository : BaseRepository() {

    /**
     * 程序列表
     */
    suspend fun appletList( page: Int, limit: Int): RResult<RPaging<Applet>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.appletAPI.appletList(page, limit)) }
    }

}