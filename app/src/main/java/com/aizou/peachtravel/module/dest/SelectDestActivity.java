package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aizou.core.constant.LayoutValue;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.FixedGridView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.CityBean;
import com.aizou.peachtravel.bean.InCityBean;
import com.aizou.peachtravel.bean.OutCountryBean;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/10/9.
 */
public class SelectDestActivity extends PeachBaseActivity {
    private ListView mInListView,mOutCountryListView;
    private RadioGroup inOutRg;
    private LinearLayout citysLl;
    private TextView startTv;
    private List<InCityBean> incityList;
    private List<OutCountryBean> outCountryList;
    private List<CityBean> allAddCityList = new ArrayList<CityBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext,R.layout.activity_select_dest, null);
        mInListView = (ListView) rootView.findViewById(R.id.lv_in_city);
        mOutCountryListView = (ListView) rootView.findViewById(R.id.lv_out_country);
        inOutRg = (RadioGroup) rootView.findViewById(R.id.in_out_rg);
        citysLl = (LinearLayout) rootView.findViewById(R.id.ll_citys);
        startTv = (TextView) rootView.findViewById(R.id.tv_start);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        inOutRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_in:
                        mInListView.setVisibility(View.VISIBLE);
                        mOutCountryListView.setVisibility(View.GONE);
                        break;
                    case R.id.rb_out:
                        mInListView.setVisibility(View.GONE);
                        mOutCountryListView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
//        ImageLoader.getInstance().displayImage("http://d.hiphotos.baidu.com/super/whfpf%3D425%2C260%2C50/sign=70ecd7664c4a20a4314b6f87f66fac10/d01373f082025aaf97f5f1bff8edab64024f1afa.jpg",(ImageView)rootView.findViewById(R.id.iv_test));
        initData();
    }
    public String sections[]={"A","B","C","D"};
    private void initData() {

        incityList = new ArrayList<InCityBean>();
        outCountryList = new ArrayList<OutCountryBean>();
        for (int i = 0; i < 4; i++) {
            InCityBean inCity = new InCityBean();
            inCity.section = sections[i];
            ArrayList<CityBean> cityBeans = new ArrayList<CityBean>();
            inCity.cityList = cityBeans;
            for (int j = 0; j < 5; j++) {
                CityBean city = new CityBean();
                city.zhName = "云南";
                city.image = "http://d.hiphotos.baidu.com/super/whfpf%3D425%2C260%2C50/sign=70ecd7664c4a20a4314b6f87f66fac10/d01373f082025aaf97f5f1bff8edab64024f1afa.jpg";
                cityBeans.add(city);
            }
            incityList.add(inCity);
        }
        for(int i=0;i<5;i++){
            OutCountryBean outCountry = new OutCountryBean();
            outCountry.name ="韩国";
            outCountry.desc ="Korea";
            outCountry.image="http://hiphotos.baidu.com/lvpics/pic/item/1c950a7b02087bf45139aa41f2d3572c10dfcf45.jpg";
            outCountryList.add(outCountry);
        }
        ListViewDataAdapter<InCityBean> inCityAdapter = new ListViewDataAdapter<InCityBean>(new ViewHolderCreator<InCityBean>() {
            @Override
            public ViewHolderBase<InCityBean> createViewHolder() {
                return new InCityViewHolder();
            }
        });
        ListViewDataAdapter<OutCountryBean> outCountryAdapter = new ListViewDataAdapter<OutCountryBean>(new ViewHolderCreator<OutCountryBean>() {
            @Override
            public ViewHolderBase<OutCountryBean> createViewHolder() {
                return new OutCountryViewHolder();
            }
        });
        mInListView.setAdapter(inCityAdapter);
        inCityAdapter.getDataList().addAll(incityList);
        inCityAdapter.notifyDataSetChanged();
        mOutCountryListView.setAdapter(outCountryAdapter);
        outCountryAdapter.getDataList().addAll(outCountryList);
        outCountryAdapter.notifyDataSetChanged();
    }


    private class InCityViewHolder extends ViewHolderBase<InCityBean> {
        private TextView sectionTv;
        private FlowLayout cityListFl;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.dest_in_item, null);
            sectionTv = (TextView) contentView.findViewById(R.id.tv_section);
            cityListFl = (FlowLayout) contentView.findViewById(R.id.fl_city_list);
            return contentView;
        }

        @Override
        public void showData(int position, InCityBean itemData) {
            sectionTv.setText(itemData.section);
            View cityView = View.inflate(mContext,R.layout.dest_in_item,null);

        }
    }

    private class OutCountryViewHolder extends ViewHolderBase<OutCountryBean> {
        private TextView nameTv;
        private ImageView imageIv;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.dest_out_country, null);
            nameTv = (TextView) contentView.findViewById(R.id.tv_country);
            imageIv = (ImageView) contentView.findViewById(R.id.iv_country);
            int width = LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(20);
            int height = width * 220 / 600;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    width, height);
            imageIv.setLayoutParams(lp);
            return contentView;
        }

        @Override
        public void showData(int position, OutCountryBean itemData) {
            nameTv.setText("");
            nameTv.setText(itemData.name);
            SpannableString impress = new SpannableString("|"+itemData.desc);
            impress.setSpan(
                    new ForegroundColorSpan(getResources().getColor(
                            R.color.route_price_color)), 0, impress.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(15)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.append(impress);
            ImageLoader.getInstance().displayImage(itemData.image, imageIv, UILUtils.getDefaultOption());

        }
    }

    private class CityListViewHoder extends ViewHolderBase<CityBean> {
        private TextView cityNameTv;
        private ImageView cityIv;
        private ImageView addIv;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.dest_in_city_item, null);
            cityNameTv = (TextView) contentView.findViewById(R.id.tv_city_name);
            cityIv = (ImageView) contentView.findViewById(R.id.iv_city);
            addIv = (ImageView) contentView.findViewById(R.id.iv_add);
            int width = (LayoutValue.SCREEN_WIDTH - LocalDisplay.dp2px(10) * 4) / 3;
            cityIv.setLayoutParams(new FrameLayout.LayoutParams(width, width));
            return contentView;
        }

        @Override
        public void showData(int position, final CityBean itemData) {
            cityNameTv.setText(itemData.zhName);
            ImageLoader.getInstance().displayImage(itemData.image, cityIv, UILUtils.getDefaultOption());
            addIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
                    citysLl.addView(cityView);
                    allAddCityList.add(itemData);
                    TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
                    ImageView cityIv = (ImageView) cityView.findViewById(R.id.iv_add_city);
                    ImageView removeIv = (ImageView) cityView.findViewById(R.id.iv_remove);
                    cityNameTv.setText(itemData.zhName);
                    ImageLoader.getInstance().displayImage(itemData.image, cityIv, UILUtils.getDefaultOption());
                    removeIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = allAddCityList.indexOf(itemData);
                            citysLl.removeViewAt(index);
                            allAddCityList.remove(itemData);

                        }
                    });
                }
            });
        }
    }


}
