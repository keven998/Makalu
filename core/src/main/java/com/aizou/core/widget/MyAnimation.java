package com.aizou.core.widget;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyAnimation {
	// 图标的动画(入动画)
	public static void startAnimationsIn(ViewGroup viewgroup,int in) {

//		viewgroup.setVisibility(0);
//		for (int i = 0; i < viewgroup.getChildCount(); i++) {
//			viewgroup.getChildAt(i).setVisibility(0);
//			viewgroup.getChildAt(i).setClickable(true);
//			viewgroup.getChildAt(i).setFocusable(true);
//		}
		Animation animation;
		animation = new RotateAnimation(-45+45*in, 0+45*in, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(500);
		viewgroup.startAnimation(animation);

	}

	// 图标的动画(出动画)
	public static void startAnimationsOut(final ViewGroup viewgroup,int out, int startOffset) {

		Animation animation;
		animation = new RotateAnimation(45-out*45, -out*45, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setFillAfter(true);
		animation.setDuration(500);
		animation.setStartOffset(startOffset);
//		animation.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation arg0) {}
//			@Override
//			public void onAnimationRepeat(Animation arg0) {}
//			@Override
//			public void onAnimationEnd(Animation arg0) {
//				viewgroup.setVisibility(8);
//				for (int i = 0; i < viewgroup.getChildCount(); i++) {
//					viewgroup.getChildAt(i).setVisibility(8);
//					viewgroup.getChildAt(i).setClickable(false);
//					viewgroup.getChildAt(i).setFocusable(false);
//				}
//			}
//		});
		viewgroup.startAnimation(animation);
	}

}







