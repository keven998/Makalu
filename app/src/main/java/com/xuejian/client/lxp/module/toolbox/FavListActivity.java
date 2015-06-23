package com.xuejian.client.lxp.module.toolbox;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.widget.prv.PullToRefreshBase;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.lv.Listener.SendMsgListener;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.FavoritesBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.swipelistview.adapters.BaseSwipeAdapter;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FavListActivity extends PeachBaseActivity {
    public final static int CONST_TYPE_SPOT = 1;
    public final static int CONST_TYPE_FOOD = 2;
    public final static int CONST_TYPE_SHOP = 3;
    public final static int CONST_TYPE_STAY = 4;
    public final static int CONST_TYPE_NOTE = 5;
    public final static int CONST_TYPE_CITY = 6;
    private final static String[] favTypeArray = {"全部", "景点", "酒店", "美食", "购物", "游记", "城市"};
    private final static String[] favTypeValueArray = {"all", "vs", "hotel", "restaurant", "shopping", "travelNote", "locality"};
    @InjectView(R.id.tv_title_bar_left)
    TextView mTvTitleBarLeft;
    @InjectView(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @InjectView(R.id.type_spinner)
    Spinner mTypeSpinner;
    @InjectView(R.id.fav_lv)
    PullToRefreshListView mFavLv;
    StringSpinnerAdapter mTypeSpinnerAdapter;
    private int currentPage = 0;
    private String curType="all";
    private CustomAdapter mAdapter;
    boolean isShare;
    String chatType;
    String toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();

        String action = getIntent().getAction();
        if ("action.chat".equals(action)) {
            mTvTitleBarTitle.setText("发送收藏");
        } else {
            mTvTitleBarTitle.setText("收藏夹");
        }
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
        listView.setHasMoreData(false);
        listView.getRefreshableView().setAdapter(mAdapter = new CustomAdapter());
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(curType, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData(curType, currentPage + 1);
            }
        });

        isShare=getIntent().getBooleanExtra("isShare", false);
        toId = getIntent().getStringExtra("toId");
        chatType = getIntent().getStringExtra("chatType");
        mTypeSpinnerAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(favTypeArray));
        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);
        mTypeSpinner.setSelection(0, true);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(mContext,"event_do_filter");
                curType = favTypeValueArray[position];
