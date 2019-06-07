package com.vmloft.develop.library.im.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.chat.msgitem.IMCallItem;
import com.vmloft.develop.library.im.chat.msgitem.IMBaseItem;
import com.vmloft.develop.library.im.chat.msgitem.IMEmotionMsgItem;
import com.vmloft.develop.library.im.chat.msgitem.IMPictureItem;
import com.vmloft.develop.library.im.chat.msgitem.IMTextMsgItem;
import com.vmloft.develop.library.im.chat.msgitem.IMUnknownItem;
import com.vmloft.develop.library.im.chat.msgitem.IMVoiceMsgItem;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.utils.IMChatUtils;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/23 19:58
 *
 * IM 聊天界面适配器
 */
public class IMChatAdapter extends VMAdapter<EMMessage, IMChatAdapter.ChatHolder> {

    private String mId;
    private int mChatType;

    public IMChatAdapter(Context context, String id, int chatType) {
        super(context);
        mId = id;
        mChatType = chatType;
        mDataList = new ArrayList<>();
        mDataList.addAll(IMChatManager.getInstance().getCacheMessages(mId, mChatType));
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ChatHolder(createMsgItem(type));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ((IMBaseItem) holder.itemView).onBind(position, getItemData(position));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads != null && !payloads.isEmpty()) {
            ((IMBaseItem) holder.itemView).onUpdate(getItemData(position));
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItemData(position);
        return IMChatUtils.getMessageType(message);
    }

    /**
     * 获取上一条消息，主要判断当前消息是否需要展示时间
     */
    public EMMessage getPrevMessage(int position) {
        if (position == 0 || position >= getItemCount()) {
            return null;
        }
        return getItemData(position - 1);
    }

    /**
     * 创建一个消息 Item
     */
    private IMBaseItem createMsgItem(int type) {
        IMBaseItem itemView = new IMUnknownItem(mContext, this, IMConstants.MsgType.IM_UNKNOWN);
        ;
        switch (type) {
        // 通知类消息
        case IMConstants.MsgType.IM_SYSTEM:
        case IMConstants.MsgType.IM_RECALL:
            break;
        // 扩展类消息
        case IMConstants.MsgExtType.IM_CALL_RECEIVE:
        case IMConstants.MsgExtType.IM_CALL_SEND:
            itemView = new IMCallItem(mContext, this, type);
            break;
        case IMConstants.MsgExtType.IM_BIG_EMOTION_RECEIVE:
        case IMConstants.MsgExtType.IM_BIG_EMOTION_SEND:
            itemView = new IMEmotionMsgItem(mContext, this, type);
            break;
        // 普通消息
        case IMConstants.MsgType.IM_TEXT_RECEIVE:
        case IMConstants.MsgType.IM_TEXT_SEND:
            itemView = new IMTextMsgItem(mContext, this, type);
            break;
        case IMConstants.MsgType.IM_IMAGE_RECEIVE:
        case IMConstants.MsgType.IM_IMAGE_SEND:
            itemView = new IMPictureItem(mContext, this, type);
            break;
        case IMConstants.MsgType.IM_VIDEO_RECEIVE:
        case IMConstants.MsgType.IM_VIDEO_SEND:
            break;
        case IMConstants.MsgType.IM_LOCATION_RECEIVE:
        case IMConstants.MsgType.IM_LOCATION_SEND:
            break;
        case IMConstants.MsgType.IM_VOICE_RECEIVE:
        case IMConstants.MsgType.IM_VOICE_SEND:
            itemView = new IMVoiceMsgItem(mContext, this, type);
            break;
        case IMConstants.MsgType.IM_FILE_RECEIVE:
        case IMConstants.MsgType.IM_FILE_SEND:
            break;
        case IMConstants.MsgType.IM_UNKNOWN: // 未知
        default:
            itemView = new IMUnknownItem(mContext, this, IMConstants.MsgType.IM_UNKNOWN);
            break;
        }
        return itemView;
    }

    /**
     * 加载消息列表
     */
    private void updateData() {
        mDataList.clear();
        mDataList.addAll(IMChatManager.getInstance().getCacheMessages(mId, mChatType));
    }

    /**
     * 更新
     */
    public void update() {
        updateData();
        notifyDataSetChanged();
    }

    /**
     * 更新插入
     *
     * @param position 插入位置
     */
    public void updateInsert(int position) {
        updateInsert(position, 1);
    }

    /**
     * 更新插入
     *
     * @param position 插入位置
     * @param count    插入个数
     */
    public void updateInsert(int position, int count) {
        updateData();
        notifyItemRangeInserted(position, count);
        // TODO 这句已定要调用
        notifyDataSetChanged();
    }

    /**
     * 更新移除
     *
     * @param position 删除位置
     */
    public void updateRemove(int position) {
        updateRemove(position, 1);
    }

    /**
     * 更新移除
     *
     * @param position 删除位置
     * @param count    删除个数
     */
    public void updateRemove(int position, int count) {
        updateData();
        notifyItemRangeRemoved(position, count);
        // TODO 这句已定要调用
        notifyDataSetChanged();
    }

    /**
     * 更新改变
     *
     * @param position 改变位置
     */
    public void updateChange(int position) {
        updateChange(position, 1);
    }

    /**
     * 更新改变
     *
     * @param position 改变位置
     * @param count    改变个数
     */
    public void updateChange(int position, int count) {
        updateData();
        notifyItemRangeChanged(position, count, 1);
        // TODO 这句已定要调用
        notifyDataSetChanged();
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
