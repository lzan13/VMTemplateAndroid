package com.vmloft.develop.library.im.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/6/5 15:43
 *
 * 自定义通话工具类，方便简单实现动画
 */
public class IMAnimator {

    // 重复模式
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    // 无限重复
    public static final int INFINITE = -1;

    // 动画类型
    public static final String ALPHA = "Alpha";
    public static final String TRANSX = "TranslationX";
    public static final String TRANSY = "TranslationY";
    public static final String SCALEX = "ScaleX";
    public static final String SCALEY = "ScaleY";
    public static final String ROTATION = "Rotation";
    public static final String ROTATIONX = "RotationX";
    public static final String ROTATIONY = "RotationY";

    // 默认执行时间
    public static final long mDuration = 1000;
    // 创建默认的插值器 LinearInterpolator 前后减速，中间加速
    public static final TimeInterpolator mInterpolator = new LinearInterpolator();

    /**
     * 创建动画管理者
     */
    public static AnimatorSetWrap createAnimator() {
        return new AnimatorSetWrap();
    }

    /**
     * 属性动画组合包装类，对应的是 AnimatorSet 类
     *
     * 它对应的主要有这四个方法，start(开始)，before(在 XXX 之前)，with(与 XXX 同步)，after(在 XXX 之后)
     * 这四个方法里面全都是填入往后儿们的 Animator 类，但是先后执行顺序不一样，
     * 我们注意到他是先执行的 after，然后是 start 和 with 同时执行，最后执行的 before
     * 所以大家记住这个顺序，无论怎么写，都是这个执行顺序。
     */
    public static class AnimatorSetWrap {
        // 联合动画的动画容器
        private AnimatorSet mAnimatorSet;
        // 联合动画的动画构造器
        private AnimatorSet.Builder mAnimatorBuilder;

        // 判断 start 方法只允许执行一次的布尔值
        boolean isPlaying = false;
        // 是否已经准备好动画
        boolean isReady = false;

        // 顺序播放或者同时播放时存储动画的列表容器
        List<Animator> mAnimatorList;

        /**
         * 私有构造，主要是负责
         * 1.初始化默认的插值器 mInterpolator
         * 2.初始化联合动画Set mAnimatorSet
         * 3.初始化顺序或同时播放动画容器 mAnimatorList
         */
        private AnimatorSetWrap() {
            mAnimatorSet = new AnimatorSet();
            mAnimatorList = new ArrayList<>(16);
        }

        /**
         * 播放多个动画时方法，在其内部生成一个 Animator 并将该 Animator 加入到动画集合中等待播放
         *
         * @param options 动画参数
         */
        public AnimatorSetWrap then(Options options) {
            ObjectAnimator animator = animator(options);
            mAnimatorList.add(animator);
            return this;
        }

        /**
         * 播放单个动画方法，整个动画过程只能调用一次，并且一旦执行 start 方法将会清空动画集合
         *
         * @param options 动画参数
         */
        public AnimatorSetWrap play(Options options) {
            if (isPlaying) {
                throw new RuntimeException("AnimatorSetWrap.start()方法只能调用一次");
            }
            ObjectAnimator animator = animator(options);
            mAnimatorList.clear();
            mAnimatorBuilder = mAnimatorSet.play(animator);
            return this;
        }

        /**
         * 封装 AnimatorSet 的 before 方法
         *
         * @param options 动画参数
         */
        public AnimatorSetWrap before(Options options) {
            ObjectAnimator animator = animator(options);
            mAnimatorBuilder = mAnimatorBuilder.before(animator);
            return this;
        }

        /**
         * 封装 AnimatorSet 的 with 方法
         *
         * @param options 动画参数
         */
        public AnimatorSetWrap with(Options options) {
            ObjectAnimator animator = animator(options);
            mAnimatorBuilder = mAnimatorBuilder.with(animator);
            return this;
        }

        /**
         * 封装 AnimatorSet 的 after 方法
         *
         * @param options 动画参数
         */
        public AnimatorSetWrap after(Options options) {
            ObjectAnimator animator = animator(options);
            mAnimatorBuilder = mAnimatorBuilder.after(animator);
            return this;
        }

        /**
         * 统一生成一个动画对象
         *
         * @param options 动画参数
         * @return
         */
        private ObjectAnimator animator(Options options) {
            if (options.target == null) {
                throw new RuntimeException("执行动画的目标不能为空");
            }
            isPlaying = true;
            ObjectAnimator animator = ObjectAnimator.ofFloat(options.target, options.anim, options.values);
            animator.setInterpolator(options.interpolator);
            animator.setDuration(options.duration);
            animator.setRepeatCount(options.repeat);
            animator.setRepeatMode(options.repeatMode);
            animator.setStartDelay(options.delay);
            return animator;
        }

