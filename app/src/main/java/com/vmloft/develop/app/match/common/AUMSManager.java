package com.vmloft.develop.app.match.common;

import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.request.bean.AAccount;
import com.vmloft.develop.app.match.request.bean.AResult;
import com.vmloft.develop.app.match.request.APIRequest;
import com.vmloft.develop.app.match.request.APIService;
import com.vmloft.develop.app.match.utils.ARXUtils;
import com.vmloft.develop.library.tools.picker.bean.VMPictureBean;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

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
    }

    /**
     * 保存用户信息
     */
    public void updateAccountDetail(AAccount account, ACallback<AAccount> callback) {
        // 更新账户信息
        Observable<AResult<AAccount>> observable = APIRequest.getInstance()
            .createApi(APIService.class)
            .updateAccountDetail(account.getGender(), account.getNickname(), account.getSignature(), account.getAddress());
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
    }

    /**
     * 加载用户列表
     */
    public void loadAccountList() {
        loadAccountList(1, 20);
    }

    public void loadAccountList(int page, int limit) {
        // 获取账户列表
        Observable<AResult<List<AAccount>>> observable = APIRequest.getInstance().createApi(APIService.class).getAccountAll(page, limit);
        observable.compose(ARXUtils.threadScheduler()).subscribe(new AResultObserver<List<AAccount>>() {
            @Override
            public void doOnNext(List<AAccount> list) {
                for (AAccount account : list) {
                    mAccountMap.put(account.getId(), account);
                }
            }

            @Override
            public void doOnError(Throwable e) {
                AExceptionManager.getInstance().disposeException(e, null);
            }
        });
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
    }
}
