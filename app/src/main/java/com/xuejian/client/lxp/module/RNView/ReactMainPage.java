package com.xuejian.client.lxp.module.RNView;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.utils.ShareUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoading();
         prepareJSBundle();
        mReactRootView = new ReactRootView(this);
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
        Bundle bundle = new Bundle();
        bundle.putString("haha","hehe");
        mReactRootView.startReactApplication(mReactInstanceManager, "GoodsDetail", bundle);
        TextView back = (TextView) findViewById(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView iv = (ImageView) findViewById(R.id.iv_location);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PeachMessageDialog dialog = new PeachMessageDialog(mContext);
//                dialog.setTitle("From Native");
//                dialog.show();
//                dialog.setCanceledOnTouchOutside(true);
//                Intent intent = new Intent();
//                intent.setClass(ReactMainPage.this,TestActivity.class);
//                startActivity(intent);
                ShareUtils.showSelectPlatformDialog(ReactMainPage.this,null);
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

    public void showLoading(){
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        }catch (Exception e){
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
        File[] files = getFilesDir().listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            System.out.println(file.getTotalSpace());
            System.out.println(file.getPath());
        }
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

