package com.aizou.peachtravel.module;

import android.os.Bundle;
import android.webkit.WebView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseWebViewActivity;

/**
 * Created by Rjm on 2014/12/13.
 */
public class PeachWebViewActivity extends BaseWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        mWebView = (WebView) findViewById(R.id.web_view);
        initWebView();
        mCurrentUrl = getIntent().getStringExtra("url");
        mWebView.loadUrl(mCurrentUrl);

    }
}
