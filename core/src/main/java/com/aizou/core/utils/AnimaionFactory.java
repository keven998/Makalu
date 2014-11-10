package com.aizou.core.utils;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

public class AnimaionFactory {
	/**
	 * 从上往下拉出动画
	 * 
	 * @return
	 */
	public static Animation getUpToDownAnimation() {
		ScaleAnimation animationEnter = new ScaleAnimation(1.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		animationEnter.setDuration(150);// 设置动画持续时间
		animationEnter.setFillAfter(false);
		return animationEnter;
	}

	/**
	 * 从下往上弹出动画器
	 * 
	 * @return
	 */
	public static Animation getFootEnterAnimation() {
		ScaleAnimation animationEnter = new ScaleAnimation(1.0f, 1.0f, 1.0f,
				0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		animationEnter.setDuration(100);// 设置动画持续时间
		animationEnter.setFillAfter(false);
		return animationEnter;
	}

	/**
	 * 从上往下弹出动画器
	 * 
	 * @return
	 */
	public static Animation getFootOutAnimation() {
		ScaleAnimation animationOut = new ScaleAnimation(1.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		animationOut.setDuration(100);// 设置动画持续时间
		animationOut.setFillAfter(false);
		return animationOut;
	}

	/**
	 * 从小到大变化的动画，先快后慢
	 * @return
	 */
	public static Animation getSmallToBigAnimation() {
		ScaleAnimation animationOut = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationOut.setDuration(2000);// 设置动画持续时间
		animationOut.setFillAfter(false);//是否停留在动画最后的状态
		animationOut.setInterpolator(new DecelerateInterpolator());//先快后慢
		return animationOut;
	}
}
