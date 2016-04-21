package com.xuejian.client.lxp.module.customization;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/1.
 */
public class DestMenuActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.strategy_title)
    TextView strategyTitle;
    @Bind(R.id.tv_copy_guide)
    TextView tvConfirm;
    @Bind(R.id.lv_main)
    ListView lvMain;
    @Bind(R.id.lv_sub)
    ListView lvSub;
    @Bind(R.id.dest_add_ll)
    LinearLayout destAddLl;
    @Bind(R.id.add_dest_scroll_panel)
    HorizontalScrollView addDestScrollPanel;
    SubAdapter subAdapter;
    ArrayList<LocBean> addedList = new ArrayList<>();
    ArrayList<LocBean> addedListForScroll = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dest_menu);
        ButterKnife.bind(this);
        ArrayList<LocBean> exist = getIntent().getParcelableArrayListExtra("exist");
        if (exist!=null){
            addedList.addAll(exist);
            for (LocBean locBean : addedList) {
                addLoc(locBean);
            }
        }
        getData();
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("selected",addedList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }

    private void getData() {
        String data = PreferenceUtils.getCacheData(this, "destination_outcountry");
        if (!TextUtils.isEmpty(data)) {
            CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(data, CountryBean.class);
            if (countryListResult.code == 0) {
                bindOutView(countryListResult.result);
            }
        } else {
            getOutCountryList();
        }

    }

    private void bindOutView(List<CountryBean> result) {
        MainAdapter mainAdapter = new MainAdapter();
        mainAdapter.getList().addAll(result);
        lvMain.setAdapter(mainAdapter);
        subAdapter = new SubAdapter();
        subAdapter.getList().addAll(result);
        lvSub.setAdapter(subAdapter);
        mainAdapter.setListener(subAdapter.listener);
        subAdapter.setOnCitySelectedListener(new OnCitySelected() {
            @Override
            public void OnAdd(LocBean bean) {
                addLoc(bean);
            }

            @Override
            public void OnRemove(LocBean bean) {
                removeLoc(bean);
            }
        });
    }

    private void removeLoc(LocBean bean) {
        int index = addedListForScroll.indexOf(bean);
        destAddLl.removeViewAt(index);
        addedListForScroll.remove(bean);
        if (addedListForScroll.size() == 0) {
            addDestScrollPanel.setVisibility(View.GONE);
        }
        autoScrollPanel();
    }

    private void addLoc(final LocBean bean) {
        View view = View.inflate(DestMenuActivity.this, R.layout.poi_bottom_cell_with_del, null);
        FrameLayout del_fl = (FrameLayout) view.findViewById(R.id.poi_del_fl);
        TextView location = (TextView) view.findViewById(R.id.names);
        location.setText(bean.zhName);
        addedListForScroll.add(bean);
        del_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLoc(bean);
                addedList.remove(bean);
                subAdapter.notifyDataSetChanged();
            }
        });
        destAddLl.addView(view);
        if (addedListForScroll.size() > 0) {
            addDestScrollPanel.setVisibility(View.VISIBLE);
        }
        autoScrollPanel();
    }

    private void autoScrollPanel() {
        addDestScrollPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                addDestScrollPanel.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
    }

    private class MainAdapter extends BaseAdapter {
        private int selected = -1;
        private TextView country_name;
        private ArrayList<CountryBean> list = new ArrayList<>();
        OnCountrySelected listener;

        public ArrayList<CountryBean> getList() {
            return list;
        }

        public void setListener(OnCountrySelected listener) {
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CountryBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.desty_name, null);
            country_name = (TextView) contentView.findViewById(R.id.contry_name);
            CountryBean bean = getItem(position);
            country_name.setText(bean.zhName);
            country_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnCountrySelected(position);
                    }
                    selected = position;
                    notifyDataSetChanged();
                }
            });
            if (selected == position) {
                country_name.setTextColor(getResources().getColor(R.color.color_text_i));
            } else {
                country_name.setTextColor(getResources().getColor(R.color.color_text_iii));
            }
            return contentView;
        }

    }

    public interface OnCountrySelected {
        void OnCountrySelected(int pos);
    }

    public interface OnCitySelected {
        void OnAdd(LocBean bean);

        void OnRemove(LocBean bean);
    }

    private class SubAdapter extends BaseAdapter {
        private TextView country_name;
        private ArrayList<CountryBean> list = new ArrayList<>();
        private ArrayList<LocBean> locList = new ArrayList<>();

        public ArrayList<CountryBean> getList() {
            return list;
        }

        OnCitySelected mOnCitySelectedListener;
        public OnCountrySelected listener = new OnCountrySelected() {
            @Override
            public void OnCountrySelected(int pos) {
                locList.clear();
                locList.addAll(list.get(pos).destinations);
                notifyDataSetChanged();
            }
        };

        @Override
        public int getCount() {
            return locList.size();
        }

        @Override
        public LocBean getItem(int position) {
            return locList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setOnCitySelectedListener(OnCitySelected mOnCitySelectedListener) {
            this.mOnCitySelectedListener = mOnCitySelectedListener;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.desty_name, null);
            country_name = (TextView) contentView.findViewById(R.id.contry_name);
            final LocBean bean = getItem(position);
            country_name.setText(bean.zhName);
            country_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addedList.contains(bean)) {
                        addedList.remove(bean);
                        if (mOnCitySelectedListener != null) {
                            mOnCitySelectedListener.OnRemove(bean);
                        }
                    } else {
                        addedList.add(bean);
                        if (mOnCitySelectedListener != null) {
                            mOnCitySelectedListener.OnAdd(bean);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            if (addedList.contains(bean)) {
                country_name.setTextColor(getResources().getColor(R.color.color_text_i));
            } else {
                country_name.setTextColor(getResources().getColor(R.color.color_text_iii));
            }
            return contentView;
        }
    }

    private void getOutCountryList() {
        TravelApi.getOutDestList("", new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
            }

            @Override
            public void doSuccess(String result, String method, Map<String, List<String>> headers) {
                CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(result, CountryBean.class);
                if (countryListResult.code == 0) {
                    bindOutView(countryListResult.result);
                    PreferenceUtils.cacheData(DestMenuActivity.this, "destination_outcountry", result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }
}
