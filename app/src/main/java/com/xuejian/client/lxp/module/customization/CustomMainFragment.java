package com.xuejian.client.lxp.module.customization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.bean.ProjectEvent;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by yibiao.qin on 2016/3/28.
 */
public class CustomMainFragment extends PeachBaseFragment {

    XRecyclerView recyclerView;
    ProjectAdapter adapter;

    private static final int PAGE_SIZE = 15;
    private static int COUNT = 15;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_info,null);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(true);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_project, (ViewGroup) view.findViewById(R.id.content),false);
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
                startActivity(new Intent(getActivity(),ProjectCreateActivity.class));
            }
        });
        getData(0,COUNT,true);
        return view;
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
