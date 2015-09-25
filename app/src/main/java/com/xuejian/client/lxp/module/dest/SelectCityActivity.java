package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.xuejian.client.lxp.module.dest.fragment.InCityFragment;
import com.xuejian.client.lxp.module.dest.fragment.InDestFragment;
import com.xuejian.client.lxp.module.dest.fragment.OutCityFragment;
import com.xuejian.client.lxp.module.dest.fragment.OutCountryFragment;
import com.xuejian.client.lxp.module.my.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SelectCityActivity extends PeachBaseActivity implements OnDestActionListener {
    public final static int REQUEST_CODE_SEARCH_LOC = 101;
    public final static int REQUEST_CODE_LOGIN = 102;
    public final static int REQUEST_CODE_NEW_PLAN = 103;
    public final static int REQUEST_CODE_SELECT_LOC=104;
    private int requestCode;

    private FixedIndicatorView inOutIndicator;
    private FixedViewPager mSelectDestVp;
    private IndicatorViewPager indicatorViewPager;
    private ArrayList<LocBean> allAddCityList = new ArrayList<LocBean>();
    private ArrayList<LocBean> hasSelectLoc;
    private String guideId;
    private Set<OnDestActionListener> mOnDestActionListeners = new HashSet<OnDestActionListener>();
    //private HorizontalScrollView mScrollPanel;

    private ArrayList<String> allSelectedPics = new ArrayList<String>();
    private EditText desty_et_search;
    private TextView desty_btn_search;
    @Override
    public void onDestAdded(final LocBean locBean, boolean isEdit, String type) {
        if (allAddCityList.contains(locBean)) {
            ToastUtil.getInstance(mContext).showToast("已添加");
            return;
        }
        View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
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

    }

    @Override
    public void onDestRemoved(LocBean locBean, String type) {
        int index = allAddCityList.indexOf(locBean);

        allAddCityList.remove(locBean);
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext, R.layout.activity_select_city, null);
        setContentView(rootView);
        initTitleBar();
        desty_et_search = (EditText)rootView.findViewById(R.id.desty_et_search);
        desty_btn_search = (TextView)rootView.findViewById(R.id.desty_btn_search);
        inOutIndicator = (FixedIndicatorView) rootView.findViewById(R.id.in_out_indicator);
        mSelectDestVp = (FixedViewPager) rootView.findViewById(R.id.select_dest_viewPager);
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

        desty_et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                desty_btn_search.setText("取消");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    desty_btn_search.setText("搜索");
                }else{
                    desty_btn_search.setText("取消");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        desty_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String keyWords=desty_et_search.getText().toString();
                if(keyWords!=null && keyWords.trim().length()>0){
                    Intent intent = new Intent(mContext,SearchSomeCityActivity.class);
                    intent.putExtra("keyWords",keyWords);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_LOC);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_select_plan_city");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_select_plan_city");
        MobclickAgent.onPause(this);
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
                convertView = inflater.inflate(R.layout.tab_rectangle_select, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.desty_title);
            textView.setText(tabNames[position]);
            if (position == 0) {
                textView.setBackgroundResource(R.drawable.lefttwo_wgrey_seletor);
            } else if (position == 1) {
                textView.setBackgroundResource(R.drawable.righttwo_wgrey_seletor);
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                InCityFragment inDestFragment = new InCityFragment(true);
                return inDestFragment;
            } else if (position == 1) {
                OutCityFragment outCountryFragment = new OutCityFragment(true);
                return outCountryFragment;
            }
            return null;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
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
            }else if(requestCode == REQUEST_CODE_SELECT_LOC){
                ArrayList<LocBean>  choosedCities =data.getParcelableArrayListExtra("choosedCities");
                if(choosedCities!=null && choosedCities.size()>0){
                    for(int i=0;i<choosedCities.size();i++){
                        onDestAdded(choosedCities.get(i), true, null);
                        for (OnDestActionListener onDestActionListener : mOnDestActionListeners) {
                            onDestActionListener.onDestAdded(choosedCities.get(i), true, null);
                        }
                    }
                }

            }
        }


    }
}
