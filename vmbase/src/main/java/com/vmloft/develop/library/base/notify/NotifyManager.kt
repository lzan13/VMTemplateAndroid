package com.vmloft.develop.library.base.notify

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vmloft.develop.library.base.R

import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

/**
 * Create by lzan13 on 2020/6/22 10:20
 * 描述：通知管理类
 */
object NotifyManager {

    // 系统通知管理类
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mContext: Context

    /**
     * 最后一条通知发出时间，用于控制相近的通知，不发出声音
     */
    private var lastNotifyTime: Long = 0

    // 通知消息通道 Id
    private const val notifyMsgChannelGroupId = "notifyMsgChannelGroupId"
    private const val notifyMsgChannelGroupName = "消息通知"
    private const val notifyMsgChannelGroupDescription = "收到实时消息相关通知通道组"

    private const val notifyMsgChannelId = "notifyMsgChannelId"
    private const val notifyMsgChannelName = "消息通知"
    private const val notifyMsgChannelDescription = "收到新消息时通知通道，建议开启"

    private const val notifyOtherChannelId = "notifyOtherChannelId"
    private const val notifyOtherChannelName = "其他通知"
    private const val notifyOtherChannelDescription = "收到其他消息时通知通道，可以关闭"

    // 其它通知通道 Id
    private const val notifyOtherChannelGroupId = "notifyOtherChannelGroupId"
    private const val notifyOtherChannelGroupName = "其它通知"
    private const val notifyOtherChannelGroupDescription = "其他类型的通知通道组"

    private const val notifyKeepAliveChannelId = "notifyKeepAliveChannelId"
    private const val notifyKeepAliveChannelName = "前台服务"
    private const val notifyKeepAliveChannelDescription = "常驻通知栏的前台服务通知，用于保活"

    /**
     * 初始化通知
     */
    fun init(context: Context) {
        mContext = context
        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        initChannel()
    }

    /**
     * 发送通知
     */
    fun sendNotify(content: String, title: String) {
        if (!CSPManager.isNotifyMsgSwitch()) {
            return
        }
        val builder: NotificationCompat.Builder = getBuilder(notifyMsgChannelId)

        // 通知标题
        if (!title.isNullOrEmpty()) {
            builder.setContentTitle(title)
        }

        // 通知内容
        // 开始在状态栏上显示的提示文案
        if (CSPManager.isNotifyMsgDetailSwitch()) {
            builder.setTicker(content)
            builder.setContentText(content)
        } else {
            builder.setTicker(VMStr.byRes(R.string.notify_msg_hint))
            builder.setContentText(VMStr.byRes(R.string.notify_msg_hint))
        }

        // TODO 视具体业务打开对应界面
//        val intent = Intent(mContext, NotifyActivity::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//         设置通知点击跳转
//        builder.setContentIntent(pendingIntent)

        val notifyId: Int = notifyMsgChannelId.hashCode()

        mNotificationManager.notify(notifyId, builder.build())
    }

    /**
     * 发送通知
     */
    fun openKeepAliveNotify(content: String, title: String) {
        val builder: NotificationCompat.Builder = getBuilder(notifyKeepAliveChannelId)

        // 通知标题
        if (!title.isNullOrEmpty()) {
            builder.setContentTitle(title)
        }

        // 通知内容
        // 开始在状态栏上显示的提示文案
        builder.setTicker(content)
        builder.setContentText(content)

        // TODO 视具体业务打开对应界面
//        val intent = Intent(mContext, NotifyActivity::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//         设置通知点击跳转
//        builder.setContentIntent(pendingIntent)

        val notifyId: Int = notifyKeepAliveChannelId.hashCode()

        mNotificationManager.notify(notifyId, builder.build())
    }

    /**
     * 检查是否开启了通知
     */
    fun checkNotifySetting(): Boolean {
        val manager = NotificationManagerCompat.from(mContext)
        // areNotificationsEnabled 方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回 true，即默认为用户已经开启了通知。
        val isOpened = manager.areNotificationsEnabled()
        if (isOpened) {
            VMLog.d("通知权限已经被打开，手机型号：" + Build.MODEL + "，SDK版本：" + Build.VERSION.SDK_INT + "，系统版本：" + Build.VERSION.RELEASE + "，包名：" + mContext.packageName)
        } else {
            VMLog.d("还没有开启通知权限")
        }
        return isOpened
    }

