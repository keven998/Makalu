package com.aizou.peachtravel.module.my;


import android.os.Bundle;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.lidroid.xutils.ViewUtils;

/**
 * Created by Rjm on 2014/10/13.
 */
public class ResetPwdActivity extends PeachBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ViewUtils.inject(this);

    }
}
