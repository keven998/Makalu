package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.HackyViewPager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.LocAlbum;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.ImageZoomAnimator2;

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
//    @ViewInject(R.id.ll_container)
//    private LinearLayout containView;
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

        TextView titleView = (TextView) findViewById(R.id.tv_title_bar_title);
        titleView.setText(getIntent().getStringExtra("title") + "画册");
        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
//        for(int i=0;i<20;i++){
//            ImageBean bean = new ImageBean();
//            bean.url="http://img0.bdstatic.com/img/image/shouye/taiwanlvyou1117.jpg";
//            imageList.add(bean);
//        }
        TravelApi.getCityGalley(id, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<LocAlbum> imageReuslt = CommonJson.fromJson(result, LocAlbum.class);
                if (imageReuslt.code == 0) {
                    picAdapter = new PicAdapter(imageReuslt.result.album);
                    mCityPicGv.setAdapter(picAdapter);
                    zoomAnimator = new ImageZoomAnimator2(mContext, mCityPicGv, zoomContainer, imageReuslt.result.album);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(CityPictureActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().stop();
        zoomContainer = null;
        picAdapter = null;
        zoomPicVp = null;
        mCityPicGv = null;
    }

    @Override
    public void finish() {
        super.finishWithNoAnim();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (zoomContainer.getVisibility() == View.VISIBLE) {
            zoomAnimator.transformOut(zoomPicVp.getCurrentItem());
        } else {
            super.onBackPressed();
        }
    }

    private class PicAdapter extends BaseAdapter{
        private List<ImageBean> imageBeanList;
        private DisplayImageOptions picOptions;

        public PicAdapter(List<ImageBean> imageBeanList){
            this.imageBeanList= imageBeanList;
            picOptions = UILUtils.getDefaultOption();
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
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                ImageView picIv = new ImageView(mContext);
                picIv.setBackgroundResource(R.drawable.frame_cell_image_frame);
                picIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                int width = (LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(40))/3;
                int width = (LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(4))/3;
                int height = width;
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        width, height);
                picIv.setLayoutParams(lp);
                picIv.setPadding(1, 1, 1, 1);
                imageView = picIv;
            }
            imageView.setImageDrawable(null);
            ImageBean itemData = imageBeanList.get(position);
            ImageLoader.getInstance().displayImage(itemData.url, imageView, picOptions);
//            Picasso.with(mContext)
//                    .load(itemData.url)
////                    .placeholder(R.drawable.avatar_placeholder)
//                    .into(picIv);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomAnimator.transformIn(position);
                }
            });
            imageView.setTag(position);
            return imageView;
        }
    }

}