    fun getKeepAliveChannelId(): String {
        return notifyKeepAliveChannelId
    }

    /**
     * 打开通知设置页面
     */
    fun openNotifySetting() {
        val intent = Intent()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 8.0及以后版本使用这两个 extra. >=API 26
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, mContext.applicationInfo.uid)
            } else {
                // 5.0-7.1 使用这两个extra.  <= API 25, >=API 21
                intent.action = Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS
                intent.putExtra("app_package", mContext.packageName)
                intent.putExtra("app_uid", mContext.applicationInfo.uid)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()

            // 其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", mContext.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            mContext.startActivity(intent)
        }
    }


    /**
     * 获取通知对象
     */
    fun getBuilder(channelId: String, clickCancel: Boolean = true): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(mContext, channelId)
        // 设置通知时间
        builder.setWhen(System.currentTimeMillis())
        // 设置点击自动取消
        builder.setAutoCancel(clickCancel)
        // 设置小图标
        builder.setSmallIcon(mContext.applicationInfo.icon)
        // 设置大图标
        val largeIcon = BitmapFactory.decodeResource(mContext.resources, mContext.applicationInfo.icon)
        builder.setLargeIcon(largeIcon)

        // 支持横幅通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }

        // 控制相近的通知只响一次声音
        if (System.currentTimeMillis() - lastNotifyTime < 2 * 1000L) {
            builder.setDefaults(0)
        }
        return builder
    }

    /**
     * 重置通知通道
     */
    private fun resetNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteNotificationChannel(notifyMsgChannelId)
            initChannel()
        }
    }

    /**
     * 初始化通道
     */
    private fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val msgChannel = mNotificationManager.getNotificationChannel(notifyMsgChannelId)
            if (msgChannel != null) {
                return
            }
            // 先创建分组
            createNotificationChannelGroup(notifyMsgChannelGroupId, notifyMsgChannelGroupName, notifyMsgChannelGroupDescription)
            createNotificationChannelGroup(notifyOtherChannelGroupId, notifyOtherChannelGroupName, notifyOtherChannelGroupDescription)
            // 然后创建通道
            createNotificationChannel(notifyMsgChannelGroupId, notifyMsgChannelId, notifyMsgChannelName, notifyMsgChannelDescription)
            createNotificationChannel(notifyMsgChannelGroupId, notifyOtherChannelId, notifyOtherChannelName, notifyOtherChannelDescription)
            createNotificationChannel(notifyOtherChannelGroupId, notifyKeepAliveChannelId, notifyKeepAliveChannelName, notifyKeepAliveChannelDescription, false)
        }
    }

    /**
     * 创建通知组
     *
     * @param groupId
     * @param name
     * @param desc
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannelGroup(groupId: String, name: String, desc: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知分组
            val group = NotificationChannelGroup(groupId, name)
            // 设置分组描述
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                group.description = desc
            }

            val groups = ArrayList<NotificationChannelGroup>()
            groups.add(group)

            mNotificationManager.createNotificationChannelGroups(groups)
        }
    }

    /**
     * 创建通知渠道
     *
     * @param groupId
     * @param channelId
     * @param name
     * @param desc
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(groupId: String, channelId: String, name: String, desc: String, showBadge: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
            // 是否在长按桌面图标时显示此渠道的通知，桌面角标也是这个选项
            channel.setShowBadge(showBadge)
            // 设置是否应在锁定屏幕上显示此频道的通知
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            // 设置绕过免打扰模式
            channel.setBypassDnd(true)
            // 设置通知所属分组
            if (!groupId.isNullOrEmpty()) {
                channel.group = groupId
            }
            // 设置通知通道描述
            channel.description = desc

            mNotificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 根据 channelId 删除指定通知通道
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun deleteNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannel(channelId)
        }
    }

    /**
     * 删除通知分组
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun deleteNotificationChannelGroup(groupId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannelGroup(groupId)
        }
    }
}