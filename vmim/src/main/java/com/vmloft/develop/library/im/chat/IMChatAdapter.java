package com.vmloft.develop.library.im.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.chat.msgitem.IMBaseItem;
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

    @Override
    public ChatHolder createHolder(@NonNull ViewGroup root, int type) {
        return new ChatHolder(IMChatUtils.createMsgItem(mContext, this, type));
    }

    @Override
    public void bindHolder(@NonNull ChatHolder holder, int position) {
        ((IMBaseItem) holder.itemView).onBind(position, getItemData(position));
    }

    @Override
    public void onBindViewHolder(@NonNull VMHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads != null && !payloads.isEmpty()) {
            ((IMBaseItem) holder.itemView).onUpdate(getItemData(position));
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemType(int position) {
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
     * 加载消息列表
     */
    private void updateData() {
        mDataList.clear();
        mDataList.addAll(IMChatManager.getInstance().getCacheMessages(mId, mChatType));
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
        //notifyDataSetChanged();
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
        //notifyDataSetChanged();
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
        //notifyDataSetChanged();
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
