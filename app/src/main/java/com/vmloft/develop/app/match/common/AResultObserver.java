package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.request.bean.AResult;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Create by lzan13 on 2019/5/21 20:17
 *
 * 简单封装 Observer 主要是为了错误的统一处理
 */
public abstract class AResultObserver<T> implements Observer<AResult<T>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(AResult<T> result) {
        if (result.getCode() == 0) {
            doOnNext(result.getData());
        }else{
            doOnError(new AException(result.getCode(), result.getMessage()));
        }
    }

    @Override
    public void onError(Throwable e) {
        doOnError(e);
    }

    @Override
    public void onComplete() {

    }

    public abstract void doOnNext(T t);
    public abstract void doOnError(Throwable e);
}
