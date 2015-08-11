package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yibiao.qin on 2015/8/11.
 */
public class GuideViewUtils {
    private Activity context;
    private ImageView imgView;
    private WindowManager windowManager;
    private static GuideViewUtils instance = null;
    private List<Integer> picList = new ArrayList<>();
    private GuideViewUtils() {
    }

    public static GuideViewUtils getInstance() {
        synchronized (GuideViewUtils.class) {
            if (null == instance) {
                instance = new GuideViewUtils();
            }
        }
        return instance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // 设置LayoutParams参数
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
                    // 设置显示格式
                    params.format = PixelFormat.RGBA_8888;
                    // 设置对齐方式
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    // 设置宽高
                    params.width = CommonUtils.getScreenWidth(context);
                    params.height = CommonUtils.getScreenHeight(context);
                    // 设置动画
               //     params.windowAnimations = R.style.view_anim;

                    // 添加到当前的窗口上
                    windowManager.addView(imgView, params);
                    break;
            }
        };
    };

    public void initGuide(Activity context, int drawableRourcesId) {
        this.context = context;
        windowManager = context.getWindowManager();
        imgView = new ImageView(context);
        imgView.setLayoutParams(new ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imgView.setScaleType(ImageView.ScaleType.FIT_XY);
        imgView.setImageResource(drawableRourcesId);
        handler.sendEmptyMessageDelayed(1, 1000);

        imgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                windowManager.removeView(imgView);
            }
        });
    }
}