package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.EventLogout;
import com.xuejian.client.lxp.bean.UpdateBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.common.utils.UpdateUtil;
import com.xuejian.client.lxp.common.widget.niceSpinner.NiceSpinner;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.config.SystemConfig;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.PeachWebViewActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;


public class SettingActivity extends PeachBaseActivity implements OnClickListener {
    TextView cacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + SystemConfig.NET_IMAGE_CACHE_DIR);
                if (file.exists()) {
                    final long size = getFolderSize(file);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            cacheSize.setText(Formatter.formatFileSize(mContext,size));
                        }
                    });
                }
            }
        }).start();

    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        cacheSize = (TextView) findViewById(R.id.tv_cache_size);
        findViewById(R.id.ll_version_update).setOnClickListener(this);
        findViewById(R.id.geek_apply).setOnClickListener(this);
        findViewById(R.id.ll_clear_cache).setOnClickListener(this);
        findViewById(R.id.ll_about_us).setOnClickListener(this);
        findViewById(R.id.recommend_app).setOnClickListener(this);
        findViewById(R.id.ll_tv_feedback).setOnClickListener(this);
        findViewById(R.id.logout_app).setOnClickListener(this);
        findViewById(R.id.setting_head_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        NiceSpinner spinner = (NiceSpinner) findViewById(R.id.sp_dev);
        final ArrayList<String> list = new ArrayList<>();
        list.add("OnLine");
        list.add("Beta");
        list.add("Dev");
        spinner.attachDataSource(list);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    SharedPreferencesUtil.saveValue(SettingActivity.this,"Header","");
                }else {
                    SharedPreferencesUtil.saveValue(SettingActivity.this,"Header",list.get(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String current = SharedPreferencesUtil.getStringValue(SettingActivity.this, "Header", "");
        if (TextUtils.isEmpty(current)){
            spinner.setSelectedIndex(0);
        }else {
            try {
                spinner.setSelectedIndex(list.indexOf(current));
            }catch (Exception e){

            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_app_setting");
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_app_setting");
        //MobclickAgent.onPause(this);
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recommend_app:
                ShareUtils.shareAppToWx(SettingActivity.this, null);
                break;
            case R.id.geek_apply:
                Intent applyIntent = new Intent(SettingActivity.this, TravelExpertApplyActivity.class);
                startActivity(applyIntent);
                break;
            case R.id.ll_about_us:
                Intent aboutIntent = new Intent(SettingActivity.this, PeachWebViewActivity.class);
                aboutIntent.putExtra("url", String.format("%s?version=%s", H5Url.ABOUT, getResources().getString(R.string.app_version)));
                aboutIntent.putExtra("title", "关于旅行派");
                startActivity(aboutIntent);
                break;
            case R.id.ll_version_update:
                update();
                break;
            case R.id.ll_clear_cache:
                clearCache();
                break;
            case R.id.ll_tv_feedback:
                Intent feedback = new Intent(SettingActivity.this, FeedbackActivity.class);
                startActivity(feedback);
                break;
            case R.id.logout_app:
                warnLogout();
                break;
            default:
                break;
        }
    }


    private void warnLogout() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定退出登录");
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    DialogManager.getInstance().showLoadingDialog(mContext, "正在登出");
                } catch (Exception e) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                UserApi.logout(AccountManager.getInstance().getLoginAccount(SettingActivity.this).getUserId(), new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        AccountManager.getInstance().logout(mContext);
                        DialogManager.getInstance().dissMissLoadingDialog();
                        EventBus.getDefault().postSticky(new EventLogout());
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });


            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void notice(boolean value) {
        SettingConfig.getInstance().setLxqPushSetting(SettingActivity.this, value);

    }

    private void clearCache() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("确定清除缓存？");
        dialog.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
                } catch (Exception e) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().clearDiskCache();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ToastUtil.getInstance(mContext).showToast("清除成功");
                                cacheSize.setText("0MB");
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
        if (!CommonUtils.isNetWorkConnected(SettingActivity.this)) {
            ToastUtil.getInstance(SettingActivity.this).showToast("请检查网络连接");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "正在检查更新");
        } catch (Exception e) {
            DialogManager.getInstance().dissMissLoadingDialog();
        }
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
