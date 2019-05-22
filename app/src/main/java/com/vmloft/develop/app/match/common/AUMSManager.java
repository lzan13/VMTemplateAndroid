package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

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
     * 保存账户头像
     */
    public void saveAvatar(final VMPictureBean bean, ACallback<AUser> callback) {
        Observable<AVFile> observable = Observable.create(new ObservableOnSubscribe<AVFile>() {
            @Override
            public void subscribe(final ObservableEmitter<AVFile> emitter) throws Exception {
                final AVFile file = AVFile.withAbsoluteLocalPath(bean.name, bean.path);
                file.addMetaData("width", bean.width);
                file.addMetaData("height", bean.height);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            emitter.onNext(file);
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        observable.flatMap(new Function<AVFile, Observable<AUser>>() {
            @Override
            public Observable<AUser> apply(final AVFile file) throws Exception {
                return Observable.create(new ObservableOnSubscribe<AUser>() {
                    @Override
                    public void subscribe(final ObservableEmitter<AUser> emitter) throws Exception {
                        final AUser user = ASignManager.getInstance().getCurrentUser();
                        user.setAvatar(file);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    emitter.onNext(user);
                                } else {
                                    emitter.onError(e);
                                }
                            }
                        });
                    }
                });
            }
        }).compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AObserver<AUser>(callback));
    }

    /**
     * 保存用户信息
     */
    public void saveUserInfo(AUser user, ACallback<AUser> callback) {
        Observable<AUser> observable = Observable.create(new ObservableOnSubscribe<AUser>() {
            @Override
            public void subscribe(final ObservableEmitter<AUser> emitter) throws Exception {
                final AUser user = ASignManager.getInstance().getCurrentUser();
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            emitter.onNext(user);
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        observable.compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AObserver<AUser>(callback));
    }
}
