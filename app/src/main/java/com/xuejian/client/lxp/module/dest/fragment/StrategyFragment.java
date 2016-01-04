package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.ComfirmDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.swipelistview.adapters.BaseSwipeAdapter;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/9/10.
 */
public class StrategyFragment extends PeachBaseFragment implements AbsListView.OnScrollListener {
    String userId;
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    ListView myFragmentList;
    MyPlaneAdapter myPlaneAdapter;
    private int currentIndex = 0;
    private int startmagrinTop = 0;
    int mCurrentPage = 0;
    private User user;
    private ArrayList<StrategyBean> planList;
    private static final int RESULT_PLAN_DETAIL = 0x222;
    public static final int REQUEST_CODE_NEW_PLAN = 0x22;
    DisplayImageOptions options;
    private boolean isPanelVisible = true;
    View footView;
    private boolean isLoading = false;
    private boolean is_divPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                .showImageOnFail(R.drawable.messages_bg_useravatar)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中// 设置成圆角图片
                .build();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        userId = user.getUserId() + "";

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.strategy_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);
        planList = new ArrayList<>();
        myPlaneAdapter = new MyPlaneAdapter(getActivity(), planList);
        myFragmentList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        myFragmentList.setAdapter(myPlaneAdapter);
        myFragmentList.setOnScrollListener(this);
        footView = new View(getActivity());
        String data = PreferenceUtils.getCacheData(getActivity(), String.format("%s_plans", AccountManager.getCurrentUserId()));
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONArray array = new JSONArray(data);
                JSONObject object = new JSONObject();
                object.put("result", array);
                CommonJson4List<StrategyBean> list = CommonJson4List.fromJson(object.toString(), StrategyBean.class);
                planList.clear();
                planList.addAll(list.result);
                myPlaneAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AbsListView.LayoutParams abp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        abp.height = 400;
        footView.setLayoutParams(abp);
        myFragmentList.addFooterView(footView);
        myFragmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myPlaneAdapter.getCount() <= position) return;
                StrategyBean bean = (StrategyBean) myPlaneAdapter.getDataList().get(position);
                Intent intent = new Intent(getActivity(), StrategyActivity.class);
                intent.putExtra("id", bean.id);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, RESULT_PLAN_DETAIL);
            }
        });
        getStrategyListData(userId);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getStrategyListData(String userId) {
        isLoading = true;
        if (userId != null) {
            TravelApi.getStrategyPlannedList(user.getUserId() + "", mCurrentPage, null, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    isLoading = false;

                    CommonJson4List<StrategyBean> strategyListResult = CommonJson4List.fromJson(result, StrategyBean.class);
                    if (strategyListResult.code == 0) {
                        List<StrategyBean> tempStragy = strategyListResult.result;
                        if (tempStragy != null) {
                            if (mCurrentPage == 0) {
                                planList.clear();
                                planList.addAll(tempStragy);
                            } else {
                                planList.addAll(tempStragy);
                            }
                            mCurrentPage++;
                            if (strategyListResult.result.size() < 15) {
                                isLoading = true;
                            }
                            if (mCurrentPage == 0 || myPlaneAdapter.getCount() < OtherApi.PAGE_SIZE * 2) {
                                cachePage();
                            }
                        } else {
                            isLoading = true;
                        }
                    }
                    myPlaneAdapter.notifyDataSetChanged();
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    isLoading = false;
                    if (!getActivity().isFinishing())
                        ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {
                    isLoading = false;
                }
            });
        }
    }

    private void cachePage() {
        if (user == null) {
            return;
        }
        int size = myPlaneAdapter.getCount();
        if (size > OtherApi.PAGE_SIZE) {
            size = OtherApi.PAGE_SIZE;
        }
        List<StrategyBean> cd = planList.subList(0, size);
        PreferenceUtils.cacheData(getActivity(), String.format("%s_plans", AccountManager.getCurrentUserId()), GsonTools.createGsonString(cd));
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentPage = 0;
        getStrategyListData(userId);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (is_divPage
                && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (!isLoading) getStrategyListData(userId);
        } else if (!is_divPage
                && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        is_divPage = (firstVisibleItem + visibleItemCount == totalItemCount);
    }

    class MyPlaneAdapter extends BaseSwipeAdapter {
        private ArrayList<StrategyBean> data;
        private Context context;
        private LayoutInflater inflater;
        private DisplayImageOptions picOptions;
        private LinearLayout swipe_ll;

        public MyPlaneAdapter(Context context, ArrayList<StrategyBean> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public void fillValues(final int position, View convertView) {


            ImageView plane_pic = (ImageView) convertView.findViewById(R.id.plane_pic);
            TextView plane_spans = (TextView) convertView.findViewById(R.id.plane_spans);
            TextView plane_title = (TextView) convertView.findViewById(R.id.plane_title);
            TextView city_hasGone = (TextView) convertView.findViewById(R.id.city_hasGone);
            TextView create_time = (TextView) convertView.findViewById(R.id.create_time);
            ImageView travel_hasGone = (ImageView) convertView.findViewById(R.id.travel_hasGone);


            TextView mCheck = (TextView) convertView.findViewById(R.id.sign_up);
            TextView mDelete = (TextView) convertView.findViewById(R.id.delete);

            final StrategyBean strategyBean = data.get(position);


            plane_spans.setText(strategyBean.dayCnt + "天");
            plane_title.setText(strategyBean.title);
            city_hasGone.setText(strategyBean.summary);
            //plane_pic.setImageResource(R.drawable.pic_loadfail);
            create_time.setText("创建时间: " + CommonUtils.getTimestampString(new Date(strategyBean.updateTime)));

            if (strategyBean.images != null && strategyBean.images.size() > 0) {
                plane_pic.setTag(strategyBean.images.get(0).url);
                if (plane_pic.getTag() != null && plane_pic.getTag().equals(strategyBean.images.get(0).url)) {
                    ImageLoader.getInstance().displayImage(strategyBean.images.get(0).url, plane_pic, picOptions);
                }

            } else {
                ImageLoader.getInstance().displayImage("", plane_pic, picOptions);
            }
            if (strategyBean.status.equals("traveled")) {
                mCheck.setText("取消\n签到");
                travel_hasGone.setVisibility(View.VISIBLE);
            } else {
                mCheck.setText("签到");
                travel_hasGone.setVisibility(View.GONE);
            }
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(strategyBean, position);
                }
            });
            mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "ell_item_plans_change_status");
                    if (strategyBean.status.equals("planned")) {
                        haveBeenVisited(strategyBean);
                        notifyDataSetChanged();
                        final ComfirmDialog cdialog = new ComfirmDialog(context);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("提示");
                        cdialog.setMessage("已去过，旅历＋1");
                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeItem(position);
                                cdialog.dismiss();
                                mCurrentPage = 0;
                                getStrategyListData(userId);
                                //mMyStrategyLv.doPullRefreshing(true, 0);(下拉刷新)
                            }
                        });
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 1:
                                        cdialog.show();
                                }
                                super.handleMessage(msg);
                            }
                        };
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 300);
                    } else {
                        cancleVisited(strategyBean, position);
                        notifyDataSetChanged();
                        //mMyStrategyLv.doPullRefreshing(true, 300);(下拉刷新)
                    }
                }
            });
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            View v = LayoutInflater.from(context).inflate(R.layout.travel_plane_item, null);
            swipe_ll = (LinearLayout) v.findViewById(R.id.swipe_bg_ll);
            return v;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.myswipe;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public ArrayList<StrategyBean> getDataList() {
            return data;
        }

        /* @Override
         public View getView(int position, View view, ViewGroup viewGroup) {
             final StrategyBean strategyBean = data.get(position);
             ViewHolder viewHolder;
             if (view == null) {
                 view = inflater.inflate(R.layout.travel_plane_item, viewGroup, false);
                 viewHolder = new ViewHolder();
                 viewHolder.plane_pic = (ImageView) view.findViewById(R.id.plane_pic);
                 viewHolder.plane_spans = (TextView) view.findViewById(R.id.plane_spans);
                 viewHolder.plane_title = (TextView) view.findViewById(R.id.plane_title);
                 viewHolder.city_hasGone = (TextView) view.findViewById(R.id.city_hasGone);
                 viewHolder.create_time = (TextView) view.findViewById(R.id.create_time);
                 view.setTag(viewHolder);
             } else {
                 viewHolder = (ViewHolder) view.getTag();
             }
             if (strategyBean.images.size() > 0) {
                 ImageLoader.getInstance().displayImage(strategyBean.images.get(0).url, viewHolder.plane_pic, picOptions);
             } else ImageLoader.getInstance().displayImage("", viewHolder.plane_pic, picOptions);
             viewHolder.plane_spans.setText(strategyBean.dayCnt + "天");
             viewHolder.plane_title.setText(strategyBean.title);
             viewHolder.city_hasGone.setText(strategyBean.summary);
             viewHolder.create_time.setText("创建时间: " + CommonUtils.getTimestampString(new Date(strategyBean.updateTime)));
             return view;
         }*/
        private void haveBeenVisited(final StrategyBean beenBean) {
            try {
                DialogManager.getInstance().showLoadingDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String visited = "traveled";
            TravelApi.modifyGuideVisited(beenBean.id, visited, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (visitedResult.code == 0) {
                        //    deleteThisItem(beenBean);
                    } else {
//                        if (!getActivity().isFinishing())
//                            ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
//                    if (!getActivity().isFinishing())
//                        ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }

        private void deleteItem(final StrategyBean itemData, final int pos) {
            MobclickAgent.onEvent(getActivity(), "cell_item_plans_delete");
            final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
            dialog.setTitle("提示");
            dialog.setTitleIcon(R.drawable.ic_dialog_tip);
            dialog.setMessage(String.format("删除\"%s\"", itemData.title));
            dialog.setPositiveButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    try {
                        DialogManager.getInstance().showLoadingDialog(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    closeItem(pos);
                    TravelApi.deleteStrategy(itemData.id, new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                deleteThisItem(itemData);
                                int cnt = AccountManager.getInstance().getLoginAccountInfo().getGuideCnt();
                                AccountManager.getInstance().getLoginAccountInfo().setGuideCnt(cnt - 1);
                            } else {
                                if (!getActivity().isFinishing())
                                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (!getActivity().isFinishing())
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
                }
            });
            dialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

        private void deleteThisItem(StrategyBean data) {
            int index = myPlaneAdapter.getDataList().indexOf(data);
            myPlaneAdapter.getDataList().remove(index);
            myPlaneAdapter.notifyDataSetChanged();
            if (myPlaneAdapter.getCount() == 0) {
                // myPlaneAdapter.doPullRefreshing(true, 0);(刷新)
            } else if (index <= OtherApi.PAGE_SIZE) {
                cachePage();
            }
        }

        private void cancleVisited(final StrategyBean beenBean, final int pos) {
            try {
                DialogManager.getInstance().showLoadingDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String planned = "planned";
            TravelApi.modifyGuideVisited(beenBean.id, planned, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (visitedResult.code == 0) {
                        closeItem(pos);
                        // deleteThisItem(beenBean);
                        mCurrentPage = 0;
                        getStrategyListData(userId);
                    } else {
//                        if (!getActivity().isFinishing())
//                            ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
//                    if (!getActivity().isFinishing())
//                        ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }

        class ViewHolder {
            ImageView plane_pic;
            TextView plane_spans;
            TextView plane_title;
            TextView city_hasGone;
            TextView create_time;
            ImageView travel_hasGone;
            TextView mCheck;
            TextView mDelete;
        }
    }
}
