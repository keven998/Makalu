package com.xuejian.client.lxp.common.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.widget.header.HeaderBarBase;
import com.xuejian.client.lxp.R;


/**
 * 普通标题头部的实现：
 * <ul>
 * <li>
 * 左侧返回
 * <li>
 * 中部标题
 * <li>
 * 右侧文字
 * </ul>
 * <p>
 * <a href="http://www.liaohuqiu.net/unified-title-header/">http://www.liaohuqiu.net/unified-title-header/</a>
 *
 * @author http://www.liaohuqiu.net
 */
public class TitleHeaderBar extends HeaderBarBase {
    private RelativeLayout containerRl;
    private TextView mTitleTextView;
    private TextView mRightTextView;
    private TextView mReturnImageView;
//	private View mMoreAction;

    public TitleHeaderBar(Context context) {
        this(context, null);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mReturnImageView = (TextView) findViewById(R.id.tv_title_bar_left);
        mTitleTextView = (TextView) findViewById(R.id.tv_title_bar_title);
        mRightTextView = (TextView) findViewById(R.id.tv_title_bar_right);
        containerRl = (RelativeLayout) findViewById(R.id.container);
//		mMoreAction = findViewById(R.id.ly_title_bar_more_action);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.base_header_bar_title;
    }

    public TextView getLeftTextView() {
        return mReturnImageView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public void showMoreMenu() {
        mRightTextView.setVisibility(GONE);
//		mMoreAction.setVisibility(VISIBLE);
    }

    public void setLeftViewImageRes(int res) {
        Drawable drawable = getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mReturnImageView.setCompoundDrawables(drawable, null, null, null);
    }

    public void setLeftDrawableToNull() {
        mReturnImageView.setCompoundDrawables(null, null, null, null);
    }

    public void setRightViewImageRes(int res) {
        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
    }

    public void enableBackKey(boolean enable) {
        if (enable) {
            findViewById(R.id.ly_title_bar_left).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) getContext()).finish();
                }
            });
        } else {
            findViewById(R.id.ly_title_bar_left).setVisibility(GONE);
        }
    }

    public void setBackground(int res) {
        containerRl.setBackgroundResource(res);
    }

    public void setRightView(String str) {
        mRightTextView.setText(str);
    }

    public void setRightOnClickListener(OnClickListener l) {
        findViewById(R.id.ly_title_bar_right).setOnClickListener(l);
    }

}