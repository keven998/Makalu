package com.aizou.peachtravel.module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.CoverStoryBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.widget.shimmer.Shimmer;
import com.aizou.peachtravel.common.widget.shimmer.ShimmerTextView;
import com.aizou.peachtravel.common.widget.swipebacklayout.SwipeBackLayout;
import com.aizou.peachtravel.common.widget.swipebacklayout.app.SwipeBackActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Rjm on 2014/11/10.
 */
public class StoryActivity extends SwipeBackActivity {
    private ImageView storyIv;
    private ShimmerTextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        setContentView(R.layout.activity_story);
        storyIv = (ImageView) findViewById(R.id.iv_story);
        start = (ShimmerTextView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_to_left_100);
            }
        });
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(1500);
        shimmer.start(start);

        SwipeBackLayout swipeBackLayout;
        swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT);
    }

    private void initData(){
        final DisplayImageOptions picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
//                .showImageOnFail(R.drawable.ic_launcher)
//                .showImageForEmptyUri(R.drawable.ic_launcher)
//				.decodingOptions(D)
                .displayer(new FadeInBitmapDisplayer(180, true, true, false))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        String storyImageUrl = SharePrefUtil.getString(this, "story_image", "");
//        if(!TextUtils.isEmpty(storyImageUrl)){
//
//        }
        ImageLoader.getInstance().displayImage(storyImageUrl, storyIv, picOptions);
        OtherApi.getCoverStory(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<CoverStoryBean> storyResult = CommonJson.fromJson(result,CoverStoryBean.class);
                if(storyResult.code == 0) {
                    SharePrefUtil.saveString(StoryActivity.this, "story_image", storyResult.result.image);
                    ImageLoader.getInstance().displayImage(storyResult.result.image,storyIv, picOptions);
                } else {
//                    ToastUtil.getInstance(StoryActivity.this).showToast("请求也是失败了");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(StoryActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }
}
