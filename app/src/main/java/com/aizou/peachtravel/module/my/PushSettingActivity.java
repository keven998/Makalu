package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.config.SettingConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.socialize.utils.OauthHelper;

/**
 * Created by Rjm on 2014/10/15.
 */
public class PushSettingActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.ll_lxq)
    private LinearLayout lxqLl;
    @ViewInject(R.id.ll_content)
    private LinearLayout contentLl;
    @ViewInject(R.id.lxq_nofity_status)
    private CheckBox lxqStatusIv;
    @ViewInject(R.id.content_nofity_status)
    private CheckBox contentStatusIv;
    private boolean lxqStatus,contentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);
        ViewUtils.inject(this);
        initData();
        bindView();

        lxqStatusIv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingConfig.getInstance().setLxqPushSetting(PushSettingActivity.this, b);
            }
        });

        contentStatusIv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingConfig.getInstance().setAdPushSetting(PushSettingActivity.this, b);
            }
        });

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.title_bar);
        titleBar.getTitleTextView().setText("消息和提醒");
    }

    private void initData() {
        lxqStatus = SettingConfig.getInstance().getLxqPushSetting(this);
        contentStatus = SettingConfig.getInstance().getAdPushSetting(this);

    }
    private void bindView(){
        if (lxqStatus) {
//            lxqStatusIv.setBackgroundResource(R.drawable.cb_on_bind);
            lxqStatusIv.setChecked(true);
        } else {
//            lxqStatusIv.setBackgroundResource(R.drawable.cb_off_bind);
            lxqStatusIv.setChecked(false);
        }
        if (contentStatus) {
//            contentStatusIv.setBackgroundResource(R.drawable.cb_on_bind);
            contentStatusIv.setChecked(true);
        } else {
//            contentStatusIv.setBackgroundResource(R.drawable.cb_off_bind);
            contentStatusIv.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_lxq:
                lxqStatus = !lxqStatus;
                SettingConfig.getInstance().setLxqPushSetting(this,lxqStatus);
                bindView();
                break;

            case R.id.ll_content:
                contentStatus =!contentStatus;
                SettingConfig.getInstance().setAdPushSetting(this,contentStatus);
                bindView();
                break;
        }
    }
}
