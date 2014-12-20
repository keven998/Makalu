package com.aizou.peachtravel.common.imageloader;

import android.graphics.Bitmap;

import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class UILUtils {
	public static final String QINIU_URL_FORMART="?imageView/1/w/%1$d/h/%2$d/q/70/format/jpg/interlace/1";
	public static DisplayImageOptions getDefaultOption() {
		DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(true)
//				.decodingOptions(D)
				.displayer(new FadeInBitmapDisplayer(300, true, true, false))
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
				
		return picOptions;
	}

    public static DisplayImageOptions getRadiusOption() {
        DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
//				.decodingOptions(D)
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(2)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        return picOptions;
    }
    public static DisplayImageOptions getRadiusOption(int radius) {
        DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
//				.decodingOptions(D)
                .displayer(new RoundedBitmapDisplayer(radius))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        return picOptions;
    }
    public static DisplayImageOptions getRadiusOption(int topLeftRadius, int topRightRadius,int bottomLeftRadius, int bottomRightRadius) {
        DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
//				.decodingOptions(D)
                .displayer(new FixedRoundedBitmapDisplayer(topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        return picOptions;
    }




    public static String formartQiNiuUrl(String imageUrl,int width,int height){
//		if(imageUrl.contains("qiniudn")&&width>0){
//			String url = String.format(imageUrl+QINIU_URL_FORMART, width,height);
//			return url;
//		}
		return imageUrl;
	}
}
