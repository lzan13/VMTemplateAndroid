package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AMatch;
import com.vmloft.develop.app.match.bean.AUser;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 用户管理类
 */
public class AUMSManager {

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AUMSManager INSTANCE = new AUMSManager();
    }

    /**
     * 获取的实例
     */
    public static final AUMSManager getInstance() {
        return InnerHolder.INSTANCE;
    }


    /**
     * 获取匹配用户
     */
    public void getMatchUser(ACallback<List<AUser>> callback) {
        Observable observable = Observable.create(new ObservableOnSubscribe<AMatch>() {
            @Override
            public void subscribe(ObservableEmitter<AMatch> emitter) throws Exception {

            }
        });
    }
}
