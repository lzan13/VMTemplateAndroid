package com.vmloft.develop.library.im.chat.msgitem;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.R;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.widget.VMFloatMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by lzan13 on 2019/6/2 22:38
 *
 * 普通消息基类
 */
public abstract class IMNormalItem extends IMBaseItem {

    protected static final int ID_COPY = 0;
    protected static final int ID_FORWARD = 1;
    protected static final int ID_DELETE = 2;

    protected VMFloatMenu mFloatMenu;
    protected List<VMFloatMenu.ItemBean> mFloatMenuList = new ArrayList<>();

    // 长按坐标
    protected int touchX;
    protected int touchY;

    public IMNormalItem(Context context, IMChatAdapter adapter, int type) {
        super(context, adapter, type);
    }

    /**
     * 初始化部分
     */
    @Override
    protected void init() {
        super.init();
        mContainerView.setOnTouchListener((View v, MotionEvent event) -> {
            touchX = (int) event.getRawX();
            touchY = (int) event.getRawY();
            return false;
        });
    }

    @Override
    public void onBind(int position, EMMessage message) {
        mPosition = position;
        mMessage = message;
        // 加载通用部分控件
        setupCommonView();
    }
    /**
     * 触发消息长按事件
     */
    public void onLongClick() {
        if (mFloatMenu == null) {
            mFloatMenu = new VMFloatMenu(getContext());
        }

        loadFloatMenu();

        // 排序
        Collections.sort(mFloatMenuList, (VMFloatMenu.ItemBean bean1, VMFloatMenu.ItemBean bean2) -> {
            if (bean1.itemId > bean2.itemId) {
                return 1;
            } else if (bean1.itemId < bean2.itemId) {
                return -1;
            }
            return 0;
        });

        mFloatMenu.clearAllItem();
        mFloatMenu.addItemList(mFloatMenuList);
        mFloatMenu.setItemClickListener((int id) -> {
            if (id == ID_FORWARD) {

            } else if (id == ID_DELETE) {

            } else {
                onFloatClick(id);
            }
        });
        mFloatMenu.showAtLocation(mContainerView, touchX, touchY);
    }

    /**
     * 加载悬浮才难
     */
    public void loadFloatMenu() {
        mFloatMenuList.clear();

        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_FORWARD, VMStr.byRes(R.string.im_msg_forward)));
        mFloatMenuList.add(new VMFloatMenu.ItemBean(ID_DELETE, VMStr.byRes(R.string.im_msg_delete)));
    }

    /**
     * 悬浮菜单点击事件
     */
    protected void onFloatClick(int id) {

    }
}
