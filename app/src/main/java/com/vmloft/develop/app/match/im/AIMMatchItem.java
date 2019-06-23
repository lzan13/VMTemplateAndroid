package com.vmloft.develop.app.match.im;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.msgitem.IMCardItem;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMStr;

/**
 * Create by lzan13 on 2019/6/10 21:21
 */
public class AIMMatchItem extends IMCardItem {

    private ImageView mAvatarView;
    private ImageView mAvatarSelfView;
    private TextView mFateView;

    private AAccount mAccount;

    public AIMMatchItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
        mAccount = ASignManager.getInstance().getCurrentAccount();
    }

    @Override
    protected boolean isReceiveMessage() {
        return mType == IMConstants.MsgType.IM_TEXT_RECEIVE;
    }

    @Override
    protected View layoutView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_item_match, null);
        mAvatarView = view.findViewById(R.id.match_msg_avatar_iv);
        mAvatarSelfView = view.findViewById(R.id.match_msg_avatar_self_iv);
        mFateView = view.findViewById(R.id.match_msg_fate_number_tv);
        return view;
    }

    @Override
    public void onBind(int position, EMMessage message) {
        super.onBind(position, message);

        int fate = message.getIntAttribute(AConstants.MsgExt.MSG_EXT_MATCH_FATE, 90);
        mFateView.setText(VMStr.byArgs("缘分指数 %d", fate));

        // 这条消息不需要显示时间
        mTimeView.setVisibility(GONE);

        loadContactInfo();
    }

    /**
     * 加载联系人信息
     */
    private void loadContactInfo() {
        // 对方头像
        mAvatarView.setOnClickListener((View v) -> {
            ARouter.goUserDetail(mContext, mContact.mId);
        });
        IPictureLoader.Options options = new IPictureLoader.Options(mContact.mAvatar);
        if (AIMManager.getInstance().isCircleAvatar()) {
            options.isCircle = true;
        } else {
            options.isRadius = true;
            options.radiusSize = VMDimen.dp2px(4);
        }
        ALoader.load(mContext, options, mAvatarView);

        // 自己头像
        options = new IPictureLoader.Options(ALoader.wrapUrl(mAccount.getAvatar()));
        if (AIMManager.getInstance().isCircleAvatar()) {
            options.isCircle = true;
        } else {
            options.isRadius = true;
            options.radiusSize = VMDimen.dp2px(4);
        }
        ALoader.load(mContext, options, mAvatarSelfView);
    }
}
