package com.vmloft.develop.library.im.conversation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.utils.IMConversationUtils;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;

import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;
import java.util.List;

import static com.hyphenate.chat.EMMessage.Type.TXT;

/**
 * Create by lzan13 on 2019/5/21 17:33
 *
 * 会话列表适配器
 */
public class IMConversationAdapter extends VMAdapter<EMConversation, IMConversationAdapter.ConversationHolder> {

    public IMConversationAdapter(Context context, List<EMConversation> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ConversationHolder(new IMConversationItem(mContext, this));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        super.onBindViewHolder(holder, position);
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
