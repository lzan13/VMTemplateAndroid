package com.vmloft.develop.library.im.conversation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMConversation;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;

import com.vmloft.develop.library.tools.utils.VMLog;

import java.util.List;

/**
 * Create by lzan13 on 2019/5/21 17:33
 *
 * 会话列表适配器
 */
public class IMConversationAdapter extends VMAdapter<EMConversation, IMConversationAdapter.ConversationHolder> {

    public IMConversationAdapter(Context context, List<EMConversation> list) {
        super(context, list);
    }

    public ConversationHolder createHolder(@NonNull ViewGroup root, int viewType) {
        return new ConversationHolder(new IMConversationItem(mContext, this));
    }

    @Override
    public void bindHolder(@NonNull ConversationHolder holder, int position) {
        VMLog.d("ConversationAdapter onBindViewHolder - %d", position);
        EMConversation conversation = getItemData(position);
        ((IMConversationItem) holder.itemView).onBind(conversation);
    }

    /**
     * 自定会会话 Holder
     */
    static class ConversationHolder extends VMHolder {

        public ConversationHolder(View itemView) {
            super(itemView);
        }
    }
}
