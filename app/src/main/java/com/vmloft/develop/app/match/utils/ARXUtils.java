package com.vmloft.develop.app.match.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lzan13 on 2019/05/11 22:33
 *
 * RxJava 相关工具类
 */
public class ARXUtils {

    /**
     * 封装 RxJava 线程切换
     */
    public static <T> ObservableTransformer<T, T> threadScheduler() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            }
        };
    }
}
