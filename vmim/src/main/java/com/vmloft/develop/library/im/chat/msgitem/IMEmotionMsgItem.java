package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.emotion.IMEmotionItem;
import com.vmloft.develop.library.im.emotion.IMEmotionManager;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMEmotionMsgItem extends IMNormalItem {

    private ImageView mEmotionView;

    public IMEmotionMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgExtType.IM_BIG_EMOTION_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_chat_item_emotion, null);
        mEmotionView = view.findViewById(R.id.im_chat_msg_emotion_iv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);

        mContainerView.setBackground(null);

        boolean isInnerEmotion = message.getBooleanAttribute(IMConstants.IM_MSG_EXT_INNER_EMOTION, false);
        String groupName = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_GROUP, "");
        String desc = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_DESC, "");
        String url = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_URL, "");
        String gifUrl = message.getStringAttribute(IMConstants.IM_MSG_EXT_EMOTION_GIF_URL, "");
        IMEmotionItem item = IMEmotionManager.getInstance().getEmotionItem(groupName, desc);
        if (isInnerEmotion && item != null) {
            mEmotionView.setImageResource(item.mResId);
        } else {
            // TODO 下载的大表情，这两个主要是为了在本地没有文件时展示

        }
    }

    /**
     * 加载悬浮才难
     */
    @Override
    public void loadFloatMenu() {
        super.loadFloatMenu();
        //mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_COPY, VMStr.byRes(R.string.im_msg_copy)));
    }

    @Override
    protected void onFloatClick(int id) {
        if (id == ID_COPY) {

        }
    }
}
