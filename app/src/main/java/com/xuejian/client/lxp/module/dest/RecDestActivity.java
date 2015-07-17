package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;

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
    public void onDestAdded(LocBean locBean, boolean isEdit, String type) {

    }

    @Override
    public void onDestRemoved(LocBean locBean, String type) {

    }
}
