package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.module.toolbox.ExpertFilterActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/14.
 */
public class GuilderListActivity extends PeachBaseActivity {

    //private PullToRefreshListView listView;
    private PullToRefreshGridView gridView;
    private ImageView user_pic;
    private TextView user_name;
    private TextView user_status_01, user_status_02, user_status_03;
    private TextView expert_age;
    private TextView user_loc;
    private TextView expert_zod;
    private ExpertAdapter adapter;
    private LayoutInflater inflater;
    private TextView places_layout;
    private int EXPERT_DES = 1;
    int mCurrentPage = 0;
    int PAGE_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);
        //search_expert=(EditText)findViewById(R.id.search_expert_name);
        findViewById(R.id.expert_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.expert_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuilderListActivity.this, ExpertFilterActivity.class);
                startActivityForResult(intent, EXPERT_DES);
                overridePendingTransition(R.anim.push_bottom_in, 0);
            }
        });
        inflater = LayoutInflater.from(this);
        initList();
    }

    private void initList() {
        gridView= (PullToRefreshGridView) findViewById(R.id.expert_grid);
        gridView.setPullLoadEnabled(false);
        gridView.setPullRefreshEnabled(true);
        gridView.setScrollLoadEnabled(false);
        gridView.setHasMoreData(false);

//        listView = (PullToRefreshListView) findViewById(R.id.expert_list);
//        listView.setPullLoadEnabled(false);
//        listView.setPullRefreshEnabled(true);
//        listView.setScrollLoadEnabled(false);
//        listView.setHasMoreData(false);

        adapter = new ExpertAdapter(this);
   //     listView.getRefreshableView().setAdapter(adapter);
        gridView.getRefreshableView().setAdapter(adapter);
//        listView.getRefreshableView().setOnItemClickListener(new DarenClick());
        gridView.getRefreshableView().setOnItemClickListener(new DarenClick());
        gridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                getExpertData(0, PAGE_SIZE);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                getExpertData(mCurrentPage + 1, PAGE_SIZE);
            }
        });
        getExpertData(0, PAGE_SIZE);
    }

    public class DarenClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpertBean xEb = (ExpertBean) adapter.getItem(position);
            Intent intent = new Intent();
            intent.setClass(GuilderListActivity.this, HisMainPageActivity.class);
            intent.putExtra("userId", xEb.userId);
            startActivity(intent);
            //ToastUtil.getInstance(ExpertListActivity.this).showToast("点击进入达人详情页或聊天页,第"+(position+1)+"个达人");
        }
    }

    public void getExpertData(final int page, final int pageSize) {
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.searchExpertContact("expert", "roles", page, pageSize, new HttpCallBack<String>() {
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
        });
    }

    public void bindView(List<ExpertBean> result) {
        if (mCurrentPage == 0) {
            adapter = new ExpertAdapter(GuilderListActivity.this);
            //mPoiList.clear();
        //    listView.getRefreshableView().setAdapter(adapter);
            gridView.getRefreshableView().setAdapter(adapter);
        }
        adapter.getDataList().addAll(result);
        adapter.notifyDataSetChanged();
        if (result == null || result.size() < PAGE_SIZE) {
            gridView.setHasMoreData(false);
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            gridView.setHasMoreData(true);
        }

        if (adapter.getCount() >= PAGE_SIZE) {
            gridView.setScrollLoadEnabled(true);
        }

        if (result.size() == 0) {
            if (mCurrentPage == 0) {
                //mMyStrategyLv.getRefreshableView().setEmptyView(findViewById(R.id.empty_view));
                //mMyStrategyLv.doPullRefreshing(true, 0);
            } else {
                ToastUtil.getInstance(this).showToast("已取完所有内容");
            }
            return;
        }
    }

    public class ExpertAdapter extends BaseAdapter {
        protected ArrayList<ExpertBean> mItemDataList = new ArrayList<ExpertBean>();
        private Context context;
        //        private String[] status={"空","忙","阻"};
        public List<ExpertBean> expertBean;
        private DisplayImageOptions options;

        public ExpertAdapter(Context context) {
            this.context = context;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        public ArrayList<ExpertBean> getDataList() {
            return mItemDataList;
        }

        @Override
        public int getCount() {
            return mItemDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                inflater = LayoutInflater.from(this.context);
                convertView = inflater.inflate(R.layout.expert_list_cont, null);
            }
            //初始化控件
            user_pic = (ImageView) convertView.findViewById(R.id.expert_pic);
            user_name = (TextView) convertView.findViewById(R.id.expert_name);
            user_loc = (TextView) convertView.findViewById(R.id.expert_location);
            places_layout = (TextView) convertView.findViewById(R.id.places_layout);
            expert_age=(TextView)convertView.findViewById(R.id.expert_age);
            expert_zod=(TextView)convertView.findViewById(R.id.expert_zod);
            //获取接口数据进行加载
            ExpertBean eb = (ExpertBean) getItem(position);
            LogUtil.d(eb.gender + "================");
            if (eb.gender.equalsIgnoreCase("M")) {
                user_pic.setBackgroundResource(R.drawable.expert_boy);
            } else if (eb.gender.equalsIgnoreCase("F")) {
                user_pic.setBackgroundResource(R.drawable.expert_girl);
            } else {
                user_pic.setBackgroundResource(R.drawable.expert_unknow);
            }
            ImageLoader.getInstance().displayImage(eb.avatarSmall, user_pic, options);
            user_name.setText(eb.nickName);
            user_loc.setText(eb.residence);
            if (!TextUtils.isEmpty(eb.birthday)){
                expert_age.setText(getAge(eb.birthday)+"岁");
            }

         //   expert_zod.setText(eb.zodiac);
           //控制达人的状态
//            if (!eb.getRolesDescription().equals("")) {
//                user_status_01.setText(eb.getRolesDescription());
//            } else {
//                user_status_01.setPadding(0, 0, 0, 0);
//            }
//            if(expertBean.get(position).travelStatus==null||expertBean.get(position).travelStatus.equals("")||expertBean.get(position).travelStatus.equals("null")){
//                changeStatusBg(user_status_01,user_status_02,user_status_03,"空");
//            }else/* if(status[position]=="忙"){
//                changeStatusBg(user_status_02,user_status_01,user_status_03,status[position]);
//            }else if(status[position]=="阻")*/{
//                changeStatusBg(user_status_03,user_status_02,user_status_01,"阻");
//            }

            places_layout.setText(eb.getTraceDescription());
            //动态添加达人去过的地方
            /*places_layout.removeAllViews();
            layout_width=places_layout.getMeasuredWidth();
            int all_views_width=0;
            for(int i=0;i<places.length;i++){
                View places_view=inflater.inflate(R.layout.expert_place_view,places_layout,false);
                user_place=(TextView)places_view.findViewById(R.id.expert_places);
                user_place.setText(places[i]);
                int tv_width=user_place.getMeasuredWidth()+ LocalDisplay.dp2px(7);
                all_views_width+=tv_width;
                if(all_views_width<=layout_width){
                    places_layout.addView(user_place);
                }else{break;}
            }*/

            return convertView;
        }
    }

    private void changeStatusBg(TextView v1, TextView v2, TextView v3, String value) {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v1.setText(value);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void refreshView(String locId) {
        // mCurrentPage=0;
        String[] strs = new String[1];
        strs[0] = locId;
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.getExpertById(strs, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                //
                //  listView.removeAllViews();
                CommonJson4List<ExpertBean> expertresult = CommonJson4List.fromJson(result, ExpertBean.class);
                if (expertresult.code == 0) {
                    bindView(expertresult.result);
                   /* adapter = new ExpertAdapter(ExpertListActivity.this, expertresult.result);
                    expertBeans = expertresult.result;
                    listView.getRefreshableView().setAdapter(adapter);*/
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                ToastUtil.getInstance(GuilderListActivity.this).showToast("好像没有网络额~");
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

    public int getAge(String birth) {
        int age = 0;
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        age = Integer.parseInt(date) - birthYear;
        return age;
    }
}
