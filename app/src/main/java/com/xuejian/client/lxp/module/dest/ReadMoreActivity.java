package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

/**
 * Created by yibiao.qin on 2015/7/11.
 */
public class ReadMoreActivity extends PeachBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more);
        TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.title_bar);
        titleHeaderBar.enableBackKey(true);
        String content = getIntent().getStringExtra("content");
        ((TextView) findViewById(R.id.tv_content)).setText("\u3000\u3000" + content);
    }
}
