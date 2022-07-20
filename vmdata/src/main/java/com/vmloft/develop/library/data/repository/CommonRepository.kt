package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.data.api.APIRequest
import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.data.common.DSPManager
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.data.db.AppDatabase
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.request.RConstants
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMSystem

import okhttp3.MultipartBody
import okhttp3.ResponseBody


/**
 * Create by lzan13 on 2020/08/09 15:08
 * 描述：一些通用数据相关处理
 */
class CommonRepository : BaseRepository() {

    /**
     * 上传附件
     */
    suspend fun upload(body: MultipartBody): RResult<Attachment> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.upload(body)) }
    }

    /**
     * 下载附件，这个比较特殊，直接返回
     */
    suspend fun download(attachment: Attachment): ResponseBody {
        val url = RConstants.mediaHost() + attachment.path
        return com.vmloft.develop.library.data.api.APIRequest.commonAPI.download(url)
    }

    /**
     * 获取分类集合
     */
    suspend fun categoryList(server: Boolean = false): RResult<RPaging<Category>> {
        // 根据参数优先从本地获取，这里检查下缓存时间，大于 1 天就重新从服务器获取
        val time = DSPManager.getCategoryTime()
        val intervalTime = System.currentTimeMillis() - time
        if (!server && intervalTime < if (CSPManager.isDebug()) CConstants.timeMinute else CConstants.timeDay) {
            val list = AppDatabase.getInstance().categoryDao().all()
            if (list.isNotEmpty()) {
                return RResult.Success("", RPaging(list.size, list.size, CConstants.defaultPage, CConstants.defaultLimitBig, list))
            }
        }
        // 从服务器获取后保存到本地数据库
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.categoryList()) }
        if (result is RResult.Success) {
            DSPManager.setCategoryTime(System.currentTimeMillis())
            // 先清空原来的数据
            AppDatabase.getInstance().categoryDao().delete()
            // 重新插入
            AppDatabase.getInstance().categoryDao().insert(*result.data!!.data.toTypedArray())
        }
        return result
    }

    /**
     * 获取职业集合
     */
    suspend fun professionList(server: Boolean = false): RResult<RPaging<Profession>> {
        // 根据参数优先从本地获取，这里检查下缓存时间，大于 1 天就重新从服务器获取
        // 根据参数优先从本地获取
        val time = DSPManager.getProfessionTime()
        val intervalTime = System.currentTimeMillis() - time
        if (!server && intervalTime < if (CSPManager.isDebug()) CConstants.timeMinute else CConstants.timeDay) {
            val list = AppDatabase.getInstance().professionDao().all()
            if (list.isNotEmpty()) {
                return RResult.Success("", RPaging(list.size, list.size, CConstants.defaultPage, CConstants.defaultLimitBig, list))
            }
        }
        // 从服务器获取后保存到本地数据库
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.professionList()) }
        if (result is RResult.Success) {
            DSPManager.setProfessionTime(System.currentTimeMillis())
            // 先清空原来的数据
            AppDatabase.getInstance().professionDao().delete()
            // 重新插入
            AppDatabase.getInstance().professionDao().insert(*result.data!!.data.toTypedArray())
        }
        return result
    }

    /**
     * 检查版本，这里控制超过 24 小时去服务器请求
     */
    suspend fun checkVersion(server: Boolean = false): RResult<Version> {
        val time = DSPManager.getCheckVersionTime()
        val intervalTime = System.currentTimeMillis() - time
        if (!server && intervalTime < if (CSPManager.isDebug()) CConstants.timeMinute else CConstants.timeHour * 4) {
            val list = AppDatabase.getInstance().versionDao().all()
            if (list.isNotEmpty()) {
                val version = list[0]
                return if (version.force || version.versionCode - VMSystem.versionCode > 10) {
                    RResult.Success("", version)
                } else {
                    // 非强制更新，返回默认数据
                    RResult.Success("", Version())
                }
            }
        }
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.checkVersion()) }
        if (result is RResult.Success) {
            DSPManager.setCheckVersionTime(System.currentTimeMillis())
        }

        return result
    }

    /**
     * 获取客户端配置，这里控制超过 1 小时去服务器请求
     */
    suspend fun clientConfig(): RResult<Config> {
        val time = DSPManager.getClientConfigTime()
        val intervalTime = System.currentTimeMillis() - time
        // 这里一般使用缓存，只有超过一定时间才去服务器获取，设置为 4 小时，TODO 测试情况为 1 分钟
        if (intervalTime < if (CSPManager.isDebug()) CConstants.timeMinute else CConstants.timeHour * 4) {
            val config = AppDatabase.getInstance().configDao().query("clientConfig")
            if (config != null) {
                return RResult.Success("", config)
            }
        }
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.clientConfig()) }
        if (result is RResult.Success) {
            DSPManager.setClientConfigTime(System.currentTimeMillis())
        }
        return result
    }

    /**
     * 获取隐私政策，这里控制超过 24 小时去服务器请求
     */
    suspend fun privatePolicy(): RResult<Config> {
        val time = DSPManager.getPrivatePolicyTime()
        val intervalTime = System.currentTimeMillis() - time
        if (intervalTime < CConstants.timeDay * 7) {
            val config = AppDatabase.getInstance().configDao().query("privatePolicy")
            if (config != null) {
                return RResult.Success("", config)
            }
        }
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.privatePolicy()) }
        if (result is RResult.Success) {
            DSPManager.setPrivatePolicyTime(System.currentTimeMillis())
        }
        return result
    }

    /**
     * 获取用户协议，这里控制超过 24 小时去服务器请求
     */
    suspend fun userAgreement(): RResult<Config> {
        val time = DSPManager.getUserAgreementTime()
        val intervalTime = System.currentTimeMillis() - time
        if (intervalTime < CConstants.timeDay * 7) {
            val config = AppDatabase.getInstance().configDao().query("userAgreement")
            if (config != null) {
                return RResult.Success("", config)
            }
        }
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.userAgreement()) }
        if (result is RResult.Success) {
            DSPManager.setUserAgreementTime(System.currentTimeMillis())
        }
        return result
    }

    /**
     * 获取用户行为规范，这里控制超过 24 小时去服务器请求
     */
    suspend fun userNorm(): RResult<Config> {
        val time = DSPManager.getUserNormTime()
        val intervalTime = System.currentTimeMillis() - time
        if (intervalTime < CConstants.timeDay * 7) {
            val config = AppDatabase.getInstance().configDao().query("userNorm")
            if (config != null) {
                return RResult.Success("", config)
            }
        }
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.userNorm()) }
        if (result is RResult.Success) {
            DSPManager.setUserNormTime(System.currentTimeMillis())
        }
        return result
    }

    /**
     * 提交反馈
     * @param contact 联系方式
     * @param content 反馈内容
     * @param user 相关用户
     * @param post 相关帖子
     * @param comment 相关评论
     * @param attachments 附件
     * @param type 反馈类型 0-意见建议 1-广告引流 2-政治敏感 3-违法违规 4-色情低俗 5-血腥暴力 6-诱导信息 7-谩骂攻击 8-涉嫌诈骗 9-引人不适 10-其他
     */
    suspend fun feedback(contact: String, content: String, type: Int, attachments: List<String>, user: String, post: String, comment: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.feedback(contact, content, type, attachments, user, post, comment)) }
    }

    /**
     * 获取反馈列表
     */
    suspend fun feedbackList(page: Int, limit: Int): RResult<RPaging<Feedback>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.feedbackList(page, limit)) }
    }

    /**
     * ----------------------------- 商品相关 -----------------------------
     */
    /**
     * 获取虚拟商品列表
     */
    suspend fun virtualCommodityList(): RResult<RPaging<Commodity>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.commonAPI.virtualCommodityList()) }
    }

}