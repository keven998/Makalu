package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/19.
 */
public class CityPictureActivity extends PeachBaseActivity {
    @ViewInject(R.id.gv_city_pic)
    private GridView mCityPicGv;
    @ViewInject(R.id.zoom_container)
    private RelativeLayout zoomContainer;
    @ViewInject(R.id.vp_zoom_pic)
    private HackyViewPager zoomPicVp;
    @ViewInject(R.id.ll_container)
    private LinearLayout containView;
    private PicAdapter picAdapter;
    private ImageZoomAnimator2 zoomAnimator;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

    }

    private void initView() {
        setContentView(R.layout.activity_city_picture);
        ViewUtils.inject(this);




    }

    private void initData() {
        id = getIntent().getStringExtra("id");
//        for(int i=0;i<20;i++){
//            ImageBean bean = new ImageBean();
//            bean.url="http://img0.bdstatic.com/img/image/shouye/taiwanlvyou1117.jpg";
//            imageList.add(bean);
//        }
        TravelApi.getCityGalley(id,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<ImageBean> imageReuslt = CommonJson4List.fromJson(result,ImageBean.class);
                if(imageReuslt.code==0){
                    picAdapter= new PicAdapter(imageReuslt.result);
                    mCityPicGv.setAdapter(picAdapter);
                    zoomAnimator = new ImageZoomAnimator2(mContext,mCityPicGv,zoomContainer,imageReuslt.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });


    }

    @Override
    public void onBackPressed() {

        if(zoomContainer.getVisibility()==View.VISIBLE){
            zoomAnimator.transformOut(zoomPicVp.getCurrentItem());
        }else{
            super.onBackPressed();
        }

    }
    private class PicAdapter extends BaseAdapter{
        private List<ImageBean> imageBeanList;
        public PicAdapter(List<ImageBean> imageBeanList){
            this.imageBeanList= imageBeanList;
        }

        @Override
        public int getCount() {
            return imageBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageBean itemData = imageBeanList.get(position);
            ImageView picIv = new ImageView(mContext);
            picIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(40))/3;
            int height = width;
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    width, height);
            picIv.setLayoutParams(lp);
            ImageLoader.getInstance().displayImage(itemData.url,picIv, UILUtils.getDefaultOption());
            picIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomAnimator.transformIn(position);
                }
            });
            picIv.setTag(position);
            return picIv;
        }
    }

}
