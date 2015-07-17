package com.xuejian.client.lxp.common.utils;

import com.xuejian.client.lxp.config.SystemConfig;

import java.io.File;

/**
 * Created by Rjm on 2014/10/14.
 */
public class PathUtils {
    private static PathUtils instance;

    public static PathUtils getInstance() {
        if (instance == null) {
            instance = new PathUtils();
        }
        return instance;
    }

    public String getLocalImageCachePath() {
        String sdcardpath = android.os.Environment
                .getExternalStorageDirectory() + File.separator + SystemConfig.LOCAL_IMAGE_CACHE_DIR;
        return sdcardpath;
    }


}
