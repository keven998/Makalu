package com.aizou.peachtravel.module;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.SharePrefUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CoverStoryBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/11/10.
 */
public class StoryActivity extends PeachBaseActivity {
    private ImageView storyIv;
    private TextView startTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        setContentView(R.layout.activity_story);
        storyIv = (ImageView) findViewById(R.id.iv_story);
        startTv = (TextView) findViewById(R.id.tv_start);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData(){
        String storyImageUrl = SharePrefUtil.getString(mContext,"story_image","");
        if(!TextUtils.isEmpty(storyImageUrl)){
            ImageLoader.getInstance().displayImage(storyImageUrl,storyIv, UILUtils.getDefaultOption());
        }
        OtherApi.getCoverStory(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<CoverStoryBean> storyResult = CommonJson.fromJson(result,CoverStoryBean.class);
                if(storyResult.code==0){
                    SharePrefUtil.saveString(mContext,"story_image",storyResult.result.image);
                    ImageLoader.getInstance().displayImage(storyResult.result.image,storyIv, UILUtils.getDefaultOption());
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }
}
