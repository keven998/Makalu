package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.widget.glide.GlideCircleTransform;
import com.xuejian.client.lxp.common.widget.glide.GlideRoundTransform;

/**
 * Created by yibiao.qin on 2016/1/8.
 */
public class ImageProxyUtils {
    private RequestManager requestManager;
    public static ImageProxyUtils imageProxyUtils = new ImageProxyUtils();

    public static ImageProxyUtils getImageProxyUtils() {
        return imageProxyUtils;
    }

    public void loadImage(Context context, String url, ImageView imageView, boolean round, int dp) {
        DrawableRequestBuilder<String> builder = Glide.with(context).load(url)
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .centerCrop();
        if (round) {
            builder.bitmapTransform(new GlideCircleTransform(context));
        }else {
            builder.bitmapTransform(new GlideRoundTransform(context,dp));
        }
        builder.into(imageView);
    }

    public void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .centerCrop()
                .into(imageView);
    }

    public void loadImage(Activity activity, String url, ImageView imageView) {
        Glide.with(activity)
                .load(url)
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .centerCrop()
                .into(imageView);
    }

    public void loadImage(Fragment fragment, String url, ImageView imageView) {
        Glide.with(fragment)
                .load(url)
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .centerCrop()
                .into(imageView);
    }

    public void loadImage(android.app.Fragment fragment, String url, ImageView imageView) {

        Glide.with(fragment)
                .load(url)
                .placeholder(R.drawable.ic_default_picture)
                .error(R.drawable.ic_default_picture)
                .centerCrop()
                .into(imageView);
    }
}
