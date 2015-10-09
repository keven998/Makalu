package com.xuejian.client.lxp.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v4.BuildConfig;

import com.aizou.core.base.BaseApplication;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.xuejian.client.lxp.common.utils.CrashHandler;

import java.io.File;


public class PeachApplication extends BaseApplication {

    public static String ChannelId ;

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
        LeakCanary.install(this);
       if (!com.xuejian.client.lxp.BuildConfig.DEBUG){
            CrashHandler.getInstance().init(this);
         }

    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, SystemConfig.NET_IMAGE_CACHE_DIR);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3)
                .memoryCacheSizePercentage(20)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(cacheDir))
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

    public void initChannelId(){
        try {
            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
           ChannelId = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
