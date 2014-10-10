package com.aizou.core.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;


import com.aizou.core.base.BaseApplication;
import com.aizou.core.exception.CrashHandler;

import java.io.File;
import java.io.IOException;

/**
 *日志管理类
 */
public final class LogManager {
	
	private static final String TAG = "LogManager";

    public static File LOG_STORAGE;
    public static File LOG_STORAGE_LOG;

    static LogContext root;

    static CrashHandler handler;

    private LogManager() {
    }

    public static void init(final Context context) {
        LOG_STORAGE = new File(BaseApplication.APP_STORAGE, "logs");
        LOG_STORAGE_LOG = new File(BaseApplication.APP_STORAGE, "log");
        try {
			LOG_STORAGE_LOG.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogGloble.exceptionPrint(e);
		}
        LOG_STORAGE.mkdirs();

        final boolean debugEnabled = isDebugEnabledByDefault(context);
        Log.i(BaseApplication.APP_NAME, "Debug logging " + (debugEnabled ? "enabled" : "disabled") + " by default");

        root = new LogContext(BaseApplication.APP_NAME, debugEnabled);
        handler = CrashHandler.getInstance();
    }

    private static boolean isDebugEnabledByDefault(final Context context) {
        boolean debugEnabled = false;
        final PackageManager pm = context.getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            final int flags = ApplicationInfo.FLAG_DEBUGGABLE | 0x100 /* ApplicationInfo.FLAG_TEST_ONLY */;
            debugEnabled = (pi.applicationInfo.flags & flags) != 0;
        } catch (final NameNotFoundException ex) {
        	 LogGloble.w(TAG, "loadCatalog error", ex);
        }
        return debugEnabled;
    }

    public static LogContext root() {
        if (root == null) {
            root = new LogContext(BaseApplication.APP_PACKAGE, false);
        }
        return root;
    }

    public static void onUnexpectedError(final Throwable th) {
        handler.processException(th);
    }
}
