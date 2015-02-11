package com.aizou.peachtravel.module.my;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.bean.UpdateBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.UpdateUtil;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;


public class SettingActivity extends PeachBaseActivity implements OnClickListener {
    // private View mTitlebar;
    private TextView versionUpdateLl, feedbackLl, xtLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        initTitlebar();
        versionUpdateLl = (TextView) findViewById(R.id.ll_version_update);
        feedbackLl = (TextView) findViewById(R.id.ll_feedback);
        xtLl = (TextView) findViewById(R.id.ll_xt);
        versionUpdateLl.setOnClickListener(this);
        feedbackLl.setOnClickListener(this);
        xtLl.setOnClickListener(this);
        findViewById(R.id.ll_clear_cache).setOnClickListener(this);
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
                update();
                break;

            case R.id.ll_feedback:
                Intent feedback = new Intent(mContext, FeedbackActivity.class);
                startActivity(feedback);
                break;

            case R.id.ll_xt:
                Intent pushIntent = new Intent(mContext, PushSettingActivity.class);
                startActivity(pushIntent);
                break;

            case R.id.ll_clear_cache:
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


                break;

            default:
                break;
        }
    }

    private void update() {
        DialogManager.getInstance().showLoadingDialog(SettingActivity.this, "正在检查更新");
        OtherApi.checkUpdate(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<UpdateBean> updateResult = CommonJson.fromJson(result, UpdateBean.class);
                if (updateResult.code == 0) {
                    if (updateResult.result.update) {
                        UpdateUtil.showUpdateDialog(mContext, "检测到新版本",
                                updateResult.result.downloadUrl);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("已是最新版本！");
                    }
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }
        });

//		LxpRequest.updateAfter(mContext, map, new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					byte[] responseBody) {
//				closeProgressDialog();
//				mUpdateResult = GsonTools.parseJsonToBean(new String(
//						responseBody), UpdateResult.class);
//				if (mUpdateResult.result.update) {
//					UpdateUtil.showUpdateDialog(mContext, "有新的版本!",
//							mUpdateResult.result.downloadUrl);
//				} else {
//					ToastUtil.getInstance(mContext).showToast("已是最新版本！");
//				}
//
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					byte[] responseBody, Throwable error) {
//				DialogManager.getInstance().dissMissLoadingDialog();
//
//			}
//
//		});
    }

}
