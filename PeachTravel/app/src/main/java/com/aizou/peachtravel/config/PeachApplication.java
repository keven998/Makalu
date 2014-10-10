package com.aizou.peachtravel.config;

import com.aizou.core.base.BaseApplication;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Rjm on 2014/10/9.
 */
public class PeachApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3)
                .memoryCacheSizePercentage(20)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(40 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                        .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);
    }

}
