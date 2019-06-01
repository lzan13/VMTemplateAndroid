package com.vmloft.develop.app.match.im;

import android.app.Activity;
import android.content.Context;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.IIMGlobalListener;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.tools.widget.toast.VMToast;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/23 09:57
 *
 * 实现 IM 全局回调接口
 */
public class AIMGlobalListener implements IIMGlobalListener {

    /**
     * 同步获取联系人信息
     *
     * @param id 联系人 id
     */
    @Override
    public IMContact getIMContact(String id) {
        AUser user = AUMSManager.getInstance().getUser(id);
        IMContact contact = new IMContact(id);
        if (user != null) {
            contact.mUsername = user.getUsername();
            contact.mNickname = user.getNickname();
            contact.mAvatar = user.getAvatar() != null ? user.getAvatar().getUrl() : null;
        }
        return contact;
    }

    /**
     * 异步获取 IM 联系人信息
     *
     * @param id 联系人 id
     */
    @Override
    public void getIMContact(final String id, final IMCallback<IMContact> callback) {
        IMContact contact = new IMContact(id);
        AUMSManager.getInstance().getUser(id, new ACallback<AUser>() {
            @Override
            public void onSuccess(AUser user) {
                if (user != null) {
                    contact.mUsername = user.getUsername();
                    contact.mNickname = user.getNickname();
                    contact.mAvatar = user.getAvatar() != null ? user.getAvatar().getUrl() : null;
                }
                if (callback != null) {
                    callback.onSuccess(contact);
                }
            }

            @Override
            public void onError(int code, String desc) {
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
        ARouter.goUserDetail(context, contact.mId);
    }
}
