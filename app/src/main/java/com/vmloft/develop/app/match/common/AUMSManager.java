package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.im.common.IMConstants;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 存储用户集合
    private Map<String, AUser> mUserMap = new HashMap<>();

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
        Observable<AVFile> observable = Observable.create((final ObservableEmitter<AVFile> emitter) -> {
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
        });
        observable.flatMap((final AVFile file) -> {
            return Observable.create((final ObservableEmitter<AUser> emitter) -> {
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
            });
        }).compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AObserver<AUser>(callback));
    }

    /**
     * 保存用户信息
     */
    public void saveUserInfo(AUser user, ACallback<AUser> callback) {
        Observable<AUser> observable = Observable.create((final ObservableEmitter<AUser> emitter) -> {
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
        });
        observable.compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AObserver<AUser>(callback));
    }

    /**
     * 加载用户列表
     */
    public void loadUserList() {
        AVQuery<AUser> query = AVUser.getQuery(AUser.class);
        query.selectKeys(Arrays.asList("nickname", "avatar"));
        //  设置缓存策略，因为是预加载，所以这里会先从最快的缓存加载一下，然后去网络更新一下
        query.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
        // 设置缓存时间
        query.setMaxCacheAge(AConstant.TIME_WEEK);
        query.findInBackground(new FindCallback<AUser>() {
            @Override
            public void done(List<AUser> list, AVException e) {
                if (e == null) {
                    for (AUser user : list) {
                        mUserMap.put(user.getObjectId(), user);
                    }
                } else {
                    AExceptionManager.getInstance().disposeException(e, null);
                }
            }
        });
    }

    /**
     * 获取用户信息
     */
    public AUser getUser(String id) {
        AUser user = mUserMap.get(id);
        if (user == null) {
            getUser(id, null);
        }
        return user;
    }

    /**
     * 根据用户 id 查询缓存用户信息
     *
     * @param id 用户 id
     */
    public void getUser(final String id, final ACallback<AUser> callback) {
        Observable<AUser> observable = Observable.create((final ObservableEmitter<AUser> emitter) -> {
            AVQuery<AUser> query = AVUser.getQuery(AUser.class);
            //  设置缓存策略
            query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
            // 设置缓存时间
            query.setMaxCacheAge(AConstant.TIME_WEEK);
            query.getInBackground(id, new GetCallback<AUser>() {
                @Override
                public void done(AUser user, AVException e) {
                    if (e == null) {
                        mUserMap.put(user.getObjectId(), user);
                        emitter.onNext(user);
                    } else {
                        emitter.onError(e);
                    }
                }
            });
        });
        observable.compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AObserver<AUser>(callback));
    }
}
