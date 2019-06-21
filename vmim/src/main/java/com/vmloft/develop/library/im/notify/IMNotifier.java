package com.vmloft.develop.library.im.notify;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.call.IMCallManager;
import com.vmloft.develop.library.im.call.IMCallVideoActivity;
import com.vmloft.develop.library.im.call.IMCallVoiceActivity;
import com.vmloft.develop.library.im.chat.IMChatActivity;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.common.IMSPManager;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Created by lzan13 on 2019/06/08 10:23
 *
 * 自定义发送通知栏通知
 */
public class IMNotifier {

    // 通知栏提醒通道 Id
    private static final String IM_CHAT = "im_chat";
    private static final String IM_CALL = "im_call";
    // 通知栏提醒通知 Id
    private static final int IM_MSG_NOTIFY_ID = 100;
    private static final int IM_CALL_NOTIFY_ID = 101;

    private Context mContext;

    // 通知栏提醒ID

    private NotificationManager mManager;
    private Notification.Builder mChatBuilder;
    private Notification.Builder mCallBuilder;

    /**
     * 私有的构造方法
     */
    private IMNotifier() {}

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMNotifier INSTANCE = new IMNotifier();
    }

    public static IMNotifier getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 初始化一些通知的通用设置
     */
    public void init(Context context) {
        mContext = context;
        mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 针对 8.0 以上设备创建通知通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifyChannel(IM_CHAT, VMStr.byRes(R.string.im_notify_channel_chat), NotificationManager.IMPORTANCE_HIGH);
            createNotifyChannel(IM_CALL, VMStr.byRes(R.string.im_notify_channel_call), NotificationManager.IMPORTANCE_HIGH);
            mChatBuilder = new Notification.Builder(mContext, IM_CHAT);
            mCallBuilder = new Notification.Builder(mContext, IM_CALL);
        } else {
            mChatBuilder = new Notification.Builder(mContext);
            mCallBuilder = new Notification.Builder(mContext);
        }
        /**
         * 设置通知小ICON
         * 设置这个标志当用户单击面板就可以让通知将自动取消
         * 设置该通知优先级
         * 设置默认提醒，默认的有声音，振动，三色灯提醒
         * Notification.DEFAULT_VIBRATE //添加默认震动提醒  需要 VIBRATE permission
         * Notification.DEFAULT_SOUND   // 添加默认声音提醒
         * Notification.DEFAULT_LIGHTS  // 添加默认三色灯提醒
         * Notification.DEFAULT_ALL     // 添加默认以上3种全部提醒
         */
        mChatBuilder.setSmallIcon(R.drawable.im_ic_chat_notify);
        mChatBuilder.setPriority(Notification.PRIORITY_HIGH);
        mChatBuilder.setAutoCancel(true);
        mChatBuilder.setDefaults(Notification.DEFAULT_ALL);

        mCallBuilder.setSmallIcon(R.drawable.im_ic_call);
        mCallBuilder.setPriority(Notification.PRIORITY_HIGH);
        mCallBuilder.setAutoCancel(true);
        mCallBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
    }

    /**
     * 创建通知栏通知通道
     *
     * @param channelId   通道 Id
     * @param channelName 通道名称
     * @param importance  优先级
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotifyChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        // 设置显示角标
        channel.setShowBadge(true);
        channel.enableVibration(true);
        channel.enableLights(true);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 通知消息
     *
     * @param message 收到的消息，根据这个消息去发送通知
     */
    public void notifyMessage(EMMessage message) {
        if (!IM.getInstance().isNotify()) {
            return;
        }
        String summary = IMChatUtils.getSummary(message);
        if (IM.getInstance().isNotifyDetail()) {
            IMContact contact = IM.getInstance().getIMContact(message.conversationId());
            if (VMStr.isEmpty(contact.mNickname)) {
                mChatBuilder.setContentTitle(contact.mUsername);
            }else{
                mChatBuilder.setContentTitle(contact.mNickname);
            }
            mChatBuilder.setContentText(summary);
        } else {
            mChatBuilder.setContentTitle(VMStr.byRes(R.string.im_notify_channel_chat_title));
            mChatBuilder.setContentText(VMStr.byRes(R.string.im_notify_channel_chat_title));
        }
        mChatBuilder.setNumber(1);
        mChatBuilder.setTicker(VMStr.byRes(R.string.im_notify_channel_chat_title));
        mChatBuilder.setWhen(System.currentTimeMillis());

        // 设置通知栏点击意图（点击通知栏跳转到相应的页面）
        Intent intent = new Intent(mContext, IMChatActivity.class);
        intent.putExtra(IMConstants.IM_CHAT_ID, message.conversationId());
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mChatBuilder.setContentIntent(pIntent);

        mManager.notify(IM_MSG_NOTIFY_ID, mChatBuilder.build());
    }

    /**
     * 发送通知栏提醒，告知用户通话继续进行中
     */
    public void notifyCall() {
        mCallBuilder.setContentText("通话进行中，点击恢复");

        mCallBuilder.setContentTitle(VMStr.byRes(R.string.app_name));
        Intent intent = new Intent();
        if (IMCallManager.getInstance().getCallType() == IMCallManager.CallType.VIDEO) {
            intent.setClass(mContext, IMCallVideoActivity.class);
        } else {
            intent.setClass(mContext, IMCallVoiceActivity.class);
        }
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mCallBuilder.setContentIntent(pIntent);
        mCallBuilder.setOngoing(true);

        mCallBuilder.setWhen(System.currentTimeMillis());

        mManager.notify(IM_CALL_NOTIFY_ID, mCallBuilder.build());
    }

    /**
     * 取消通话状态通知栏提醒
     */
    public void notifyCallCancel() {
        if (mManager != null) {
            mManager.cancel(IM_CALL_NOTIFY_ID);
        }
    }

    /**
     * 检查是否需要通知栏提醒
     */
    private boolean checkNotify() {
        return IMSPManager.getInstance().getNotify();
    }
}
