package com.xuejian.client.lxp.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.aizou.core.dialog.ToastUtil;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Rjm on 2015/1/4.
 */
public class MapUtils {
    // 调用地图客户端uri
    // 例子：map_uri("龙泽","北京南站","北京");
    public final static String COMPANY_NAME = "aizou";
    public final static String APP_NAME = "旅行派";

    private static String getBDMapMarkerUri(double lat, double lng, String title, String content) {
        return "intent://map/marker?location=" + lat + "," + lng + "&title=" + title + "&content" +
                "=" + content + "&src=" + COMPANY_NAME + "|" + APP_NAME + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
    }

    private static String getAMapMarkerUri(double lat, double lng, String poiName) {
        return "androidamap://viewMap?sourceApplication=" + APP_NAME + "&poiname=" + poiName + "&lat=" + lat + "&lon=" + lng + "&dev=0";

    }

    private static String getBDMapNaviUri(double startLat, double startLng, String startName, double endLat, double endLng, String endName) {
        return "intent://map/direction?origin=latlng:" + startLat + "," + startLng + "|" +
                "name:" + startName + "&destination=latlng:" + endLat + "," + endLng + "|" +
                "name:" + endName + "&mode=driving&src=" + COMPANY_NAME + "|" + APP_NAME + "#Intent;scheme=bdapp;" +
                "package=com.baidu.BaiduMap;end";
    }

    private static String getAMapNaviUri(double startLat, double startLng, String startName, double endLat, double endLng, String endName) {
        return "androidamap://route?sourceApplication=" + APP_NAME + "&slat=" + startLat + "&slon=" + startLng + "&sname=" + startName + "&dlat=" + endLat + "&dlon=" + endLng + "&dname=" + endName + "&dev=0&m=0&t=2&showType=1";
    }


    private static boolean isInstallByread(Context context,String packageName) {
        boolean isInstall=false;
        try {
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(packageName, 0);
            isInstall = packageInfo != null;
        } catch (Exception e) {
            isInstall=false;
        }
        return isInstall;
    }

    public static void showSelectMapDialog(final Context context, final double startLat, final double startLng, final String startName, final double endLat, final double endLng, final String endName) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        // 设置对话框的图标
        // b.setIcon(R.drawable.tools);
        // 设置对话框的标题
        b.setTitle("请选择您地图客户端：");
        boolean baidumap = isInstallByread(context,"com.baidu.BaiduMap");
        boolean gaodemap = isInstallByread(context,"com.autonavi.minimap");
        final ArrayList<String> typeList = new ArrayList<>();
        int tag = 0;
        if (baidumap) {
            typeList.add("百度地图");
        }
        if (gaodemap) {
            typeList.add("高德地图");
        }
        if (typeList.size() == 0) {
            ToastUtil.getInstance(context).showToast("请先安装百度地图或高德地图");
            return;
        }
        String[] typeArray=new String[typeList.size()];
        typeList.toArray(typeArray);
        b.setItems(typeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String type = typeList.get(which);
                if (type.equals("百度地图")) {
                    try {
                        // Intent intent =
                        //
                        // Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        Intent intent = Intent.getIntent(getBDMapNaviUri(startLat, startLng, startName, endLat, endLng, endName));

                        if (isInstallByread(context,"com.baidu.BaiduMap")) {
                            context.startActivity(intent); // 启动调用
                            Log.e("GasStation", "百度地图客户端已经安装");
                        } else {
                            Log.e("GasStation", "没有安装百度地图客户端");
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("高德地图")) {
                    String uri = getAMapNaviUri(startLat, startLng, startName, endLat, endLng, endName);
                    Intent intent = new Intent(
                            "android.intent.action.VIEW",
                            android.net.Uri.parse(uri));
                    intent.setPackage("com.autonavi.minimap");
                    context.startActivity(intent);
                }

            }
        });
        b.setNegativeButton("取消", null);
        b.show();
    }
}
