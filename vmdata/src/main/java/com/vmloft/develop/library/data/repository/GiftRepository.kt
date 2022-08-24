package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.data.common.DSPManager
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.db.AppDatabase
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.request.RPaging


/**
 * Create by lzan13 on 2020/08/09 15:08
 * 描述：一些通用数据相关处理
 */
class GiftRepository : BaseRepository() {

    /**
     * 赠送礼物
     */
    suspend fun giftGive(userId: String, giftId: String, count: Int): RResult<Any> {
        return safeRequest { executeResponse(APIRequest.giftAPI.giftGive(userId, giftId, count)) }
    }

    /**
     * 获取礼物集合
     */
    suspend fun giftList(server: Boolean = false): RResult<RPaging<Gift>> {
        // 根据参数优先从本地获取，这里检查下缓存时间，大于 1 天就重新从服务器获取
        // 根据参数优先从本地获取
        val time = DSPManager.getGiftTime()
        val intervalTime = System.currentTimeMillis() - time
        if (!server && intervalTime < if (CSPManager.isDebug()) CConstants.timeMinute else CConstants.timeHour * 4) {
            val list = AppDatabase.getInstance().giftDao().all()
            if (list.isNotEmpty()) {
                return RResult.Success("", RPaging(list.size, list.size, CConstants.defaultPage, CConstants.defaultLimitBig, list))
            }
        }
        // 从服务器获取后保存到本地数据库
        val result = safeRequest { executeResponse(APIRequest.giftAPI.giftList()) }
        if (result is RResult.Success) {
            DSPManager.setGiftTime(System.currentTimeMillis())
            // 先清空原来的数据
            AppDatabase.getInstance().giftDao().clear()
            // 重新插入
            AppDatabase.getInstance().giftDao().insert(*result.data!!.data.toTypedArray())
        }
        return result
    }

    /**
     * 收到的礼物列表
     */
    suspend fun giftRelationList(userId: String): RResult<RPaging<Gift>> {
        return safeRequest { executeResponse(APIRequest.giftAPI.giftRelationList(userId)) }
    }
}