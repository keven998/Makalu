package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseWebViewActivity;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/13.
 */
public class TravelNoteDetailActivity extends BaseWebViewActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleBar;
    TravelNoteBean noteBean;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        initWebView();
        titleBar.getTitleTextView().setText("游记详情");
        titleBar.setRightViewImageRes(R.drawable.ic_share);
        id = getIntent().getStringExtra("id");
        noteBean = getIntent().getParcelableExtra("travelNote");
        mWebView.loadUrl(H5Url.TRAVEL_NOTE+id);
        titleBar.enableBackKey(true);
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMUtils.onClickImShare(mContext);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext,noteBean,requestCode,resultCode,data,null);
    }
}
