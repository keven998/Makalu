/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.CountryCodeBean;
import com.xuejian.client.lxp.bean.StartCity;
import com.xuejian.client.lxp.common.httpclient.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommonUtils {


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static String  getPriceString(double price){
        BigDecimal bd = new BigDecimal(price);
        BigDecimal  bd2 = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bd2.toString();
      //  return String.valueOf((double) Math.round(price * 100 / 100));
    }


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
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

//    public static String getTopActivity(Context context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//
//        if (runningTaskInfos != null)
//            return runningTaskInfos.get(0).topActivity.getClassName();
//        else
//            return "";
//    }

    public static int getScreenWidth(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }
    public static int getScreenHeight(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
        return display.getHeight();
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
            return df.format(distance / 1000d) + "km";
        } else {
            return (int) distance + "m";
        }

    }

    public static String getNearbyDistance(double lat1, double lon1, double lat2, double lon2) {
        DecimalFormat df = new DecimalFormat(".0");
        double distance = getDistance(lat1, lon1, lat2, lon2);
        if (distance > 1000) {
            if (distance > 10000) {
                return ">10km";
            } else {
                return df.format(distance / 1000d) + "km";
            }

        } else {
            return (int) distance + "m";
        }

    }

    public static boolean checkIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        //在系统中查询指定的Activity Action
        List resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        return resolveInfo.size() != 0;
    }


    public static String getLastModifyForHeader(Header[] headers) {
        for (Header header : headers) {
            if (header.getName().equals("Last-Modify")) {
                return header.getValue();
            }
        }
        return null;

    }

    public static String getLastModifyForHeader(Map<String, List<String>> headers) {
        if (headers==null)return "";
        if (!headers.containsKey("Last-Modify"))return "";
        return headers.get("Last-Modify").get(0);
    }
//    public static boolean isBackground(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.processName.equals(context.getPackageName())) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    Log.i("后台", appProcess.processName);
//                    return true;
//                } else {
//                    Log.i("前台", appProcess.processName);
//                    return false;
//                }
//            }
//        }
//        return false;
//    }

    public static Parcelable clone(Parcelable parcelable) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("clone", parcelable);
        return bundle.getParcelable("clone");
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

    public static ArrayList<CountryCodeBean> parserCountryCodeJson(Context context) {
        String json = getFromAssets(context, "countryCode.json");
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<CountryCodeBean>>() {
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
            result =  getString(buffer, "utf-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String getString(final byte[] data, final String charset) {
         notNull(data, "Input");
        return getString(data, 0, data.length, charset);
    }

    public static String getString(
            final byte[] data,
            int offset,
            int length,
            String charset) {
        notNull(data, "Input");
        notEmpty(charset, "Charset");
        try {
            return new String(data, offset, length, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }
    public static <T extends CharSequence> T notEmpty(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (TextUtils.isEmpty(argument)) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
        return argument;
    }
    public static <T> T notNull(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        return argument;
    }
    public static <E, T extends Collection<E>> T notEmpty(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (argument.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
        return argument;
    }
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static String getTimestampString(Date var0) {
        String var1 = null;
        long var2 = var0.getTime();
        if (isSameDay(var2)) {
            Calendar var4 = GregorianCalendar.getInstance();
            var4.setTime(var0);
            int var5 = var4.get(11);
            if (var5 > 17) {
                var1 = "晚上 hh:mm";
            } else if (var5 >= 0 && var5 <= 6) {
                var1 = "凌晨 hh:mm";
            } else if (var5 > 11 && var5 <= 17) {
                var1 = "下午 hh:mm";
            } else {
                var1 = "上午 hh:mm";
            }
        } else if (isYesterday(var2)) {
            var1 = "昨天 HH:mm";
        } else {
            var1 = "M月d日 HH:mm";
        }

        return (new SimpleDateFormat(var1, Locale.CHINA)).format(var0);
    }

    private static boolean isSameDay(long var0) {
        com.xuejian.client.lxp.common.utils.TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar var5 = Calendar.getInstance();
        var5.set(11, 23);
        var5.set(12, 59);
        var5.set(13, 59);
        var5.set(14, 999);
        Date var6 = var5.getTime();
        long var7 = var6.getTime();
        TimeInfo var9 = new TimeInfo();
        var9.setStartTime(var2);
        var9.setEndTime(var7);
        return var9;
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(5, -1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(5, -1);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static String getSystemProperty() {
        String line = null;
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = reader.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return "UNKNOWN";
    }
    public static int checkOp(Context context, int op){
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19){
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static String formatDuring(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return hours + " 小时 " + minutes + " 分 "
                + seconds + " 秒 ";
    }
}
