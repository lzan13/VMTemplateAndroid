package com.vmloft.develop.library.im

import android.content.Context

import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions

import com.vmloft.develop.library.base.common.CError
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.im.call.IMCallManager
import com.vmloft.develop.library.im.chat.IMChatManager
import com.vmloft.develop.library.im.common.IMExceptionManager
import com.vmloft.develop.library.im.common.IMSPManager
import com.vmloft.develop.library.im.connect.IMConnectionManager
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.library.tools.utils.VMSystem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.util.concurrent.CountDownLatch

/**
 * Create by lzan13 on 2019/5/20 22:22
 * 描述：IM 入口类
 */
object IM {
    // 记录已经初始化
    private var isInit = false

    /**
     * 初始化
     */
    fun init(context: Context): Boolean {
        // 获取当前进程 id 并取得进程名
        val processName: String? = VMSystem.processName
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processName == null || !processName.equals(context.packageName, ignoreCase = true)) {
            // 则此 application 是被 Service 调用的，直接返回
            return true
        }

        if (isInit) {
            return isInit
        }

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(context, optionConfig())

        // 设置开启 debug 模式
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)

        // IM 内部相关管理类的初始化
        IMConnectionManager.init()
        // 初始化聊天管理类
        IMChatManager.init()
        // 初始化通话管理类
        IMCallManager.init(context)

        // 初始化完成
        isInit = true
        return isInit
    }

    /**
     * 返回是否成功登录过，同时没有调用 signOut()
     */
    fun isSignIn(): Boolean {
        return EMClient.getInstance().isLoggedInBefore
    }

    /**
     * 登录 IM
     */
    suspend fun signIn(user: User) = withContext(Dispatchers.IO) {
        signOut(false)

        var result: RResult<String> = IMExceptionManager.disposeError(CError.unknown, "")
        // 阻塞线层将 callback 转为同步
        val countDownLatch = CountDownLatch(1)
        EMClient.getInstance().login(user.id, user.password, object : EMCallBack {
            override fun onSuccess() {
                IMSPManager.putSelfId(user.id)
                // 因为这个必须要登录之后才能加载，所以这里也加载一次
                EMClient.getInstance().chatManager().loadAllConversations()

                result = RResult.Success("IM登录成功")

                // 解除锁
                countDownLatch.countDown()
            }

            override fun onError(code: Int, error: String) {
                result = IMExceptionManager.disposeError(code, error)

                // 解除锁
                countDownLatch.countDown()
            }

            override fun onProgress(progress: Int, status: String) {}
        })

        // 等待回调结束
        countDownLatch.await()
        result
    }

    /**
     * 退出登录
     *
     * @param unbuild 是否解绑推送 Token
     */
    fun signOut(unbuild: Boolean = false) {
        // 退出前检查一下是否已经成功登录
        if (!isSignIn()) {
            return
        }
        IMSPManager.putSelfId("")
        EMClient.getInstance().logout(unbuild)
    }

    /**
     * IM 配置
     */
    private fun optionConfig(): EMOptions {
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        val options = EMOptions()

        // 是否启动 DNS 信息配置，如果是私有化部署，这里要设置为 false
        options.enableDNSConfig(true)
        // 设置私有化 IM 地址
        //options.setIMServer("im1.easemob.com");
        // 设置私有化 IM 端口号
        //options.setImPort(443);
        // 设置私有化 Rest 地址+端口号
        //options.setRestServer("a1.easemob.com:80");
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        //options.setAppKey("yunshangzhijia#yunyue");

        // 设置只使用 https
        options.usingHttpsOnly = false
        // 设置自动登录
        options.autoLogin = true
        // 设置是否按照服务器时间排序，false按照本地时间排序，默认 true
        options.isSortMessageByServerTime = false
        // 设置是否需要发送已读回执
        options.requireAck = true
        // 设置是否需要发送回执
        options.requireDeliveryAck = true
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.acceptInvitationAlways = true
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.isAutoAcceptGroupInvitation = true
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.isDeleteMessagesAsExitGroup = true
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true)
        return options
    }

    /**
     * 头像点击
     */
    fun onHeadClick(userId: String) {
        val user = CacheManager.getUser(userId)
        CRouter.go("/App/UserInfo", obj0 = user)
    }

    /**
     * ---------------------------------------------------------------------
     */
    /**
     * 通知开关
     */
    var isNotify: Boolean
        get() = CSPManager.isNotifyMsgSwitch()
        set(open) {
            CSPManager.setNotifyMsgSwitch(open)
        }

    /**
     * 通知详情
     */
    var isNotifyDetail: Boolean
        get() = CSPManager.isNotifyMsgDetailSwitch()
        set(open) {
            CSPManager.setNotifyMsgDetailSwitch(open)
        }
    /**
     * ---------------------------------------------------------------------
     */
    /**
     * 圆形头像
     */
    var isCircleAvatar: Boolean
        get() = IMSPManager.isCircleAvatar()
        set(open) {
            IMSPManager.putCircleAvatar(open)
        }

    /**
     * 扬声器播放语音
     */
    var isSpeakerVoice: Boolean
        get() = IMSPManager.isSpeakerVoice()
        set(speaker) {
            IMSPManager.putSpeakerVoice(speaker)
        }
}
