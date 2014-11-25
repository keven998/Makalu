package com.aizou.mapdemo.mapdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

/**
 * Created by Rjm on 2014/11/21.
 */
public class JMapActivity extends FragmentActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jmap);
        mWebView = (WebView) findViewById(R.id.wv_map);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/demo.html");
    }
}
