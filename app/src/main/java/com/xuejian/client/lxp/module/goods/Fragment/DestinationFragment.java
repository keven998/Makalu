package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CityBean;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.VerticalTextView;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleLayout;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleTextView;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;
import com.xuejian.client.lxp.module.dest.SearchAllActivity;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/5.
 */
public class DestinationFragment extends PeachBaseFragment implements CircleLayout.OnRotationFinishedListener, AbsListView.OnScrollListener {
    private ListView listView;
    private TalentLocAdapter adapter;
    CircleLayout circleLayout;
    LinearLayout menu;
    VerticalTextView showMenu;
    String[] code = new String[]{"RCOM", "AS", "EU", "AF", "NA", "OC", "SA"};
    String[] zhName = new String[]{"热门", "亚洲", "欧洲", "非洲", "北美洲", "大洋洲", "南美洲"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_destination, container, false);
        initView(view);
        getData(code[0]);
        return view;
    }


    private void getData(String code) {
        if ("RCOM".equals(code)){
            TravelApi.getRecommendCity(new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    CommonJson4List<CityBean> list = CommonJson4List.fromJson(result, CityBean.class);
                    adapter.getList().clear();
                    adapter.getList().addAll(list.result);
                    adapter.notifyDataSetChanged();

                    if (menu.getVisibility() == View.VISIBLE) {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
                        menu.startAnimation(animation);
                        showMenu.setVisibility(View.VISIBLE);
                        menu.setVisibility(View.GONE);
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }else {
            TravelApi.getCountryList(code, new HttpCallBack<String>() {

                @Override
                public void doSuccess(String result, String method) {
                    CommonJson4List<CountryBean> list = CommonJson4List.fromJson(result, CountryBean.class);
                    adapter.getList().clear();
                    adapter.getList().addAll(list.result);
                    adapter.notifyDataSetChanged();

                    if (menu.getVisibility() == View.VISIBLE) {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
                        menu.startAnimation(animation);
                        showMenu.setVisibility(View.VISIBLE);
                        menu.setVisibility(View.GONE);
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


    private void initView(View view) {
        view.findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchAllActivity.class);
//                intent.putExtra("chatType", chatType);
//                intent.putExtra("toId", toChatUsername);
//                intent.putExtra("conversation", conversation);
                intent.putExtra("userId", AccountManager.getCurrentUserId());
                intent.putExtra("isShare", false);
                intent.putExtra("type", "loc");
                startActivity(intent);
            }
        });
        showMenu = (VerticalTextView) view.findViewById(R.id.iv_show);
        menu = (LinearLayout) view.findViewById(R.id.ll_round_menu);
        circleLayout = (CircleLayout) view.findViewById(R.id.circle_layout);
        circleLayout.setOnRotationFinishedListener(this);
        listView = (ListView) view.findViewById(R.id.talent_loc_list);
        listView.setOnScrollListener(this);
        ArrayList<Object> data = new ArrayList<>();
        adapter = new TalentLocAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideMenu();

                if (adapter.getItem(position) instanceof CountryBean) {
                    Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                    intent.putExtra("id", ((CountryBean) adapter.getItem(position)).id);
                    intent.putExtra("isCountry",true);
                  //  intent.putExtra("name", ((CountryBean) adapter.getItem(position)).zhName);
                    startActivity(intent);
                } else if (adapter.getItem(position) instanceof CityBean) {
                    Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                    intent.putExtra("id", ((CityBean) adapter.getItem(position)).id);
                    //    intent.putExtra("name", ((CountryBean)adapter.getItem(position)).zhName);
                    startActivity(intent);
                }

            }
        });
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        showMenu.setTextColor(getResources().getColor(R.color.base_color_white));
        showMenu.setTextSize(12);
        showMenu.setText("热门");
        ((TextView)view.findViewById(R.id.tv_search_content)).setText("目的地搜索");
    }

    private void showMenu(){
        MobclickAgent.onEvent(getActivity(),"event_changeContinent");
        showMenu.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_right);
        menu.startAnimation(animation);
    }
    private void hideMenu(){
        if (menu.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
            menu.startAnimation(animation);
        }
        showMenu.setVisibility(View.VISIBLE);
        menu.setVisibility(View.GONE);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_countryList");
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_countryList");
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onRotationFinished(View view, String name) {
        ((CircleTextView) view).setTextColor(getResources().getColor(R.color.app_theme_color));
        ((CircleTextView) view).setTextSize(14f);
        getData(code[Integer.parseInt(name)]);
        showMenu.setText(zhName[Integer.parseInt(name)]);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            hideMenu();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    private class TalentLocAdapter extends BaseAdapter {
        private DisplayImageOptions poptions;
        private ArrayList<Object> list;
        private Fragment mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Fragment context, ArrayList<Object> list) {
            mCxt = context;
            this.list = list;
            mImgLoader = ImageLoader.getInstance();
//            poptions = new DisplayImageOptions.Builder()
//                    .showImageOnFail(R.drawable.expert_country_list_bg)
//                    .showImageForEmptyUri(R.drawable.expert_country_list_bg)
//                    .showImageOnLoading(R.drawable.expert_country_list_bg)
//                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//             //       .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//                    .cacheOnDisk(true)
//                    .build();
        }

        public ArrayList<Object> getList() {
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (getItem(position) instanceof CountryBean){
                final CountryBean bean = (CountryBean) getItem(position);
                if (convertView == null) {
                    convertView = View.inflate(mCxt.getActivity(), R.layout.cell_destinaiton, null);
                    holder = new ViewHolder();
                    holder.bgImage = (ImageView) convertView.findViewById(R.id.iv_country_img);
                    holder.zhName = (TextView) convertView.findViewById(R.id.tv_country_zh_name);
                    holder.enName = (TextView) convertView.findViewById(R.id.tv_country_en_name);
                    holder.storeCount = (TextView) convertView.findViewById(R.id.tv_store_num);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (bean.images.size() > 0) {
                    Glide.with(mCxt)
                            .load(bean.images.get(0).url)
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.bgImage);
                    // mImgLoader.displayImage(bean.images.get(0).url, holder.bgImage, poptions);
                } else {
                    Glide.with(mCxt)
                            .load("")
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.bgImage);
                    //   mImgLoader.displayImage("", holder.bgImage, poptions);
                }
                holder.enName.setText(bean.enName);
                holder.zhName.setText(bean.zhName);
                holder.storeCount.setVisibility(View.GONE);
           //     holder.storeCount.setText(String.valueOf(bean.commoditiesCnt));
                return convertView;

            }else {

                final CityBean bean = (CityBean) getItem(position);
                if (convertView == null) {
                    convertView = View.inflate(mCxt.getActivity(), R.layout.cell_destinaiton, null);
                    holder = new ViewHolder();
                    holder.bgImage = (ImageView) convertView.findViewById(R.id.iv_country_img);
                    holder.zhName = (TextView) convertView.findViewById(R.id.tv_country_zh_name);
                    holder.enName = (TextView) convertView.findViewById(R.id.tv_country_en_name);
                    holder.storeCount = (TextView) convertView.findViewById(R.id.tv_store_num);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (bean.images.size() > 0) {
                    Glide.with(mCxt)
                            .load(bean.images.get(0).url)
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.bgImage);
                    // mImgLoader.displayImage(bean.images.get(0).url, holder.bgImage, poptions);
                } else {
                    Glide.with(mCxt)
                            .load("")
                            .placeholder(R.drawable.ic_default_picture)
                            .error(R.drawable.ic_default_picture)
                            .centerCrop()
                            .into(holder.bgImage);
                    //   mImgLoader.displayImage("", holder.bgImage, poptions);
                }
                holder.enName.setText(bean.enName);
                holder.zhName.setText(bean.zhName);
                if (bean.commoditiesCnt==0){
                    holder.storeCount.setVisibility(View.GONE);
                }else {
                    holder.storeCount.setVisibility(View.VISIBLE);
                    holder.storeCount.setText(String.valueOf(bean.commoditiesCnt));
                }

//
//                holder.storeCount.setVisibility(View.VISIBLE);
//                holder.storeCount.setText(String.valueOf(bean.commoditiesCnt));
                return convertView;

            }


        }


        private class ViewHolder {
            private ImageView bgImage;
            private TextView zhName;
            private TextView enName;
            private TextView storeCount;
        }
    }


}
