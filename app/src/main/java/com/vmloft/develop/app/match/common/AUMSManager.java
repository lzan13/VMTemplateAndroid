package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.AAccount;
import com.vmloft.develop.app.match.bean.AResult;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.request.APIRequest;
import com.vmloft.develop.app.match.request.APIService;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Create by lzan13 on 2019/5/9 13:47
 *
 * 用户管理类
 */
public class AUMSManager {

    // 存储用户集合
    private Map<String, AAccount> mAccountMap = new HashMap<>();

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
    public void saveAvatar(final VMPictureBean bean, ACallback<AAccount> callback) {
        File file = new File(bean.path);
        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
//        PhotoRequestBody photoRequestBody = new PhotoRequestBody(body, new UploadProgress());
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), body);
        Observable<AResult<AAccount>> observable = APIRequest.getInstance().createApi(APIService.class).updateAccountAvatar(part);
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<AAccount>() {
            @Override
            public void doOnNext(AAccount account) {
                mAccountMap.put(account.getId(), account);
                if (callback != null) {
                    callback.onSuccess(account);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }
        });


//        Observable<AVFile> observable = Observable.create((final ObservableEmitter<AVFile> emitter) -> {
//            final AVFile file = AVFile.withAbsoluteLocalPath(bean.name, bean.path);
//            file.addMetaData("width", bean.width);
//            file.addMetaData("height", bean.height);
//            file.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(AVException e) {
//                    if (e == null) {
//                        emitter.onNext(file);
//                    } else {
//                        emitter.onError(e);
//                    }
//                }
//            });
//        });
//        observable.flatMap((final AVFile file) -> {
//            return Observable.create((final ObservableEmitter<AUser> emitter) -> {
//                final AUser user = ASignManager.getInstance().getCurrentAccount();
//                user.setAvatar(file);
//                user.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null) {
//                            emitter.onNext(user);
//                        } else {
//                            emitter.onError(e);
//                        }
//                    }
//                });
//            });
//        }).compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AResultObserver<AUser>(callback));
    }

    /**
     * 保存用户信息
     */
    public void updateAccountDetail(AAccount account, ACallback<AAccount> callback) {
        // 更新账户信息
        Observable<AResult<AAccount>> observable = APIRequest.getInstance().createApi(APIService.class).updateAccountDetail(account.getGender(), account.getNickname(), account.getSignature(), account.getAddress());
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<AAccount>() {
            @Override
            public void doOnNext(AAccount account) {
                mAccountMap.put(account.getId(), account);
                if (callback != null) {
                    callback.onSuccess(account);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }
        });
//
//        Observable<AUser> observable = Observable.create((final ObservableEmitter<AUser> emitter) -> {
//            user.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(AVException e) {
//                    if (e == null) {
//                        emitter.onNext(user);
//                    } else {
//                        emitter.onError(e);
//                    }
//                }
//            });
//        });
//        observable.compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AResultObserver<AUser>(callback));
    }

    /**
     * 加载用户列表
     */
    public void loadUserList() {
//        AVQuery<AUser> query = AVUser.getQuery(AUser.class);
//        query.selectKeys(Arrays.asList("nickname", "avatar", "signature"));
//        //  设置缓存策略，因为是预加载，所以这里会先从最快的缓存加载一下，然后去网络更新一下
//        query.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
//        // 设置缓存时间
//        query.setMaxCacheAge(AConstants.TIME_WEEK);
//        query.findInBackground(new FindCallback<AUser>() {
//            @Override
//            public void done(List<AUser> list, AVException e) {
//                if (e == null) {
//                    for (AUser user : list) {
//                        mUserMap.put(user.getObjectId(), user);
//                    }
//                } else {
//                    AExceptionManager.getInstance().disposeException(e, null);
//                }
//            }
//        });
    }

    /**
     * 获取用户信息
     */
    public AAccount getAccount(String id) {
        AAccount account = mAccountMap.get(id);
        if (account == null) {
            getAccount(id, null);
        }
        return account;
    }

    /**
     * 根据用户 id 查询缓存用户信息
     *
     * @param id 用户 id
     */
    public void getAccount(final String id, final ACallback<AAccount> callback) {
        // 获取账户信息
        Observable<AResult<AAccount>> observable = APIRequest.getInstance().createApi(APIService.class).getAccount(id);
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<AAccount>() {
            @Override
            public void doOnNext(AAccount account) {
                mAccountMap.put(account.getId(), account);
                if (callback != null) {
                    callback.onSuccess(account);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }
        });
//        observable.doOnNext((AResult<AAccount> result) -> {
//            if (result.getCode() != 0) {
//                throw new AException(result.getCode(), result.getMessage());
//            }
//        })
//        Observable<AUser> observable = Observable.create((final ObservableEmitter<AUser> emitter) -> {
//            AVQuery<AUser> query = AVUser.getQuery(AUser.class);
//            //  设置缓存策略
//            query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
//            // 设置缓存时间
//            query.setMaxCacheAge(AConstants.TIME_WEEK);
//            query.getInBackground(id, new GetCallback<AUser>() {
//                @Override
//                public void done(AUser user, AVException e) {
//                    if (e == null) {
//                        mUserMap.put(user.getObjectId(), user);
//                        emitter.onNext(user);
//                    } else {
//                        emitter.onError(e);
//                    }
//                }
//            });
//        });
//        observable.compose(ARXUtils.<AUser>threadScheduler()).subscribe(new AResultObserver<AUser>(callback));
    }
}
