package com.xuejian.client.lxp.config;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v4.BuildConfig;
import android.widget.Toast;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.http.AuthenticationFailed;
import com.aizou.core.http.OkHttpClientManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.lv.net.HttpUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.CrashHandler;
import com.xuejian.client.lxp.module.my.LoginActivity;

import java.io.File;
import java.io.InputStream;


public class PeachApplication extends BaseApplication implements AuthenticationFailed{

    public static String ChannelId;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initPeachConfig();
        initChannelId();
        initImageLoader();
      //  LeakCanary.install(this);
        if (!com.xuejian.client.lxp.BuildConfig.DEBUG) {
            CrashHandler.getInstance().init(this);
        }
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OkHttpClientManager.getInstance().getmOkHttpClient()));
        OkHttpClientManager.getInstance().setOnAuthenticationFailed(this);
        HttpUtils.setAuthenticationFailed(new com.lv.net.AuthenticationFailed() {
            @Override
            public void onFailed(String msg) {
                System.out.println(msg);
                if (AccountManager.getInstance().getLoginAccount(PeachApplication.this)!=null){
                    logout();
                }
            }
        });
    }

    public void logout(){
        Toast.makeText(this,"授权失败，请重新登录",Toast.LENGTH_LONG).show();
        AccountManager.getInstance().logout(this);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }
    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, SystemConfig.NET_IMAGE_CACHE_DIR);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(2)
                .memoryCacheSizePercentage(10)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .memoryCache(new LruMemoryCache(1 * 1024 * 1024))
                .memoryCacheSize(1 * 1024 * 1024)
                .diskCacheSize(40 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initPeachConfig() {
        if (BuildConfig.DEBUG) {
            SystemConfig.BASE_URL = SystemConfig.DEBUG_BASE_URL;
        } else {
            SystemConfig.BASE_URL = SystemConfig.RELEASE_BASE_URL;
        }
    }

    public void initChannelId() {
        try {
            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            ChannelId = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFailed(String msg) {
        System.out.println(msg);
        if (AccountManager.getInstance().getLoginAccount(this)!=null){
            logout();
        }

    }
}
