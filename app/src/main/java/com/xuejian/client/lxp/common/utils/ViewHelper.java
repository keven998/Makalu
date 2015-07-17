/**
 * ViewHelper.java
 * StickyScrollView
 * <p>
 * Created by likebamboo on 2014-4-21
 * Copyright (c) 1998-2014 https://github.com/likebamboo All rights reserved.
 */

package com.xuejian.client.lxp.common.utils;

import android.annotation.SuppressLint;
import android.view.View;

import com.nineoldandroids.view.animation.AnimatorProxy;


/**
 * 动画代理，来源于NineOldAndroids
 *
 * @author likebamboo
 */
public final class ViewHelper {
    private ViewHelper() {
    }

    public static float getTranslationY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getTranslationY() : Honeycomb.getTranslationY(view);
    }

    public static void setTranslationY(View view, float translationY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setTranslationY(translationY);
        } else {
            Honeycomb.setTranslationY(view, translationY);
        }
    }

    @SuppressLint("NewApi")
    private static final class Honeycomb {
        static float getTranslationY(View view) {
            return view.getTranslationY();
        }

        static void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }
    }
}
