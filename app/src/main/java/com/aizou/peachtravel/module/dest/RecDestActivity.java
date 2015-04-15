package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;

/**
 * Created by lxp_dqm07 on 2015/4/11.
 */
public class RecDestActivity extends PeachBaseActivity implements OnDestActionListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec_des);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestAdded(LocBean locBean) {

    }

    @Override
    public void onDestRemoved(LocBean locBean) {

    }
}
