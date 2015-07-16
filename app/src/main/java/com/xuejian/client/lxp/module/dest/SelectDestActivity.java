package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.IndicatorViewPager;
import com.aizou.core.widget.pagerIndicator.viewpager.FixedViewPager;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.dest.fragment.InDestFragment;
import com.xuejian.client.lxp.module.dest.fragment.OutCountryFragment;
import com.xuejian.client.lxp.module.my.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rjm on 2014/10/9.
 */
public class SelectDestActivity extends PeachBaseActivity implements OnDestActionListener {
    public final static int REQUEST_CODE_SEARCH_LOC = 101;
    public final static int REQUEST_CODE_LOGIN = 102;
    public final static int REQUEST_CODE_NEW_PLAN = 103;

    private int requestCode;
    private RelativeLayout mBottomPanel;
    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();
    private ArrayList<LocBean> hasSelectLoc;
    private String guideId;
    private Set<OnDestActionListener> mOnDestActionListeners = new HashSet<OnDestActionListener>();
    //private HorizontalScrollView mScrollPanel;
    private TextView next;
    private ArrayList<String> allSelectedPics = new ArrayList<String>();
    HorizontalScrollView mScrollPanel;
    LinearLayout citysLl;

    @Override
    public void onDestAdded(final LocBean locBean, boolean isEdit, String type) {
        MobclickAgent.onEvent(mContext, "event_select_city");
        if (allAddCityList.contains(locBean)) {
            ToastUtil.getInstance(mContext).showToast("已添加");
            return;
        }
        View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
        citysLl.addView(cityView);
        allAddCityList.add(locBean);
        TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
        cityNameTv.setText(locBean.zhName);
        /*cityNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
                allAddCityList.remove(locBean);
                if (allAddCityList.size() == 0) {
                    next.setEnabled(false);
=======
                int index = allAddCityList.indexOf(locBean);
                citysLl.removeViewAt(index);
                allAddCityList.remove(locBean);
                if (allAddCityList.size() == 0) {
                    mBottomPanel.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
>>>>>>> dev_1.1
                }
                for (OnDestActionListener onDestActionListener : mOnDestActionListeners) {
                    onDestActionListener.onDestRemoved(locBean, null);
                }
                autoScrollPanel();
            }
        });*/

        if (allAddCityList.size() > 0) {
            //next.setEnabled(true);
            mBottomPanel.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        autoScrollPanel();
    }

    @Override
    public void onDestRemoved(LocBean locBean, String type) {
        int index = allAddCityList.indexOf(locBean);
        citysLl.removeViewAt(index);
        allAddCityList.remove(locBean);
        if (allAddCityList.size() == 0) {
            mBottomPanel.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }
        autoScrollPanel();
    }

    private void autoScrollPanel() {
        mScrollPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollPanel.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext, R.layout.activity_select_dest, null);
        setContentView(rootView);
        initTitleBar();
        citysLl = (LinearLayout) rootView.findViewById(R.id.ll_citys);
        mScrollPanel = (HorizontalScrollView) rootView.findViewById(R.id.scroll_panel);
        mBottomPanel = (RelativeLayout) rootView.findViewById(R.id.bottom_panel);

        next = (TextView) rootView.findViewById(R.id.tv_confirm);
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.select_dest_viewPager);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "event_select_done_go_next");
                User user = AccountManager.getInstance().getLoginAccount(mContext);
                if (user != null) {
                    if (requestCode == StrategyActivity.EDIT_LOC_REQUEST_CODE) {
                        DialogManager.getInstance().showLoadingDialog(SelectDestActivity.this);
                        TravelApi.modifyGuideLoc(guideId, allAddCityList, new HttpCallBack<String>() {
                            @Override
                            public void doSuccess(String result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                CommonJson<ModifyResult> modfiyResult = CommonJson.fromJson(result, ModifyResult.class);
                                if (modfiyResult.code == 0) {
                                    Intent intent = new Intent();
                                    intent.putParcelableArrayListExtra("destinations", allAddCityList);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    if (!isFinishing())
                                        ToastUtil.getInstance(mContext).showToast(modfiyResult.err.message);
                                }
                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                if (!isFinishing())
                                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));

                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method, int code) {

                            }
                        });

                    } else {
                        Intent intent = new Intent(mContext, StrategyActivity.class);
                        intent.putParcelableArrayListExtra("destinations", allAddCityList);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    ToastUtil.getInstance(mContext).showToast("请先登录");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_LOGIN);
                }
            }
        });
        indicatorViewPager = new IndicatorViewPager(inOutIndicator, mSelectDestVp);
        indicatorViewPager.setAdapter(new InOutFragmentAdapter(getSupportFragmentManager()));
        mSelectDestVp.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSelectDestVp.setOffscreenPageLimit(2);
        // 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
        mSelectDestVp.setPrepareNumber(0);
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                if (currentItem == 1) {
                    MobclickAgent.onEvent(mContext, "event_go_aboard");
                }
            }
        });
        requestCode = getIntent().getIntExtra("request_code", 0);
        guideId = getIntent().getStringExtra("guide_id");
        hasSelectLoc = getIntent().getParcelableArrayListExtra("locList");
        if (hasSelectLoc != null) {
            for (LocBean locBean : hasSelectLoc) {
                onDestAdded(locBean, true, null);
                for (OnDestActionListener onDestActionListener : mOnDestActionListeners) {
                    onDestActionListener.onDestAdded(locBean, true, null);
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_destinations");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_destinations");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            OnDestActionListener listener = (OnDestActionListener) fragment;
            mOnDestActionListeners.add(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onAttachFragment(fragment);
    }

    public ArrayList<LocBean> getAllSelectedLoc() {
        return allAddCityList;
    }

    private void initTitleBar() {
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private class InOutFragmentAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"国内", "国外"};
        private LayoutInflater inflater;

        public InOutFragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_select_dest, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tv_title);
            textView.setText(tabNames[position]);
            if (position == 0) {
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
            } else if (position == 1) {
                textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_01);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                InDestFragment inDestFragment = new InDestFragment(true);
                return inDestFragment;
            } else if (position == 1) {
                OutCountryFragment outCountryFragment = new OutCountryFragment(true);
                return outCountryFragment;
            }
            return null;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SEARCH_LOC) {
                LocBean locBean = data.getParcelableExtra("loc");
                onDestAdded(locBean, true, null);
                for (OnDestActionListener onDestActionListener : mOnDestActionListeners) {
                    onDestActionListener.onDestAdded(locBean, true, null);
                }
            } else if (requestCode == REQUEST_CODE_LOGIN) {
                Intent intent = new Intent(mContext, StrategyActivity.class);
                intent.putParcelableArrayListExtra("destinations", allAddCityList);
                startActivityWithNoAnim(intent);
                finishWithNoAnim();
            } else if (requestCode == REQUEST_CODE_NEW_PLAN) {
                setResult(RESULT_OK, data);
                finishWithNoAnim();
            }
        }
    }
}
