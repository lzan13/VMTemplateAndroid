package com.vmloft.develop.library.im.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by lzan13 on 2019/5/21 11:11
 */
public class IMExecutor {

    /**
     * 单任务队列
     */
    private static ExecutorService mExecutorSingle = Executors.newSingleThreadExecutor();
    /**
     * 线程池队列
     */
    private static ExecutorService mExecutorPool = Executors.newCachedThreadPool();


    /**
     * 异步多任务执行
     */
    public static void asyncMultiTask(Runnable runnable) {
        mExecutorPool.submit(runnable);
    }

    /**
     * 异步单任务执行
     */
    public static void asyncSingleTask(Runnable runnable) {
        mExecutorSingle.submit(runnable);
    }

}
