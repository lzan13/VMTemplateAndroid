package com.vmloft.develop.app.match.common;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.bean.UserBean;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.base.IMException;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMStr;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by lzan13 on 2019/05/09 15:48
 *
 * 登录注册管理类
 */
public class ASignManager {

    private UserBean mPrevUser;
    private UserBean mCurrUser;

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private ASignManager() {
    }

    /**
     * 内部类实现单例模式
     */
    private static class InnerHolder {
        private static final ASignManager INSTANCE = new ASignManager();
    }

    /**
     * 获取的实例
     */
    public static final ASignManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 通过邮箱注册
     *
     * @param email    用户邮箱
     * @param password 用户密码
     * @param callback 回调
     */
    public void signUpByEmail(final String email, final String password, final ACallback<UserBean> callback) {
        // 注册用户体系账户
        Observable<UserBean> observable = Observable.create(new ObservableOnSubscribe<UserBean>() {
            @Override
            public void subscribe(final ObservableEmitter<UserBean> emitter) {
                final AVUser user = new AVUser();
                user.setUsername(email);
                user.setEmail(email);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            emitter.onNext(new UserBean(user));
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        // 注册 IM 账户
        observable.flatMap(new Function<UserBean, Observable<UserBean>>() {
            @Override
            public Observable<UserBean> apply(final UserBean bean) throws Exception {
                return Observable.create(new ObservableOnSubscribe<UserBean>() {
                    @Override
                    public void subscribe(final ObservableEmitter<UserBean> emitter) throws Exception {
                        IM.getInstance().signUp(bean.getId(), password, new IMCallback() {
                            @Override
                            public void onSuccess(Object o) {
                                emitter.onNext(bean);
                            }

                            @Override
                            public void onError(int code, String desc) {
                                emitter.onError(new IMException(code, desc));
                            }
                        });
                    }
                });
            }
        }).compose(ARXUtils.<UserBean>threadScheduler()).subscribe(new Observer<UserBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserBean user) {
                VMLog.d("注册成功 user:" + user);
                callback.onSuccess(user);
            }

            @Override
            public void onError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 通过邮箱登录
     *
     * @param email    用户邮箱
     * @param password 用户密码
     * @param callback 回调
     */
    public void signInByEmail(final String email, final String password, final ACallback<UserBean> callback) {
        // 登录 用户体系账户
        Observable<UserBean> observable = Observable.create(new ObservableOnSubscribe<UserBean>() {
            @Override
            public void subscribe(final ObservableEmitter<UserBean> emitter) throws Exception {
                AVUser.logInInBackground(email, password, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser user, AVException e) {
                        if (e == null) {
                            emitter.onNext(new UserBean(user));
                        } else {
                            emitter.onError(e);
                        }
                    }
                });
            }
        });
        // 登录 IM
        observable.flatMap(new Function<UserBean, Observable<UserBean>>() {
            @Override
            public Observable<UserBean> apply(final UserBean bean) throws Exception {
                return Observable.create(new ObservableOnSubscribe<UserBean>() {
                    @Override
                    public void subscribe(final ObservableEmitter<UserBean> emitter) throws Exception {
                        IM.getInstance().signIn(bean.getId(), password, new IMCallback() {
                            @Override
                            public void onSuccess(Object o) {
                                emitter.onNext(bean);
                            }

                            @Override
                            public void onError(int code, String desc) {
                                emitter.onError(new Throwable(desc));
                            }
                        });
                    }
                });
            }
        }).compose(ARXUtils.<UserBean>threadScheduler()).subscribe(new Observer<UserBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserBean user) {
                VMLog.d("登录成功 user:" + user);
                callback.onSuccess(user);
            }

            @Override
            public void onError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, callback);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 退出登录
     */
    public void signOut() {
        AVUser.logOut();
        setCurrUser(null);
    }

    /**
     * 判断是否登录
     */
    public boolean isSingIn() {
        if (getCurrUser() == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取上次登录的账户信息
     */
    public UserBean getPrevUser() {
        if (mPrevUser == null) {
            mPrevUser = parseUser(ASPManager.getInstance().getPrevUser());
        }
        return mPrevUser;
    }

    /**
     * 记录上次登录的账户，下次登录可以查询
     */
    public void setPrevUser(UserBean user) {
        mPrevUser = user;
        ASPManager.getInstance().putPrevUser(convertUser(user));
    }

    /**
     * 获取当前登录的账户信息
     */
    public UserBean getCurrUser() {
        if (mCurrUser == null) {
            mCurrUser = parseUser(ASPManager.getInstance().getCurrUser());
        }
        return mCurrUser;
    }

    /**
     * 记录当前登录的账户
     */
    public void setCurrUser(UserBean user) {
        mCurrUser = user;
        ASPManager.getInstance().putCurrUser(convertUser(user));
    }

    /**
     * 通过 json 文件解析用户信息，这个主要是保存的登录用户信息
     *
     * @param userStr 用户信息 json 串
     */
    public UserBean parseUser(String userStr) {
        if (VMStr.isEmpty(userStr)) {
            return null;
        }
        UserBean user = new UserBean();
        try {
            JSONObject object = new JSONObject(userStr);
            user.setUsername(object.optString("username"));
            user.setEmail(object.optString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 用户信息转为 json 串
     */
    public String convertUser(UserBean user) {
        if (user == null) {
            return "";
        }
        JSONObject object = new JSONObject();
        try {
            object.put("username", user.getUsername());
            object.put("email", user.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 重置
     */
    public void reset() {
        mPrevUser = null;
        mCurrUser = null;
        ASPManager.getInstance().putCurrUser("");
    }
}
