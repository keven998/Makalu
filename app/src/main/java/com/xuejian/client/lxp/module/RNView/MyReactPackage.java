package com.xuejian.client.lxp.module.RNView;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jing on 15/9/22.
 */
public class MyReactPackage extends MainReactPackage {

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> main = super.createViewManagers(reactContext);
        List<ViewManager> result = new ArrayList<>();
        result.addAll(main);
    //    result.add(new ReactDotViewManager());
        return result;
    }
    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = super.createNativeModules(reactContext);
        //new an Array intstead of abstract List exception
        List<NativeModule> result = new ArrayList<>();
        result.addAll(modules);
        result.add(new NativeActions(reactContext));
        return result;
    }

//    @Override
//    public List<NativeModule> createNativeModules(
//            ReactApplicationContext reactContext) {
//        List<NativeModule> modules = super.createNativeModules(reactContext);
//        //new an Array intstead of abstract List exception
//        List<NativeModule> result = new ArrayList<>();
//        result.addAll(modules);
//        result.add(new ActivityTrans(reactContext));
//        return result;
//    }
}
