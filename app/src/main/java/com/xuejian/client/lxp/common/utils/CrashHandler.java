package com.xuejian.client.lxp.common.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.lv.im.IMClient;
import com.xuejian.client.lxp.module.SplashActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yibiao.qin on 2015/8/31.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("LXP", "crash !\n " + ex.getMessage());
        saveCrashInfo2File(ex);
        restartApplication();


        // if (!handleException(ex) && mDefaultHandler != null) {
        // mDefaultHandler.uncaughtException(thread, ex);
        // } else {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(10);
        // }

//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)
//                        .setMessage("程序崩溃了...").setNeutralButton("我知道了", new OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
//                    }
//                })
//                        .create().show();
//                Looper.loop();
//            }
//        }.start();
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        // new Handler(Looper.getMainLooper()).post(new Runnable() {
        // @Override
        // public void run() {
        // new AlertDialog.Builder(mContext).setTitle("提示")
        // .setMessage("程序崩溃了...").setNeutralButton("我知道了", null)
        // .create().show();
        // }
        // });

        return true;
    }
    public void restartApplication(){
       // System.exit(0);
//        Intent i = new Intent(mContext, SplashActivity.class);
//                i.addFlags(
//                  //      Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(i);
//        android.os.Process.killProcess(android.os.Process.myPid());


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(mContext, SplashActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(i);
//            }
//        });


        Intent mStartActivity = new Intent(mContext, SplashActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 700, mPendingIntent);
        System.exit(0);
    }
    private String saveCrashInfo2File(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMessage());
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
          //  copyDBToSDcrad(time,timestamp);
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }

        return null;
    }


//    private String saveCrashInfoToFile(Throwable ex) {
//        Writer info = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(info);
//        ex.printStackTrace(printWriter);
//
//        Throwable cause = ex.getCause();
//        while (cause != null) {
//            cause.printStackTrace(printWriter);
//            cause = cause.getCause();
//        }
//
//        String result = info.toString();
//        printWriter.close();
//        mDeviceCrashInfo.put(STACK_TRACE, result);
//        String fileName = "";
//        try {
//            long timestamp = System.currentTimeMillis();
//            fileName = "crash-" + timestamp + ".log";
//            FileOutputStream trace = mContext.openFileOutput(fileName,
//                    Context.MODE_PRIVATE);
//            mDeviceCrashInfo.store(trace, "");
//            trace.flush();
//            trace.close();
//            return fileName;
//        } catch (Exception e) {
//            Log.e(TAG, "an error occured while writing report file..."
//                    + fileName, e);
//        }
//        return null;
//    }

    private void copyDBToSDcrad(String time,long timestamp)
    {
        String path = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/crash/";
        String fileName = "db-" + time + "-" + timestamp + ".db";
        String oldPath = IMClient.getInstance().getDBFilename();
        copyFile(oldPath, path + fileName);
    }

    public static void copyFile(String oldPath, String newPath)
    {
        try
        {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists())
            {
                newfile.createNewFile();
            }
            if (oldfile.exists())
            { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1)
                {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
}