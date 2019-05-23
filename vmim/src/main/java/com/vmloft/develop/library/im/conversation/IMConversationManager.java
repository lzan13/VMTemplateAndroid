package com.vmloft.develop.library.im.conversation;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.vmloft.develop.library.im.utils.IMConversationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Create by lzan13 on 2019/5/9 10:24
 *
 * IM 最近会话管理类
 */
public class IMConversationManager {

    /**
     * 私有的构造方法
     */
    private IMConversationManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        public static IMConversationManager INSTANCE = new IMConversationManager();
    }

    /**
     * 获取单例类实例
     */
    public static IMConversationManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void init(Context context) {
        // 将会话加载到内存
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    /**
     * 根据会话 id 获取会话
     *
     * @param id 会话 id
     */
    public EMConversation getConversation(String id) {
        return EMClient.getInstance().chatManager().getConversation(id);
    }

    /**
     * 获取全部会话，并进行排序
     */
    public List<EMConversation> getAllConversation() {
        Map<String, EMConversation> map = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> list = new ArrayList<>();
        list.addAll(map.values());
        // 排序
        Collections.sort(list, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                if (IMConversationUtils.getConversationLastTime(o1) > IMConversationUtils.getConversationLastTime(o2)) {
                    return -1;
                } else if (IMConversationUtils.getConversationLastTime(o1) < IMConversationUtils.getConversationLastTime(o2)) {
                    return 1;
                }
                return 0;
            }
        });

        // 排序之后，重新将置顶的条目设置到顶部
        List<EMConversation> result = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (IMConversationUtils.getConversationTop(list.get(i))) {
                result.add(count, list.get(i));
                count++;
            } else {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 获取当前会话的所有消息
     */
    public List<EMMessage> getAllMessages(String id) {
        return getConversation(id).getAllMessages();
    }
}
