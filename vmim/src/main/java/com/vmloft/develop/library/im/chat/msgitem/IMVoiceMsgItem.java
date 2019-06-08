package com.vmloft.develop.library.im.chat.msgitem;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.IMVoiceManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

/**
 * Create by lzan13 on 2019/5/23 22:17
 *
 * 实现文本消息展示
 */
public class IMVoiceMsgItem extends IMNormalItem {

    private ImageView mStatusIcon;
    private BaseVisualizer mVisualizerView;
    private TextView mContentView;

    public IMVoiceMsgItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_VOICE_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.im_msg_item_voice, null);
        mStatusIcon = view.findViewById(R.id.im_msg_voice_play_iv);
        mVisualizerView = view.findViewById(R.id.im_msg_voice_visualizer);
        mContentView = view.findViewById(R.id.im_msg_content_tv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);
        EMVoiceMessageBody body = (EMVoiceMessageBody) message.getBody();
        int length = body.getLength();
        if (length < 5 * IMConstants.IM_TIME_SECOND) {
            mVisualizerView.getLayoutParams().width = VMDimen.dp2px(48);
        } else if (length < 10 * IMConstants.IM_TIME_SECOND) {
            mVisualizerView.getLayoutParams().width = VMDimen.dp2px(72);
        } else if (length < 20 * IMConstants.IM_TIME_SECOND) {
            mVisualizerView.getLayoutParams().width = VMDimen.dp2px(96);
        } else if (length < 30 * IMConstants.IM_TIME_SECOND) {
            mVisualizerView.getLayoutParams().width = VMDimen.dp2px(128);
        } else {
            mVisualizerView.getLayoutParams().width = VMDimen.dp2px(192);
        }

        int minute = (length / 1000 / 60);
        int seconds = (length / 1000 % 60);
        int millisecond = (length % 1000 / 100);
        String time = VMStr.byArgs("%d'%d''%d'''", minute, seconds, millisecond);
        mContentView.setText(time);

        checkVoiceStatus();
    }

    @Override
    public void onClick() {
        IMVoiceManager.getInstance().onPlayMessage(mMessage, this);
    }

    /**
     * 加载悬浮菜单
     */
    @Override
    public void loadFloatMenu() {
        super.loadFloatMenu();
        if (IM.getInstance().isSpeakerVoice()) {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_VOICE, VMStr.byRes(R.string.im_msg_voice_erduo)));
        } else {
            mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_VOICE, VMStr.byRes(R.string.im_msg_voice_speaker)));
        }
    }

    @Override
    protected void onFloatClick(int id) {
        if (id == ID_VOICE) {
            if (IM.getInstance().isSpeakerVoice()) {
                IM.getInstance().setSpeakerVoice(false);
            } else {
                IM.getInstance().setSpeakerVoice(true);
            }
            IMVoiceManager.getInstance().onPlayMessage(mMessage, this);
        }
    }

    /**
     * 获取可视化控件
     */
    public BaseVisualizer getVisualizerView() {
        return mVisualizerView;
    }

    /**
     * 检查语音播放状态
     */
    public void checkVoiceStatus() {
        if (IM.getInstance().isSpeakerVoice()) {
            mStatusIcon.setImageResource(R.drawable.im_ic_speaker);
        } else {
            mStatusIcon.setImageResource(R.drawable.im_ic_erduo);
        }
        if (IMVoiceManager.getInstance().isPlaying(mMessage)) {
            //mStatusIcon.setImageResource(R.drawable.im_ic_pause);
            mVisualizerView.setVisibility(VISIBLE);
        } else {
            //mStatusIcon.setImageResource(R.drawable.im_ic_play);
            mVisualizerView.setVisibility(INVISIBLE);
        }
    }
}
