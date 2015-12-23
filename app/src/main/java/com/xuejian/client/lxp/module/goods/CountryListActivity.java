package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.CityInfoActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/2.
 */
public class CountryListActivity extends PeachBaseActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleBar;
    @InjectView(R.id.gv_country)
    GridView gvCountry;
    CountryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        ButterKnife.inject(this);
        String name = getIntent().getStringExtra("name");
        String id = getIntent().getStringExtra("id");
        titleBar.enableBackKey(true);
        titleBar.getTitleTextView().setText(name);
        adapter = new CountryAdapter(this);
        gvCountry.setAdapter(adapter);
        gvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CountryListActivity.this, CityInfoActivity.class);
                intent.putExtra("id", adapter.getItem(position).id);
                startActivity(intent);
            }
        });
        initData(id);
    }

    private void initData(String id) {
        if (TextUtils.isEmpty(id)) return;
        TravelApi.getCityList(id, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<CountryBean> list = CommonJson4List.fromJson(result, CountryBean.class);
                adapter.getData().clear();
                adapter.getData().addAll(list.result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    class CountryAdapter extends BaseAdapter {
        private DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        private Context mContext;
        private ArrayList<CountryBean> data;

        public CountryAdapter(Context context) {
            mContext = context;
            data = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CountryBean getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ArrayList<CountryBean> getData() {
            return data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final CountryBean bean = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_country_list, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (bean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(bean.images.get(0).url, holder.ivCountry, options);
            }
            holder.tvCityName.setText(bean.zhName + "\n" + bean.enName);
            holder.tvStoreNum.setText("0");
            return convertView;
        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_country_list.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        class ViewHolder {
            @InjectView(R.id.iv_country_img)
            ImageView ivCountry;
            @InjectView(R.id.tv_store_num)
            TextView tvStoreNum;
            @InjectView(R.id.tv_city_name)
            TextView tvCityName;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

}
