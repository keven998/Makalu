package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.VerticalTextView;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleLayout;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleTextView;
import com.xuejian.client.lxp.module.dest.SearchAllActivity;
import com.xuejian.client.lxp.module.goods.CountryListActivity;

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
                intent.putExtra("type", "vs");
                startActivity(intent);
            }
        });
        showMenu = (VerticalTextView) view.findViewById(R.id.iv_show);
        menu = (LinearLayout) view.findViewById(R.id.ll_round_menu);
        circleLayout = (CircleLayout) view.findViewById(R.id.circle_layout);
        circleLayout.setOnRotationFinishedListener(this);
        listView = (ListView) view.findViewById(R.id.talent_loc_list);
        listView.setOnScrollListener(this);
        ArrayList<CountryBean> data = new ArrayList<>();
        adapter = new TalentLocAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideMenu();
                Intent intent = new Intent(getActivity(), CountryListActivity.class);
                intent.putExtra("id", adapter.getItem(position).id);
                intent.putExtra("name", adapter.getItem(position).zhName);
                startActivity(intent);
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
    }

    private void showMenu(){
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
        private ArrayList<CountryBean> list;
        private Context mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Context context, ArrayList<CountryBean> list) {
            mCxt = context;
            this.list = list;
            mImgLoader = ImageLoader.getInstance();
            poptions = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.drawable.expert_country_list_bg)
                    .showImageForEmptyUri(R.drawable.expert_country_list_bg)
                    .showImageOnLoading(R.drawable.expert_country_list_bg)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565)
             //       .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)
                    .build();
        }

        public ArrayList<CountryBean> getList() {
            return list;
        }

        @Override
        public CountryBean getItem(int position) {
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
            final CountryBean bean = (CountryBean) getItem(position);
            if (convertView == null) {
                convertView = View.inflate(mCxt, R.layout.cell_destinaiton, null);
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
                mImgLoader.displayImage(bean.images.get(0).url, holder.bgImage, poptions);
            } else {
                mImgLoader.displayImage("", holder.bgImage, poptions);
            }
            holder.enName.setText(bean.enName);
            holder.zhName.setText(bean.zhName);
            holder.storeCount.setText(String.valueOf(bean.commodityCnt));
            return convertView;
        }


        private class ViewHolder {
            private ImageView bgImage;
            private TextView zhName;
            private TextView enName;
            private TextView storeCount;
        }
    }


}
