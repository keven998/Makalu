package com.aizou.peachtravel.module.toolbox;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.FavoritesBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.BaseApi;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.module.dest.CityDetailActivity;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.aizou.peachtravel.module.dest.adapter.StringSpinnerAdapter;
import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class FavListActivity extends PeachBaseActivity {
    public final static int CONST_TYPE_SPOT = 1;
    public final static int CONST_TYPE_FOOD = 2;
    public final static int CONST_TYPE_SHOP = 3;
    public final static int CONST_TYPE_STAY = 4;
    public final static int CONST_TYPE_NOTE = 5;
    public final static int CONST_TYPE_CITY = 6;
    private final static String[] favTypeArray ={"全部","景点","酒店","餐厅","购物","游记","城市"};
    private final static String[] favTypeValueArray ={"all","vs ","hotel","restaurant","shopping","travelNote","locality"};
    @InjectView(R.id.tv_title_bar_left)
    TextView mTvTitleBarLeft;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @InjectView(R.id.type_spinner)
    Spinner mTypeSpinner;
    @InjectView(R.id.fav_lv)
    PullToRefreshListView mFavLv;
    @InjectView(R.id.edit_btn)
    CheckedTextView mEditBtn;
    StringSpinnerAdapter mTypeSpinnerAdapter;
    private int currentPage = 0;
    private String curType="all";
    private CustomAdapter mAdapter;
    private boolean isEditable = false;
    boolean isShare;
    int chatType;
    String toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();

        mTvTitleBarTitle.setText("收藏夹");
        mTvTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        PullToRefreshListView listView = mFavLv;
        listView.setPullLoadEnabled(false);
        listView.setPullRefreshEnabled(true);
        listView.setScrollLoadEnabled(false);
        listView.getRefreshableView().setAdapter(mAdapter = new CustomAdapter());
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(curType,0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(curType,currentPage + 1);
            }
        });
        listView.doPullRefreshing(true, 100);
        isShare=getIntent().getBooleanExtra("isShare",false);
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getIntExtra("chatType",0);
        mTypeSpinnerAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(favTypeArray));
        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);
        mTypeSpinner.setSelection(0, true);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curType = favTypeValueArray[position];
                initData(curType, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        initData(0);
    }

    private void initView() {
        setContentView(R.layout.activity_fav_list);
        ButterKnife.inject(this);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = mEditBtn.isChecked();
                mEditBtn.setChecked(!status);
                isEditable = !status;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData(String type, final int page) {
        OtherApi.getFavist(type,page, new HttpCallBack() {
            @Override
            public void doSucess(Object result, String method) {
                CommonJson4List<FavoritesBean> lists = CommonJson4List.fromJson(result.toString(), FavoritesBean.class);
                if (lists.code == 0) {
                    currentPage = page;
                    setupView(lists.result);

                }
                mFavLv.onPullUpRefreshComplete();
                mFavLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mFavLv.onPullUpRefreshComplete();
                mFavLv.onPullDownRefreshComplete();
            }
        });
    }

    public void setupView(List<FavoritesBean> datas) {

        if (currentPage == 0) {
            mAdapter.getDataList().clear();
        }
        mAdapter.appendData(datas);

        if (datas == null || datas.size() ==0) {
            mFavLv.setHasMoreData(false);
            if (currentPage == 0) {
                Toast.makeText(FavListActivity.this, "没有相关内容", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FavListActivity.this, "已列出全部收藏", Toast.LENGTH_SHORT).show();
            }
            // ptrLv.setScrollLoadEnabled(false);
        } else {
            mFavLv.setScrollLoadEnabled(true);
            mFavLv.setHasMoreData(true);
        }

    }

    class CustomAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        protected ArrayList<FavoritesBean> mItemDataList = new ArrayList<FavoritesBean>();
        DisplayImageOptions poptions;
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public CustomAdapter() {
            inflater = getLayoutInflater();
            poptions = UILUtils.getRadiusOption();
        }

        public void appendData(List<FavoritesBean> itemDataList) {
            mItemDataList.addAll(itemDataList);
            notifyDataSetChanged();
        }

        public ArrayList<FavoritesBean> getDataList() {
            return mItemDataList;
        }

        @Override
        public int getCount() {
            return mItemDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return mItemDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View contentView, ViewGroup viewGroup) {
            View view = contentView;
            ViewHolder vh;
            if (view == null) {
                view = inflater.inflate(R.layout.favorite_list_item, null);
                vh = new ViewHolder();
                vh.imgView = (ImageView) view.findViewById(R.id.stand_img);
                vh.titleView = (TextView) view.findViewById(R.id.tv_title);
                vh.tvLocal = (TextView) view.findViewById(R.id.tv_local);
                vh.typeView = (TextView) view.findViewById(R.id.tv_type);
                vh.timeView = (TextView) view.findViewById(R.id.tv_create_time);
                vh.descView = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
                vh.flagView = (ImageView) view.findViewById(R.id.iv_flag);
                vh.deleteBtn = (ImageButton) view.findViewById(R.id.delete);


//                int width = LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(20);
//                int height = width * 260 / 640;
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
//                vh.imgView.setLayoutParams(lp);

                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
                if (!vh.descView.isCollpased()) {
                    vh.descView.reset();
                }
            }
            final FavoritesBean item = mItemDataList.get(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isShare){
                        IMUtils.showImShareDialog(mContext, item, new IMUtils.OnDialogShareCallBack() {
                            @Override
                            public void onDialogShareOk(Dialog dialog, int type, String content) {
                                DialogManager.getInstance().showProgressDialog(mContext);
                                IMUtils.sendExtMessage(mContext, type, content, chatType, toId, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        DialogManager.getInstance().dissMissProgressDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("发送成功");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        DialogManager.getInstance().dissMissProgressDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("发送失败");

                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }

                            @Override
                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
                            }
                        });
                    }else{
                        Intent intent = new Intent();
                        if(item.type.equals("vs")){
                            intent.setClass(mContext, SpotDetailActivity.class);
                            intent.putExtra("id",item.itemId);
                        }else if(item.type.equals("hotel")||item.type.equals("restaurant")||item.type.equals("shopping")){
                            intent.setClass(mContext, PoiDetailActivity.class);
                            intent.putExtra("id",item.itemId);
                            intent.putExtra("type",item.type);
                        }else if(item.type.equals("locality")){
                            intent.setClass(mContext, CityDetailActivity.class);
                            intent.putExtra("id",item.itemId);
                        }
                        startActivity(intent);
                    }
                }
            });
            if (isEditable) {
                vh.deleteBtn.setVisibility(View.VISIBLE);
                vh.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteItem(item);
                    }
                });
            } else {
                vh.deleteBtn.setVisibility(View.GONE);
            }


            if (item.images != null && item.images.size() > 0) {
                ImageLoader.getInstance().displayImage(item.images.get(0).url, vh.imgView, poptions);
            } else {
                vh.imgView.setImageResource(R.drawable.guide_1);
            }

            vh.titleView.setText(item.zhName);
            if (item.locality != null) {
                vh.tvLocal.setVisibility(View.VISIBLE);
                vh.tvLocal.setText(item.locality.zhName);
            } else {
                vh.tvLocal.setVisibility(View.GONE);
            }


            vh.descView.setText(item.desc);
            int type = item.getType();
            int res = 0;
            String typeText = "";
            switch (type) {
                case CONST_TYPE_FOOD:
                    typeText = "美食";
                    res = R.drawable.ic_fav_delicacy;
                    break;

                case CONST_TYPE_NOTE:
                    typeText = "游记";
                    res = R.drawable.ic_fav_tnote;
                    break;

                case CONST_TYPE_SHOP:
                    typeText = "购物";
                    res = R.drawable.ic_fav_shopping;
                    break;

                case CONST_TYPE_STAY:
                    typeText = "酒店";
                    res = R.drawable.ic_fav_stay;
                    break;

                case CONST_TYPE_SPOT:
                    typeText = "景点";
                    res = R.drawable.ic_fav_spot;
                    break;

                case CONST_TYPE_CITY:
                    typeText = "城市";
                    res = R.drawable.ic_fav_city;
                    break;

                default:
                    break;
            }
            vh.typeView.setText(typeText);
            vh.timeView.setText(simpleDateFormat.format(new Date(item.createTime)));
            vh.flagView.setImageResource(res);

            return view;
        }

        private void deleteItem(final FavoritesBean itemData) {
            new MaterialDialog.Builder(FavListActivity.this)
                    .title(null)
                    .content("删除后就找不到了")
                    .positiveText("删除")
                    .negativeText("取消")
                    .autoDismiss(false)
                    .positiveColor(getResources().getColor(R.color.app_theme_color))
                    .negativeColor(getResources().getColor(R.color.app_theme_color))
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            DialogManager.getInstance().showProgressDialog(FavListActivity.this);
                            OtherApi.deleteFav(itemData.itemId, new HttpCallBack<String>() {
                                @Override
                                public void doSucess(String result, String method) {
                                    DialogManager.getInstance().dissMissProgressDialog();
                                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                                    if (deleteResult.code == 0) {
//                                        mItemDataList.remove(i);
                                        mItemDataList.remove(itemData);
                                        notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {
                                    DialogManager.getInstance().dissMissProgressDialog();

                                }
                            });
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    class ViewHolder {
        ImageView imgView;
        TextView titleView;
        TextView tvLocal;
        ImageView flagView;
        TextView typeView;
        TextView timeView;
        ExpandableTextView descView;
        ImageButton deleteBtn;
    }

    class FavoriteItem {

    }

    @Override
    public void finish() {
        super.finish();
    }

}
