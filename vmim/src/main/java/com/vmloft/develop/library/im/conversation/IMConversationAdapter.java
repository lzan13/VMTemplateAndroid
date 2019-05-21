package com.vmloft.develop.library.im.conversation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMConversation;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import com.vmloft.develop.library.tools.adapter.VMHolder;

import java.util.List;

/**
 * Create by lzan13 on 2019/5/21 17:33
 */
public class IMConversationAdapter extends VMAdapter<EMConversation, IMConversationAdapter.ConversationHolder> {


    public IMConversationAdapter(Context context, List<EMConversation> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        super.onBindViewHolder(holder, position);


    }

    static class ConversationHolder extends VMHolder {

        public ConversationHolder(View itemView) {
            super(itemView);
        }
    }
}
