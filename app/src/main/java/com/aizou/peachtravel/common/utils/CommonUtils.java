/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aizou.peachtravel.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;

import com.aizou.peachtravel.bean.StartCity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;

public class CommonUtils {

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static int getScreenWidth(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        float[] results = new float[1];

        Location.distanceBetween(lat1, lon1, lat2, lon2, results);

        return results[0];

    }

    public static String getDistanceStr(double lat1, double lon1, double lat2, double lon2) {
        DecimalFormat df = new DecimalFormat(".0");
        double distance = getDistance(lat1, lon1, lat2, lon2);
        if (distance > 1000) {
            return df.format(distance / 1000d)+"km";
        } else {
            return (int) distance + "m";
        }

    }
    public static String getNearbyDistance(double lat1, double lon1, double lat2, double lon2) {
        DecimalFormat df = new DecimalFormat(".0");
        double distance = getDistance(lat1, lon1, lat2, lon2);
        if (distance > 1000) {
            if(distance>10000){
                return ">10km";
            }else{
                return df.format(distance / 1000d)+"km";
            }

        } else {
            return (int) distance + "m";
        }

    }

    public static boolean checkIntent(Context context,Intent intent){
        PackageManager packageManager = context.getPackageManager();
        //在系统中查询指定的Activity Action
        List resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if(resolveInfo.size() == 0){
            return false;
        }else{
            return true;
        }
    }


    public static String getLastModifyForHeader(Header[] headers){
        for(Header header:headers){
            if(header.getName().equals("Last-Modify")){
                return header.getValue();
            }
        }
        return null;

    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static Parcelable clone(Parcelable parcelable){
        Bundle bundle = new Bundle();
        bundle.putParcelable("clone",parcelable);
        return  bundle.getParcelable("clone");
    }

    public static ArrayList<StartCity> parserStartCityJson(Context context) {
        String json = getFromAssets(context, "startCity.json");
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<StartCity>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