        /**
         * 设置动画队列开始的延迟
         *
         * @param delay 延迟时间 毫秒值
         */
        public AnimatorSetWrap after(long delay) {
            mAnimatorBuilder.after(delay);
            return this;
        }

        /**
         * 开始执行动画，该动画操作主要用作执行 AnimatorSet 的组合动画，如果动画列表不为空，则执行逐一播放动画
         */
        public void start() {
            readyAnim(false);
            mAnimatorSet.start();
        }

        /**
         * 指定动画时长播放动画，如果动画列表不为空，则执行逐一播放动画
         *
         * @param duration 动画时长
         */
        public void start(long duration) {
            readyAnim(false);
            mAnimatorSet.setDuration(duration);
            mAnimatorSet.start();
        }

        /**
         * 开始执行动画，同时指定是否按照顺序执行
         */
        public void start(boolean together) {
            readyAnim(together);
            mAnimatorSet.start();
        }

        /**
         * 在一定时长内运行完该组合动画
         *
         * @param duration 动画时长
         */
        public void start(boolean together, long duration) {
            readyAnim(together);
            mAnimatorSet.setDuration(duration);
            mAnimatorSet.start();
        }

        /**
         * 延迟一定时长播放动画，如果动画列表不为空，则执行逐一播放动画
         *
         * @param delay 延迟时长
         */
        public void startDelay(long delay) {
            readyAnim(false);
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }

        /**
         * 延迟一定时长播放动画
         *
         * @param delay 延迟时长
         */
        public void startDelay(boolean together, long delay) {
            readyAnim(together);
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }

        /**
         * 准备动画，主要是初始化动画播放方式
         *
         * @param together 是否同时播放
         */
        private void readyAnim(boolean together) {
            if (isReady || mAnimatorList.size() <= 0) {
                return;
            }
            isReady = true;
            AnimatorSet set = new AnimatorSet();
            if (together) {
                set.playTogether(mAnimatorList);
            } else {
                set.playSequentially(mAnimatorList);
            }
            mAnimatorBuilder.before(set);
        }

        /**
         * 取消动画
         */
        public void cancel() {
            mAnimatorSet.cancel();
            mAnimatorList.clear();
        }

        /**
         * 获取AnimatorSet的实例
         */
        private AnimatorSet getAnimatorSet() {
            return mAnimatorSet;
        }

        /**
         * 添加动画监听
         */
        public AnimatorSetWrap addListener(Animator.AnimatorListener listener) {
            mAnimatorSet.addListener(listener);
            return this;
        }

        /**
         * 移除动画监听
         */
        public void removeListner(Animator.AnimatorListener listener) {
            mAnimatorSet.removeListener(listener);
        }

        /**
         * 取消全部AnimatorSet的监听
         */
        public void removeAllListeners() {
            mAnimatorSet.removeAllListeners();
        }

        /**
         * 判断一个View是否在当前的屏幕中可见（肉眼真实可见）
         *
         * @return 返回true则可见
         */
        public static boolean isVisibleOnScreen(View view) {
            if (view == null) {
                return false;
            }
            return view.getWindowVisibility() == View.VISIBLE && view.getVisibility() == View.VISIBLE && view.isShown();
        }
    }

    /**
     * 创建动画属性，
     */
    public static Options createOptions(Object target, String anim, float... values) {
        return createOptions(target, anim, mDuration, values);
    }

    /**
     * 创建动画属性，
     */
    public static Options createOptions(Object target, String anim, long duration, float... values) {
        return createOptions(target, anim, duration, 0, values);
    }

    /**
     * 创建动画属性，
     */
    public static Options createOptions(Object target, String anim, long duration, int repeat, float... values) {
        Options options = new Options();
        options.setTarget(target);
        options.setAnim(anim);
        options.setInterpolator(mInterpolator);
        options.setDuration(duration);
        options.setRepeat(repeat);
        options.setRepeatMode(RESTART);
        options.setDelay(0);
        options.setValues(values);
        return options;
    }

    /**
     * 动画执行参数
     */
    public static class Options {

        // 动画执行的目标
        public Object target;
        // 动画类型
        public String anim;
        // 动画插值器
        public TimeInterpolator interpolator;
        // 动画时长
        public long duration;
        // 动画重复次数
        public int repeat;
        // 重复模式
        public int repeatMode;
        // 动画开始延迟
        public int delay;
        // 动画执行值
        public float[] values;

        public void setTarget(Object target) {
            this.target = target;
        }

        public void setAnim(String anim) {
            this.anim = anim;
        }

        public void setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setRepeat(int repeat) {
            this.repeat = repeat;
        }

        public void setRepeatMode(int mode) {
            this.repeatMode = mode;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        /**
         * 设置不确定参数
         */
        public void setValues(float... values) {
            this.values = values;
        }
    }
}
