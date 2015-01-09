package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseWebViewActivity;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.widget.NumberProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/13.
 */
public class MoreCommentActivity extends BaseWebViewActivity {

    //    @InjectView(R.id.ly_header_bar_title_wrap)
//    TitleHeaderBar titleBar;
    TravelNoteBean noteBean;
    String id;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        mTvTitleBarTitle.setText("更多点评");
//        titleBar.getTitleTextView().setText("游记详情");
        id = getIntent().getStringExtra("id");
        mWebView.loadUrl(H5Url.MORE_COMMENT + id);
//        titleBar.enableBackKey(true);
        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
