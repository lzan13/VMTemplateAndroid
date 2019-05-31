package com.vmloft.develop.library.im.router;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.call.IMCallActivity;
import com.vmloft.develop.library.im.chat.IMChatActivity;
import com.vmloft.develop.library.im.chat.IMPreviewActivity;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.router.VMRouter;

/**
 * Create by lzan13 on 2019/05/28
 *
 * 项目路由
 */
public class IMRouter extends VMRouter {

    /**
     * -------------------------- IM 相关界面 --------------------------
     * 跳转到聊天界面
     */
    public static void goIMChat(Context context, String id) {
        Intent intent = new Intent(context, IMChatActivity.class);
        intent.putExtra(IMConstants.IM_CHAT_ID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overlay(context, intent);
    }

    /**
     * 跳转到通话界面
     */
    public static void goIMCall(Context context, String id, boolean isCall) {
        Intent intent = new Intent(context, IMCallActivity.class);
        intent.putExtra(IMConstants.IM_CHAT_ID, id);
        intent.putExtra(IMConstants.IM_CHAT_IS_CALL, isCall);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overlay(context, intent);
    }

    /**
     * 跳转到图片预览界面
     */
    public static void goIMPriview(Context context, EMMessage message) {
        Intent intent = new Intent(context, IMPreviewActivity.class);
        intent.putExtra(IMConstants.IM_CHAT_MSG, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overlay(context, intent);
    }
}
