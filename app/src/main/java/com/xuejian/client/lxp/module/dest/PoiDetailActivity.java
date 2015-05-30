package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
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
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CommentBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.RecommendBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.xuejian.client.lxp.common.widget.FlowLayout;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;

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

    @InjectView(R.id.btn_book)
    TextView mBtnBook;
    @InjectView(R.id.tv_poi_rank)
    TextView mTvRank;
    @InjectView(R.id.tv_more_cmt)
    TextView mTvMoreCmt;

    @InjectView(R.id.poi_det_back)
    TextView titleBack;
    @InjectView(R.id.poi_det_title)
    TextView title;

    @InjectView(R.id.rl_address)
    RelativeLayout rl_address;
    @InjectView(R.id.rl_fee)
    RelativeLayout rl_fee;
    @InjectView(R.id.rl_level)
    RelativeLayout rl_level;
    @InjectView(R.id.rl_phone)
    RelativeLayout rl_phone;
    @InjectView(R.id.rl_poi_desc)
    RelativeLayout rl_poi_desc;


    View headerView;
    View footerView;
    /*@InjectView(R.id.iv_comment_top_mark)
    ImageView mIvCommentTopMark;
    @InjectView(R.id.iv_comment_bottom_mark)
    ImageView mIvCommentBottomMark;*/
    private String id;
    PoiDetailBean poiDetailBean;
    private String type;
    ListViewDataAdapter commentAdapter;

    private ImageView mIvFav;
    private ImageView mChat;
    private PopupWindow mPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_poi_detail);
        mIvFav = (ImageView) findViewById(R.id.iv_fav);
        mChat = (ImageView) findViewById(R.id.iv_chat);
        mLvFoodshopDetail = (ListView) findViewById(R.id.lv_poi_detail);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.y = LocalDisplay.dp2px(5);
        p.height = (int) (d.getHeight());    /*- LocalDisplay.dp2px(64)*/
        p.width = (int) (d.getWidth());   /*- LocalDisplay.dp2px(28)*/

        getWindow().setAttributes(p);
        headerView = View.inflate(mContext, R.layout.view_poi_detail_header, null);
        //footerView = View.inflate(mContext, R.layout.footer_more_comment, null);
        mLvFoodshopDetail.addHeaderView(headerView);
        //mLvFoodshopDetail.addFooterView(footerView);
        commentAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new CommentViewHolder();
            }
        });
        ButterKnife.inject(this);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        });
        title.setText("景点介绍");
        mLvFoodshopDetail.setAdapter(commentAdapter);

    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        getDetailData();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//            MobclickAgent.onPageStart("page_delicacy_detail");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//            MobclickAgent.onPageStart("page_shopping_detail");
//        }else if (type.equals(TravelApi.PeachType.HOTEL)) {
//            MobclickAgent.onPageStart("page_hotel_detail");
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
//            MobclickAgent.onPageEnd("page_delicacy_detail");
//        } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
//            MobclickAgent.onPageEnd("page_shopping_detail");
//        }else if (type.equals(TravelApi.PeachType.HOTEL)) {
//            MobclickAgent.onPageEnd("page_hotel_detail");
//        }
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
                    DialogManager.getInstance().dissMissModelessLoadingDialog();
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
            mIvFav.setImageResource(R.drawable.ic_favorite_sleceted);
        } else {
            mIvFav.setImageResource(R.drawable.ic_favorite_normal);
        }
    }

    private void bindView(final PoiDetailBean bean) {
        if (bean.images != null && bean.images.size() > 0) {
            ImageLoader.getInstance().displayImage(bean.images.get(0).url, mIvPoi, UILUtils.getDefaultOption());
        }
        mTvPoiName.setText(bean.zhName);
        if (TextUtils.isEmpty(bean.priceDesc)) {
            rl_fee.setVisibility(View.GONE);
        } else {
            mTvPoiPrice.setText(bean.priceDesc);
        }
        if(TextUtils.isEmpty(bean.lyPoiUrl)){
            mBtnBook.setVisibility(View.GONE);
        }else{
            mBtnBook.setVisibility(View.VISIBLE);
            mBtnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext,"event_go_booking_room");
                    Intent intent = new Intent(mContext,PeachWebViewActivity.class);
                    intent.putExtra("enable_bottom_bar", true);
                    intent.putExtra("url",bean.lyPoiUrl);
                    intent.putExtra("title",bean.zhName);
                    startActivity(intent);
                }
            });
        }

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "event_spot_share_to_talk");
                IMUtils.onClickImShare(PoiDetailActivity.this);
            }
        });

        mPoiStar.setRating(bean.getRating());
        if (!poiDetailBean.getFormatRank().equals("0")) {
//            mTvRank.setText("热度排名 " + poiDetailBean.getFormatRank());
            mTvRank.setText(String.format("%s排名 %s", poiDetailBean.getPoiTypeName(), poiDetailBean.getFormatRank()));
        }
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
            rl_phone.setVisibility(View.GONE);
        }

        String address;
        if (TextUtils.isEmpty(bean.address)) {
            address =  bean.zhName;
        } else {
            address = bean.address;   //"<img src=\"" + R.drawable.ic_poi_address + "\" />  " +
        }
