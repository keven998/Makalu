package com.xuejian.client.lxp.module.dest.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
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
import io.techery.properratingbar.ProperRatingBar;


public class SearchResultAdapter extends BaseSectionAdapter {
    private Context mContext;
    private ArrayList<SearchTypeBean> mSearchList;
    private boolean mIsShowMore;
    private boolean mIsSend;
    private OnSearchResultClickListener mOnSearchResultClickListener;

    public SearchResultAdapter(Context context, ArrayList<SearchTypeBean> searchList, boolean isShowMore, boolean isSend) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_search_resultl, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SearchTypeBean typeBean = mSearchList.get(section);
        LocBean locBean = null;
        final PoiDetailBean poiBean;
        final Object itemObject = typeBean.resultList.get(position);
        holder.tvPoiTime.removeAllViews();
        holder.tvPoiTime.setVisibility(View.GONE);
        holder.ratingBar.setVisibility(View.GONE);
        if (itemObject instanceof LocBean) {
            holder.address_tv.setVisibility(View.GONE);
            locBean = (LocBean) itemObject;
            holder.tvPoiTitle.setText(locBean.zhName);
            if (locBean.images != null && locBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(locBean.images.get(0).url, holder.ivPoiImg, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            } else {
                ImageLoader.getInstance().displayImage("", holder.ivPoiImg, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));

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
                holder.btnSend.setText("发送");
                holder.btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnSearchResultClickListener != null) {
                            mOnSearchResultClickListener.onSendClick(TravelApi.PeachType.LOC, finalLocBean.id, finalLocBean);
                        }
                    }
                });
                holder.btnSend.setVisibility(View.VISIBLE);
            } else {
                holder.btnSend.setVisibility(View.GONE);
            }

        } else if (itemObject instanceof PoiDetailBean) {
            holder.tvPoiTime.setVisibility(View.VISIBLE);
            poiBean = (PoiDetailBean) itemObject;
            holder.ratingBar.setVisibility(View.VISIBLE);
            if (poiBean.rating >= 0) holder.ratingBar.setRating((int)poiBean.getRating());
            if (poiBean.style.size() > 0){
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
            holder.tvPoiTitle.setText(poiBean.zhName);
            if (poiBean.images != null && poiBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiBean.images.get(0).url, holder.ivPoiImg, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
            } else {
                ImageLoader.getInstance().displayImage("", holder.ivPoiImg, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));

            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSearchResultClickListener != null) {
                        mOnSearchResultClickListener.onItemOnClick(typeBean.type, poiBean.id, poiBean);
                    }
                }
            });

            if(poiBean.address!=null){
                holder.address_tv.setVisibility(View.VISIBLE);
                holder.address_tv.setText(poiBean.address);
            }else{
                holder.address_tv.setVisibility(View.GONE);
            }


            if (mIsSend) {
                holder.btnSend.setText("发送");
                holder.btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnSearchResultClickListener != null) {
                            mOnSearchResultClickListener.onSendClick(typeBean.type, poiBean.id, poiBean);
                        }
                    }
                });
                holder.btnSend.setVisibility(View.VISIBLE);
            } else {
                holder.btnSend.setVisibility(View.GONE);
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
                if (typeBean.resultList.size() < 3) searchMore.setVisibility(View.INVISIBLE);
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

    public interface OnSearchResultClickListener {
        void onMoreResultClick(String type);

        void onItemOnClick(String type, String id, Object object);

        void onSendClick(String type, String id, Object object);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_plan_day_detil.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.iv_poi_img)
        ImageView ivPoiImg;
        @Bind(R.id.btn_send)
        CheckedTextView btnSend;
        @Bind(R.id.tv_poi_title)
        TextView tvPoiTitle;
        @Bind(R.id.tv_poi_time)
        TagListView tvPoiTime;
        @Bind(R.id.rb_poi)
        ProperRatingBar ratingBar;
        @Bind(R.id.address_tv)
        TextView address_tv;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
