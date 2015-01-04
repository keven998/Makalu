package com.aizou.peachtravel.module;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseWebViewActivity;
import com.aizou.peachtravel.common.widget.NumberProgressBar;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/13.
 */
public class PeachWebViewActivity extends BaseWebViewActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleHeaderBar;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
        titleHeaderBar.enableBackKey(true);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        mCurrentUrl = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        if(!TextUtils.isEmpty(title)){
            titleHeaderBar.getTitleTextView().setText(title);
        }
        LogUtil.d("webUrl=" + mCurrentUrl);
        mWebView.loadUrl(mCurrentUrl);

    }
}
