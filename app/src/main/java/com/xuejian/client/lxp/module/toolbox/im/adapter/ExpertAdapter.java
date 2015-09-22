package com.xuejian.client.lxp.module.toolbox.im.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yibiao.qin on 2015/9/22.
 */
public class ExpertAdapter extends BaseAdapter {
    protected ArrayList<ExpertBean> mItemDataList = new ArrayList<ExpertBean>();
    private Context context;
    private DisplayImageOptions options;
    private LayoutInflater inflater;
    private int width;
    private ImageLoader imgLoader;
    private int[] lebelColors =new int[]{
            R.drawable.all_light_green_label,R.drawable.all_light_red_label,R.drawable.all_light_perple_label,R.drawable.all_light_blue_label,R.drawable.all_light_yellow_label
    };
    private int limit;
    public ExpertAdapter(Context cxt, int limit) {
        this.context = cxt;
        this.limit = limit;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .displayer(new RoundedBitmapDisplayer(cxt.getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
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
        if (limit==-1){
            return mItemDataList.size();
        }else if (mItemDataList.size()>limit){
            return limit;
        }else {
            return mItemDataList.size();
        }

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
        ViewCompat.setElevation(convertView, CommonUtils.dip2px(context, 5));
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

        ViewCompat.setElevation(vh.expert_level, CommonUtils.dip2px(context, 5));
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
        return convertView;
    }
    private class ViewHolder {
        ImageView avatarView;
        TextView expert_level;
        TextView residenceView;
        TextView nickView;
        TextView tv_comment;
        TagListView  expert_tag;
    }
    private int getAge(String birth) {
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        return Integer.parseInt(date) - birthYear;
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
    public int getNextColor(int currentcolor){
        Random random = new Random();
        int nextValue = random.nextInt(4);
        if(nextValue==0){
            nextValue++;
        }
        return (nextValue+currentcolor)%5;
    }
}
