package com.vmloft.develop.library.im.chat;

import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseFragment;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.conversation.IMConversationManager;
import com.vmloft.develop.library.tools.adapter.VMAdapter;
import java.util.List;

/**
 * Create by lzan13 on 2019/05/09 10:11
 *
 * IM 可自定义加载的聊天界面
 */
public class IMChatFragment extends IMBaseFragment {

    // 发送按钮
    private ImageButton mSendBtn;
    // 表情按钮
    private ImageButton mEmojiBtn;
    // 输入框
    private EditText mInputET;

    private RecyclerView mRecyclerView;
    private IMChatAdapter mAdapter;
    private List<EMMessage> mList;

    private String mId;
    private IMContact mContact;

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static IMChatFragment newInstance(String id) {
        IMChatFragment fragment = new IMChatFragment();

        Bundle args = new Bundle();
        args.putString(IMConstants.IM_CHAT_KEY_ID, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.im_fragment_chat;
    }

    @Override
    protected void init() {
        mId = getArguments().getString(IMConstants.IM_CHAT_KEY_ID);
        mList = IMConversationManager.getInstance().getAllMessages(mId);

        mSendBtn = getView().findViewById(R.id.im_chat_input_send_btn);
        mEmojiBtn = getView().findViewById(R.id.im_chat_input_emoji_btn);
        mInputET = getView().findViewById(R.id.im_chat_input_et);

        initRecyclerView();

        refreshUI();
    }

    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.im_chat_recycler_view);
        mRecyclerView.setOnClickListener(viewListener);
        mAdapter = new IMChatAdapter(mContext, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new VMAdapter.IClickListener() {
            @Override
            public void onItemAction(int action, Object object) {

            }

            @Override
            public boolean onItemLongAction(int action, Object object) {
                return false;
            }
        });
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.im_chat_input_send_btn) {

            } else if (v.getId() == R.id.im_chat_input_emoji_btn) {

            } else if (v.getId() == R.id.im_chat_recycler_view) {

            }
        }
    };

    /**
     * 刷新 UI
     */
    private void refreshUI() {

    }
}
