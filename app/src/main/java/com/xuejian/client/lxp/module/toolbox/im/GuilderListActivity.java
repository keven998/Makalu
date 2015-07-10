package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
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
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.module.toolbox.ExpertFilterActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxp_dqm07 on 2015/4/14.
 */
public class GuilderListActivity extends PeachBaseActivity {
    private PullToRefreshGridView gridView;
    private ExpertAdapter adapter;
    private int EXPERT_DES = 1;
    private int mCurrentPage = 0;
    private int PAGE_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);

        findViewById(R.id.expert_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText("~派派 · 阿尔及利亚 · 达人~");
        TextView stView = (TextView) findViewById(R.id.tv_subtitle);
        stView.setText("9999位");

        initList();
    }

    private void initList() {
        gridView = (PullToRefreshGridView) findViewById(R.id.expert_grid);
        gridView.setPullLoadEnabled(false);
        gridView.setPullRefreshEnabled(true);
        gridView.setScrollLoadEnabled(false);
        gridView.setHasMoreData(false);

        adapter = new ExpertAdapter(this);
        gridView.getRefreshableView().setAdapter(adapter);
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
            if (adapter == null) {
                adapter = new ExpertAdapter(GuilderListActivity.this);
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

    private class ExpertAdapter extends BaseAdapter {
        protected ArrayList<ExpertBean> mItemDataList = new ArrayList<ExpertBean>();
        private Context context;
        private DisplayImageOptions options;
        private LayoutInflater inflater;
        private int width;
        private ImageLoader imgLoader;

        public ExpertAdapter(Context cxt) {
            this.context = cxt;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

            width = CommonUtils.getScreenWidth((Activity) cxt) - LocalDisplay.dp2px(24);
            inflater = LayoutInflater.from(cxt);

            imgLoader = ImageLoader.getInstance();
        }

        public void reset() {
            mItemDataList.clear();
            notifyDataSetChanged();
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
            ViewHolder vh;
            if (convertView == null) {
                inflater = LayoutInflater.from(this.context);
                convertView = inflater.inflate(R.layout.expert_list_cont, null);
                int padding = LocalDisplay.dp2px(6);
                AbsListView.LayoutParams lparms = new AbsListView.LayoutParams(width / 2, width * 39 / 54);
                convertView.setLayoutParams(lparms);
                convertView.setPadding(padding, padding, padding, padding);

                vh = new ViewHolder();
                vh.avatarView = (ImageView) convertView.findViewById(R.id.expert_pic);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams((int) (0.41 * width / 2.0), (int) (0.41 * 41 * width / 74));
                llp.gravity = Gravity.CENTER_HORIZONTAL;
                llp.topMargin = 5 * padding / 2;
                vh.avatarView.setLayoutParams(llp);
                int dp = llp.width * 4 / 74;
                vh.avatarView.setPadding(dp, dp * 34 / 12, dp, dp);

                vh.titleView = (TextView) convertView.findViewById(R.id.tv_track_summary);
                vh.residenceView = (TextView) convertView.findViewById(R.id.expert_location);
                vh.nickView = (TextView) convertView.findViewById(R.id.expert_name);
                vh.consView = (TextView) convertView.findViewById(R.id.expert_zod);
                vh.ageView = (TextView) convertView.findViewById(R.id.expert_age);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            //获取接口数据进行加载
            ExpertBean eb = (ExpertBean) getItem(position);
            if (eb.gender.equalsIgnoreCase("M")) {
                vh.avatarView.setBackgroundResource(R.drawable.expert_boy);
            } else if (eb.gender.equalsIgnoreCase("F")) {
                vh.avatarView.setBackgroundResource(R.drawable.expert_girl);
            } else {
                vh.avatarView.setBackgroundResource(R.drawable.expert_unknow);
            }
            imgLoader.displayImage(eb.avatarSmall, vh.avatarView, options);
            vh.nickView.setText(eb.nickName);
            if (!TextUtils.isEmpty(eb.residence)) {
                vh.residenceView.setText(eb.residence);
            } else {
                vh.residenceView.setText("");
            }
            if (!TextUtils.isEmpty(eb.birthday)) {
                vh.ageView.setText(String.valueOf(getAge(eb.birthday)));
            } else {
                vh.ageView.setText("");
            }
            if (!TextUtils.isEmpty(eb.birthday)) {
                vh.consView.setText("射手座");
            } else {
                vh.consView.setText("");
            }

            //足迹
            String st1 = "99个城市\n";
            String st2 = "泰国足迹";
            SpannableString attrStr2 = new SpannableString(st2);
            attrStr2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_text_iii)), 0, st2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            attrStr2.setSpan(new AbsoluteSizeSpan(13, true), 0, attrStr2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb = new SpannableStringBuilder();
            spb.append(st1).append(attrStr2);
            vh.titleView.setText(spb);

            return convertView;
        }
    }

    public void refreshView(String locId) {
        String[] strs = new String[1];
        strs[0] = locId;
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.getExpertById(strs, new HttpCallBack<String>() {
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

    private int getAge(String birth) {
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        return Integer.parseInt(date) - birthYear;
    }

    private class ViewHolder {
        ImageView avatarView;
        TextView titleView;
        TextView scoreView;
        TextView residenceView;
        TextView consView;
        TextView nickView;
        TextView ageView;
    }
}
