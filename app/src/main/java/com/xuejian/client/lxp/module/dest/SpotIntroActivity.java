package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

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
        String spot = getIntent().getStringExtra("spot");
        mTitleBar.getTitleTextView().setText(spot);
        String content = getIntent().getStringExtra("content");
        mTvSpotIntro.setText(content);
        mTitleBar.enableBackKey(true);
    }
}