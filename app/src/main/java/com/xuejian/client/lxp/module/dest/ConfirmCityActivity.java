package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CouponBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.widget.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/6/7.
 */
public class ConfirmCityActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView mTvTitleBack;
    @Bind(R.id.tv_list_title)
    TextView mTvListTitle;
    @Bind(R.id.rl_normal_bar)
    RelativeLayout mRlNormalBar;
    @Bind(R.id.lv_city_list)
    XRecyclerView mLvCityList;
    @Bind(R.id.tv_save)
    TextView mTvSave;
    ArrayList<LocBean> list;
    ArrayList<ArrayList<LocBean>> data = new ArrayList<>();
    ArrayList <String> locIds = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_city_list);
        ButterKnife.bind(this);
        list = getIntent().getParcelableArrayListExtra("loc");

        resizeData();
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStrategyByCityIds();
            }
        });
       bindView();
    }

    private void resizeData() {
        for (LocBean locBean : list) {
            ArrayList<LocBean> item = new ArrayList<>();
            item.add(locBean);
            data.add(item);
            locIds.add(locBean.id);
        }
    }

    private void bindView() {
        mLvCityList.setLayoutManager(new LinearLayoutManager(this));
        GoodsListAdapter adapter = new GoodsListAdapter();
        adapter.getDataList().addAll(list);
        mLvCityList.setAdapter(adapter);
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position, CouponBean id);
    }

    private class GoodsListAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<LocBean> mDataList;
        private OnItemClickListener listener;
        private Context mContext;

        public GoodsListAdapter() {
            mDataList = new ArrayList<LocBean>();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ArrayList<LocBean> getDataList() {
            return mDataList;
        }


        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_confirm_city, parent, false);
            mContext = parent.getContext();
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final LocBean bean = (LocBean) getItem(position);
            holder.mTvCityName.setText(bean.zhName);
            holder.mSelectNum.setListenr(new NumberPicker.OnButtonClick() {
                @Override
                public void OnValueChange(int value) {
                    ArrayList<LocBean> list= new ArrayList<LocBean>();
                    for (int i = 0;i<value;i++){
                        list.add(bean);
                    }
                    data.set(position,list);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_city_name)
        TextView mTvCityName;
        @Bind(R.id.select_num)
        NumberPicker mSelectNum;
        @Bind(R.id.ll_container)
        LinearLayout mLlContainer;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void createStrategyByCityIds() {
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        } catch (Exception e) {
            DialogManager.getInstance().dissMissLoadingDialog();
        }
        JSONArray array = new JSONArray();
        int j=0;
        for (int i = 0; i < data.size(); i++) {
            for (LocBean locBean : data.get(i)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("dayIndex",j++);
                    JSONObject object = new JSONObject();
                    object.put("id",locBean.id);
                    object.put("zhName",locBean.zhName);
                    object.put("enName",locBean.enName);
                    jsonObject.put("locality",object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(jsonObject);
            }
        }

        TravelApi.createGuide("create", locIds, false,array, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<StrategyBean> strategyResult = CommonJson.fromJson(result, StrategyBean.class);
                if (strategyResult.code == 0) {
                    Intent intent = new Intent(ConfirmCityActivity.this,StrategyActivity.class);
                    intent.putExtra("strategy",strategyResult.result);
                    intent.putExtra("id","1");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(ConfirmCityActivity.this).showToast("创建失败");
                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }
}
