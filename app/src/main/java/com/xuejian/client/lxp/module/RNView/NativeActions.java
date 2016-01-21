package com.xuejian.client.lxp.module.RNView;

import android.content.Intent;
import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.ShareCommodityBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.goods.OrderCreateActivity;
import com.xuejian.client.lxp.module.goods.StoreDetailActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import java.util.ArrayList;
import java.util.List;

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
    public void startActivity(String name, String data,String id,String data1) {
        Intent intent = new Intent();
        Gson gson = new Gson();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (name) {
            case "Store":
                intent.setClass(reactContext, StoreDetailActivity.class);
                intent.putExtra("sellerId",id);
                reactContext.startActivity(intent);
                break;
            case "Order":
                if (AccountManager.getInstance().getLoginAccount(reactContext)==null){
                    intent.putExtra("isFromGoods",true);
                    intent.setClass(reactContext, LoginActivity.class);
                    reactContext.startActivity(intent);
                }else {
                    ArrayList<PlanBean> list = gson.fromJson(data, new TypeToken<List<PlanBean>>() {
                    }.getType());
                    intent.putExtra("planList", list);
                    intent.putExtra("commodityId",id);
                    intent.putExtra("name",data1);
                    intent.setClass(reactContext, OrderCreateActivity.class);
                    reactContext.startActivity(intent);
                }
                break;
            case "Chat":
                if (AccountManager.getInstance().getLoginAccount(reactContext)==null){
                    intent.putExtra("isFromGoods",true);
                    intent.setClass(reactContext, LoginActivity.class);
                    reactContext.startActivity(intent);
                }else {
                    ShareCommodityBean shareCommodityBean = gson.fromJson(data, new TypeToken<ShareCommodityBean>() {
                    }.getType());
                    intent.putExtra("friend_id", id);
                    intent.putExtra("chatType", "single");
                    intent.putExtra("shareCommodityBean",shareCommodityBean);
                    intent.putExtra("fromTrade",true);
                    //     intent.putExtra("commodityId",id);
                    intent.setClass(reactContext, ChatActivity.class);
                    reactContext.startActivity(intent);
                }


                break;
            case "WebView":
                intent.putExtra("url",data);
                intent.putExtra("title",id);
                intent.setClass(reactContext, PeachWebViewActivity.class);
                reactContext.startActivity(intent);
                break;
            case "Detail":
                if (TextUtils.isDigitsOnly(id)){
                    intent.putExtra("commodityId",Long.parseLong(id));
                    intent.setClass(reactContext, ReactMainPage.class);
                    reactContext.startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @ReactMethod
    public void feedBack(long commodityid ,final Callback callBack) {
        TravelApi.getCommodity(commodityid, -1, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                callBack.invoke("finish");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });


    }
}
