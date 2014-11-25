package com.aizou.peachtravel.module.dest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CommentBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.RecommendBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Rjm on 2014/11/22.
 */
public class PoiDetailActivity extends PeachBaseActivity {
    @Optional
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mLyHeaderBarTitleWrap;
    ListView mLvFoodshopDetail;
    @Optional
    @InjectView(R.id.iv_poi)
    ImageView mIvPoi;
    @Optional
    @InjectView(R.id.tv_poi_name)
    TextView mTvPoiName;
    @Optional
    @InjectView(R.id.ratingBar_poi)
    RatingBar mPoiStar;
    @Optional
    @InjectView(R.id.tv_poi_price)
    TextView mTvPoiPrice;
    @Optional
    @InjectView(R.id.expand_text_view)
    ExpandableTextView mExpandableText;
    @Optional
    @InjectView(R.id.tv_tel)
    TextView mTvTel;
    @Optional
    @InjectView(R.id.tv_addr)
    TextView mTvAddr;
    @Optional
    @InjectView(R.id.rv_rec_some)
    RecyclerView mRvRecSome;
    PoiDetailBean poiDetailBean;
    private String id;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_poi_detail);
        View headerView = View.inflate(mContext, R.layout.view_poi_detail_header, null);
        mLvFoodshopDetail = (ListView) findViewById(R.id.lv_poi_detail);
        mLvFoodshopDetail.addHeaderView(headerView);
        ButterKnife.inject(this);


    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        type="restaurant";
        if("restaurant".equals(type)){
            id = "53b0599710114e05dc63b5a2";
        }else{
            id = "53b0599710114e05dc63b5a5";
        }
        getDetailData();

    }

    private void getDetailData() {
        if("restaurant".equals(type)){
            TravelApi.getRESTDetail(id, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PoiDetailBean> detailBean = CommonJson.fromJson(result, PoiDetailBean.class);
                    if (detailBean.code == 0) {
                        poiDetailBean = detailBean.result;
                        bindView(detailBean.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }else if("shopping".equals(type)){
            TravelApi.getShoppingDetail(id, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PoiDetailBean> detailBean = CommonJson.fromJson(result, PoiDetailBean.class);
                    if (detailBean.code == 0) {
                        poiDetailBean = detailBean.result;
                        bindView(detailBean.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }

    }

    private void bindView(PoiDetailBean bean) {
        if (bean.images != null && bean.images.size() > 0) {
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, mIvPoi, UILUtils.getDefaultOption());
        }
        mTvPoiName.setText(bean.zhName);
        mTvPoiPrice.setText(bean.priceDesc);
        mPoiStar.setRating(bean.rating);
        mTvTel.setText(bean.telephone);
        mTvAddr.setText(bean.address);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvRecSome.setLayoutManager(linearLayoutManager);
        //设置适配器
        GalleryAdapter recAdapter = new GalleryAdapter(this, bean.recommends);
        mRvRecSome.setAdapter(recAdapter);
        ListViewDataAdapter commentAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new CommentViewHolder();
            }
        });
        mLvFoodshopDetail.setAdapter(commentAdapter);
        commentAdapter.getDataList().addAll(bean.comments);
        commentAdapter.notifyDataSetChanged();
        mExpandableText.setText(bean.desc);

    }


    public class CommentViewHolder extends ViewHolderBase<CommentBean> {
        @InjectView(R.id.tv_username)
        TextView mTvUsername;
        @InjectView(R.id.tv_date)
        TextView mTvDate;
        @InjectView(R.id.tv_comment)
        TextView mTvComment;
        @InjectView(R.id.comment_star)
        RatingBar mCommentStar;
        @InjectView(R.id.tv_comment_num)
        TextView mTvCommentNum;
        @InjectView(R.id.tv_more)
        TextView mTvMore;
        @InjectView(R.id.ll_comment_index)
        RelativeLayout mLlCommentIndex;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_comment, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, CommentBean itemData) {
            if(position==0){
                mLlCommentIndex.setVisibility(View.VISIBLE);
                mTvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mTvCommentNum.setText("网友点评");
                SpannableString impress = new SpannableString("( "+ poiDetailBean.commentCnt+" )");
                impress.setSpan(
                        new ForegroundColorSpan(getResources().getColor(
                                R.color.base_divider_color)), 0, impress.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(12)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTvCommentNum.append(impress);
            }else{
                mLlCommentIndex.setVisibility(View.GONE);
            }
            mTvUsername.setText(itemData.nickName);
            mTvDate.setText(itemData.commentTime);
            mTvComment.setText(itemData.commentDetails);
            mCommentStar.setRating(itemData.rating);

        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>

    {

        private LayoutInflater mInflater;
        private List<RecommendBean> mDatas;

        public GalleryAdapter(Context context, List<RecommendBean> datats) {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
            }

            ImageView mImg;
            TextView mTxt;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.row_rec_some,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.rec_some_iv);
            viewHolder.mTxt = (TextView) view
                    .findViewById(R.id.rec_name_tv);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            RecommendBean bean = mDatas.get(i);
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, viewHolder.mImg, UILUtils.getDefaultOption());
            viewHolder.mTxt.setText(bean.title);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }


}
