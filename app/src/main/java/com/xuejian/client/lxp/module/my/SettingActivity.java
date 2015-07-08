package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.UpdateBean;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.UpdateUtil;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.config.SettingConfig;


public class SettingActivity extends PeachBaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        initTitlebar();
        findViewById(R.id.ll_version_update).setOnClickListener(this);
        findViewById(R.id.ll_clear_cache).setOnClickListener(this);

        CheckedTextView ctv = (CheckedTextView) findViewById(R.id.ll_xt);
        ctv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView cv = (CheckedTextView) v;
                boolean checked = !cv.isChecked();
                cv.setChecked(checked);
                SettingConfig.getInstance().setLxqPushSetting(SettingActivity.this, checked);
            }
        });

        boolean notifyStatus = SettingConfig.getInstance().getLxqPushSetting(this);
        ctv.setChecked(notifyStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_app_setting");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_app_setting");
    }

    private void initTitlebar() {
        TitleHeaderBar thb = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        thb.getTitleTextView().setText("设置");
        thb.enableBackKey(true);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_version_update:
                MobclickAgent.onEvent(mContext, "event_check_version_update");
                update();
                break;

            case R.id.ll_clear_cache:
                MobclickAgent.onEvent(mContext, "event_clear_cache");
                clearCache();
                break;

            default:
                break;
        }
    }

    private void clearCache() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定清除缓存？");
        dialog.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().clearDiskCache();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ToastUtil.getInstance(mContext).showToast("清除成功");
                            }
                        });
                    }
                }).start();

            }
        });
        dialog.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void update() {
        DialogManager.getInstance().showLoadingDialog(SettingActivity.this, "正在检查更新");
        OtherApi.checkUpdate(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<UpdateBean> updateResult = CommonJson.fromJson(result, UpdateBean.class);
                if (updateResult.code == 0) {
                    if (updateResult.result.update) {
                        UpdateUtil.showUpdateDialog(mContext, "检测到新版本",
                                updateResult.result.downloadUrl);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("已是最新版本");
                    }
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }
        });
    }

}
