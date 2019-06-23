package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.request.bean.AResult;
import com.vmloft.develop.app.match.request.APIRequest;
import com.vmloft.develop.app.match.request.APIService;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.im.IM;
import com.vmloft.develop.library.im.base.IMCallback;
import com.vmloft.develop.library.im.common.IMException;
import com.vmloft.develop.library.tools.utils.VMStr;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by lzan13 on 2019/05/09 15:48
 *
 * 登录注册管理类
 */
public class ASignManager {

    // 当前登录账户
    private AAccount mCurrentAccount;
    private AAccount mHistoryAccount;

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
    public void signUpByEmail(String email, String password, final ACallback<AAccount> callback) {
        // 注册用户体系账户
        Observable<AResult<AAccount>> observable = APIRequest.getInstance().createApi(APIService.class).createAccount(email, password);
        observable.doOnNext((AResult<AAccount> result) -> {
            if (result.getCode() != 0) {
                throw new AException(result.getCode(), result.getMessage());
            }
        }).flatMap((AResult<AAccount> result) -> {
            // 注册 IM 账户
            AAccount account = result.getData();
            return Observable.create((final ObservableEmitter<AAccount> emitter) -> {
                IM.getInstance().signUp(account.getId(), password, new IMCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        emitter.onNext(account);
                    }

                    @Override
                    public void onError(int code, String desc) {
                        emitter.onError(new IMException(code, desc));
                    }
                });
            });
        }).compose(ARXUtils.<AAccount>threadScheduler()).subscribe(new AObserver<AAccount>(callback));
    }

    /**
     * 通过邮箱登录
     *
     * @param email    用户邮箱
     * @param password 用户密码
     * @param callback 回调
     */
    public void signInByEmail(final String email, final String password, final ACallback<AAccount> callback) {
        // 登录 用户体系账户
        Observable<AResult<AAccount>> observable = APIRequest.getInstance().createApi(APIService.class).loginAccount(email, password);
        observable.doOnNext((AResult<AAccount> result) -> {
            if (result.getCode() != 0) {
                throw new AException(result.getCode(), result.getMessage());
            }
        }).flatMap((AResult<AAccount> result) -> {
            // 登录 IM
            AAccount account = result.getData();
            return Observable.create((final ObservableEmitter<AAccount> emitter) -> {
                IM.getInstance().signIn(account.getId(), password, new IMCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        setCurrentAccount(account);
                        emitter.onNext(account);
                    }

                    @Override
                    public void onError(int code, String desc) {
                        emitter.onError(new AException(code, desc));
                    }
                });
            });
        }).compose(ARXUtils.<AAccount>threadScheduler()).subscribe(new AObserver<AAccount>(callback));
    }

    /**
     * 退出登录
     */
    public void signOut() {
        setCurrentAccount(null);
        IM.getInstance().signOut(false);

        reset();
    }

    /**
     * 判断是否登录
     */
    public boolean isSingIn() {
        if (getCurrentAccount() == null) {
            return false;
        }
        return true;
    }

    public AAccount getCurrentAccount() {
        if (mCurrentAccount == null) {
            mCurrentAccount = parseAccount(ASPManager.getInstance().getCurrUser());
        }
        return mCurrentAccount;
    }

    /**
     * 记录上次登录的账户，下次登录可以查询
     */
    public void setCurrentAccount(AAccount account) {
        mCurrentAccount = account;
        ASPManager.getInstance().putCurrUser(convertAccount(account));
    }

    /**
     * 获取上次登录的账户信息
     */
    public AAccount getHistoryAccount() {
        if (mHistoryAccount == null) {
            mHistoryAccount = parseAccount(ASPManager.getInstance().getPrevUser());
        }
        return mHistoryAccount;
    }

    /**
     * 记录上次登录的账户，下次登录可以查询
     */
    public void setHistoryAccount(AAccount account) {
        mHistoryAccount = account;
        ASPManager.getInstance().putPrevUser(convertAccount(account));
    }

    /**
     * 通过 json 文件解析用户信息，这个主要是保存的登录用户信息
     *
     * @param str 用户信息 json 串
     */
    private AAccount parseAccount(String str) {
        if (VMStr.isEmpty(str)) {
            return null;
        }
        AAccount account = new AAccount();
        try {
            JSONObject object = new JSONObject(str);
            account.setId(object.optString("id"));
            account.setUsername(object.optString("username"));
            account.setEmail(object.optString("email"));
            account.setPhone(object.optString("phone"));
            account.setToken(object.optString("token"));
            account.setAvatar(object.optString("avatar"));
            account.setCover(object.optString("cover"));
            account.setGender(object.optInt("gender"));
            account.setNickname(object.optString("nickname"));
            account.setSignature(object.optString("signature"));
            account.setAddress(object.optString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
    }

    /**
     * 用户信息转为 json 串
     */
    public String convertAccount(AAccount account) {
        if (account == null) {
            return "";
        }
        JSONObject object = new JSONObject();
        try {
            object.put("id", account.getId());
            object.put("username", account.getUsername());
            object.put("email", account.getEmail());
            object.put("phone", account.getPhone());
            object.put("token", account.getToken());
            object.put("avatar", account.getAvatar());
            object.put("cover", account.getCover());
            object.put("gender", account.getGender());
            object.put("nickname", account.getNickname());
            object.put("signature", account.getSignature());
            object.put("address", account.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 重置
     */
    public void reset() {
        mHistoryAccount = null;
        mCurrentAccount = null;
    }
}
