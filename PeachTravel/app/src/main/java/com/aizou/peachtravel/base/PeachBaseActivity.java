package com.aizou.peachtravel.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachBaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

    }
}
