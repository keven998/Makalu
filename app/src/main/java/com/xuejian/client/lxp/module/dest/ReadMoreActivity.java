package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

/**
 * Created by yibiao.qin on 2015/7/11.
 */
public class ReadMoreActivity extends PeachBaseActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title=getIntent().getStringExtra("title");
        setContentView(R.layout.activity_read_more);
        TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.title_bar);
        titleHeaderBar.getTitleTextView().setText(title);
        titleHeaderBar.enableBackKey(true);
        String content = getIntent().getStringExtra("content");
        ((TextView) findViewById(R.id.tv_content)).setText(Html.fromHtml(content));
    }
}
