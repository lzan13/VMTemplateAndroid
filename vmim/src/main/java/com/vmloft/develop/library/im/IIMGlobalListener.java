package com.vmloft.develop.library.im;

import android.content.Context;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.msgitem.IMBaseItem;

/**
 * Create by lzan13 on 2019/5/23 09:47
 *
 * 定义 IM 全局回调接口
 */
public interface IIMGlobalListener {

    /**
     * 同步获取 IM 联系人信息
     *
     * @param id 联系人 Id
     */
    IMContact getIMContact(String id);

    /**
     * 更新联系人信息
     * @param id 联系人 Id
     */
    void updateIMContact(String id);

    /**
     * 异步获取 IM 联系人信息
     *
     * @param id       联系人 Id
     * @param callback 结果回调
     */
    void getIMContact(String id, IMCallback<IMContact> callback);

    /**
     * 联系人头像点击
     *
     * @param context 上下文对象
     * @param contact 点击的头像
     */
    void onHeadClick(Context context, IMContact contact);

    /**
     * 获取消息类型
     *
     * @param message 消息对象
     */
    int onMsgType(EMMessage message);

    /**
     * 获取消息摘要
     *
     * @param message 消息对象
     */
    String onMsgSummary(EMMessage message);

    /**
     * 生成消息展示控件
     *
     * @param context 上下文对象
     * @param adapter 消息适配器
     * @param type    消息类型
     */
    IMBaseItem onMsgItem(Context context, IMChatAdapter adapter, int type);
}
