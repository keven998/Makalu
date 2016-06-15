package com.xuejian.client.lxp.module.customization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.bean.ColumnBean;
import com.xuejian.client.lxp.bean.CusCount;
import com.xuejian.client.lxp.bean.ProjectEvent;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.my.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

/**
 * Created by yibiao.qin on 2016/3/28.
 */
public class CustomMainFragment extends PeachBaseFragment {

    XRecyclerView recyclerView;
    ProjectAdapter adapter;
    TextView tv_info;
    private static final int PAGE_SIZE = 15;
    private static int COUNT = 15;
    AutoScrollViewPager mAutoScrollViewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_info,null);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(true);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_project, (ViewGroup) view.findViewById(R.id.content),false);
        mAutoScrollViewPager = (AutoScrollViewPager) headView.findViewById(R.id.vp_pic);
        tv_info = (TextView) headView.findViewById(R.id.tv_info);
        recyclerView.addHeaderView(headView);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                getData(adapter.getItemCount(),COUNT,false);
            }
        });
        setupRecyclerView(recyclerView);
        TextView tvCreate = (TextView) headView.findViewById(R.id.tv_create_project);
        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().getLoginAccount(getActivity())==null){
                    Intent logIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityWithNoAnim(logIntent);
                }else {
                    startActivity(new Intent(getActivity(),ProjectCreateActivity.class));
                }
            }
        });
        getData(0,COUNT,true);
        getCnt();
        getImg();
        return view;
    }

    private void getImg() {
        long userId = -1;
        User user = AccountManager.getInstance().getLoginAccount(getActivity());
        if (user!=null){
            userId = user.getUserId();
        }
        TravelApi.getMainPageColumns(userId,new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<ColumnBean> list = CommonJson4List.fromJson(result, ColumnBean.class);
                bindView(list.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private void bindView(List<ColumnBean> result) {
        for (ColumnBean columnBean : result) {
            if ("slide".equals(columnBean.getColumnType())) {
                mAutoScrollViewPager.setAdapter(new GoodsPageAdapter(getActivity(), result.get(0).getColumns()));
            }
        }
    }
    class GoodsPageAdapter extends PagerAdapter {

        private Context mContext;
        private List< ColumnBean.ColumnsEntity> mDatas;

        public GoodsPageAdapter(Context context, List< ColumnBean.ColumnsEntity> datas) {
            mDatas = datas;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.spot_detail_picture_height));
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundColor(getResources().getColor(R.color.color_gray_light));
            ColumnBean.ColumnsEntity ib = mDatas.get(position);
            ImageLoader.getInstance().displayImage(ib.getImages().get(0).url, imageView, UILUtils.getDefaultOption());
            container.addView(imageView, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
    private void getCnt() {
        TravelApi.CUS_TOTAL_COUNT(new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<CusCount> commonJson = CommonJson.fromJson(result.toString(),CusCount.class);
                tv_info.setText(String.format(Locale.CHINA,"已为%d位旅行者提供了定制服务",commonJson.result.serviceCnt));
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnProjectEvent(ProjectEvent event){
        if ("success".equals(event.status)){
            getData(0,COUNT,true);
        }else if ("refresh".equals(event.status)){
            getData(0,COUNT,true);
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new ProjectAdapter(getActivity(), 0);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id, boolean expire) {
                Intent intent = new Intent();
                intent.putExtra("id",id);
                intent.setClass(getActivity(),ProjectDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getData(int start , int count,final boolean refresh) {
        TravelApi.getBounties(start+"",count+"",new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<BountiesBean> list = CommonJson4List.fromJson(result,BountiesBean.class);
                if (list.code==0){

                    if (refresh)adapter.getDataList().clear();

                    adapter.getDataList().addAll(list.result);
                    adapter.notifyDataSetChanged();

                    if (list.result.size() >= COUNT) {

                    } else {
                        recyclerView.setLoadingMoreEnabled(false);
                    }
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
