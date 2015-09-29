package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CountryWithExpertsBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/7/8.
 */
public class OverSeasFragment extends PeachBaseFragment {

    private ListView listView;
    private TalentLocAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.talentloc,container,false);
        initView(view);
        initData();
        return view;
    }



    private void initData() {
        TravelApi.getRecomendCountry(new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson4List<CountryWithExpertsBean> expertResult = CommonJson4List.fromJson(result.toString(), CountryWithExpertsBean.class);
                resizeData(expertResult.result);
                if (getActivity()!=null)PreferenceUtils.cacheData(getActivity(), "countryList", result.toString());
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }
        }, true);
    }

    private void resizeData(List<CountryWithExpertsBean> list) {
        adapter.getList().clear();
        if(list!=null && list.size()>0){
            adapter.getList().addAll(list);
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


        ArrayList<CountryWithExpertsBean>data = new ArrayList<>();
        adapter = new TalentLocAdapter(getActivity(),data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                    intent.putExtra("id",adapter.getList().get(position).id);
                    intent.putExtra("isFromStrategy", false);
                    startActivity(intent);
                }catch (Exception ex){

                }

            }
        });
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



    private class TalentLocAdapter extends BaseAdapter {
        private TextView header;
        private DisplayImageOptions poptions;
        private ArrayList<CountryWithExpertsBean> list;
        private Context mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Context context,ArrayList<CountryWithExpertsBean> list) {
            mCxt = context;
            this.list = list;
            mImgLoader = ImageLoader.getInstance();
            poptions = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.expert_country_list_bg)
                    .showImageForEmptyUri(R.drawable.expert_country_list_bg)
                    .showImageOnLoading(R.drawable.expert_country_list_bg)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)
                    .build();
        }
        public ArrayList<CountryWithExpertsBean> getList(){
            return list;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mCxt, R.layout.talent_loc_cell_content, null);
            }

            final CountryWithExpertsBean item = list.get(position);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.rl_country = (FrameLayout) convertView.findViewById(R.id.fl_country);
                holder.bgImage = (ImageView) convertView.findViewById(R.id.talent_loc_img);
                holder.numSum = (TextView) convertView.findViewById(R.id.talent_loc_num);
                convertView.setTag(holder);
            }
            if (item.images.size() > 0) {
                mImgLoader.displayImage(item.images.get(0).url, holder.bgImage, poptions);
            } else {
                mImgLoader.displayImage("", holder.bgImage, poptions);
            }


            holder.numSum.setText(item.zhName + "");

            return convertView;
        }


        private class ViewHolder {
            private FrameLayout rl_country;
            private ImageView bgImage;
            private TextView numSum;

        }
    }



}
