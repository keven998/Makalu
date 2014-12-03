package com.aizou.peachtravel.module.my;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

/**
 * Created by luoyong on 14/12/1.
 */
public class MessageContents extends PeachBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        TitleHeaderBar thb = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        thb.getTitleTextView().setText("消息中心");
        thb.enableBackKey(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
