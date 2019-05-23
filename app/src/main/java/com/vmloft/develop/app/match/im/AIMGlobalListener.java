package com.vmloft.develop.app.match.im;

import android.app.Activity;
import android.content.Context;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.library.im.IMIGlobalListener;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.tools.widget.toast.VMToast;

/**
 * Create by lzan13 on 2019/5/23 09:57
 *
 * 实现 IM 全局回调接口
 */
public class AIMGlobalListener implements IMIGlobalListener {

    /**
     * 获取 IM 联系人对象
     *
     * @param id 联系人 id
     */
    @Override
    public void getIMContact(final String id, final IMCallback<IMContact> callback) {
        final IMContact contact = new IMContact(id);
        AUMSManager.getInstance().getUser(id, new ACallback<AUser>() {
            @Override
            public void onSuccess(AUser user) {
                contact.mUsername = user.getUsername();
                contact.mNickname = user.getNickname();
                contact.mAvatar = user.getAvatar() != null ? user.getAvatar().getUrl() : null;
                if (callback != null) {
                    callback.onSuccess(contact);
                }
            }
        });
    }

    /**
     * 联系人头像点击
     *
     * @param context 上下文对象
     * @param contact 点击的头像
     */
    @Override
    public void onHeadClick(Context context, IMContact contact) {
        VMToast.make((Activity) context, "点击头像 %s", contact.mId);
    }
}
