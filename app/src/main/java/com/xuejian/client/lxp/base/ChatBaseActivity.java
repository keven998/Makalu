package com.xuejian.client.lxp.base;

import android.os.Bundle;


/**
 * Created by Rjm on 2014/10/21.
 */
public class ChatBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
