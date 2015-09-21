package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/9/2.
 */
public class SearchExpertActivity extends PeachBaseActivity {

    @InjectView(R.id.search_city_cancel)
    ImageView searchCityCancel;
    @InjectView(R.id.search_city_text)
    EditText searchCityText;
    @InjectView(R.id.search_city_button)
    TextView searchCityButton;
    @InjectView(R.id.search_city_bar)
    LinearLayout searchCityBar;
    @InjectView(R.id.expert_list)
    PullToRefreshListView expertList;
    @InjectView(R.id.iv_clean)
    ImageView iv_clean;
    ExpertAdapter adapter;
    private int[] lebelColors =new int[]{
            R.drawable.all_light_green_label,R.drawable.all_light_red_label,R.drawable.all_light_perple_label,R.drawable.all_light_blue_label,R.drawable.all_light_yellow_label
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expert_search);
        ButterKnife.inject(this);
        initList();
        searchCityCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCityText.setText("");
            }
        });
        searchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchCityText.getText().toString())) {
                    //  ToastUtil.getInstance(mContext).showToast("");
                } else {
                    searchExpert(searchCityText.getText().toString().trim());
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        searchCityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    adapter.reset();
                    iv_clean.setVisibility(View.INVISIBLE);
                }
                if (s.length() > 0) {
                    iv_clean.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    public void bindView(List<ExpertBean> result) {
        if (adapter == null) {
            adapter = new ExpertAdapter(SearchExpertActivity.this);
            expertList.getRefreshableView().setAdapter(adapter);
        } else {
            adapter.reset();
        }
        adapter.getDataList().addAll(result);
        adapter.notifyDataSetChanged();
        expertList.setHasMoreData(false);
    }

    private void initList() {
        expertList.setPullLoadEnabled(false);
        expertList.setPullRefreshEnabled(false);
        expertList.setScrollLoadEnabled(false);
        expertList.setHasMoreData(false);

        adapter = new ExpertAdapter(this);
        expertList.getRefreshableView().setAdapter(adapter);
        expertList.getRefreshableView().setOnItemClickListener(new DarenClick());
        expertList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //   getExpertData(0, PAGE_SIZE);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //   getExpertData(mCurrentPage + 1, PAGE_SIZE);
                expertList.onPullUpRefreshComplete();
            }
        });
        //  getExpertData(0, PAGE_SIZE);
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
            ViewCompat.setElevation(convertView, CommonUtils.dip2px(mContext, 5));
            imgLoader.displayImage(eb.avatar, vh.avatarView, options);
            if (!TextUtils.isEmpty(eb.residence)) {
                vh.residenceView.setText(eb.residence);
            } else {
                vh.residenceView.setText("");
            }

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
           /* String str1 = "派派点评：";
            SpannableString attrStr1 = new SpannableString(str1);
            attrStr1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.tv_comment.setText(attrStr1);*/
            return convertView;
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

    }

    public class DarenClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpertBean xEb = (ExpertBean) adapter.getItem(position);
            Intent intent = new Intent();
            intent.setClass(SearchExpertActivity.this, HisMainPageActivity.class);
            intent.putExtra("userId", (long) xEb.userId);
            intent.putExtra("isFromExperts", true);
            startActivity(intent);
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
        TextView loc;
        TextView nickView;
        TextView tv_comment;
        TagListView expert_tag;
    }
}
