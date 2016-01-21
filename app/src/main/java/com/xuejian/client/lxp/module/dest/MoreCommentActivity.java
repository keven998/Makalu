package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseWebViewActivity;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.common.widget.NumberProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/13.
 */
public class MoreCommentActivity extends BaseWebViewActivity {

    //    @InjectView(R.id.ly_header_bar_title_wrap)
//    TitleHeaderBar titleBar;
    PoiDetailBean poiBean;
    String id;
    @Bind(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.bind(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        mTvTitleBarTitle.setText("更多点评");
//        titleBar.getTitleTextView().setText("游记详情");
        id = getIntent().getStringExtra("id");
        poiBean = getIntent().getParcelableExtra("poi");
        mWebView.loadUrl(poiBean.moreCommentsUrl);
//        titleBar.enableBackKey(true);
        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
