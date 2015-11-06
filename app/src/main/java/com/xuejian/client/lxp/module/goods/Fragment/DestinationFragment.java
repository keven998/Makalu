package com.xuejian.client.lxp.module.goods.Fragment;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CountryWithExpertsBean;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleLayout;
import com.xuejian.client.lxp.common.widget.circleMenu.CircleTextView;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/5.
 */
public class DestinationFragment extends PeachBaseFragment implements CircleLayout.OnRotationFinishedListener, AbsListView.OnScrollListener {
    private ListView listView;
    private TalentLocAdapter adapter;
    boolean flag = true;
    CircleLayout circleLayout;
    LinearLayout menu;
    ImageView showMenu;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_destination, container, false);
        initView(view);
        initData();
        return view;
    }


    private void initData() {
    }


    private void initView(View view) {
        showMenu = (ImageView) view.findViewById(R.id.iv_show);
        menu = (LinearLayout) view.findViewById(R.id.ll_round_menu);
        circleLayout = (CircleLayout) view.findViewById(R.id.circle_layout);
        circleLayout.setOnRotationFinishedListener(this);
        listView = (ListView) view.findViewById(R.id.talent_loc_list);
        listView.setOnScrollListener(this);
        ArrayList<CountryWithExpertsBean> data = new ArrayList<>();
        adapter = new TalentLocAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_from_right);
                menu.startAnimation(animation);
            }
        });
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
        ((CircleTextView)view).setTextColor(getResources().getColor(R.color.app_theme_color));
        ((CircleTextView)view).setTextSize(14f);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        System.out.println("scrollState "+scrollState);
        if (scrollState==SCROLL_STATE_FLING||scrollState==SCROLL_STATE_TOUCH_SCROLL){
            showMenu.setVisibility(View.VISIBLE);
            menu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    private class TalentLocAdapter extends BaseAdapter {
        private TextView header;
        private DisplayImageOptions poptions;
        private ArrayList<CountryWithExpertsBean> list;
        private Context mCxt;
        private ImageLoader mImgLoader;

        public TalentLocAdapter(Context context, ArrayList<CountryWithExpertsBean> list) {
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

        public ArrayList<CountryWithExpertsBean> getList() {
            return list;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 10;
         //   return list.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mCxt, R.layout.cell_destinaiton, null);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.rl_country = (FrameLayout) convertView.findViewById(R.id.fl_country);
                holder.bgImage = (ImageView) convertView.findViewById(R.id.talent_loc_img);
                holder.numSum = (TextView) convertView.findViewById(R.id.talent_loc_num);
                holder.ennameText =(TextView) convertView.findViewById(R.id.loc_en_name);
                convertView.setTag(holder);
            }

            if (flag){
                mImgLoader.displayImage("http://images.taozilvxing.com/78b3baf2f60d02ea70d7a8a30dfaf0b1?imageMogr2/auto-orient/strip/gravity/NorthWest/crop/!994x460a4a186/thumbnail/1200", holder.bgImage, poptions);
                flag=false;
            }else {
                flag=true;
                mImgLoader.displayImage("http://images.taozilvxing.com/af563f2f2e6bea2560857c6026e428a1?imageMogr2/auto-orient/strip/gravity/NorthWest/crop/!998x570a2a2/thumbnail/1200", holder.bgImage, poptions);
            }
             holder.ennameText.setText("China");
            holder.numSum.setText("中国");
            return convertView;
        }


        private class ViewHolder {
            private FrameLayout rl_country;
            private ImageView bgImage;
            private TextView numSum;
            private TextView ennameText;
        }
    }




}
