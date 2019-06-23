package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.request.bean.AMatch;
import com.vmloft.develop.app.match.request.bean.AResult;
import com.vmloft.develop.app.match.request.APIRequest;
import com.vmloft.develop.app.match.request.APIService;
import com.vmloft.develop.app.match.utils.ARXUtils;

import io.reactivex.Observable;

import java.util.List;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 匹配关系管理类
 */
public class AMatchManager {

    private AMatchManager() {}

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
        // 获取匹配信息
        Observable<AResult<AMatch>> observable = APIRequest.getInstance().createApi(APIService.class).createMatch();
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<AMatch>() {
            @Override
            public void doOnNext(AMatch match) {
                if (callback != null) {
                    callback.onSuccess(match);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }
        });
    }

    /**
     * 查询匹配列表
     */
    public void getMatchList(ACallback<List<AMatch>> callback) {
        getMatchList(1, 20, callback);
    }

    public void getMatchList(int page, int limit, ACallback<List<AMatch>> callback) {
        // 获取匹配信息
        Observable<AResult<List<AMatch>>> observable = APIRequest.getInstance().createApi(APIService.class).getMatchAll(page, limit);
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<List<AMatch>>() {
            @Override
            public void doOnNext(List<AMatch> list) {
                if (callback != null) {
                    callback.onSuccess(list);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }
        });
    }

    /**
     * 关闭匹配
     */
    public void stopMatch(AMatch match) {
        // 删除匹配信息
        Observable<AResult<AMatch>> observable = APIRequest.getInstance().createApi(APIService.class).removeMatch(match.getId());
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<AMatch>() {
            @Override
            public void doOnNext(AMatch match) {
                //if (callback != null) {
                //    callback.onSuccess(list);
                //}
            }

            @Override
            public void doOnError(Throwable e) {
                //AExceptionManager.getInstance().disposeException(e, callback);
            }
        });
    }
}
