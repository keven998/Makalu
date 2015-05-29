package com.xuejian.client.lxp.module.toolbox.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/2.
 */
public class ShareActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.create_new_talk)
    TextView mCreateNewTalk;
    private ListView mImShareLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_im_share);
        mImShareLv = (ListView) findViewById(R.id.im_share_lv);
        View headerView = View.inflate(mContext, R.layout.header_im_share, null);
        mImShareLv.addHeaderView(headerView);
        mTitleBar.enableBackKey(true);
    }

    private void initData() {

    }
}