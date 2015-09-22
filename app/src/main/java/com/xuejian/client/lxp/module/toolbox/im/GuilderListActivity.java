package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpertAdapter;

import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/14.
 */
public class GuilderListActivity extends PeachBaseActivity {
    //private final int PAGE_SIZE = 16;

    private PullToRefreshListView gridView;
    private ExpertAdapter adapter;
    private int EXPERT_DES = 1;
    private int mCurrentPage = 0;
    private int PAGE_SIZE = 6;
    private String countryId;
    private String countryName;
    TextView stView;

    private String zone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryId = getIntent().getStringExtra("countryId");
        countryName = getIntent().getStringExtra("countryName");
        zone = getIntent().getStringExtra("zone");
        setContentView(R.layout.activity_expert);

        findViewById(R.id.expert_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.tv_title);
        if (TextUtils.isEmpty(zone)){
            titleView.setText(String.format("~派派 · %s · 达人~", countryName));
        }else {
            titleView.setText(String.format("~派派 · %s · 达人~", zone));
        }
        stView = (TextView) findViewById(R.id.tv_subtitle);
        stView.setText("0位");

        initList();
    }
    private void initList() {
        gridView = (PullToRefreshListView) findViewById(R.id.expert_grid);
        gridView.setPullLoadEnabled(false);
        gridView.setPullRefreshEnabled(true);
        gridView.setScrollLoadEnabled(false);
        gridView.setHasMoreData(false);

        adapter = new ExpertAdapter(this,-1);
        gridView.getRefreshableView().setAdapter(adapter);

        gridView.getRefreshableView().setOnItemClickListener(new DarenClick());
        gridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (TextUtils.isEmpty(zone)) {
                    getExpertData(0, PAGE_SIZE);
                } else {
                    searchExpert(zone);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //   getExpertData(mCurrentPage + 1, PAGE_SIZE);
                gridView.onPullUpRefreshComplete();
            }
        });
        if (TextUtils.isEmpty(zone)){
            getExpertData(0, PAGE_SIZE);
        }else {
            searchExpert(zone);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_lxp_guide_lists");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_lxp_guide_lists");
        MobclickAgent.onPause(this);
    }

    public class DarenClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            try{
                ExpertBean xEb = (ExpertBean) adapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(GuilderListActivity.this, HisMainPageActivity.class);
                intent.putExtra("userId", (long) xEb.userId);
                intent.putExtra("isFromExperts", true);
                startActivity(intent);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void searchExpert(final String keyword) {
        UserApi.searchExpert(keyword, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<ExpertBean> list = CommonJson4List.fromJson(result, ExpertBean.class);
                if (list.code == 0) {
                    try {
                        if (list.result.size() == 0) {
                            ToastUtil.getInstance(mContext).showToast(String.format("暂时还没有达人去过“%s”", keyword));
                        } else {
                            bindView(list.result);
                        }
                    } catch (Exception ex) {

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
    public void getExpertData(final int page, final int pageSize) {
        String[] countryIds = {countryId};
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        } catch (Exception e) {
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        UserApi.getExpertById(countryIds, page, pageSize, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<ExpertBean> expertresult = CommonJson4List.fromJson(result, ExpertBean.class);
                if (expertresult.code == 0) {
                    mCurrentPage = page;
                    bindView(expertresult.result);
                }
                gridView.onPullUpRefreshComplete();
                gridView.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                gridView.onPullUpRefreshComplete();
                gridView.onPullDownRefreshComplete();
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                ToastUtil.getInstance(GuilderListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void bindView(List<ExpertBean> result) {
        stView.setText(result.size() + "位");
        if (mCurrentPage == 0) {
            if (adapter == null) {
                adapter = new ExpertAdapter(GuilderListActivity.this,-1);
                gridView.getRefreshableView().setAdapter(adapter);
            } else {
                adapter.reset();
            }
        }
        adapter.getDataList().addAll(result);
        adapter.notifyDataSetChanged();
        if (result == null || result.size() < PAGE_SIZE) {
            gridView.setHasMoreData(false);
        } else {
            gridView.setHasMoreData(true);
        }

        if (adapter.getCount() >= PAGE_SIZE) {
            gridView.setScrollLoadEnabled(true);
        }

    }

    public void refreshView(String locId) {
        String[] strs = new String[1];
        strs[0] = locId;
        try {
            DialogManager.getInstance().showModelessLoadingDialog(mContext);
        } catch (Exception e) {
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        UserApi.getExpertById(strs, 0, 20, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<ExpertBean> expertresult = CommonJson4List.fromJson(result, ExpertBean.class);
                if (expertresult.code == 0) {
                    bindView(expertresult.result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                ToastUtil.getInstance(GuilderListActivity.this).showToast("好像没有网络~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EXPERT_DES) {
            //刷新本页
            if (data != null) {
                String id = data.getExtras().getString("locId");
                refreshView(id);
            }
        }
    }


    private class ViewHolder {
        ImageView avatarView;
        TextView expert_level;
        TextView residenceView;
        TextView nickView;
        TextView tv_comment;
        TagListView  expert_tag;
    }
}
