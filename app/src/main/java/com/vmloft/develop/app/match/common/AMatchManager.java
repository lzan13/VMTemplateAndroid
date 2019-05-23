package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AMatch;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.tools.utils.VMStr;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 匹配关系管理类
 */
public class AMatchManager {

    private AMatchManager(){}
    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final AMatchManager INSTANCE = new AMatchManager();
    }

    /**
     * 获取的实例
     */
    public static final AMatchManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 开启匹配
     */
    public void startMatch(final ACallback<AMatch> callback) {
        Observable<AMatch> observable = Observable.create(new ObservableOnSubscribe<AMatch>() {
            @Override
            public void subscribe(final ObservableEmitter<AMatch> emitter) throws Exception {
                final AMatch match = new AMatch();
                match.setUser(AVUser.getCurrentUser(AUser.class));
                match.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            emitter.onNext(match);
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        observable.compose(ARXUtils.<AMatch>threadScheduler()).subscribe(new AObserver<AMatch>(callback));
    }

    /**
     * 查询匹配列表
     */
    public void getMatchList(ACallback<List<AMatch>> callback) {
        Observable<List<AMatch>> observable = Observable.create(new ObservableOnSubscribe<List<AMatch>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<AMatch>> emitter) throws Exception {
                AVQuery<AMatch> query = AVObject.getQuery(AMatch.class);
                query.orderByDescending("createdAt");
                query.include("user");
                query.findInBackground(new FindCallback<AMatch>() {
                    @Override
                    public void done(List<AMatch> list, AVException e) {
                        if (e == null) {
                            emitter.onNext(list);
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        observable.compose(ARXUtils.<List<AMatch>>threadScheduler()).subscribe(new AObserver<List<AMatch>>(callback));
    }

    /**
     * 关闭匹配
     */
    public void stopMatch(AMatch match) {
        // 执行 CQL 语句实现删除一个对象
        String sql = VMStr.byArgs("delete from AMatch where objectId='%s'", match.getObjectId());
        AVQuery.doCloudQueryInBackground(sql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                // 如果 e 为空，说明操作成功
            }
        });
    }

}
