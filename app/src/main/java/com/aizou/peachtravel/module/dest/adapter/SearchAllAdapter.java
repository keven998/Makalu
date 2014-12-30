package com.aizou.peachtravel.module.dest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.SearchTypeBean;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchAllAdapter extends BaseSectionAdapter {
    private Context mContext;
    private ArrayList<SearchTypeBean> mSearchList;
    private boolean mIsShowMore;
    private OnSearchResultClickListener mOnSearchResultClickListener;

    public SearchAllAdapter(Context context, ArrayList<SearchTypeBean> searchList,boolean isShowMore) {
        mContext = context;
        mSearchList = searchList;
        mIsShowMore = isShowMore;
    }
    public void setOnSearchResultClickListener(OnSearchResultClickListener onSearchResultClickListener){
        mOnSearchResultClickListener = onSearchResultClickListener;
    }

    @Override
    public int getContentItemViewType(int section, int position) {
        return 0;
    }

    @Override
    public int getHeaderItemViewType(int section) {
        return 0;
    }

    @Override
    public int getItemViewTypeCount() {
        return 1;
    }

    @Override
    public int getHeaderViewTypeCount() {
        return 1;
    }

    @Override
    public Object getItem(int section, int position) {
        return mSearchList.get(section).resultList.get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return getGlobalPositionForItem(section,position);
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ContentViewHolder holder;
        if(convertView==null){
            convertView = View.inflate(mContext, R.layout.row_search_all, null);
            holder =new ContentViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ContentViewHolder) convertView.getTag();
        }
        final SearchTypeBean typeBean = mSearchList.get(section);
        LocBean locBean=null;
        PoiDetailBean poiBean;
        final Object itemObject =typeBean.resultList.get(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnSearchResultClickListener!=null){
                    mOnSearchResultClickListener.onItemOnClick(itemObject);
                }
            }
        });
        if(itemObject instanceof LocBean){
            locBean = (LocBean) itemObject;
            holder.mNameTv.setText(locBean.zhName);
            holder.mAddressTv.setText("");
            if(locBean.images!=null&&locBean.images.size()>0){
                ImageLoader.getInstance().displayImage(locBean.images.get(0).url,holder.mImageIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            }else{
                holder.mImageIv.setImageResource(R.drawable.ic_launcher);
            }

        }else if(itemObject instanceof PoiDetailBean){
            poiBean = (PoiDetailBean) itemObject;
            holder.mAddressTv.setText(poiBean.address);
            holder.mNameTv.setText(poiBean.zhName);
            if(poiBean.images!=null&&poiBean.images.size()>0){
                ImageLoader.getInstance().displayImage(poiBean.images.get(0).url,holder.mImageIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            }else{
                holder.mImageIv.setImageResource(R.drawable.ic_launcher);
            }
        }
        if(mIsShowMore){
            if(position == 4) {
                holder.mAllResultTv.setVisibility(View.VISIBLE);
                if (typeBean.type.equals("loc")) {
                    holder.mAllResultTv.setText("查看全部相关城市");
                } else if (typeBean.type.equals("vs")) {
                    holder.mAllResultTv.setText("查看全部相关景点");
                } else if (typeBean.type.equals("hotel")) {
                    holder.mAllResultTv.setText("查看全部相关酒店");
                } else if (typeBean.type.equals("restaurant")) {
                    holder.mAllResultTv.setText("查看全部相关美食");
                } else if (typeBean.type.equals("shopping")) {
                    holder.mAllResultTv.setText("查看全部相关购物");
                }
                holder.mAllResultTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnSearchResultClickListener != null) {
                            mOnSearchResultClickListener.onMoreResultClick(typeBean.type);
                        }
                    }
                });
            } else {
                holder.mAllResultTv.setVisibility(View.GONE);
            }
        }else{
            holder.mAllResultTv.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public View getHeaderView(int section, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = View.inflate(mContext,R.layout.row_search_all_section,null);
        }
        TextView typeName = (TextView) convertView.findViewById(R.id.type_name_tv);
        SearchTypeBean typeBean = mSearchList.get(section);
        if(typeBean.type.equals("loc")){
            typeName.setText("相关城市");
        }else if(typeBean.type.equals("vs")){
            typeName.setText("相关景点");
        }else if(typeBean.type.equals("hotel")){
            typeName.setText("相关酒店");
        }else if(typeBean.type.equals("restaurant")){
            typeName.setText("相关美食");
        }else if(typeBean.type.equals("shopping")){
            typeName.setText("相关购物");
        }
        return convertView;
    }

    @Override
    public int getSectionCount() {

        return mSearchList.size();
    }

    @Override
    public int getCountInSection(int section) {
        int size=mSearchList.get(section).resultList.size();
        if(mIsShowMore){
            if(size>=5){
                return 5;
            }else{
                return size;
            }
        }else{
            return size;
        }

    }

    @Override
    public boolean doesSectionHaveHeader(int section) {
        return true;
    }

    @Override
    public boolean shouldListHeaderFloat(int headerIndex) {
        return false;
    }

    public class ContentViewHolder {

        @InjectView(R.id.image_iv)
        ImageView mImageIv;
        @InjectView(R.id.name_tv)
        TextView mNameTv;
        @InjectView(R.id.address_tv)
        TextView mAddressTv;
        @InjectView(R.id.all_result_tv)
        TextView mAllResultTv;

        public ContentViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    public interface OnSearchResultClickListener {
        void onMoreResultClick(String type);
        void onItemOnClick(Object object);
    }
}
