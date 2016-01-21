package com.xuejian.client.lxp.module.dest.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SearchTypeBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;
import com.xuejian.client.lxp.common.widget.TagView.TagListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/9.
 */
public class SearchAllAdapter extends BaseSectionAdapter {
    private Context mContext;
    private ArrayList<SearchTypeBean> mSearchList;
    private boolean mIsShowMore;
    private boolean mIsSend;
    private OnSearchResultClickListener mOnSearchResultClickListener;

    public SearchAllAdapter(Context context, ArrayList<SearchTypeBean> searchList, boolean isShowMore, boolean isSend) {
        mContext = context;
        mSearchList = searchList;
        mIsShowMore = isShowMore;
        mIsSend = isSend;
    }

    public void setOnSearchResultClickListener(OnSearchResultClickListener onSearchResultClickListener) {
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
        return getGlobalPositionForItem(section, position);
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ContentViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.row_search_all, null);
            holder = new ContentViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ContentViewHolder) convertView.getTag();
        }
        final SearchTypeBean typeBean = mSearchList.get(section);
        LocBean locBean = null;
        final PoiDetailBean poiBean;
        final Object itemObject = typeBean.resultList.get(position);
        holder.tvPoiTime.removeAllViews();
        holder.tvPoiTime.setVisibility(View.GONE);
        holder.ratingBar.setVisibility(View.GONE);
        if (itemObject instanceof LocBean) {
            locBean = (LocBean) itemObject;
            holder.mNameTv.setText(locBean.zhName);
            holder.mAddressTv.setText(locBean.zhName);
            // holder.mAddressTv.setVisibility(View.GONE);
            if (locBean.images != null && locBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(locBean.images.get(0).url, holder.mImageIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            } else {
                holder.mImageIv.setImageResource(R.drawable.bg_common_default);
            }
            final LocBean finalLocBean = locBean;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSearchResultClickListener != null) {
                        mOnSearchResultClickListener.onItemOnClick(TravelApi.PeachType.LOC, finalLocBean.id, finalLocBean);
                    }
                }
            });
            if (mIsSend) {
                holder.mSendTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnSearchResultClickListener != null) {
                            mOnSearchResultClickListener.onSendClick(TravelApi.PeachType.LOC, finalLocBean.id, finalLocBean);
                        }
                    }
                });
                holder.mSendTv.setVisibility(View.VISIBLE);
            } else {
                holder.mSendTv.setVisibility(View.GONE);
            }

        } else if (itemObject instanceof PoiDetailBean) {
            poiBean = (PoiDetailBean) itemObject;
            holder.mAddressTv.setText(poiBean.address);
            holder.mNameTv.setText(poiBean.zhName);
            holder.tvPoiTime.setVisibility(View.VISIBLE);
            holder.ratingBar.setVisibility(View.VISIBLE);
            if (poiBean.rating >= 0) holder.ratingBar.setRating(poiBean.getRating());
            if (poiBean.address.equals("") || poiBean.address == null) {
                holder.mAddressTv.setText(poiBean.zhName);
            }
            if (poiBean.images != null && poiBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiBean.images.get(0).url, holder.mImageIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            } else {
                holder.mImageIv.setImageDrawable(null);
            }

            if(poiBean.style.size()>0){
                List<Tag> mKeyTags = new ArrayList<Tag>();
                for (int i = 0; i < poiBean.style.size()&& i<4; i++) {
                    Tag tag = new Tag();
                    tag.setId(i);
                    tag.setChecked(true);
                    tag.setTitle(poiBean.style.get(i));
                    tag.setBackgroundResId(R.drawable.all_whitesolid_greenline);
                    tag.setTextColor(R.color.color_text_iii);
                    mKeyTags.add(tag);
                }
                holder.tvPoiTime.setmTagViewResId(R.layout.search_tag);
                holder.tvPoiTime.setTags(mKeyTags);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSearchResultClickListener != null) {
                        mOnSearchResultClickListener.onItemOnClick(typeBean.type, poiBean.id, poiBean);
                    }
                }
            });
            if (mIsSend) {
                holder.mSendTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnSearchResultClickListener != null) {
                            mOnSearchResultClickListener.onSendClick(typeBean.type, poiBean.id, poiBean);
                        }
                    }
                });
                holder.mSendTv.setVisibility(View.VISIBLE);
            } else {
                holder.mSendTv.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int section, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.row_search_all_section, null);
        }
        TextView typeName = (TextView) convertView.findViewById(R.id.type_name_tv);
        TextView searchMore = (TextView) convertView.findViewById(R.id.tv_search_more);
        final SearchTypeBean typeBean = mSearchList.get(section);
        if (typeBean.type.equals("loc")) {
            typeName.setText("城市");
        } else if (typeBean.type.equals("vs")) {
            typeName.setText("景点");
        } else if (typeBean.type.equals("hotel")) {
            typeName.setText("酒店");
        } else if (typeBean.type.equals("restaurant")) {
            typeName.setText("美食");
        } else if (typeBean.type.equals("shopping")) {
            typeName.setText("购物");
        }
        if (mIsShowMore) {
            if (typeBean.type.equals("loc")) {
                if (typeBean.resultList.size()<3)searchMore.setVisibility(View.INVISIBLE);
                searchMore.setText("查看全部 城市>");
            } else if (typeBean.type.equals("vs")) {
                searchMore.setText("查看全部 景点>");
            } else if (typeBean.type.equals("hotel")) {
                searchMore.setText("查看全部 酒店>");
            } else if (typeBean.type.equals("restaurant")) {
                searchMore.setText("查看全部 美食>");
            } else if (typeBean.type.equals("shopping")) {
                searchMore.setText("查看全部 购物>");
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(searchMore.getText().toString());
            ForegroundColorSpan Span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.app_theme_color_highlight));
            builder.setSpan(Span, 4, searchMore.getText().length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            searchMore.setText(builder);
        } else {
            searchMore.setVisibility(View.INVISIBLE);
        }
        searchMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSearchResultClickListener != null) {
                    mOnSearchResultClickListener.onMoreResultClick(typeBean.type);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getSectionCount() {

        return mSearchList.size();
    }

    @Override
    public int getCountInSection(int section) {
        int size = mSearchList.get(section).resultList.size();
        if (mIsShowMore) {
            if (size >= 5) {
                return 5;
            } else {
                return size;
            }
        } else {
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

        @Bind(R.id.image_iv)
        ImageView mImageIv;
        @Bind(R.id.name_tv)
        TextView mNameTv;
        @Bind(R.id.address_tv)
        TextView mAddressTv;
        @Bind(R.id.btn_send)
        CheckedTextView mSendTv;
        @Bind(R.id.rb_poi)
        RatingBar ratingBar;
        @Bind(R.id.tv_poi_time)
        TagListView tvPoiTime;
        public ContentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    public interface OnSearchResultClickListener {
        void onMoreResultClick(String type);

        void onItemOnClick(String type, String id, Object object);

        void onSendClick(String type, String id, Object object);
    }
}
