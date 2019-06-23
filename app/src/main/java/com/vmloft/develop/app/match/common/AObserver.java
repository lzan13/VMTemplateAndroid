package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Create by lzan13 on 2019/5/21 20:17
 *
 * 简单封装 Observer 主要是为了错误的统一处理
 */
public class AObserver<T> implements Observer<T> {

    private ACallback<T> mCallback;

    public AObserver(ACallback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        if (mCallback != null) {
            mCallback.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        AExceptionManager.getInstance().disposeException(e, mCallback);
    }

    @Override
    public void onComplete() {

    }
}
