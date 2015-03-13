package com.aizou.peachtravel.common.utils;


import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.nineoldandroids.view.animation.AnimatorProxy;

public class StickyUtils {

	public static int calTranslationY(View stickyView, View headView,
			View holderView, ListView listView) {
		
		int scrollY = getScrollY(headView, listView);
//		Log.d("scroolY", scrollY+"");
		int translationY = Math.max(30,
				getTop(holderView) - scrollY);
		
//		if (endView != null) {
//			if (scrollY + stickyView.getHeight() > getTop(endView)) {
//				if (scrollY > getTop(endView)) {
//					translationY = -stickyView.getHeight();
//				} else {
//					translationY = getTop(endView) -scrollY
//							- stickyView.getHeight();
//				}
//			}
//		}
		return translationY;
	}

	private static int getTop(View v) {
		return v.getTop();
	}
	
	public static void translateY(View stickyView,int position) {
		if (AnimatorProxy.NEEDS_PROXY) {
			int l = stickyView.getLeft();
			int r = stickyView.getRight();
			stickyView.layout(l, position, r,
					position + stickyView.getHeight());
		} else {
			ViewHelper.setTranslationY(stickyView, position);
		}
	}
	
	public static int getScrollY(View headView,ListView listView) {
		View c = listView.getChildAt(0);
		if (c == null) {
			return 0;
		}

		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int top = c.getTop();

		int headerHeight = 0;
		if (firstVisiblePosition >= 1) {
			headerHeight = headView.getHeight();
		}

		return -top + firstVisiblePosition * c.getHeight() + headerHeight;
	}


}
