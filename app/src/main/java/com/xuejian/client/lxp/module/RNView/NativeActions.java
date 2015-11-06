package com.xuejian.client.lxp.module.RNView;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by yibiao.qin on 2015/10/29.
 */
public class NativeActions extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;
    public NativeActions(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "NativeActions";
    }

    @ReactMethod
    public void startActivity(String name){
        Intent intent= new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (name){
            case "Store":
                intent.setClass(reactContext,TestActivity.class);
                reactContext.startActivity(intent);
                break;
        }
    }
}
