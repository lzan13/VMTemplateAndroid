package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.common.SPManager
import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.bean.Version
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.request.RPaging


/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：首页相关请求
 */
class MainRepository : BaseRepository() {

    /**
     * 获取当前用户信息
     */
    suspend fun loadCurrUser(): RResult<User> {
        return safeRequest { executeResponse(APIRequest.userInfoAPI.current()) }
    }

}