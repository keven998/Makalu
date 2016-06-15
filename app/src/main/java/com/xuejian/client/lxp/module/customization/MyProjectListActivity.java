package com.xuejian.client.lxp.module.customization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;
import com.xuejian.client.lxp.module.my.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/8.
 */
public class MyProjectListActivity extends PeachBaseActivity {

    @Bind(R.id.iv_nav_back)
    ImageView ivNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @Bind(R.id.recyclerview)
    XRecyclerView recyclerview;
    @Bind(R.id.content)
    LinearLayout content;
    @Bind(R.id.tv_create_project)
    TextView tv_create_project;

    ProjectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);
        ButterKnife.bind(this);
        tvTitleBarTitle.setText("我发布的需求");
        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerview.setPullRefreshEnabled(false);
        setupRecyclerView(recyclerview);
        long userId = AccountManager.getInstance().getLoginAccount(this).getUserId();
        getData(userId);
        tv_create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().getLoginAccount(MyProjectListActivity.this)==null){
                    Intent logIntent = new Intent(MyProjectListActivity.this, LoginActivity.class);
                    startActivityWithNoAnim(logIntent);
                }else {
                    startActivity(new Intent(MyProjectListActivity.this,ProjectCreateActivity.class));
                }
            }
        });
    }

    private void getData(long userId) {

        if (userId<0)return;
        TravelApi.getMyCustomList(userId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<BountiesBean> list = CommonJson4List.fromJson(result,BountiesBean.class);
                if (list.code==0){
                    if (list.result.size()==0){
                        tv_create_project.setVisibility(View.VISIBLE);
                    }else {
                        adapter.getDataList().addAll(list.result);
                        adapter.notifyDataSetChanged();
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

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        adapter = new ProjectAdapter(this, 0);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id, boolean expire) {
                Intent intent = new Intent();
                intent.putExtra("id",id);
                intent.setClass(MyProjectListActivity.this,ProjectDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
