package com.aizou.peachtravel.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Rjm on 2015/1/4.
 */
public class MapUtils {
    // 调用地图客户端uri
    // 例子：map_uri("龙泽","北京南站","北京");
    public final static String COMPANY_NAME="aizou";
    public final static String APP_NAME="桃子旅行";
    private static String getBDMapMarkerUri(double lat,double lng,String title,String content){
        return "intent://map/marker?location="+lat+","+lng+"&title="+title+"&content" +
                "="+content+"&src="+COMPANY_NAME+"|"+APP_NAME+"#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
    }

    private static String getAMapMarkerUri(double lat,double lng,String poiName){
        return "androidamap://viewMap?sourceApplication="+APP_NAME+"&poiname="+poiName+"&lat="+lat+"&lon="+lng+"&dev=0";

    }

    private static String getBDMapNaviUri(){
        return null;
    }

    private static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static void showSelectMapDialog(final Context context) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        // 设置对话框的图标
        // b.setIcon(R.drawable.tools);
        // 设置对话框的标题
        b.setTitle("请选择您喜欢的地图客户端：");
        boolean baidumap = isInstallByread("com.baidu.BaiduMap");
        boolean gaodemap = isInstallByread("com.autonavi.minimap");
        String[] type = null;
        int tag = 0;
        if (baidumap && gaodemap) {
            // type = new String[] { "百度地图", "高德地图" };
            tag = 0;
        } else if (baidumap && !gaodemap) {
            tag = 1;
        } else if (!baidumap && gaodemap) {
            tag = 2;
        } else {
            tag = 3;
        }
        switch (tag) {
            case 0:
                type = new String[]{"百度地图", "高德地图"};
                break;
            case 1:
                type = new String[]{"百度地图"};
                break;
            case 2:
                type = new String[]{"高德地图"};
                break;
            case 3:
                Toast toast = Toast.makeText(context, "请您安装百度地图或高德地图客户端",
                        Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
        if (tag < 3) {
            b.setItems(type, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        try {
                            // Intent intent =
                            //
                            // Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                            Intent intent = Intent.getIntent("");

                            if (isInstallByread("com.baidu.BaiduMap")) {
                                context.startActivity(intent); // 启动调用
                                Log.e("GasStation", "百度地图客户端已经安装");
                            } else {
                                Log.e("GasStation", "没有安装百度地图客户端");
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String uri = "";
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
}
