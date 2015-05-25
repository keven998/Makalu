package com.aizou.peachtravel.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.BuildConfig;

import com.aizou.core.base.BaseApplication;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.aizou.peachtravel.db.DaoMaster;
import com.aizou.peachtravel.db.DaoSession;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachApplication extends BaseApplication {
    public DaoSession daoSession;
    public static PeachHXSDKHelper hxSDKHelper = new PeachHXSDKHelper();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //MultiDex.install(this);
        initPeachConfig();
        initImageLoader();
//        refreshUserInfo();
//        BaseApi.testHttps();
        setupDatabase();
        initIM();
        IMClient.initIM(this);
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "peachtravel-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private void initIM(){
        hxSDKHelper.onInit(this);
    }


    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,SystemConfig.NET_IMAGE_CACHE_DIR);
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

    private void initPeachConfig(){
        if(BuildConfig.DEBUG){
            SystemConfig.BASE_URL = SystemConfig.RELEASE_BASE_URL;
        }else{
            SystemConfig.BASE_URL = SystemConfig.DEBUG_BASE_URL;
        }

    }


}
