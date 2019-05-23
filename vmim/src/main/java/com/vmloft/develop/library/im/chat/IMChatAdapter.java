package com.vmloft.develop.library.im.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.chat.msgitem.IMMsgItem;
import com.vmloft.develop.library.im.chat.msgitem.IMTextMsgItem;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/23 19:58
 *
 * IM 聊天界面适配器
 */
public class IMChatAdapter extends VMAdapter<EMMessage, IMChatAdapter.ChatHolder> {

    public IMChatAdapter(Context context, List<EMMessage> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ChatHolder(createMsgItem(type));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItemData(position);
        int itemType = IMConstants.IM_CHAT_TYPE_TEXT; // 默认文本
        if (message.getType() == EMMessage.Type.TXT) {
            // 读取扩展消息类型，如果没有扩展那就是默认文本消息
            itemType = message.getIntAttribute(IMConstants.IM_CHAT_MSG_TYPE, IMConstants.IM_CHAT_TYPE_TEXT);
        } else if (message.getType() == EMMessage.Type.IMAGE) {
            // 同样的，先从扩展中获取消息类型，如果没有扩展那就是默认图片消息
            itemType = message.getIntAttribute(IMConstants.IM_CHAT_MSG_TYPE, IMConstants.IM_CHAT_TYPE_IMAGE);
        } else if (message.getType() == EMMessage.Type.VIDEO) {
        } else if (message.getType() == EMMessage.Type.LOCATION) {
        } else if (message.getType() == EMMessage.Type.VOICE) {
            // 同样的，先从扩展中获取消息类型，如果没有扩展那就是默认语音消息
            itemType = message.getIntAttribute(IMConstants.IM_CHAT_MSG_TYPE, IMConstants.IM_CHAT_TYPE_VOICE);
        } else if (message.getType() == EMMessage.Type.FILE) {
        } else {
            // 未知类型，显示提示文本
            itemType = IMConstants.IM_CHAT_TYPE_UNKNOWN;
        }
        return itemType;
    }

    /**
     * 创建一个消息 Item
     */
    private IMMsgItem createMsgItem(int type) {
        IMMsgItem itemView = null;
        switch (type) {
        case IMConstants.IM_CHAT_TYPE_TEXT:
            itemView = new IMTextMsgItem(mContext, this, type);
            break;
        case IMConstants.IM_CHAT_TYPE_IMAGE:
        case IMConstants.IM_CHAT_TYPE_VIDEO:
        case IMConstants.IM_CHAT_TYPE_LOCATION:
        case IMConstants.IM_CHAT_TYPE_VOICE:
        case IMConstants.IM_CHAT_TYPE_FILE:
        case IMConstants.IM_CHAT_TYPE_CALL:
        case IMConstants.IM_CHAT_TYPE_SYSTEM:
        case IMConstants.IM_CHAT_TYPE_RECALL:
            break;
        case IMConstants.IM_CHAT_TYPE_UNKNOWN: // 未知
        default:
            itemView = new IMTextMsgItem(mContext, this, IMConstants.IM_CHAT_TYPE_UNKNOWN);
            break;
        }

        return itemView;
    }

    /**
     * IM 消息 ItemHolder
     */
    static class ChatHolder extends VMHolder {

        public ChatHolder(View itemView) {
            super(itemView);
        }
    }
}
