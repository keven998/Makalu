package com.xuejian.client.lxp.module;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseWebViewActivity;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.common.widget.NumberProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/13.
 */
public class PeachWebViewActivity extends BaseWebViewActivity implements View.OnClickListener {

    @InjectView(R.id.web_view_go_back)
    ImageView go_back;
    @InjectView(R.id.web_view_go_forward)
    ImageView go_forward;
    @InjectView(R.id.web_view_refresh)
    ImageView refresh;
    @InjectView(R.id.web_view_share)
    ImageView share;
    String title;
    StrategyBean strategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
//        titleHeaderBar.enableBackKey(true);
        if (getIntent().getBooleanExtra("enable_bottom_bar", false)) {
            findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                  //  resetGoback();
                } else {
                    finish();
                }

            }
        });
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        mCurrentUrl = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.tv_title_bar_title)).setText(title);
        } else {
            title = "分享";
        }
        strategy = new StrategyBean();
        strategy.detailUrl = mCurrentUrl;
        strategy.title = title;
        go_back.setOnClickListener(this);
        go_forward.setOnClickListener(this);
        refresh.setOnClickListener(this);
        share.setOnClickListener(this);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mCurrentUrl);
       // resetForward();
       // resetGoback();
    }

    public void resetForward(){
        if(!mWebView.canGoForward()){
            go_forward.setEnabled(false);
        }else{
            go_forward.setEnabled(true);
        }
    }

    public void resetGoback(){
        if(!mWebView.canGoBack()){
            go_back.setEnabled(false);
        }else{
            go_back.setEnabled(true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_view_go_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                break;

            case R.id.web_view_go_forward:
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
                break;

            case R.id.web_view_refresh:
                mWebView.reload();
                break;

            case R.id.web_view_share:
                ShareUtils.showSelectPlatformDialog(PeachWebViewActivity.this, strategy);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            go_back.setEnabled(mWebView.canGoBack());
            go_forward.setEnabled(mWebView.canGoForward());
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            //设置程序的标题为网页的标题
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            go_back.setEnabled(mWebView.canGoBack());
            go_forward.setEnabled(mWebView.canGoForward());
        }
    }
}
