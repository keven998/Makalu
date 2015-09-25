package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
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
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;
import com.xuejian.client.lxp.module.dest.SearchExpertActivity;
import com.xuejian.client.lxp.module.toolbox.im.GuilderListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/7/8.
 */
public class OverSeasFragment extends PeachBaseFragment {

    private ListView listView;


    private TalentLocAdapter adapter;
    private ArrayList<Integer> headerPos = new ArrayList<Integer>();
    private int lastPos = 0;
    private String[] delta = {"亚洲", "欧洲", "北美洲", "南美洲", "非洲", "大洋洲"};
    private String[] deltaEN = {"AS", "EU", "NA", "SA", "AF", "OC"};
    List<String> lists;
    List<String> listsEN;
    List<String> continentNames = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.talentloc,container,false);
        lists = Arrays.asList(delta);
        listsEN = Arrays.asList(deltaEN);
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

                resizeData(expertResult.result);
                PreferenceUtils.cacheData(getActivity(),"countryList",result.toString());
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
        adapter.getList().clear();
        continentNames.clear();
        for (int i = 0; i < 6; i++) {
            adapter.getList().add(new ArrayList<CountryWithExpertsBean>());
        }
//        for (CountryWithExpertsBean bean : list) {
//            adapter.getList().get(lists.indexOf(bean.continents.zhName)).add(bean);
//        }
        for (CountryWithExpertsBean bean : list) {
            int p = listsEN.indexOf(bean.continents.code);
            if (p>=0)adapter.getList().get(p).add(bean);
        }

        ArrayList<ArrayList<CountryWithExpertsBean>> del = new ArrayList<>();
        for (ArrayList<CountryWithExpertsBean> beans :  adapter.getList()) {
            if (beans.size() == 0) {
                del.add(beans);
            } else {
                sortCountries(beans);
            }
        }
        adapter.getList().removeAll(del);
//        adapter = new TalentLocAdapter(getActivity());
//        listView.setAdapter(adapter);
        getHeaderPos();
        for (ArrayList<CountryWithExpertsBean> beans :  adapter.getList()) {
            String name = beans.get(0).continents.zhName;
        //    if ("美洲".equals(name))name = "南美洲";
            continentNames.add(name);
        }
        adapter.notifyDataSetChanged();
    }

    private void initView(View view) {
       /* view.findViewById(R.id.expert_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchExpertActivity.class);
                startActivity(intent);
            }
        });*/



        listView = (ListView) view.findViewById(R.id.talent_loc_list);


        ArrayList<ArrayList<CountryWithExpertsBean>> data = new ArrayList<>();
        adapter = new TalentLocAdapter(getActivity(),data);
        listView.setAdapter(adapter);
        String datas = PreferenceUtils.getCacheData(getActivity(), "countryList");

        if (!TextUtils.isEmpty(datas)){
            CommonJson4List<CountryWithExpertsBean> expertResult = CommonJson4List.fromJson(datas, CountryWithExpertsBean.class);
            try {
                resizeData(expertResult.result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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




    public void getHeaderPos() {
        headerPos.clear();
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
        private ArrayList<ArrayList<CountryWithExpertsBean>> list;
        private Context mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Context context,ArrayList<ArrayList<CountryWithExpertsBean>> list) {
            mCxt = context;
            this.list = list;
            mImgLoader = ImageLoader.getInstance();
            poptions = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .showImageOnLoading(R.drawable.messages_bg_useravatar)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)
                    .build();
        }
        public ArrayList<ArrayList<CountryWithExpertsBean>> getList(){
            return list;
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
            return list.get(section).get(position);
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

            if (!TextUtils.isEmpty(item.zhName)&&!TextUtils.isEmpty(item.enName)){
                StringBuilder sb = new StringBuilder();
                sb.append(item.zhName).append("\n").append(item.enName);
                SpannableString zh =new SpannableString(sb);
                zh.setSpan(new AbsoluteSizeSpan(24,true),0,item.zhName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                zh.setSpan(new TypefaceSpan("default-bold"),0,item.zhName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                zh.setSpan(new AbsoluteSizeSpan(18,true),item.zhName.length()+1,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.numSum.setText(zh);
            }

            holder.loc.setText(String.valueOf(item.expertCnt));
            holder.rl_country.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                    intent.putExtra("id", item.id);
                    intent.putExtra("isFromStrategy", false);
                    startActivity(intent);
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
            return list.size();
        }

        @Override
        public int getCountInSection(int section) {
            return list.get(section).size();
        }

        @Override
        public String getSectionStr(int section) {
            return list.get(section).get(0).continents.zhName;
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

}
