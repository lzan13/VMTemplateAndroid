package com.vmloft.develop.library.im.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Create by lzan13 on 2019/6/9 15:37
 */
public class IMDialog {

    public static void showAlertDialog(Context context, String title, String cancelStr, String okStr,
        DialogInterface.OnClickListener listener) {
        showAlertDialog(context, title, "", cancelStr, okStr, listener);
    }

    /**
     * 显示提醒对话框
     *
     * @param title     标题
     * @param message   内容
     * @param cancelStr 取消按钮文本
     * @param okStr     确认按钮文本
     * @param listener  确认事件回调
     */
    public static void showAlertDialog(Context context, String title, String message, String cancelStr, String okStr,
        DialogInterface.OnClickListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(cancelStr, (DialogInterface dialog, int which) -> {dialog.dismiss();})
            .setPositiveButton(okStr, listener)
            .create();
        alertDialog.show();
    }

    /**
     * 显示提醒对话框
     *
     * @param items 对话框列表内容
     */
    public static void showAlertDialog(Context context, String[] items, DialogInterface.OnClickListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setItems(items, listener).create();
        alertDialog.show();
    }
}
