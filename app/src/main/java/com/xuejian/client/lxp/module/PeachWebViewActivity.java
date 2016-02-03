package com.xuejian.client.lxp.module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.NumberProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/13.
 */
public class PeachWebViewActivity extends BaseWebViewActivity implements View.OnClickListener {

    @Bind(R.id.web_view_go_back)
    ImageView go_back;
    @Bind(R.id.web_view_go_forward)
    ImageView go_forward;
    @Bind(R.id.web_view_refresh)
    ImageView refresh;
    @Bind(R.id.web_view_share)
    ImageView share;
    String title;
    StrategyBean strategy;
    boolean  showAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.bind(this);
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
        showAnim = getIntent().getBooleanExtra("showAnim",false);
        if (showAnim){
            findViewById(R.id.ly_title_bar_left).setVisibility(View.GONE);
            findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
            go_back.setVisibility(View.GONE);
            go_forward.setVisibility(View.GONE);
            refresh.setImageResource(R.drawable.icon_close);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishWithNoAnim();
                    overridePendingTransition(0,R.anim.push_bottom_out);
                }
            });
        }
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        mCurrentUrl = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.tv_title_bar_title)).setText(title);
        } else {
            if (!TextUtils.isEmpty(mCurrentUrl)){
                Uri uri = Uri.parse(mCurrentUrl);
                ((TextView) findViewById(R.id.tv_title_bar_title)).setText(uri.getQueryParameter("title"));
            }
            title = "分享";
        }
        strategy = new StrategyBean();
        strategy.detailUrl = mCurrentUrl;
        strategy.title = title;
        go_back.setOnClickListener(this);
        go_forward.setOnClickListener(this);
        if (!showAnim)refresh.setOnClickListener(this);
        share.setOnClickListener(this);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mCurrentUrl);
        // resetForward();
        // resetGoback();
    }

    public void resetForward() {
        if (!mWebView.canGoForward()) {
            go_forward.setEnabled(false);
        } else {
            go_forward.setEnabled(true);
        }
    }

    public void resetGoback() {
        if (!mWebView.canGoBack()) {
            go_back.setEnabled(false);
        } else {
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
            //    ShareUtils.showSelectPlatformDialog(PeachWebViewActivity.this, strategy,"");
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

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("lvxingpai")) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.route");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse(url));
                if (CommonUtils.checkIntent(PeachWebViewActivity.this, intent))
                    startActivity(intent);
                return true;
            }
            if ((url.toLowerCase().startsWith("http://")) || (url.toLowerCase().startsWith("https://"))) {
                return false;
            }

            return true;
            // return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
