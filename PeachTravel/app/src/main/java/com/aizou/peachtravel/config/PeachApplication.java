package com.aizou.peachtravel.config;

import android.support.v4.BuildConfig;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.StringUtil;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.PathUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.stat.common.User;

import java.io.File;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initPeachConfig();
        initImageLoader();
        refreshUserInfo();

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
                .denyCacheImageMultipleSizesInMemory()
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

    private void refreshUserInfo(){
        final PeachUser user = AccountManager.getInstance().getLoginAccountFromPref(this);
        if(user!=null){
            UserApi.getUserInfo(user,new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PeachUser> userResult = CommonJson.fromJson(result,PeachUser.class);
                    if(userResult.code==0){
                        AccountManager.getInstance().saveLoginAccount(context,userResult.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }
    }

}
