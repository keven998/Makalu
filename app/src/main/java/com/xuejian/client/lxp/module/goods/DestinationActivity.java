package com.xuejian.client.lxp.module.goods;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.LocBean;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/10/19.
 */
public class DestinationActivity extends PeachBaseActivity {
    ListViewDataAdapter<CountryBean> mDestinationNameAdapter;
    ListViewDataAdapter<LocBean> mDestinationAdapter;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.iv_clean)
    ImageView ivClean;
    @Bind(R.id.btn_search)
    TextView btnSearch;
    @Bind(R.id.search_bar)
    LinearLayout searchBar;
    @Bind(R.id.lv_country_name)
    ListView lvDestinationName;
    @Bind(R.id.lv_out_country)
    GridView gvDestination;
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        ButterKnife.bind(this);
        mDestinationNameAdapter = new ListViewDataAdapter<CountryBean>(new ViewHolderCreator<CountryBean>() {
            @Override
            public ViewHolderBase<CountryBean> createViewHolder() {
                return new DestinationNameHolder();
            }
        });
        mDestinationAdapter = new ListViewDataAdapter<LocBean>(new ViewHolderCreator<LocBean>() {
            @Override
            public ViewHolderBase<LocBean> createViewHolder() {
                return new DestinationViewHolder();
            }
        });
        lvDestinationName.setAdapter(mDestinationNameAdapter);
        gvDestination.setAdapter(mDestinationAdapter);
        ArrayList<CountryBean> countryBeanArrayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            countryBeanArrayList.add(new CountryBean());
        }
        ArrayList<LocBean> locBeanArrayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            locBeanArrayList.add(new LocBean());
        }
        mDestinationNameAdapter.getDataList().addAll(countryBeanArrayList);
        mDestinationAdapter.getDataList().addAll(locBeanArrayList);
    }

    private class DestinationNameHolder extends ViewHolderBase<CountryBean> {
        private TextView contry_name;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.desty_name, null);
            contry_name = (TextView) contentView.findViewById(R.id.contry_name);
            return contentView;
        }

        @Override
        public void showData(int position, final CountryBean itemData) {
            contry_name.setText("日本");
            if (mCurrentIndex == position) {
                contry_name.setTextColor(getResources().getColor(R.color.color_text_i));
            } else {
                contry_name.setTextColor(getResources().getColor(R.color.color_text_iii));
            }
        }
    }

    private class DestinationViewHolder extends ViewHolderBase<LocBean> {
        TextView cityNameTv;
        ImageView desBgImage;
        private DisplayImageOptions poptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.item_destination, null);
            cityNameTv = (TextView) contentView.findViewById(R.id.tv_des);
            desBgImage = (ImageView) contentView.findViewById(R.id.iv_pic);
            return contentView;
        }

        @Override
        public void showData(int position, final LocBean itemData) {
            cityNameTv.setText("韩国\n美丽城市 烂漫天眼");
            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/097b5bade72c60272634a5127f1e8152?imageView2/2/w/960", desBgImage, poptions);
//            cityNameTv.setText(itemData.zhName);
//            if (itemData.images.size() > 0) {
//                ImageLoader.getInstance().displayImage(itemData.images.get(0).url, desBgImage, poptions);
//            }


        }
    }
}
