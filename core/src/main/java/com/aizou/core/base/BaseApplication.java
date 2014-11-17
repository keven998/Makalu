/**
 * 文件名	：BaseApplication.java
 * 创建日期	：2012-10-15
 * Copyright (c) 2003-2012 北京联龙博通

 * All rights reserved.
 */
package com.aizou.core.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.aizou.core.constant.LayoutValue;
import com.aizou.core.utils.LengthUtils;
import com.aizou.core.utils.LocalDisplay;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * 描述:BaseApplication
 * <p/>
 * <p/>
 * 基础Application
 * <p/>
 * <p/>
 *
 * @version 1.00
 */
public class BaseApplication extends Application {

    private static BaseApplication mBaseApplication;
    private static final String TAG = "BaseApplication";

    public static Context context;

    public static int APP_VERSION_CODE;

    public static String APP_VERSION_NAME;

    public static String APP_PACKAGE;

    public static File EXT_STORAGE;

    public static File APP_STORAGE;

    public static Properties BUILD_PROPS;

    public static String APP_NAME;

    public static Locale defLocale;

    public static Locale appLocale;

    public static boolean isExit = false;

    private List<Map<String, String>> signList;
    private Calendar systemCalendar;
    /**
     * 双击退出的消息处理
     */
    public Handler mHandlerExit = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseApplication = this;
//        LogGloble.i(TAG, "BaseApplicationon onCreate...");
//        LogManager.init(this);
        init();

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init();

        // 屏幕宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dm.widthPixels <= dm.heightPixels) {
            LayoutValue.SCREEN_WIDTH = dm.widthPixels;
            LayoutValue.SCREEN_HEIGHT = dm.heightPixels;
        } else {
            LayoutValue.SCREEN_WIDTH = dm.heightPixels;
            LayoutValue.SCREEN_HEIGHT = dm.widthPixels;
        }
        LocalDisplay.init(dm);
//		try {
//			String db_path = Environment.getExternalStorageDirectory()
//					.getAbsolutePath();
//			String db_name = SystemConfig.DB_NAME;
//			String db_path_name = db_path + File.separator + db_name;// 数据库的路径
//			LogGloble.d(TAG, "-----db Path " + db_path_name);
//			FileUtils.file_put_contents(db_path_name, getAssets()
//					.open("itl.db"));
//			dbUtils=DbUtils.create(context, db_path, db_name);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}// 把assert里的数据库存复制到相应的目录下
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
//        LogGloble.i(TAG, "BaseApplicationon onTerminate...");
        super.onTerminate();
    }

    /**
     * 描述:获取上下文
     *
     * @return
     */
    public static BaseApplication getContext() {
        return mBaseApplication;
    }

    /**
     * 初始化基础信息
     */
    protected void init() {
        context = getApplicationContext();
        final Configuration config = context.getResources().getConfiguration();
        appLocale = defLocale = config.locale;

        BUILD_PROPS = new Properties();
        try {
            BUILD_PROPS.load(new FileInputStream("/system/build.prop"));
        } catch (final Throwable th) {
        }

        final PackageManager pm = getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            APP_NAME = getString(pi.applicationInfo.labelRes);
            APP_VERSION_CODE
                    = pi.versionCode;
            APP_VERSION_NAME =
                    LengthUtils.safeString(pi.versionName, "DEV");
            APP_PACKAGE =
                    pi.packageName;
            EXT_STORAGE =
                    Environment.getExternalStorageDirectory();
            APP_STORAGE =
                    getAppStorage(APP_PACKAGE);
        } catch (final NameNotFoundException e) {
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        final Configuration oldConfig = getResources().getConfiguration();
        final int diff = oldConfig.diff(newConfig);
        final Configuration target = diff == 0 ? oldConfig : newConfig;

        if (appLocale != null) {
            setAppLocaleIntoConfiguration(target);
        }
        super.onConfigurationChanged(target);
    }

    protected File getAppStorage(final String appPackage) {
        File dir = EXT_STORAGE;
        if (dir != null) {
            final File appDir = new File(dir, "." + appPackage);
            if (appDir.isDirectory() || appDir.mkdir()) {
                dir = appDir;
            }
        } else {
            dir = context.getFilesDir();
        }
        dir.mkdirs();
        return dir.getAbsoluteFile();
    }

    public static void setAppLocale(final String lang) {
        final Configuration config = context.getResources().getConfiguration();
        appLocale = LengthUtils.isNotEmpty(lang) ? new Locale(lang) : defLocale;
        setAppLocaleIntoConfiguration(config);
    }

    protected static void setAppLocaleIntoConfiguration(
            final Configuration config) {
        if (!config.locale.equals(appLocale)) {
            Locale.setDefault(appLocale);
            config.locale = appLocale;
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
        }
    }


}
