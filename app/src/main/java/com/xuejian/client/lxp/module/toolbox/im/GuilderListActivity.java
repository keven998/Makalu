package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private int[] lebelColors =new int[]{
            R.drawable.all_light_green_label,R.drawable.all_light_red_label,R.drawable.all_light_perple_label,R.drawable.all_light_blue_label,R.drawable.all_light_yellow_label
    };
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

        adapter = new ExpertAdapter(this);
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

            }
        }
    }
    private void searchExpert(final String keyword) {
        UserApi.searchExpert(keyword, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<ExpertBean> list = CommonJson4List.fromJson(result, ExpertBean.class);
                if (list.code == 0) {
                    try{
                        if (list.result.size() == 0) {
                            ToastUtil.getInstance(mContext).showToast(String.format("暂时还没有达人去过“%s”", keyword));
                        } else {
                            bindView(list.result);
                        }
                    }catch (Exception ex){

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
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
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
                vh = new ViewHolder();
                vh.avatarView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                vh.residenceView = (TextView) convertView.findViewById(R.id.tv_expert_loc);
                vh.nickView = (TextView) convertView.findViewById(R.id.tv_expert_name);
                vh.expert_level = (TextView) convertView.findViewById(R.id.tv_expert_level);
                vh.tv_comment = (TextView) convertView.findViewById(R.id.tv_pi_comment);
                vh.expert_tag = (TagListView)convertView.findViewById(R.id.expert_tag);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            //获取接口数据进行加载

            ExpertBean eb = (ExpertBean) getItem(position);
            ViewCompat.setElevation(convertView, CommonUtils.dip2px(mContext, 5));
            imgLoader.displayImage(eb.avatar, vh.avatarView, options);
            vh.nickView.setText(eb.nickName);
            boolean  flag = false;
            StringBuffer sb = new StringBuffer();
            if (!TextUtils.isEmpty(eb.residence)) {
                sb.append(eb.residence);
                flag = true;
            }
            if(!TextUtils.isEmpty(eb.birthday)){
                if(flag){
                    sb.append("  "+getAge(eb.birthday)+"岁");
                }else{
                    sb.append(""+getAge(eb.birthday)+"岁");
                }
            }
            vh.residenceView.setText(sb.toString());

            ViewCompat.setElevation(vh.expert_level, CommonUtils.dip2px(mContext, 5));
            vh.expert_level.setText(String.format("V%d", eb.level));

            if(eb.tags!=null && eb.tags.size()>0){
                List<Tag> mTags = new ArrayList<Tag>();
                initData(mTags,eb.tags);
                vh.expert_tag.removeAllViews();
                vh.expert_tag.setTagViewTextColorRes(R.color.white);
                vh.expert_tag.setmTagViewResId(R.layout.expert_tag);
                vh.expert_tag.setTags(mTags);
            }else{
                vh.expert_tag.removeAllViews();
            }

            if(eb.expertInfo!=null){
                vh.tv_comment.setText(eb.expertInfo.getProfile());
            }
//            else{
//                vh.tv_comment.setText("Ta还没添加任何描述！");
//            }
            //足迹
            /*String st1 = "服务城市：";
            String st2 = countryName;
            SpannableString attrStr2 = new SpannableString(st1);
            attrStr2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, st1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //   attrStr2.setSpan(new AbsoluteSizeSpan(13, true), 0, attrStr2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb = new SpannableStringBuilder();
            spb.append(attrStr2).append(st2);
            vh.titleView.setText(spb);*/

            /*String str1 = "派派点评：";
            SpannableString attrStr1 = new SpannableString(str1);
            attrStr1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.tv_comment.setText(attrStr1);*/
            return convertView;
        }
    }
    public int getNextColor(int currentcolor){
        Random random = new Random();
        int nextValue = random.nextInt(4);
        if(nextValue==0){
            nextValue++;
        }
        return (nextValue+currentcolor)%5;
    }
    public void initData(List<Tag> mTags,ArrayList<String> tagStr) {
        Random random = new Random();
        int lastColor = random.nextInt(4);
        for (int i = 0; i <tagStr.size(); i++) {
            Tag tag = new Tag();
            tag.setTitle(tagStr.get(i));
            tag.setId(i);
            tag.setBackgroundResId(lebelColors[lastColor]);
            mTags.add(tag);
            lastColor=getNextColor(lastColor);
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

    private int getAge(String birth) {
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        return Integer.parseInt(date) - birthYear;
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
