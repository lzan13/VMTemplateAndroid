package com.vmloft.develop.app.match.common;

import android.Manifest;
import android.content.Context;

import com.vmloft.develop.app.match.R;
import com.vmloft.develop.library.tools.permission.VMPermission;
import com.vmloft.develop.library.tools.permission.VMPermissionBean;

import java.util.ArrayList;
import java.util.List;

public class APermissionManager {

    /**
     * 私有构造
     */
    private APermissionManager() {
    }

    /**
     * 内部类实现单例
     */
    private static class InnerHolder {
        public static final APermissionManager INSTANCE = new APermissionManager();
    }

    /**
     * 获取单例类的实例
     */
    public static APermissionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    /**
     * 检查相机权限
     *
     * @param context
     */
    public boolean checkCameraPermission(Context context) {
        VMPermissionBean bean = new VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用");
        return checkPermission(context, bean);
    }

    /**
     * 检查相机权限
     *
     * @param context
     */
    public boolean checkStoragePermission(Context context) {
        VMPermissionBean bean = new VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写手机存储", "发送和保存图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用");
        return checkPermission(context, bean);
    }

    /**
     * 统一检查权限实现
     *
     * @param context 上下文对象
     * @param bean    权限实体
     * @return 是否授权
     */
    private boolean checkPermission(Context context, VMPermissionBean bean) {
        return VMPermission.getInstance(context)
            .setPermission(bean)
            .checkPermission(bean.permission);
    }

    /**
     * 请求相机权限
     *
     * @param context 上下文对象
     */
    private void requestCameraPermission(Context context) {
        VMPermissionBean bean = new VMPermissionBean(Manifest.permission.CAMERA, "访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用");
        requestPermission(context, bean);
    }

    /**
     * 请求单个权限，这个主要在调用某项功能检查权限未被授予的情况下调用
     *
     * @param context 上下文对象
     * @param bean    需要请求的权限实体类
     */
    private void requestPermission(Context context, VMPermissionBean bean) {
        VMPermission.getInstance(context)
            .setPermission(bean)
            .requestPermission(new VMPermission.PCallback() {
                @Override
                public void onReject() {}

                @Override
                public void onComplete() {}
            });
    }

    /**
     * 请求项目需要的权限
     */
    public void requestPermissions(Context context) {
        List<VMPermissionBean> list = new ArrayList<>();
        list.add(new VMPermissionBean(Manifest.permission.CAMERA, R.drawable.im_ic_camera,"访问相机", "拍摄照片需要访问相机，请允许我们获取访问相机权限，否则你将无法使用应用"));
        list.add(new VMPermissionBean(Manifest.permission.RECORD_AUDIO, R.drawable.im_ic_mic,"录音", "发送语音消息需要录音，请允许我们获取录音权限，否则你将无法发送语音消息"));
        list.add(new VMPermissionBean(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.drawable.ic_file, "读写手机存储", "发送和保存图片需要读写手机存储，请允许我们访问读写手机存储权限，否则你将无法使用应用"));
        VMPermission.getInstance(context)
            .setEnableDialog(false)
            .setPermissionList(list)
            .requestPermission(new VMPermission.PCallback() {
                @Override
                public void onReject() {}

                @Override
                public void onComplete() {}
            });
    }
}
