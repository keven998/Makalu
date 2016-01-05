package com.xuejian.client.lxp.module.RNView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yibiao.qin on 2015/10/26.
 */
public class ReactMainPage extends PeachBaseActivity implements DefaultHardwareBackBtnHandler {
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    public SimpleCommodityBean bean;
    public LinearLayout llEmptyView;
    RelativeLayout rlError;
    TextView tvRetry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long commodityId = getIntent().getLongExtra("commodityId", -1);
        showLoading();
     //   prepareJSBundle();

        Uri uri = getIntent().getData();
        if (uri != null) {
            if (TextUtils.isDigitsOnly(uri.getLastPathSegment())){
                commodityId = Long.parseLong(uri.getLastPathSegment());
            }
//            System.out.println(uri.getHost());
//            System.out.println(uri.getPath());
//            System.out.println(uri.getScheme());
//            System.out.println(uri.getQueryParameter("id"));
//            System.out.println(uri.getLastPathSegment());
//            System.out.println(uri.getPathSegments().toString());
        }

     //   mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("ReactNativeDevBundle.js")
                        //    .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("./src/index.android")
                        //         .setJSMainModuleName("./src/GoodDetailInfo")
                .addPackage(new MyReactPackage())
                .setUseDeveloperSupport(true)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        setContentView(R.layout.activity_rnview);
        mReactRootView = (ReactRootView) findViewById(R.id.root_view);
        llEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        rlError = (RelativeLayout)findViewById(R.id.rl_error);
        final long finalCommodityId = commodityId;
        findViewById(R.id.tv_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReactRootView.setVisibility(View.VISIBLE);
                rlError.setVisibility(View.GONE);
                getData(finalCommodityId);
            }
        });
        getData(commodityId);

//        Bundle bundle = new Bundle();
//        bundle.putString("haha", "hehe");
//        mReactRootView.startReactApplication(mReactInstanceManager, "GoodsDetail", bundle);

        //   System.out.println("time1 :" + (time2 - time1) + "\n time2 :" + (time3 - time2) + "\n time3 :" + (time4 - time3) + "\n time4 :" + (time5 - time4));

//        Handler handler  =new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("已发送");
//                WritableMap event = Arguments.createMap();
//                event.putString("result", "hahh");
//                sendEvent(mReactInstanceManager.getCurrentReactContext(), "test", event);
//            }
//        },5000);

        TextView back = (TextView) findViewById(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onPause();
        }
    }

    public void getData(long commodityId) {
        if (commodityId <= 0) return;
        TravelApi.getCommodity(commodityId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
//                    WritableMap event = Arguments.createMap();
//                    event.putString("result", jsonObject.getJSONObject("result").toString());
//                    sendEvent(mReactInstanceManager.getCurrentReactContext(), "test", event);

                        CommonJson<SimpleCommodityBean> commodity = CommonJson.fromJson(result, SimpleCommodityBean.class);
                        bean = commodity.result;
                        Bundle bundle = new Bundle();
                        bundle.putString("result", jsonObject.getJSONObject("result").toString());
                        mReactRootView.startReactApplication(mReactInstanceManager, "GoodsDetail", bundle);
                        ReactMainPage.this.findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShareUtils.showSelectPlatformDialog(ReactMainPage.this, null);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        llEmptyView.setVisibility(View.VISIBLE);
                        mReactRootView.setVisibility(View.GONE);
                    }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                if (code==404){
                    llEmptyView.setVisibility(View.VISIBLE);
                    mReactRootView.setVisibility(View.GONE);
                }else if (code==-1){
                    mReactRootView.setVisibility(View.GONE);
                    rlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    public void showLoading() {
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onResume(this);
        }
//        File[] files = getFilesDir().listFiles();
//        for (File file : files) {
//            System.out.println(file.getName());
//            System.out.println(file.getTotalSpace());
//            System.out.println(file.getPath());
//        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext, bean.creteShareBean(), requestCode, resultCode, data, null);
    }

    private static void copyFile(InputStream paramInputStream, OutputStream paramOutputStream)
            throws IOException {
        byte[] arrayOfByte = new byte[1024];
        for (; ; ) {
            int i = paramInputStream.read(arrayOfByte);
            if (i == -1) {
                break;
            }
            paramOutputStream.write(arrayOfByte, 0, i);
        }
    }

    private void prepareJSBundle() {
        Object localObject = new File(getFilesDir(), "ReactNativeDevBundle.js");
        if (((File) localObject).exists()) {
            return;
        }
        try {
            localObject = new FileOutputStream((File) localObject);
            InputStream localInputStream = getAssets().open("ReactNativeDevBundle.js");
            copyFile(localInputStream, (OutputStream) localObject);
            ((OutputStream) localObject).close();
            localInputStream.close();
            return;
        } catch (FileNotFoundException localFileNotFoundException) {
            localFileNotFoundException.printStackTrace();
            return;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

}

