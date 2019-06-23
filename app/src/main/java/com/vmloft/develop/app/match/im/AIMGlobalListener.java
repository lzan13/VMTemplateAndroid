package com.vmloft.develop.app.match.im;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.common.AConstants;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.app.match.common.AUMSManager;
import com.vmloft.develop.app.match.glide.ALoader;
import com.vmloft.develop.app.match.router.ARouter;
import com.vmloft.develop.library.im.IIMGlobalListener;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.bean.IMContact;
import com.vmloft.develop.library.im.chat.IMChatAdapter;
import com.vmloft.develop.library.im.chat.msgitem.IMBaseItem;
import com.vmloft.develop.library.im.common.IMConstants;

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
        AAccount account = ASignManager.getInstance().getCurrentAccount();
        if (account != null && id.equals(account.getId())) {
            account = ASignManager.getInstance().getCurrentAccount();
        } else {
            account = AUMSManager.getInstance().getAccount(id);
        }
        IMContact contact = new IMContact(id);
        if (account != null) {
            contact.mUsername = account.getUsername();
            contact.mNickname = account.getNickname();
            contact.mAvatar = ALoader.wrapUrl(account.getAvatar());
        }
        return contact;
    }

    @Override
    public void updateIMContact(String id) {
        AUMSManager.getInstance().getAccount(id, null);
    }

    /**
     * 异步获取 IM 联系人信息
     *
     * @param id 联系人 id
     */
    @Override
    public void getIMContact(final String id, final IMCallback<IMContact> callback) {
        IMContact contact = new IMContact(id);
        AUMSManager.getInstance().getAccount(id, new ACallback<AAccount>() {
            @Override
            public void onSuccess(AAccount account) {
                if (account != null) {
                    contact.mUsername = account.getUsername();
                    contact.mNickname = account.getNickname();
                    contact.mAvatar = ALoader.wrapUrl(account.getAvatar());
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

    /**
     * 获取消息类型，供 IM 调用
     *
     * @param message 消息对象
     */
    @Override
    public int onMsgType(EMMessage message) {
        int extType = message.getIntAttribute(AConstants.MsgExt.MSG_EXT_TYPE, IMConstants.MsgType.IM_UNKNOWN);
        if (extType == AConstants.MsgExt.MSG_MATCH) {
            return extType;
        }
        return extType;
    }

    /**
     * 获取消息摘要，供 IM 调用
     *
     * @param message 消息对象
     */
    @Override
    public String onMsgSummary(EMMessage message) {
        if (onMsgType(message) == AConstants.MsgExt.MSG_MATCH) {
            return ((EMTextMessageBody) message.getBody()).getMessage();
        }
        return null;
    }

    /**
     * 生成消息展示控件，供 IM 调用
     *
     * @param context 上下文对象
     * @param adapter 消息适配器
     * @param type    消息类型
     */
    @Override
    public IMBaseItem onMsgItem(Context context, IMChatAdapter adapter, int type) {
        if (type == AConstants.MsgExt.MSG_MATCH) {
            return new AIMMatchItem(context, adapter, type);
        }
        return null;
    }
}
