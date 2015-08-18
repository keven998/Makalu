package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.SharePrefUtil;
import com.xuejian.client.lxp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yibiao.qin on 2015/8/11.
 */
public class GuideViewUtils {
    private Activity context;
    private ImageView imgView;
    private WindowManager windowManager;
    private String guideName;
    private static GuideViewUtils instance = null;
    private List<Integer> picList = new ArrayList<>();
    private RelativeLayout view;

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
                    windowManager.addView(view, params);
                    break;
            }
        }

    };

    public void initGuide(final Activity context, final String guideName, String content, int topMargin, int rightMargin, int res) {
        if (CommonUtils.getSystemProperty().equals("V6") || (android.os.Build.BRAND.equals("Xiaomi") && Build.VERSION.SDK_INT >= 18)) {
            SharePrefUtil.saveBoolean(context, guideName, true);
            return;
        }
        this.context = context;
        this.guideName = guideName;
        windowManager = context.getWindowManager();
        view = (RelativeLayout) context.getLayoutInflater().inflate(R.layout.guide_view, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_guide_text);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        if (topMargin != -1) params.topMargin = topMargin;
        if (rightMargin != -1) params.rightMargin = rightMargin;
        textView.setLayoutParams(params);
        if (res != -1) textView.setBackgroundResource(res);
        textView.setText(content);
        handler.sendEmptyMessageDelayed(1, 300);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                windowManager.removeView(view);
                SharePrefUtil.saveBoolean(context, guideName, true);
            }
        });
    }
}