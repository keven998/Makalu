package com.aizou.peachtravel.config;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.BuildConfig;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.aizou.peachtravel.db.DaoMaster;
import com.aizou.peachtravel.db.DaoSession;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
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
    public void onCreate() {
        super.onCreate();
        initPeachConfig();
        initImageLoader();
//        refreshUserInfo();
//        BaseApi.testHttps();
        setupDatabase();
        initIM();
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
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(40 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .writeDebugLogs() // Remove for release app
                .build();


        ImageLoader.getInstance().init(config);
    }

    private void initPeachConfig(){
        if(BuildConfig.DEBUG){
            SystemConfig.BASE_URL = SystemConfig.DEBUG_BASE_URL;
        }else{
            SystemConfig.BASE_URL = SystemConfig.RELEASE_BASE_URL;
        }

    }


}
