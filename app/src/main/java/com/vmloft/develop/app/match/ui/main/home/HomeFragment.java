package com.vmloft.develop.app.match.ui.main.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;

import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppLazyFragment;

import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.request.bean.AMatch;
import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.common.AMatchManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.im.AIMManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.app.match.utils.AUtils;
import com.vmloft.develop.library.im.chat.IMChatManager;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.tools.animator.VMAnimator;
import com.vmloft.develop.library.tools.picker.IPictureLoader;
import com.vmloft.develop.library.tools.router.VMParams;
import com.vmloft.develop.library.tools.utils.VMColor;
import com.vmloft.develop.library.tools.utils.VMDimen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by lzan13 on 2019/04/12
 *
 * 主界面
 */
public class HomeFragment extends AppLazyFragment {

    @BindView(R.id.home_swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.home_match_container) FrameLayout mMatchContainer;
    @BindView(R.id.home_match_cover) ImageView mMatchCoverView;

    // 自己
    private AAccount mAccount;
    private int avatarSize;

    // 正在匹配的人，使用 Map 是为了过滤掉重复的信息
    private Map<String, AMatch> mMatchMap = new HashMap<>();

    /**
     * Fragment 的工厂方法，方便创建并设置参数
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        super.initView();
        mRefreshLayout.setColorSchemeColors(VMColor.byRes(R.color.app_accent));
        mRefreshLayout.setOnRefreshListener(() -> {
            mRefreshLayout.setRefreshing(false);
            loadMatchData();
        });
    }

    @Override
    protected void initData() {

        mAccount = ASignManager.getInstance().getCurrentAccount();
        avatarSize = VMDimen.dp2px(48);

        // 加载背景
        IPictureLoader.Options options = new IPictureLoader.Options(ALoader.wrapUrl(mAccount.getAvatar()));
        options.isBlur = true;
        ALoader.load(mContext, options, mMatchCoverView);

        loadMatchData();
    }

    @OnClick({ R.id.home_match_text_border, R.id.home_match_voice_border })
    public void onClick(View view) {
        VMParams params = new VMParams();
        switch (view.getId()) {
        case R.id.home_match_text_border:
            params.arg0 = MatchActivity.MATCH_TYPE_TEXT;
            ARouter.goMatch(getActivity(), params);
            break;
        case R.id.home_match_voice_border:
            params.arg0 = MatchActivity.MATCH_TYPE_CALL;
            ARouter.goMatch(getActivity(), params);
            break;
        }
    }

    /**
     * 获取匹配数据
     */
    private void loadMatchData() {
        AMatchManager.getInstance().getMatchList(new ACallback<List<AMatch>>() {
            @Override
            public void onSuccess(List<AMatch> list) {
                for (AMatch match : list) {
                    // 过滤掉自己的匹配信息
                    String accountId = match.getAccountId();
                    if (accountId.equals(mAccount.getId())) {
                        continue;
                    }
                    mMatchMap.put(match.getId(), match);
                    // 只显示最近已定数量参与匹配的人
                    if (mMatchMap.size() >= 30) {
                        break;
                    }
                }
                setupMatchList();
            }

            @Override
            public void onError(int code, String desc) {
            }
        });
    }

    /**
     * 加载匹配数据
     */
    private void setupMatchList() {
        mMatchContainer.removeAllViews();
        for (AMatch match : mMatchMap.values()) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(avatarSize, avatarSize);

            int x = AUtils.random(mMatchContainer.getWidth() - avatarSize);
            int y = AUtils.random(mMatchContainer.getHeight() - avatarSize);
            imageView.setX(x);
            imageView.setY(y);
            imageView.setAlpha(0.0f);
            mMatchContainer.addView(imageView, lp);

            AAccount account = AUMSManager.getInstance().getAccount(match.getAccountId());
            if (account == null) {
                continue;
            }
            // 加载头像
            IPictureLoader.Options options = new IPictureLoader.Options(ALoader.wrapUrl(account.getAvatar()));
            if (AIMManager.getInstance().isCircleAvatar()) {
                options.isCircle = true;
            } else {
                options.isRadius = true;
                options.radiusSize = VMDimen.dp2px(4);
            }
            ALoader.load(mContext, options, imageView);

            imageView.setOnClickListener((View v) -> {
                startMatch(account);
            });

            // 动画出现
            long delay = AUtils.random(5) * 500;
            VMAnimator.Options animOptions = VMAnimator.createOptions(imageView, VMAnimator.ALPHA, 0.0f, 1.0f);
            animOptions.setRepeat(VMAnimator.INFINITE);
            animOptions.setRepeatMode(VMAnimator.REVERSE);
            VMAnimator.createAnimator().play(animOptions).startDelay(delay);
        }
    }

    /**
     * 开始匹配
     */
    private void startMatch(AAccount account) {
        String id = account.getId();
        int chatType = IMConstants.ChatType.IM_SINGLE_CHAT;
        int msgCount = IMChatManager.getInstance().getMessagesCount(id, chatType);
        // 判断是否匹配过
        boolean isMatch = (boolean) IMChatManager.getInstance().getConversationExt(id, chatType, AConstants.CHAT_EXT_MATCH, false);
        if (msgCount == 0 && !isMatch) {
            // 发送匹配扩展消息
            EMMessage message = IMChatManager.getInstance().createTextMessage("[匹配信息]", id, true);
            message.setAttribute(AConstants.MsgExt.MSG_EXT_TYPE, AConstants.MsgExt.MSG_MATCH);
            message.setAttribute(AConstants.MsgExt.MSG_EXT_MATCH_FATE, AUtils.random(90, 100));
            IMChatManager.getInstance().sendMessage(message, null);
            IMChatManager.getInstance().setConversationExt(id, chatType, AConstants.CHAT_EXT_MATCH, true);
        }
        IMRouter.goIMChat(mContext, id);
    }
}
