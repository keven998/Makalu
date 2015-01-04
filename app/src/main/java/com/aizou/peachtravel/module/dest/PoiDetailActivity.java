package com.aizou.peachtravel.module.dest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CommentBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.RecommendBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    TitleHeaderBar mTitleBar;
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
    @InjectView(R.id.iv_fav)
    ImageView mIvFav;
    private String id;
    PoiDetailBean poiDetailBean;
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
        mTitleBar.setRightViewImageRes(R.drawable.ic_share);
        mTitleBar.enableBackKey(true);

    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
//        type = "restaurant";
//        if ("restaurant".equals(type)) {
//            id = "53b0599710114e05dc63b5a2";
//        } else {
//            id = "53b0599710114e05dc63b5a5";
//        }
        getDetailData();

    }


    private void getDetailData() {
        TravelApi.getPoiDetail(type, id, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<PoiDetailBean> detailBean = CommonJson.fromJson(result, PoiDetailBean.class);
                if (detailBean.code == 0) {
                    poiDetailBean = detailBean.result;
                    bindView(detailBean.result);
                } else {
//                    ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });

    }

    private void refreshFav(PoiDetailBean detailBean){
        if(detailBean.isFavorite){
            mIvFav.setImageResource(R.drawable.ic_fav);
        }else{
            mIvFav.setImageResource(R.drawable.ic_unfav);
        }
    }

    private void bindView(PoiDetailBean bean) {
        if (bean.images != null && bean.images.size() > 0) {
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, mIvPoi, UILUtils.getDefaultOption());
        }
        mTvPoiName.setText(bean.zhName);
        mTitleBar.getTitleTextView().setText(bean.zhName);
        mTvPoiPrice.setText(bean.priceDesc);
        mPoiStar.setRating(bean.getRating());
        mTvTel.setText("电话:" + bean.telephone);
        mTvAddr.setText(bean.address);
        refreshFav(bean);
        mTitleBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMUtils.onClickImShare(mContext);
//                PoiMoreMenu fragment = new PoiMoreMenu();
//                Bundle args = new Bundle();
//                args.putInt(
//                        SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS,
//                        4
//                );
//                args.putFloat(
//                        SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR,
//                        5
//                );
//
//                fragment.setArguments(args);
//                fragment.show(getSupportFragmentManager(), "more_menu");
            }
        });
        mIvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().showLoadingDialog(PoiDetailActivity.this);
                if(poiDetailBean.isFavorite){
                    OtherApi.deleteFav(poiDetailBean.id, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                poiDetailBean.isFavorite = false;
                                refreshFav(poiDetailBean);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                        }
                    });
                }else{
                    OtherApi.addFav(poiDetailBean.id, poiDetailBean.type, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result,ModifyResult.class);
                            if (deleteResult.code == 0) {
                                poiDetailBean.isFavorite =true;
                                refreshFav(poiDetailBean);
                            } else {
                                ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                }
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext,poiDetailBean,requestCode,resultCode,data,null);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_comment, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, CommentBean itemData) {
            if (position == 0) {
                mLlCommentIndex.setVisibility(View.VISIBLE);
                mTvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                    }
                });
                mTvCommentNum.setText("网友点评");
//                SpannableString impress = new SpannableString("( "+ poiDetailBean.commentCnt+" )");
//                impress.setSpan(
//                        new ForegroundColorSpan(getResources().getColor(
//                                R.color.base_divider_color)), 0, impress.length(),
//                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(12)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                mTvCommentNum.append(impress);
            } else {
                mLlCommentIndex.setVisibility(View.GONE);
            }
            mTvUsername.setText(itemData.userName);
            mTvDate.setText(dateFormat.format(new Date(itemData.cTime)));
            mTvComment.setText(Html.fromHtml(itemData.contents));
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

    public static class PoiMoreMenu extends BlurDialogFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog connectionDialog = new Dialog(getActivity(), R.style.TransparentDialog);
            View customView = getActivity().getLayoutInflater().inflate(R.layout.menu_poi_more, null);
            connectionDialog.setContentView(customView);
//            customView.findViewById(R.id.dialog_frame).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dismiss();
//                }
//            });
            customView.findViewById(R.id.add_fav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo:添加收藏
                    dismiss();
                }
            });

            customView.findViewById(R.id.im_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dismiss();
                }
            });
            return connectionDialog;
        }
    }


}
