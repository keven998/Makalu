package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CountryWithExpertsBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.XDialog;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.module.toolbox.im.GuilderListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/7/8.
 */
public class TalentLocFragement extends PeachBaseFragment implements AbsListView.OnScrollListener {

    private ListView listView;
    private CheckedTextView titleTextView;

    private TalentLocAdapter adapter;
    private ArrayList<Integer> headerPos = new ArrayList<Integer>();
    private int lastPos = 0;
    private String[] delta = {"亚洲", "欧洲", "北美洲", "美洲", "非洲", "大洋洲","南极洲"};
    List<String> lists;
    List<String> continentNames = new ArrayList<>();
    ArrayList<ArrayList<CountryWithExpertsBean>> data;
    XDialog xDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.talentloc,container,false);
        lists = Arrays.asList(delta);
        initView(view);
        initData();
        return view;
    }



    private void initData() {
        try {
            DialogManager.getInstance().showLoadingDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        TravelApi.getExpertList(new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<CountryWithExpertsBean> expertResult = CommonJson4List.fromJson(result.toString(), CountryWithExpertsBean.class);
                //  CommonJson4List
                resizeData(expertResult.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void resizeData(List<CountryWithExpertsBean> list) {
        data = new ArrayList<ArrayList<CountryWithExpertsBean>>();
        for (int i = 0; i < 7; i++) {
            data.add(new ArrayList<CountryWithExpertsBean>());
        }
        for (CountryWithExpertsBean bean : list) {
            data.get(lists.indexOf(bean.continents.zhName)).add(bean);
        }
        ArrayList<ArrayList<CountryWithExpertsBean>> del = new ArrayList<>();
        for (ArrayList<CountryWithExpertsBean> beans : data) {
            if (beans.size() == 0) {
                del.add(beans);
            } else {
                sortCountries(beans);
            }
        }
        data.removeAll(del);
        adapter = new TalentLocAdapter(getActivity());
        listView.setAdapter(adapter);
        getHeaderPos();
        for (ArrayList<CountryWithExpertsBean> beans : data) {
            continentNames.add(beans.get(0).continents.zhName);
        }
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xDialog = new XDialog(getActivity(), R.layout.loc_select, R.style.LocSelectDialog);
                WindowManager.LayoutParams wlmp = xDialog.getWindow().getAttributes();
                wlmp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ListView lv = xDialog.getListView();
                MapsDayAdapter adapter = new MapsDayAdapter();
                lv.setAdapter(adapter);
                xDialog.show();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listView.setSelection(headerPos.get(position));
                        titleTextView.setText(continentNames.get(position));
                        if (xDialog != null) xDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        titleTextView = (CheckedTextView) view.findViewById(R.id.tv_title_bar_title);
        titleTextView.setText(delta[0]);

        listView = (ListView) view.findViewById(R.id.talent_loc_list);

        listView.setOnScrollListener(this);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent expertIntent = new Intent(TalentLocActivity.this, GuilderListActivity.class);
//                expertIntent.putExtra("countryId", "5434d70e10114e684bb1b4ee");
//                startActivity(expertIntent);
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_lxp_guide_distribute");
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_lxp_guide_distribute");
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        int pos = listView.getFirstVisiblePosition();
        for (int j = 0; j < headerPos.size(); j++) {
            if (j < headerPos.size() - 1) {
                if (pos > headerPos.get(j) && pos < headerPos.get(j + 1)) {
                    if (!titleTextView.getText().equals(delta[j])) {
                        titleTextView.setText(delta[j]);
                        if (pos > lastPos) {
                            titleTextView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom));
                        } else {
                            titleTextView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_top));
                        }
                        lastPos = pos;
                    }
                }
            } else {
                if (pos > headerPos.get(j)) {
                    if (!titleTextView.getText().equals(delta[j])) {
                        titleTextView.setText(delta[j]);
                        titleTextView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom));
                        lastPos = pos;
                    }
                }
            }
        }
    }

    public void getHeaderPos() {
        int pos = 0;
        headerPos.add(pos);
        for (int i = 0; i < adapter.getSectionCount() - 1; i++) {
            pos += adapter.getCountInSection(i) + 1;
            headerPos.add(pos);
        }
    }

    private class TalentLocAdapter extends BaseSectionAdapter {
        private TextView header;
        private DisplayImageOptions poptions;
        ;
        private Context mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Context context) {
            mCxt = context;
            mImgLoader = ImageLoader.getInstance();
            poptions = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .showImageOnLoading(R.drawable.messages_bg_useravatar)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)
                    .build();
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            return 0;
        }

        @Override
        public int getHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 1;
        }

        @Override
        public int getHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public CountryWithExpertsBean getItem(int section, int position) {
            return data.get(section).get(position);
        }

        @Override
        public long getItemId(int section, int position) {
            return getGlobalPositionForItem(section, position);
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mCxt, R.layout.talent_loc_cell_content, null);
            }

            final CountryWithExpertsBean item = getItem(section, position);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.rl_country = (FrameLayout) convertView.findViewById(R.id.fl_country);
                holder.bgImage = (ImageView) convertView.findViewById(R.id.talent_loc_img);
                holder.numSum = (TextView) convertView.findViewById(R.id.talent_loc_num);
                holder.loc = (TextView) convertView.findViewById(R.id.talent_loc_city);
                convertView.setTag(holder);
            }
            if (item.images.size() > 0) {
                mImgLoader.displayImage(item.images.get(0).url, holder.bgImage, poptions);
            } else {
                mImgLoader.displayImage("", holder.bgImage, poptions);
            }

            holder.numSum.setText(item.zhName);
            holder.loc.setText(String.format("~派派 · %d位 · 达人~", item.expertCnt));
            holder.rl_country.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent expertIntent = new Intent(getActivity(), GuilderListActivity.class);
                    expertIntent.putExtra("countryId", item.id);
                    expertIntent.putExtra("countryName", item.zhName);
                    startActivity(expertIntent);
                }
            });
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mCxt, R.layout.talent_loc_cell_head, null);
            }
            header = (TextView) convertView.findViewById(R.id.talent_loc_head);
            header.setText(getSectionStr(section));
            return convertView;
        }

        @Override
        public int getSectionCount() {
            return data.size();
        }

        @Override
        public int getCountInSection(int section) {
            return data.get(section).size();
        }

        @Override
        public String getSectionStr(int section) {
            return data.get(section).get(0).continents.zhName;
        }

        @Override
        public boolean doesSectionHaveHeader(int section) {
            return true;
        }

        @Override
        public boolean shouldListHeaderFloat(int headerIndex) {
            return false;
        }

        private class ViewHolder {
            private FrameLayout rl_country;
            private ImageView bgImage;
            private TextView numSum;
            private TextView loc;
        }
    }

    private void sortCountries(List<CountryWithExpertsBean> conversationList) {
        Collections.sort(conversationList, new Comparator<CountryWithExpertsBean>() {
            @Override
            public int compare(final CountryWithExpertsBean con1, final CountryWithExpertsBean con2) {

                long LastTime2 = con2.rank;
                long LastTime1 = con1.rank;
                if (LastTime1 == 0 || LastTime2 == 0) {
                    return -1;
                }
                if (LastTime2 == LastTime1) {
                    return 0;
                } else if (LastTime2 < LastTime1) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public class MapsDayAdapter extends BaseAdapter {

        public MapsDayAdapter() {
            super();
        }


        @Override
        public int getCount() {
            return continentNames.size();
        }

        @Override
        public Object getItem(int position) {
            return continentNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.loc_cell, null);

            }
            TextView days_title = (TextView) convertView.findViewById(R.id.days_title);
            if (titleTextView.getText().toString().equals(getItem(position).toString()))
                days_title.setTextColor(getResources().getColor(R.color.app_theme_color));
            else days_title.setTextColor(getResources().getColor(R.color.base_color_white));
            days_title.setText(getItem(position).toString());
            //selected=(ImageView) convertView.findViewById(R.id.map_days_selected);
            //if(position==(whichDay-1)){selected.setVisibility(View.VISIBLE);}else{selected.setVisibility(View.GONE);}
            return convertView;
        }
    }
}
