package com.vmloft.develop.library.im.chat;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.base.IMBaseActivity;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMNavBarUtil;
import com.vmloft.develop.library.tools.widget.VMViewPager;

import java.util.List;

/**
 * Create by lzan13 on 2019/05/17
 *
 * 图片预览界面
 */
public class IMPreviewActivity extends IMBaseActivity {

    // 预览图片集合
    protected List<EMMessage> mMessageList;

    protected String mId;
    protected int mChatType;
    protected EMMessage mCurrMessage;

    private int mCurrPosition;

    protected VMViewPager mViewPager;
    // 预览适配器
    protected IMPreviewPageAdapter mAdapter;
    // 确认图片的选择
    private View mBottomBar;
    private View mSpaceView;

    @Override
    protected int layoutId() {
        return R.layout.im_activity_preview;
    }

    /**
     * 初始化
     */
    @Override
    protected void initUI() {
        super.initUI();
        mViewPager = findViewById(R.id.im_preview_viewpager);
        mBottomBar = findViewById(R.id.im_preview_bottom_bar);
        mSpaceView = findViewById(R.id.im_preview_bottom_space);

        getTopBar().setTitleColor(R.color.vm_white_87);
    }

    @Override
    protected void initData() {
        mCurrMessage = getIntent().getParcelableExtra(IMConstants.IM_CHAT_MSG);
        mId = mCurrMessage.conversationId();
        mChatType = mCurrMessage.getChatType().ordinal();

        mMessageList = IMChatManager.getInstance().getCachePictureMessage(mId, mChatType);

        mCurrPosition = mMessageList.indexOf(mCurrMessage);

        getTopBar().setTitle(mCurrPosition + 1 + "/" + mMessageList.size());

        initViewPager();

        initNavBarListener();
    }

    /**
     * 初始化底部导航栏变化监听
     */
    private void initNavBarListener() {
        VMNavBarUtil.with(this).setListener(new VMNavBarUtil.OnNavBarChangeListener() {
            @Override
            public void onShow(int orientation, int height) {
                mSpaceView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = mSpaceView.getLayoutParams();
                if (layoutParams.height == 0) {
                    layoutParams.height = VMDimen.getNavigationBarHeight();
                    mSpaceView.requestLayout();
                }
            }

            @Override
            public void onHide(int orientation) {
                mSpaceView.setVisibility(View.GONE);
            }
        });
        VMNavBarUtil.with(this, VMNavBarUtil.ORIENTATION_HORIZONTAL).setListener(new VMNavBarUtil.OnNavBarChangeListener() {
            @Override
            public void onShow(int orientation, int height) {
                mTopBar.setPadding(0, 0, height, 0);
                mBottomBar.setPadding(0, 0, height, 0);
            }

            @Override
            public void onHide(int orientation) {
                mTopBar.setPadding(0, 0, 0, 0);
                mBottomBar.setPadding(0, 0, 0, 0);
            }
        });
    }

    /**
     * 初始化预览适配器
     */
    private void initViewPager() {
        mAdapter = new IMPreviewPageAdapter(mActivity, mMessageList);
        mAdapter.setPreviewClickListener(new IMPreviewPageAdapter.OnPreviewClickListener() {
            @Override
            public void onPreviewClick(View view, float v, float v1) {
                onPictureClick();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrPosition, false);

        // ViewPager 滑动的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrPosition = position;
                getTopBar().setTitle(mCurrPosition + 1 + "/" + mMessageList.size());
            }
        });
    }

    /**
     * 单击时，隐藏头和尾
     */
    public void onPictureClick() {
        if (mTopBar.getVisibility() == View.VISIBLE) {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.vm_fade_out));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.vm_fade_out));
            mTopBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        } else {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.vm_fade_in));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.vm_fade_in));
            mTopBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
        }
    }
}
