package com.aizou.peachtravel.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
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
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
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
    @InjectView(R.id.tv_tel)
    TextView mTvTel;
    @Optional
    @InjectView(R.id.tv_addr)
    TextView mTvAddr;
    @InjectView(R.id.tv_poi_desc)
    TextView mTvDesc;
    @Optional
    @InjectView(R.id.iv_fav)
    ImageView mIvFav;
    @InjectView(R.id.iv_close)
    ImageView mIvClose;
    @InjectView(R.id.tv_recommend)
    TextView mTvRecommend;
    @InjectView(R.id.iv_share)
    ImageView mIvShare;
    @InjectView(R.id.btn_book)
    TextView mBtnBook;
    @InjectView(R.id.tv_poi_rank)
    TextView mTvRank;
    @InjectView(R.id.tv_more_cmt)
    TextView mTvMoreCmt;

    View headerView;
    View footerView;
    private String id;
    PoiDetailBean poiDetailBean;
    private String type;
    ListViewDataAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_poi_detail);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.y = LocalDisplay.dp2px(5);
        p.height = (int) (d.getHeight() - LocalDisplay.dp2px(80));
        p.width = (int) (d.getWidth() - LocalDisplay.dp2px(12));
//        p.alpha = 1.0f;      //设置本身透明度
//        p.dimAmount = 0.0f;      //设置黑暗度
        getWindow().setAttributes(p);
         headerView = View.inflate(mContext, R.layout.view_poi_detail_header, null);
         footerView = View.inflate(mContext, R.layout.footer_more_comment, null);
        mLvFoodshopDetail = (ListView) findViewById(R.id.lv_poi_detail);
        mLvFoodshopDetail.addHeaderView(headerView);
        mLvFoodshopDetail.addFooterView(footerView);
        ButterKnife.inject(this);
//        mTitleBar.setRightViewImageRes(R.drawable.ic_share);
//        mTitleBar.enableBackKey(true);
//        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finishWithNoAnim();
//            }
//        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithNoAnim();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        commentAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new CommentViewHolder();
            }
        });
        mLvFoodshopDetail.setAdapter(commentAdapter);

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
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        TravelApi.getPoiDetail(type, id, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
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
                if (!isFinishing()) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0, R.anim.fade_out);
    }

    private void refreshFav(PoiDetailBean detailBean) {
        if (detailBean.isFavorite) {
            mIvFav.setImageResource(R.drawable.ic_poi_fav_selected);
        } else {
            mIvFav.setImageResource(R.drawable.ic_poi_fav_normal);
        }
    }

    private void bindView(final PoiDetailBean bean) {
        if (bean.images != null && bean.images.size() > 0) {
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, mIvPoi, UILUtils.getDefaultOption());
        }
        mTvPoiName.setText(bean.zhName);
        if (TextUtils.isEmpty(bean.priceDesc)) {
            mTvPoiPrice.setVisibility(View.INVISIBLE);
            mBtnBook.setVisibility(View.INVISIBLE);
        } else {
            mTvPoiPrice.setText(bean.priceDesc);
            mBtnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        mPoiStar.setRating(bean.getRating());
        mTvRank.setText("同城排名:");
        mTvMoreCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreCommentActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("poi", poiDetailBean);
                startActivity(intent);
            }
        });

        if (bean.tel != null && bean.tel.size() > 0) {
            mTvTel.setVisibility(View.VISIBLE);
            mTvTel.setText(bean.tel.get(0));
        } else {
            mTvTel.setVisibility(View.INVISIBLE);
        }
        mTvAddr.setText(bean.address);
        mTvAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.location != null && bean.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + bean.location.coordinates[1] + "," + bean.location.coordinates[0] + "?q=" + bean.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(mContext, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("没有找到地图应用");
                    }
                }
            }
        });
        refreshFav(bean);
//        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                IMUtils.onClickImShare(mContext);
//            }
//        });
        mIvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogManager.getInstance().showLoadingDialog(PoiDetailActivity.this);
                if (poiDetailBean.isFavorite) {
                    OtherApi.deleteFav(poiDetailBean.id, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                poiDetailBean.isFavorite = false;
                                refreshFav(poiDetailBean);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                        }
                    });
                } else {
                    OtherApi.addFav(poiDetailBean.id, poiDetailBean.type, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                poiDetailBean.isFavorite = true;
                                refreshFav(poiDetailBean);
                            } else {
                                if (!isFinishing()) {
                                    ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                                }
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing()) {
                                ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        }
                    });
                }
            }
        });
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });
        if (TextUtils.isEmpty(bean.desc)) {
            mTvDesc.setVisibility(View.GONE);
        } else {
            mTvDesc.setVisibility(View.VISIBLE);
            mTvDesc.setText(bean.desc);
        }
        commentAdapter.getDataList().addAll(bean.comments);
        if(bean.comments==null||bean.comments.size()<2){
            if(mLvFoodshopDetail.getFooterViewsCount()>0){
                mLvFoodshopDetail.removeFooterView(footerView);
            }
        }
        commentAdapter.notifyDataSetChanged();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext, poiDetailBean, requestCode, resultCode, data, null);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_comment, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, final CommentBean itemData) {
            mTvUsername.setText(itemData.userName);
            mTvDate.setText(dateFormat.format(new Date(itemData.publishTime)));
            mTvComment.setText(Html.fromHtml(itemData.contents));
//            mCommentStar.setRating(itemData.getRating());

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

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.share_to_talk_confirm_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setText("Talk分享");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMUtils.onClickImShare(PoiDetailActivity.this);
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

}
