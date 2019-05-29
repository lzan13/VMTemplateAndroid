package com.vmloft.develop.library.im.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by lzan13 on 2019/5/28 22:19
 *
 * IM 工具类
 */
public class IMUtils {

    /**
     * 单任务队列
     */
    private static ExecutorService mSingleExecutor = Executors.newSingleThreadExecutor();

    private static ExecutorService mMultiExecutor = Executors.newCachedThreadPool();

    /**
     * 单任务方式执行异步任务
     */
    public static void singleAsync(Runnable runnable) {
        mSingleExecutor.execute(runnable);
    }

    /**
     * 多任务方式异步任务
     */
    public static void multiAsync(Runnable runnable) {
        mMultiExecutor.execute(runnable);
    }
}
