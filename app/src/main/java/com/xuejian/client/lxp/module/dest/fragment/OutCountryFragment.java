package com.xuejian.client.lxp.module.dest.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.DynamicBox;
import com.xuejian.client.lxp.common.widget.FlowLayout;
import com.xuejian.client.lxp.module.dest.OnDestActionListener;
import com.xuejian.client.lxp.module.dest.SelectDestActivity;
import com.xuejian.client.lxp.module.my.MyFootPrinterActivity;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/3.
 */
@SuppressLint("ValidFragment")
public class OutCountryFragment extends PeachBaseFragment implements OnDestActionListener {
    @InjectView(R.id.lv_out_country)
    ListView mLvOutCountry;
    ListViewDataAdapter<CountryBean> outCountryAdapter;
    OnDestActionListener mOnDestActionListener;
    DynamicBox box;
    private Drawable add,selected;
    private boolean isClickable;

    public OutCountryFragment(boolean isClickable){
        this.isClickable=isClickable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_out_country, container, false);
        ButterKnife.inject(this, rootView);
        box = new DynamicBox(getActivity(),mLvOutCountry);
        outCountryAdapter = new ListViewDataAdapter<CountryBean>(new ViewHolderCreator<CountryBean>() {
            @Override
            public ViewHolderBase<CountryBean> createViewHolder() {
                return new OutCountryViewHolder();
            }
        });
        if(isClickable) {
            View view = new View(getActivity());
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(70)));
            mLvOutCountry.addFooterView(view);
        }
        mLvOutCountry.setAdapter(outCountryAdapter);

        initData();
        return rootView;
    }

    private void initData() {
        String data = PreferenceUtils.getCacheData(getActivity(), "destination_outcountry");
        if (!TextUtils.isEmpty(data)) {
            CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(data, CountryBean.class);
            if (countryListResult.code == 0) {
                bindOutView(countryListResult.result);
            }
        }else{
            box.showLoadingLayout();
        }
        getOutCountryList();
    }

    private void getOutCountryList(){
        String lastModify= PreferenceUtils.getCacheData(getActivity(), "outcountry_last_modify");
        TravelApi.getOutDestList(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
            }

            @Override
            public void doSucess(String result, String method, Header[] headers) {
                box.hideAll();
                CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(result, CountryBean.class);
                if (countryListResult.code == 0) {
                    bindOutView(countryListResult.result);
                    PreferenceUtils.cacheData(getActivity(), "destination_outcountry", result);
                    PreferenceUtils.cacheData(getActivity(), "outcountry_last_modify", CommonUtils.getLastModifyForHeader(headers));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                box.hideAll();
//                if (isAdded())
//                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mOnDestActionListener = (OnDestActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement On OnDestActionListener");
        }
        super.onAttach(activity);
    }

    private void bindOutView(List<CountryBean> result) {
        outCountryAdapter.getDataList().clear();
        ArrayList<LocBean> allSelectLoc = null;
        if(getActivity()!=null&&isClickable){
            allSelectLoc = ((SelectDestActivity)getActivity()).getAllSelectedLoc();
        }else if(getActivity() != null && !isClickable){
            allSelectLoc = ((MyFootPrinterActivity) getActivity()).getAllSelectedLoc();
        }
        if(allSelectLoc!=null){
            for(CountryBean countryBean:result){
                for(LocBean kLocBean :countryBean.destinations ){
                    if(allSelectLoc.contains(kLocBean)){
                        kLocBean.isAdded = true;
                    }

                }
            }
        }
        outCountryAdapter.getDataList().addAll(result);
        outCountryAdapter.notifyDataSetChanged();
        mLvOutCountry.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLvOutCountry.performItemClick(mLvOutCountry.getChildAt(0),0,0);
            }
        },200);


    }

    @Override
    public void onDestAdded(LocBean locBean) {
        if(outCountryAdapter!=null){
            for(CountryBean countryBean:outCountryAdapter.getDataList()){
                for(LocBean kLocBean :countryBean.destinations ){
                    if(locBean.id.equals(kLocBean.id)){
                        kLocBean.isAdded=true;
                    }
                }
            }
            outCountryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        if(outCountryAdapter!=null){
            for(CountryBean countryBean:outCountryAdapter.getDataList()){
                for(LocBean kLocBean :countryBean.destinations ){
                    if(locBean.id.equals(kLocBean.id)){
                        kLocBean.isAdded=false;
                    }
                }
            }
            outCountryAdapter.notifyDataSetChanged();
        }
    }

    private class OutCountryViewHolder extends ViewHolderBase<CountryBean> {
        private TextView sectionTv;
        private FlowLayout cityListFl;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.dest_in_item, null);
            sectionTv = (TextView) contentView.findViewById(R.id.tv_section);
            cityListFl = (FlowLayout) contentView.findViewById(R.id.fl_city_list);
            return contentView;
        }

        @Override
        public void showData(int position, final CountryBean itemData) {
            sectionTv.setText(itemData.zhName);
            cityListFl.removeAllViews();
            for (final LocBean bean : itemData.destinations) {
                View contentView = View.inflate(getActivity(), R.layout.dest_select_city, null);
                final CheckedTextView cityNameTv = (CheckedTextView) contentView.findViewById(R.id.tv_cell_name);
                cityNameTv.setText(bean.zhName);
                cityNameTv.setChecked(bean.isAdded);

                /*if(isClickable) {*/

                    //更新按钮的图片的
                    add = getResources().getDrawable(R.drawable.ic_cell_item_unchoose);
                    selected = getResources().getDrawable(R.drawable.ic_cell_item_chooesed);
                    /// 这一步必须要做,否则不会显示.
                    add.setBounds(0, 0, add.getMinimumWidth(), add.getMinimumHeight());
                    selected.setBounds(0, 0, selected.getMinimumWidth(), selected.getMinimumHeight());
                    if (!bean.isAdded) {
                        if(isClickable) {
                            cityNameTv.setCompoundDrawables(add, null, null, null);
                        }
                    } else {
                        if(isClickable) {
                            cityNameTv.setCompoundDrawables(selected, null, null, null);
                        }
                    }

                    cityNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bean.isAdded = !bean.isAdded;
                            if (mOnDestActionListener != null) {
                                if (bean.isAdded) {
                                    if(isClickable){
                                    cityNameTv.setCompoundDrawables(selected, null, null, null);
                                    }
                                    mOnDestActionListener.onDestAdded(bean);
                                } else {
                                    if(isClickable){
                                    cityNameTv.setCompoundDrawables(add, null, null, null);
                                    }
                                    mOnDestActionListener.onDestRemoved(bean);
                                }
                            }
                            outCountryAdapter.notifyDataSetChanged();
                        }
                    });

               /* }else{
                    cityNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bean.isAdded = !bean.isAdded;
                            if(mOnDestActionListener != null){
                                if(bean.isAdded){
                                    mOnDestActionListener.onDestAdded(bean);
                                } else {
                                    mOnDestActionListener.onDestRemoved(bean);
                                }
                            }
                        }
                    });
                }*/

                cityListFl.addView(contentView);
            }

        }
    }

}