//        mTvAddr.setText(Html.fromHtml(address, imageGetter, null));

        Spanned spanned = Html.fromHtml(address, imageGetter , null);
        if (spanned instanceof SpannableStringBuilder) {
            ImageSpan[] imageSpans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
            for (ImageSpan imageSpan : imageSpans) {
                int start = spanned.getSpanStart(imageSpan);
                int end = spanned.getSpanEnd(imageSpan);
                Drawable d = imageSpan.getDrawable();
                StickerSpan newImageSpan = new StickerSpan(d, ImageSpan.ALIGN_BASELINE);
                ((SpannableStringBuilder) spanned).setSpan(newImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                ((SpannableStringBuilder) spanned).removeSpan(imageSpan);
            }
        }
        mTvAddr.setText(spanned);

        rl_address.setOnClickListener(new View.OnClickListener() {
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

        rl_fee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_go_booking_room");
                Intent intent = new Intent(mContext,PeachWebViewActivity.class);
                intent.putExtra("url",bean.lyPoiUrl);
                intent.putExtra("title",bean.zhName);
                startActivity(intent);
            }
        });
        rl_poi_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//自定义布局
                ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                        R.layout.text_diaplay, null, true);
                TextView pop_dismiss = (TextView) menuView.findViewById(R.id.pop_dismiss);

                TextView tv = (TextView) menuView.findViewById(R.id.msg);
                tv.setText(bean.desc);
                mPop = new PopupWindow(menuView, FlowLayout.LayoutParams.MATCH_PARENT,
                        FlowLayout.LayoutParams.MATCH_PARENT, true);
                mPop.setContentView(menuView);//设置包含视图
                mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
                mPop.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
                mPop.setAnimationStyle(R.style.PopAnimation);
                mPop.showAtLocation(findViewById(R.id.poi_det_parent), Gravity.BOTTOM, 0, 0);
                pop_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPop.dismiss();
                    }
                });

            }
        });
        rl_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        refreshFav(bean);
        mIvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(PoiDetailActivity.this);
                if (user == null || TextUtils.isEmpty(user.easemobUser)) {
                    ToastUtil.getInstance(PoiDetailActivity.this).showToast("请先登录");
                    Intent intent = new Intent(PoiDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11);
                    return;
                } else {
                    favorite();
                }
            }
        });
       /* mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });*/
        if (TextUtils.isEmpty(bean.desc)) {
            rl_poi_desc.setVisibility(View.GONE);
        } else {
            mTvDesc.setVisibility(View.VISIBLE);
            mTvDesc.setText(bean.desc);
        }
        commentAdapter.getDataList().addAll(bean.comments);
        if (bean.comments == null || bean.comments.size() < 2) {

            if (mLvFoodshopDetail.getFooterViewsCount() > 0) {
                mLvFoodshopDetail.removeFooterView(footerView);
            }

        }
        if(bean.comments==null||bean.comments.size()==0){
           /* mIvCommentTopMark.setVisibility(View.GONE);
            mIvCommentBottomMark.setVisibility(View.GONE);*/
        }
        commentAdapter.notifyDataSetChanged();
    }

    Html.ImageGetter imageGetter = new Html.ImageGetter() {

        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            int rId = Integer.parseInt(source);
            drawable = getResources().getDrawable(rId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        }
    };

    private void favorite() {
        if (poiDetailBean.isFavorite) {
            OtherApi.deleteFav(poiDetailBean.id, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0) {
                        poiDetailBean.isFavorite = false;
                        refreshFav(poiDetailBean);
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        } else {
            if (type.equals(TravelApi.PeachType.RESTAURANTS)) {
                MobclickAgent.onEvent(mContext,"event_favorite_delicacy");
            } else if (type.equals(TravelApi.PeachType.SHOPPING)) {
                MobclickAgent.onEvent(mContext,"event_favorite_shopping");
            }else if (type.equals(TravelApi.PeachType.HOTEL)) {
                MobclickAgent.onEvent(mContext,"event_favorite_hotel");
            }
            OtherApi.addFav(poiDetailBean.id, poiDetailBean.type, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0 || deleteResult.code == getResources().getInteger(R.integer.response_favorite_exist)) {
                        poiDetailBean.isFavorite = true;
                        refreshFav(poiDetailBean);
                        ToastUtil.getInstance(PoiDetailActivity.this).showToast("已收藏");
                    } else {
                        if (!isFinishing()) {
                            ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                        }
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing()) {
                        ToastUtil.getInstance(PoiDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                favorite();
            } else {
                IMUtils.onShareResult(mContext, poiDetailBean, requestCode, resultCode, data, null);
            }
        }
    }

    public class CommentViewHolder extends ViewHolderBase<CommentBean> {
        @InjectView(R.id.poi_detail_dp_name)
        TextView mTvName;
        @InjectView(R.id.poi_detail_dp_time)
        TextView mTvTime;
        @InjectView(R.id.tv_comment)
        TextView mTvComment;
        @InjectView(R.id.poi_detail_dp_pic)
        ImageView mImageView;
        /*@InjectView(R.id.comment_star)
        RatingBar mCommentStar;*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_comment, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, final CommentBean itemData) {
            mTvName.setText(itemData.authorName);
            mTvTime.setText(dateFormat.format(new Date(itemData.publishTime)));
            ImageLoader.getInstance().displayImage(itemData.authorAvatar, mImageView, UILUtils.getDefaultOption());
            //mTvProperty.setText(String.format("%s  %s", itemData.authorName, dateFormat.format(new Date(itemData.publishTime))));
            mTvComment.setText(Html.fromHtml(itemData.contents));
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

    public class StickerSpan extends ImageSpan {

        public StickerSpan(Drawable b, int verticalAlignment) {
            super(b, verticalAlignment);

        }

        @Override
        public void draw(Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();
            int transY = bottom - b.getBounds().bottom - LocalDisplay.dp2px(2);
            if (mVerticalAlignment == ALIGN_BASELINE) {
                int textLength = text.length();
                for (int i = 0; i < textLength; i++) {
                    if (Character.isLetterOrDigit(text.charAt(i))) {
                        transY -= paint.getFontMetricsInt().descent;
                        break;
                    }
                }
            }
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }

}
