/*
 * Copyright 2012 flyrise. All rights reserved.
 * Create at 2012-6-6
 */
package com.aizou.peachtravel.common.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * <b>类功能描述：</b><div style="margin-left:40px;margin-top:-10px">
 * 动画设置器(封装了简单的动画)
 * 升级,改用建造者模式~~加callback方法~
 * </div>
 * @author <a href="mailto:184618345@qq.com">017</a>
 * @version 1.0  
 * </p>
 * 修改时间：</br>
 * 修改备注：</br>
 */
public class AnimationSimple {
	/**长时间的动画展示时间*/
	public static final int DURATIONTIME_1000 = 1000;
	/**较长的动画展示时间*/
	public static final int DURATIONTIME_500 = 500;
	/**较短的动画展示时间*/
	public static final int DURATIONTIME_300 = 300;
	/**极短的动画展示时间*/
	public static final int DURATIONTIME_100 = 100;

	/*--此工具类无需实例化--*/
	private AnimationSimple() {
	}

	/**
	 * 自身现在位置移动至新位置,两点之间移动动画
	 * @param view 需要绑定动画的控件,or Null
	 * @param durationTime 动画持续时间
	 * @param distanceX 绝对位移X
	 * @param distanceY 绝对位移Y
	 * @param interpolator 插值器,动画按照函数经行播放
	 * @return 移动动画
	 */
	public static TranslateAnimation move(View view, int durationTime, float distanceX, float distanceY, Interpolator interpolator) {
		TranslateAnimation ta = new TranslateAnimation(AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.ABSOLUTE, distanceX, AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.ABSOLUTE, distanceY);
		ta.setDuration(durationTime);
		if (interpolator != null) {
			ta.setInterpolator(interpolator);
		}
		if (view != null)
			view.startAnimation(ta);
		return ta;
	}

	/**
	 * 自身渐隐消失动画
	 * @param view 需要绑定动画的控件,or Null
	 * @param durationTime 动画持续时间
	 * @return 消失动画
	 */
	public static AlphaAnimation disappear(View view, int durationTime) {
		AlphaAnimation ta = new AlphaAnimation(1, 0);
		ta.setDuration(durationTime);
		if (view != null)
			view.startAnimation(ta);
		return ta;
	}

	/**
	 * 自身渐显出现动画
	 * @param view 需要绑定动画的控件,or Null
	 * @param durationTime 动画持续时间
	 * @return 出现动画
	 */
	public static AlphaAnimation appear(View view, int durationTime) {
		AlphaAnimation ta = new AlphaAnimation(0, 1);
		ta.setDuration(durationTime);
		if (view != null)
			view.startAnimation(ta);
		return ta;
	}

	/**
	 * 自身缩放动画
	 * @param view 需要绑定动画的控件,or Null
	 * @param durationTime 动画持续时间
	 * @param before 比例尺寸
	 * @param behind 比例尺寸
	 * @return 缩放动画
	 */
	public static ScaleAnimation scale(View view, int durationTime, float before, float behind) {
		// ScaleAnimation sa = new ScaleAnimation(before, behind, before, behind);
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, AnimationSet.RELATIVE_TO_SELF, 0.5f, AnimationSet.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(durationTime);
		if (view != null)
			view.startAnimation(sa);
		return sa;
	}

	/**
	 * 自身旋转动画
	 * @param view 需要绑定动画的控件,or Null
	 * @param durationTime 动画持续时间
	 * @param degrees 旋转角度,360度,正顺负逆
	 * @return 旋转动画
	 */
	public static RotateAnimation rotate(View view, int durationTime, float degrees) {
		RotateAnimation ra = new RotateAnimation(0, degrees, AnimationSet.RELATIVE_TO_SELF, 0.5f, AnimationSet.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(durationTime);
		ra.setInterpolator(new AccelerateDecelerateInterpolator());
		if (view != null)
			view.startAnimation(ra);
		view.setAnimation(ra);
		return ra;
	}
}
