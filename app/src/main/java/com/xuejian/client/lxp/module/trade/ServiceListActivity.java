package com.xuejian.client.lxp.module.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.customization.ProjectAdapter;
import com.xuejian.client.lxp.module.customization.ProjectDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/8.
 */
public class ServiceListActivity extends PeachBaseActivity {


    @Bind(R.id.iv_nav_back)
    ImageView ivNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView tvTitleBarTitle;
    @Bind(R.id.recyclerview)
    XRecyclerView recyclerview;
    @Bind(R.id.content)
    LinearLayout content;

    ProjectAdapter adapter;
    long userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);
        ButterKnife.bind(this);
        tvTitleBarTitle.setText("我的服务");
        User user = AccountManager.getInstance().getLoginAccount(this);
        if (user!=null){
            userId = user.getUserId();
        }
        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerview.setPullRefreshEnabled(false);
        setupRecyclerView(recyclerview);
        getData(userId);
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
                intent.setClass(ServiceListActivity.this,ProjectDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData(long userId) {

    }
}
