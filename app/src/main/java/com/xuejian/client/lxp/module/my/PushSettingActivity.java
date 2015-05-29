package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.hxsdk.controller.HXSDKHelper;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.config.SettingConfig;

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
    private EMChatOptions chatOptions;

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
                chatOptions.setNotifyBySoundAndVibrate(b);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                SettingConfig.getInstance().setLxqPushSetting(PushSettingActivity.this, b);
                HXSDKHelper.getInstance().getModel().setSettingMsgNotification(b);
            }
        });

        contentStatusIv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingConfig.getInstance().setAdPushSetting(PushSettingActivity.this, b);
            }
        });
        lxqLl.setOnClickListener(this);
        contentLl.setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.title_bar);
        titleBar.getTitleTextView().setText("消息和提醒");
        titleBar.enableBackKey(true);
    }

    private void initData() {
        chatOptions = EMChatManager.getInstance().getChatOptions();
        lxqStatus = chatOptions.getNotifyBySoundAndVibrate();
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
                chatOptions.setNotifyBySoundAndVibrate(lxqStatus);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                HXSDKHelper.getInstance().getModel().setSettingMsgNotification(lxqStatus);
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
