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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ImageUtils {
    public static final int SCALE_IMAGE_WIDTH = 640;
    public static final int SCALE_IMAGE_HEIGHT = 960;

//	public static String getThumbnailImagePath(String thumbRemoteUrl) {
//		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
//		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
//        Log.d("msg", "thum image path:" + path);
//        return path;
//    }

    public static Bitmap getMaskImage(Bitmap mask, Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(source, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap var0) {
        return getRoundedCornerBitmap(var0, 6.0F);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap var0, float var1) {
        Bitmap var2 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas var3 = new Canvas(var2);
        int var4 = -12434878;
        Paint var5 = new Paint();
        Rect var6 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
        RectF var7 = new RectF(var6);
        var5.setAntiAlias(true);
        var3.drawARGB(0, 0, 0, 0);
        var5.setColor(-12434878);
        var3.drawRoundRect(var7, var1, var1, var5);
        var5.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        var3.drawBitmap(var0, var6, var6, var5);
        return var2;
    }

    public static Bitmap getVideoThumbnail(String var0, int var1, int var2, int var3) {
        Bitmap var4 = null;
        var4 = ThumbnailUtils.createVideoThumbnail(var0, var3);
        Log.d("getVideoThumbnail", "video thumb width:" + var4.getWidth());
        Log.d("getVideoThumbnail", "video thumb height:" + var4.getHeight());
        var4 = ThumbnailUtils.extractThumbnail(var4, var1, var2, 2);
        return var4;
    }

    public static String saveVideoThumb(File var0, int var1, int var2, int var3) {
        return null;
//        Bitmap var4 = getVideoThumbnail(var0.getAbsolutePath(), var1, var2, var3);
//        File var5 = new File(PathUtil.getInstance().getVideoPath(), "th" + var0.getName());
//
//        try {
//            var5.createNewFile();
//        } catch (IOException var11) {
//            var11.printStackTrace();
//        }
//
//        FileOutputStream var6 = null;
//
//        try {
//            var6 = new FileOutputStream(var5);
//        } catch (FileNotFoundException var10) {
//            var10.printStackTrace();
//        }
//
//        var4.compress(Bitmap.CompressFormat.JPEG, 100, var6);
//
//        try {
//            var6.flush();
//        } catch (IOException var9) {
//            var9.printStackTrace();
//        }
//
//        try {
//            var6.close();
//        } catch (IOException var8) {
//            var8.printStackTrace();
//        }
//
//        return var5.getAbsolutePath();
    }

    public static Bitmap decodeScaleImage(String path, int width, int height) {
        BitmapFactory.Options option = getBitmapOptions(path);
        int var4 = calculateInSampleSize(option, width, height);
        Log.d("img", "original wid" + option.outWidth + " original height:" + option.outHeight + " sample:" + var4);
        option.inSampleSize = var4;
        option.inJustDecodeBounds = false;
        Bitmap var5 = BitmapFactory.decodeFile(path, option);
        int var6 = readPictureDegree(path);
        Bitmap var7 = null;
        if (var5 != null && var6 != 0) {
            var7 = rotaingImageView(var6, var5);
            var5.recycle();
            var5 = null;
            return var7;
        } else {
            return var5;
        }
    }

    public static Bitmap decodeScaleImage(Context var0, int var1, int var2, int var3) {
        BitmapFactory.Options var5 = new BitmapFactory.Options();
        var5.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(var0.getResources(), var1, var5);
        int var6 = calculateInSampleSize(var5, var2, var3);
        var5.inSampleSize = var6;
        var5.inJustDecodeBounds = false;
        Bitmap var4 = BitmapFactory.decodeResource(var0.getResources(), var1, var5);
        return var4;
    }

    public static int calculateInSampleSize(BitmapFactory.Options option, int width, int height) {

        int realHeight = option.outHeight;
        int realWidth = option.outWidth;
        if (realHeight>realWidth*5){
            return 5;
        }
        int sample = 1;
        if (realHeight > height || realWidth > width) {
            int var6 = Math.round((float) realHeight / (float) height);
            int var7 = Math.round((float) realWidth / (float) width);
          //   sample = var6 > var7 ? var6 : var7;
            sample = var6 < var7 ? var6 : var7;
        }
        return sample;
    }

    public static String getThumbnailImage(String var0, int var1) {
        Bitmap var2 = decodeScaleImage(var0, var1, var1);

        try {
            File var3 = File.createTempFile("image", ".jpg");
            FileOutputStream var4 = new FileOutputStream(var3);
            var2.compress(Bitmap.CompressFormat.JPEG, 60, var4);
            var4.close();
            Log.d("img", "generate thumbnail image at:" + var3.getAbsolutePath() + " size:" + var3.length());
            return var3.getAbsolutePath();
        } catch (Exception var5) {
            var5.printStackTrace();
            return var0;
        }
    }

    public static String getScaledImage(Context var0, String var1) {
        File var2 = new File(var1);
        if (!var2.exists()) {
            return var1;
        } else {
            long var3 = var2.length();
            Log.d("img", "original img size:" + var3);
            if (var3 <= 102400L) {
                Log.d("img", "use original small image");
                return var1;
            } else {
                Bitmap var5 = decodeScaleImage(var1, 640, 960);

                try {
                    File var6 = File.createTempFile("image", ".jpg", var0.getFilesDir());
                    FileOutputStream var7 = new FileOutputStream(var6);
                    var5.compress(Bitmap.CompressFormat.JPEG, 70, var7);
                    var7.close();
                    Log.d("img", "compared to small fle" + var6.getAbsolutePath() + " size:" + var6.length());
                    return var6.getAbsolutePath();
                } catch (Exception var8) {
                    var8.printStackTrace();
                    return var1;
                }
            }
        }
    }

    public static String getScaledImage(Context var0, String var1, int var2) {
        File var3 = new File(var1);
        if (var3.exists()) {
            long var4 = var3.length();
            Log.d("img", "original img size:" + var4);
            if (var4 > 102400L) {
                Bitmap var6 = decodeScaleImage(var1, 640, 960);

                try {
                    File var7 = new File(var0.getExternalCacheDir(), "eaemobTemp" + var2 + ".jpg");
                    FileOutputStream var8 = new FileOutputStream(var7);
                    var6.compress(Bitmap.CompressFormat.JPEG, 60, var8);
                    var8.close();
                    Log.d("img", "compared to small fle" + var7.getAbsolutePath() + " size:" + var7.length());
                    return var7.getAbsolutePath();
                } catch (Exception var9) {
                    var9.printStackTrace();
                }
            }
        }

        return var1;
    }

    public static Bitmap mergeImages(int var0, int var1, List<Bitmap> var2) {
        Bitmap var3 = Bitmap.createBitmap(var0, var1, Bitmap.Config.ARGB_8888);
        Canvas var4 = new Canvas(var3);
        var4.drawColor(-3355444);
        Log.d("img", "merge images to size:" + var0 + "*" + var1 + " with images:" + var2.size());
        byte var5;
        if (var2.size() <= 4) {
            var5 = 2;
        } else {
            var5 = 3;
        }

        int var6 = 0;
        int var7 = (var0 - 4) / var5;

        for (int var8 = 0; var8 < var5; ++var8) {
            for (int var9 = 0; var9 < var5; ++var9) {
                Bitmap var10 = var2.get(var6);
                Bitmap var11 = Bitmap.createScaledBitmap(var10, var7, var7, true);
                Bitmap var12 = getRoundedCornerBitmap(var11, 2.0F);
                var11.recycle();
                var4.drawBitmap(var12, (float) (var9 * var7 + var9 + 2), (float) (var8 * var7 + var8 + 2), null);
                var12.recycle();
                ++var6;
                if (var6 == var2.size()) {
                    return var3;
                }
            }
        }

        return var3;
    }

    public static int readPictureDegree(String var0) {
        short var1 = 0;

        try {
            ExifInterface var2 = new ExifInterface(var0);
            int var3 = var2.getAttributeInt("Orientation", 1);
            switch (var3) {
                case 3:
                    var1 = 180;
                    break;
                case 6:
                    var1 = 90;
                    break;
                case 8:
                    var1 = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return var1;
    }

    public static Bitmap rotaingImageView(int var0, Bitmap var1) {
        Matrix var2 = new Matrix();
        var2.postRotate((float) var0);
        Bitmap var3 = Bitmap.createBitmap(var1, 0, 0, var1.getWidth(), var1.getHeight(), var2, true);
        return var3;
    }

    public static BitmapFactory.Options getBitmapOptions(String var0) {
        BitmapFactory.Options var1 = new BitmapFactory.Options();
        var1.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(var0, var1);
        return var1;
    }

    public static int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return (int) (var1 * var2 + 0.5F);
    }

    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int) (var1 * var2 + 0.5F);
    }
}