//                initData(curType, 0);
                mFavLv.onPullUpRefreshComplete();
                mFavLv.onPullDownRefreshComplete();
                mFavLv.doPullRefreshing(true, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        initData(0);

        setupViewFromCache();
        mFavLv.doPullRefreshing(true, 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_my_favorites");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_my_favorites");
    }

    private void initView() {
        setContentView(R.layout.activity_fav_list);
        ButterKnife.inject(this);
    }

    private void setupViewFromCache() {
        AccountManager account = AccountManager.getInstance();
//        String data = PreferenceUtils.getCacheData(this, String.format("%s_favorites", account.user.userId));
//        if (!TextUtils.isEmpty(data)) {
//            List<FavoritesBean> lists = GsonTools.parseJsonToBean(data,
//                    new TypeToken<List<FavoritesBean>>() {
//                    });
//            mAdapter.appendData(lists);
//            if (mAdapter.getCount() >= OtherApi.PAGE_SIZE) {
//                mFavLv.setHasMoreData(true);
//                mFavLv.setScrollLoadEnabled(true);
//            }
//            initData(curType, 0);
//        } else {
            mFavLv.doPullRefreshing(true, 0);
//        }
    }

    private void cachePage() {
        AccountManager account = AccountManager.getInstance();
        int size = mAdapter.getCount();
        if (size > OtherApi.PAGE_SIZE) {
            size = OtherApi.PAGE_SIZE;
        }
        List<FavoritesBean> cd = mAdapter.getDataList().subList(0, size);
        PreferenceUtils.cacheData(FavListActivity.this, String.format("%s_favorites", account.getCurrentUserId()), GsonTools.createGsonString(cd));
    }

    private void initData(final String type, final int page) {
        OtherApi.getFavist(type, page, new HttpCallBack() {
            @Override
            public void doSucess(Object result, String method) {
                if (!curType.equals(type)) {
                    return;
                }
                CommonJson4List<FavoritesBean> lists = CommonJson4List.fromJson(result.toString(), FavoritesBean.class);
                if (lists.code == 0) {
                    currentPage = page;
                    setupView(lists.result);
                    if ((page == 0 || mAdapter.getCount() < OtherApi.PAGE_SIZE * 2) && favTypeValueArray[0].equals(type)) {
                        cachePage();
                    }
                }

                mFavLv.onPullUpRefreshComplete();
                mFavLv.onPullDownRefreshComplete();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                mFavLv.onPullUpRefreshComplete();
                mFavLv.onPullDownRefreshComplete();
                if (!isFinishing())
                    ToastUtil.getInstance(FavListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    public void setupView(List<FavoritesBean> datas) {

        if (currentPage == 0) {
            mAdapter.getDataList().clear();
        }
        mAdapter.appendData(datas);

        if (datas == null || datas.size() == 0) {
            if (currentPage == 0) {
//                ToastUtil.getInstance(this).showToast("No收藏");
            } else {
                ToastUtil.getInstance(this).showToast("已取完所有内容啦");
                mFavLv.setHasMoreData(false);
            }
            // ptrLv.setScrollLoadEnabled(false);
        } else if (mAdapter.getCount() >= OtherApi.PAGE_SIZE) {
            mFavLv.setHasMoreData(true);
        }

    }

    class CustomAdapter extends BaseSwipeAdapter {
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
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            View view =inflater.inflate(R.layout.favorite_list_item, null);
            return view;
        }

        @Override
        public void fillValues(int position, View convertView) {
            ImageView imgView = (ImageView) convertView.findViewById(R.id.stand_img);
            TextView titleView = (TextView) convertView.findViewById(R.id.tv_title);
            TextView tvLocal = (TextView) convertView.findViewById(R.id.tv_local);
            TextView typeView = (TextView) convertView.findViewById(R.id.tv_type);
            TextView timeView = (TextView) convertView.findViewById(R.id.tv_create_time);
            TextView descView = (TextView) convertView.findViewById(R.id.tv_summary);
            ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.delete);
            RelativeLayout sendRl = (RelativeLayout) convertView.findViewById(R.id.rl_send);
            TextView sendBtn = (TextView) convertView.findViewById(R.id.btn_send);
            final FavoritesBean item = mItemDataList.get(position);
            if(isShare){
                sendRl.setVisibility(View.VISIBLE);
                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMUtils.showImShareDialog(mContext, item, new IMUtils.OnDialogShareCallBack() {
                            @Override
                            public void onDialogShareOk(Dialog dialog, int type, String content) {
                                DialogManager.getInstance().showLoadingDialog(mContext);
                                IMClient.getInstance().sendExtMessage(AccountManager.getCurrentUserId(),toId, chatType, content, type, new SendMsgListener() {
                                    @Override
                                    public void onSuccess() {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("已发送~");
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                ToastUtil.getInstance(mContext).showToast("好像发送失败了");
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onDialogShareCancle(Dialog dialog, int type, String content) {
                            }
                        });
                    }
                });
            }else{
                sendRl.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!item.type.equals(TravelApi.PeachType.NOTE)){
                        IntentUtils.intentToDetail(FavListActivity.this, item.type, item.itemId);
                    } else {
                        TravelNoteBean noteBean = new TravelNoteBean();
                        noteBean.setFieldFromFavBean(item);
                        IntentUtils.intentToNoteDetail(FavListActivity.this, noteBean);
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(mContext,"event_delete_favorite");
                    deleteItem(item);
                }
            });

            if (item.images != null && item.images.size() > 0) {
                ImageLoader.getInstance().displayImage(item.images.get(0).url, imgView, poptions);
            } else {
                imgView.setImageDrawable(null);
            }

            titleView.setText(item.zhName);
            tvLocal.setText(item.locality.zhName);

            descView.setText(item.desc);
            int type = item.getType();
            String typeText = "";
            switch (type) {
                case CONST_TYPE_FOOD:
                    typeText = "美食";
                    break;

                case CONST_TYPE_NOTE:
                    typeText = "游记";
                    break;

                case CONST_TYPE_SHOP:
                    typeText = "购物";
                    break;

                case CONST_TYPE_STAY:
                    typeText = "酒店";
                    break;

                case CONST_TYPE_SPOT:
                    typeText = "景点";
                    break;

                case CONST_TYPE_CITY:
                    typeText = "城市";
                    break;

                default:
                    break;
            }

            typeView.setText(typeText);
            timeView.setText(simpleDateFormat.format(new Date(item.createTime)));
        }


        private void deleteItem(final FavoritesBean item) {
            final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
            dialog.setTitle("提示");
            dialog.setTitleIcon(R.drawable.ic_dialog_tip);
            dialog.setMessage("确定移除收藏");
            dialog.setPositiveButton("确定",new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    DialogManager.getInstance().showLoadingDialog(FavListActivity.this);
                    OtherApi.deleteFav(item.itemId, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
//                                        mItemDataList.remove(i);
                                int index = mItemDataList.indexOf(item);
                                closeItem(index);
                                mItemDataList.remove(index);
                                notifyDataSetChanged();
                                if (mItemDataList.size() == 0) {
                                    mFavLv.doPullRefreshing(true, 0);
                                }
                                if (index < OtherApi.PAGE_SIZE) {
                                    cachePage();
                                }
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!isFinishing())
                                ToastUtil.getInstance(FavListActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });
                }
            });
            dialog.setNegativeButton("取消",new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

    }

    class ViewHolder {
        ImageView imgView;
        TextView titleView;
        TextView tvLocal;
        TextView typeView;
        TextView timeView;
        TextView descView;
        ImageButton deleteBtn;
        RelativeLayout sendRl;
        TextView sendBtn;
    }

    class FavoriteItem {

    }

    @Override
    public void finish() {
        super.finish();
    }

}
