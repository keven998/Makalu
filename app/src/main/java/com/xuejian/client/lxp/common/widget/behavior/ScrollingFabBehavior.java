package com.xuejian.client.lxp.common.widget.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.xuejian.client.lxp.common.utils.CommonUtils;

/**
 * Created by sunger on 2015/12/15.
 */
public class ScrollingFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolbarHeight;

    public ScrollingFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = CommonUtils.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin+20;
            float ratio = (float)dependency.getY()/(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }
}
