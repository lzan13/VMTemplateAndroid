package com.vmloft.develop.app.match.ui.main.home;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.bean.AMatch;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.AConstant;
import com.vmloft.develop.app.match.common.AMatchManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.utils.AUtils;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.im.router.IMRouter;
import com.vmloft.develop.library.im.utils.IMAnimator;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Create by lzan13 on 2019/5/15 23:13
 *
 * 匹配界面
 */
public class MatchActivity extends AppActivity {

    @BindView(R.id.match_anim_view) View mAnimView;
    @BindView(R.id.match_avatar_iv) ImageView mAvatarView;
    @BindView(R.id.match_container) FrameLayout mMatchContainer;

    // 自己
    private AUser mUser;
    private AMatch mMatch;
    // 正在匹配的人
    private List<AMatch> mMatchList = new ArrayList<>();

    private int avatarSize;

    private IMAnimator.AnimatorSetWrap mAnimatorWrap;

    @Override
    protected int layoutId() {
        return R.layout.activity_match;
    }

    @Override
    protected void initUI() {
        super.initUI();
        startMatch();
    }

    @Override
    protected void initData() {
        setTopTitle(R.string.match);
        getTopBar().setTitleColor(R.color.app_title_light);

        mUser = ASignManager.getInstance().getCurrentUser();
        avatarSize = VMDimen.dp2px(48);

        setupUserInfo();

        loadMatchData();
    }

    /**
     * 装载用户信息
     */
    private void setupUserInfo() {
        String url = mUser.getAvatar() != null ? mUser.getAvatar().getUrl() : null;
        ALoader.loadAvatar(mActivity, url, mAvatarView);
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
                    if (match.getUser().getObjectId().equals(mUser.getObjectId())) {
                        continue;
                    }
                    mMatchList.add(match);
                }
                setupMatchList();
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, "好像有问题哎，稍后再来吧").error();
            }
        });
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private void startMatch() {

        AMatchManager.getInstance().startMatch(new ACallback<AMatch>() {
            @Override
            public void onSuccess(AMatch match) {
                mMatch = match;
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, "提交匹配信息失败").error();
            }
        });

        mAnimatorWrap = IMAnimator.createAnimator()
            .play(IMAnimator.createOptions(mAnimView, IMAnimator.SCALEX, 1500, IMAnimator.INFINITE, 0f, 20f))
            .with(IMAnimator.createOptions(mAnimView, IMAnimator.SCALEY, 1500, IMAnimator.INFINITE, 0f, 20f))
            .with(IMAnimator.createOptions(mAnimView, IMAnimator.ALPHA, 1500, IMAnimator.INFINITE, 1.0f, 0.0f));
        mAnimatorWrap.startDelay(200);
    }

    /**
     * 加载匹配数据
     */
    private void setupMatchList() {
        for (AMatch match : mMatchList) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(avatarSize, avatarSize);

            int x = AUtils.random(mMatchContainer.getWidth() - avatarSize);
            int y = AUtils.random(mMatchContainer.getHeight() - avatarSize);
            imageView.setX(x);
            imageView.setY(y);
            imageView.setAlpha(0.0f);
            mMatchContainer.addView(imageView, lp);

            final AUser user = match.getUser();
            String url = user.getAvatar() != null ? user.getAvatar().getUrl() : null;
            ALoader.loadAvatar(mActivity, url, imageView);

            imageView.setOnClickListener((View v) -> {
                IMRouter.goIMChat(mActivity, user.getObjectId());
                onFinish();
            });
            // 动画出现
            long delay = AUtils.random(5) * 500;
            IMAnimator.createAnimator().play(IMAnimator.createOptions(imageView, IMAnimator.ALPHA, 0.0f, 1.0f)).startDelay(delay);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMatch != null) {
            AMatchManager.getInstance().stopMatch(mMatch);
        }
        if (mAnimatorWrap != null) {
            mAnimatorWrap.cancel();
        }
        super.onDestroy();
    }
}
