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

    public static final String ALPHA = "Alpha";
    public static final String TRANSX = "TranslationX";
    public static final String TRANSY = "TranslationY";
    public static final String SCALEX = "ScaleX";
    public static final String SCALEY = "ScaleY";
    public static final String ROTATION = "Rotation";
    public static final String ROTATIONX = "RotationX";
    public static final String ROTATIONY = "RotationY";

    /**
     * 创建动画管理者
     */
    public static AnimatorSetWrap createAnimator() {
        return new AnimatorSetWrap();
    }

    /**
     * 创建动画管理者，这里会指定一个插值器
     */
    public static AnimatorSetWrap createAnimator(TimeInterpolator interpolator) {
        return new AnimatorSetWrap(interpolator);
    }

    /**
     * 属性动画组合包装类，对应的是 AnimatorSet 类
     *
     * 它对应的主要有这四个方法，play(开启)，before(之前)，with(同步)，after(之后)
     * 这四个方法里面全都是填入往后儿们的 Animator 类，但是先后执行顺序不一样，
     * 分别对应着开启，最后，同步，最开始执行
     * 我们注意到他是先执行的 after，然后是 play 和 with 同时执行，最后执行的 before
     * 所以大家记住这个顺序，无论怎么写，都是这个执行顺序。
     */
    public static class AnimatorSetWrap {
        // 联合动画的动画容器
        private AnimatorSet mAnimatorSet;
        // 联合动画的动画构造器
        private AnimatorSet.Builder mAnimatorBuilder;
        // 动画默认插值器
        private TimeInterpolator mInterpolator;

        // 默认执行时间
        private int mDuration = 1000;
        // 默认循环次数
        private int mRepeat = 0;

        // 判断 play 方法只允许执行一次的布尔值
        boolean isPlaying = false;
        // 是否已经初始化then动画
        boolean mHasInitThenAnim = false;

        // 顺序播放或者同时播放时存储动画的列表容器
        List<Animator> mAnimatorList;

        /**
         * 私有构造，创建默认的插值器 LinearInterpolator 前后减速，中间加速
         */
        private AnimatorSetWrap() {
            this(new LinearInterpolator());
        }

        /**
         * 构造方法
         * 主要是负责
         * 1.初始化默认的插值器 mInterpolator
         * 2.初始化联合动画Set mAnimatorSet
         * 3.初始化顺序或同时播放动画容器 mAnimatorList
         *
         * @param interpolator
         */
        private AnimatorSetWrap(TimeInterpolator interpolator) {
            mInterpolator = interpolator;
            mAnimatorSet = new AnimatorSet();
            mAnimatorList = new ArrayList<>(16);
        }

        /**
         * 播放多个动画时方法，在其内部生成一个 Animator 并将该 Animator 加入到动画集合中等待播放
         *
         * @param target       动画执行的目标
         * @param anim         动画类型
         * @param interpolator 动画插值器
         * @param duration     动画时长
         * @param repeat       动画重复次数
         * @param values       动画执行值
         * @return
         */
        public AnimatorSetWrap then(Object target, String anim, @Nullable TimeInterpolator interpolator, int duration, int repeat, float... values) {
            if (target == null) {
                throw new RuntimeException("执行动画的目标不能为空");
            }
            isPlaying = true;
            ObjectAnimator thenAnimator = animator(target, anim, interpolator, duration, repeat, values);
            mAnimatorList.add(thenAnimator);
            return this;
        }

        /**
         * 播放单个动画方法，整个动画过程只能调用一次，并且一旦执行 play 方法将会清空动画集合
         *
         * @param options 动画参数
         * @return
         */
        public AnimatorSetWrap play(Options options) {
            if (isPlaying) {
                throw new RuntimeException("AnimatorSetWrap.play()方法只能调用一次");
            }
            if (options.target == null) {
                throw new RuntimeException("执行动画的目标不能为空");
            }
            isPlaying = true;
            ObjectAnimator playAnimator = animator(options);

            mAnimatorList.clear();
            mAnimatorBuilder = mAnimatorSet.play(playAnimator);
            return this;
        }

        /**
         * 播放单个动画方法，整个动画过程只能调用一次，并且一旦执行 play 方法将会清空动画集合
         *
         * @param target       动画执行的目标
         * @param anim         动画类型
         * @param interpolator 动画插值器
         * @param duration     动画时长
         * @param repeat       动画重复次数
         * @param values       动画执行值
         * @return
         */
        public AnimatorSetWrap play(Object target, String anim, @Nullable TimeInterpolator interpolator, int duration, int repeat, float... values) {
            if (isPlaying) {
                throw new RuntimeException("AnimatorSetWrap.play()方法只能调用一次");
            }
            if (target == null) {
                throw new RuntimeException("执行动画的目标不能为空");
            }
            isPlaying = true;
            ObjectAnimator playAnimator = animator(target, anim, interpolator, duration, repeat, values);

            mAnimatorList.clear();
            mAnimatorBuilder = mAnimatorSet.play(playAnimator);
            return this;
        }

        /**
         * AnimatorSet的Before方法
         *
         * @param target       动画执行的目标
         * @param anim         动画类型
         * @param interpolator 动画插值器
         * @param duration     动画时长
         * @param repeat       动画重复次数
         * @param values       动画执行值
         * @return
         */
        public AnimatorSetWrap before(Object target, String anim, @Nullable TimeInterpolator interpolator, int duration, int repeat, float... values) {
            if (target == null) {
                throw new RuntimeException("执行动画的目标不能为空");
            }
            ObjectAnimator beforeAnimator = animator(target, anim, interpolator, duration, repeat, values);
            mAnimatorBuilder = mAnimatorBuilder.before(beforeAnimator);
            return this;
        }

        /**
         * 统一生成一个动画对象
         *
         * @param target 动画执行的目标
         * @param anim   动画类型
         * @param values 动画执行值
         * @return
         */
        private ObjectAnimator animator(Object target, String anim, float... values) {
            return animator(target, anim, mInterpolator, mDuration, mRepeat, values);
        }

        /**
         * 统一生成一个动画对象
         *
         * @param target   动画执行的目标
         * @param anim     动画类型
         * @param duration 动画时长
         * @param values   动画执行值
         * @return
         */
        private ObjectAnimator animator(Object target, String anim, int duration, float... values) {
            return animator(target, anim, mInterpolator, duration, mRepeat, values);
        }

        /**
         * 统一生成一个动画对象
         *
         * @param target   动画执行的目标
         * @param anim     动画类型
         * @param duration 动画时长
         * @param repeat   动画重复次数
         * @param values   动画执行值
         * @return
         */
        private ObjectAnimator animator(Object target, String anim, int duration, int repeat, float... values) {
            return animator(target, anim, mInterpolator, duration, repeat, values);
        }

        /**
         * 统一生成一个动画对象
         *
         * @param target       动画执行的目标
         * @param anim         动画类型
         * @param interpolator 动画插值器
         * @param duration     动画时长
         * @param repeat       动画重复次数
         * @param values       动画执行值
         * @return
         */
        private ObjectAnimator animator(Object target, String anim, @Nullable TimeInterpolator interpolator, int duration, int repeat, float... values) {
            ObjectAnimator beforeAnimator = ObjectAnimator.ofFloat(target, anim, values);
            beforeAnimator.setInterpolator(interpolator == null ? mInterpolator : interpolator);
            beforeAnimator.setRepeatCount(repeat);
            beforeAnimator.setDuration(duration < 0 ? mDuration : duration);
            return beforeAnimator;
        }

        /**
         * 统一生成一个动画对象
         *
         * @param options 动画参数
         * @return
         */
        private ObjectAnimator animator(Options options) {
            ObjectAnimator beforeAnimator = ObjectAnimator.ofFloat(options.target, options.anim, options.values);
            beforeAnimator.setInterpolator(options.interpolator);
            beforeAnimator.setDuration(options.duration);
            beforeAnimator.setRepeatCount(options.repeat);
            return beforeAnimator;
        }

        public AnimatorSetWrap before(Animator animator) {
            mAnimatorBuilder = mAnimatorBuilder.before(animator);
            return this;
        }

        public AnimatorSetWrap before(AnimatorSetWrap animator) {
            mAnimatorBuilder = mAnimatorBuilder.before(animator.getAnimatorSet());
            return this;
        }


        public AnimatorSetWrap with(View view, String animName, @Nullable TimeInterpolator interpolator, @Size(min = 0, max = Integer.MAX_VALUE) int repeatCount, @Size(min = 0, max = Integer.MAX_VALUE) int duration, float... values) {
            if (view == null) {
                throw new RuntimeException("view 不能为空");
            }
            ObjectAnimator withAnimator = animator(view, animName, interpolator, repeatCount, duration, values);

            mAnimatorBuilder = mAnimatorBuilder.with(withAnimator);
            return this;
        }

        public AnimatorSetWrap with(Animator animator) {
            mAnimatorBuilder = mAnimatorBuilder.with(animator);
            return this;
        }

        public AnimatorSetWrap with(AnimatorSetWrap animator) {
            mAnimatorBuilder = mAnimatorBuilder.with(animator.getAnimatorSet());
            return this;
        }


        public AnimatorSetWrap after(View view, String animName, @Nullable TimeInterpolator interpolator, @Size(min = 0, max = Integer.MAX_VALUE) int repeatCount, @Size(min = 0, max = Integer.MAX_VALUE) int duration, float... values) {
            if (view == null) {
                throw new RuntimeException("view 不能为空");
            }
            ObjectAnimator afterAnimator = animator(view, animName, interpolator, repeatCount, duration, values);

            mAnimatorBuilder = mAnimatorBuilder.after(afterAnimator);
            return this;
        }

        public AnimatorSetWrap after(Animator animator) {
            mAnimatorBuilder = mAnimatorBuilder.after(animator);
            return this;
        }

        public AnimatorSetWrap after(AnimatorSetWrap animator) {
            mAnimatorBuilder = mAnimatorBuilder.after(animator.getAnimatorSet());
            return this;
        }


        public AnimatorSetWrap after(long delay) {
            mAnimatorBuilder.after(delay);
            return this;
        }

        /**
         * 直接执行动画，该动画操作主要用作执行AnimatorSet的组合动画
         * 如果mAnimatorList不为0 则执行逐一播放动画
         */
        public void playAnim() {
            if (mAnimatorList.size() > 0) {
                readyThen(true);
            }
            mAnimatorSet.start();
        }

        /**
         * 在一定时长内运行完该组合动画
         * 如果mAnimatorList不为0 则执行逐一播放动画
         *
         * @param duration 动画时长
         */
        public void playAnim(long duration) {
            if (mAnimatorList.size() > 0) {
                readyThen(true);
            }
            mAnimatorSet.setDuration(duration);
            mAnimatorSet.start();
        }

        /**
         * 延迟一定时长播放动画
         * 如果mAnimatorList不为0 则执行逐一播放动画
         *
         * @param delay 延迟时长
         */
        public void playAnimDelay(long delay) {
            if (mAnimatorList.size() > 0) {
                readyThen(true);
            }
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }

        /**
         * 直接执行动画，该动画操作主要用作执行AnimatorSet的组合动画
         */
        public void playAnim(boolean isSequentially) {
            readyThen(isSequentially);
            mAnimatorSet.start();
        }

        /**
         * 在一定时长内运行完该组合动画
         *
         * @param duration 动画时长
         */
        public void playAnim(boolean isSequentially, long duration) {
            readyThen(isSequentially);
            mAnimatorSet.setDuration(duration);
            mAnimatorSet.start();
        }

        /**
         * 延迟一定时长播放动画
         *
         * @param delay 延迟时长
         */
        public void playAnimDelay(boolean isSequentially, long delay) {
            readyThen(isSequentially);
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }

        /**
         * 顺序播放动画
         *
         * @param isSequentially 是逐一播放还是同时播放
         */
        private void readyThen(boolean isSequentially) {
            // 只在第一次启动时初始化
            if (mHasInitThenAnim) {
                return;
            }
            mHasInitThenAnim = true;
            if (mAnimatorList.size() > 0) {
                AnimatorSet set = new AnimatorSet();
                if (isSequentially) {
                    set.playSequentially(mAnimatorList);
                } else {
                    set.playTogether(mAnimatorList);
                }
                mAnimatorBuilder.before(set);
            }
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
         *
         * @return
         */
        private AnimatorSet getAnimatorSet() {
            return mAnimatorSet;
        }

        /**
         * 为 AnimatorSet 设置监听
         *
         * @param listener
         * @return
         */
        public AnimatorSetWrap setAnimatorSetListener(Animator.AnimatorListener listener) {
            mAnimatorSet.addListener(listener);
            return this;
        }

        /**
         * 取消AnimatorSet的监听
         *
         * @param listener
         */
        public void removeSetListner(Animator.AnimatorListener listener) {
            mAnimatorSet.removeListener(listener);
        }

        /**
         * 取消全部AnimatorSet的监听
         */
        public void removeAllLSetisteners() {
            mAnimatorSet.removeAllListeners();
        }

        /**
         * 判断一个View是否在当前的屏幕中可见（肉眼真实可见）
         *
         * @param mView
         * @return 返回true则可见
         */
        public static boolean isVisibleOnScreen(View mView) {
            if (mView == null) {
                return false;
            }
            return mView.getWindowVisibility() == View.VISIBLE && mView.getVisibility() == View.VISIBLE && mView.isShown();
        }
    }

    /**
     * 动画执行参数
     */
    class Options {
        private Object target;
        private String anim;
        private TimeInterpolator interpolator;
        private int duration;
        private int repeat;
        private float[] values;

        public Options() {
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public String getAnim() {
            return anim;
        }

        public void setAnim(String anim) {
            this.anim = anim;
        }

        public TimeInterpolator getInterpolator() {
            return interpolator;
        }

        public void setInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getRepeat() {
            return repeat;
        }

        public void setRepeat(int repeat) {
            this.repeat = repeat;
        }

        public float[] getValues() {
            return values;
        }

        public void setValues(float... values) {
            this.values = values;
        }
    }
}
