package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rjm on 2015/1/26.
 */
public class SpotIntroActivity extends PeachBaseActivity {
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.tv_spot_intro)
    TextView mTvSpotIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_intro);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("景点介绍");
        String content = getIntent().getStringExtra("content");
        mTvSpotIntro.setText(content);
        mTitleBar.enableBackKey(true);
    }
}
