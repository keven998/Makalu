package com.xuejian.client.lxp.module.dest.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/3.
 */
@SuppressLint("ValidFragment")
public class OutCountryFragment extends PeachBaseFragment implements OnDestActionListener {
    @InjectView(R.id.lv_out_country)
    GridView mLvOutCountry;
    ListView mCountryMame;
    ListViewDataAdapter<LocBean> outCountryAdapter;
    ListViewDataAdapter<CountryBean> outContryNameAdapter;
    OnDestActionListener mOnDestActionListener;
    DynamicBox box;
    private Drawable add, selected;
    private boolean isClickable;
    private CountryBean lastCountryBean;
    public OutCountryFragment(boolean isClickable) {
        this.isClickable = isClickable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_out_country, container, false);
        mCountryMame = (ListView)rootView.findViewById(R.id.lv_country_name);
        ButterKnife.inject(this, rootView);
        box = new DynamicBox(getActivity(), mLvOutCountry);
        outCountryAdapter = new ListViewDataAdapter<LocBean>(new ViewHolderCreator<LocBean>() {
            @Override
            public ViewHolderBase<LocBean> createViewHolder() {
                return new OutCountryViewHolder();
            }
        });

        outContryNameAdapter = new ListViewDataAdapter<CountryBean>(new ViewHolderCreator<CountryBean>() {
            @Override
            public ViewHolderBase<CountryBean> createViewHolder() {
                return new OutCountryNameHolder();
            }
        });
       /* if (isClickable) {
            View view = new View(getActivity());
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(70)));
            mLvOutCountry.addFooterView(view);
        }*/
        mLvOutCountry.setAdapter(outCountryAdapter);
        mLvOutCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LocBean theCity = outCountryAdapter.getItem(position);
                theCity.isAdded = !theCity.isAdded;
                if (mOnDestActionListener != null) {
                    if(theCity.isAdded){
                        mOnDestActionListener.onDestAdded(theCity, true, "in");
                    }else{
                        mOnDestActionListener.onDestRemoved(theCity, "in");
                    }
                }
                outCountryAdapter.notifyDataSetChanged();
            }
        });
        mCountryMame.setAdapter(outContryNameAdapter);
        mCountryMame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(lastCountryBean!=null){
                    lastCountryBean.isSelect=false;
                }
                final CountryBean currentCountryBean = outContryNameAdapter.getDataList().get(position);
                lastCountryBean=currentCountryBean;
                lastCountryBean.isSelect=true;
                ArrayList<LocBean> citys = currentCountryBean.destinations;
                if(citys==null){
                    citys=new ArrayList<LocBean>();
                }
                outCountryAdapter.getDataList().clear();
                outCountryAdapter.getDataList().addAll(citys);
                outCountryAdapter.notifyDataSetChanged();
            }
        });

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
        } else {
            box.showLoadingLayout();
        }
        getOutCountryList();
    }

    private void getOutCountryList(){
        String lastModify = PreferenceUtils.getCacheData(getActivity(), "outcountry_last_modify");
        TravelApi.getOutDestList(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
            }

            @Override
            public void doSuccess(String result, String method, Map<String, List<String>> headers) {
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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
        outContryNameAdapter.getDataList().clear();
        ArrayList<LocBean> allSelectLoc = null;
        if (getActivity() != null && isClickable) {
            allSelectLoc = ((SelectDestActivity) getActivity()).getAllSelectedLoc();
        } else if (getActivity() != null && !isClickable) {
            allSelectLoc = ((MyFootPrinterActivity) getActivity()).getAllSelectedLoc();
        }
      if (allSelectLoc != null) {
            for (CountryBean countryBean : result) {
                for (LocBean kLocBean : countryBean.destinations) {
                    if (allSelectLoc.contains(kLocBean)) {
                        kLocBean.isAdded = true;
                    }

                }
            }
        }
        outContryNameAdapter.getDataList().addAll(result);
        outContryNameAdapter.notifyDataSetChanged();
        mCountryMame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountryMame.performItemClick(mCountryMame.getChildAt(0), 0, 0);
            }
        }, 200);


    }

    @Override
    public void onDestAdded(LocBean locBean, boolean isEdit, String type) {
        if (outCountryAdapter != null) {
                for (LocBean kLocBean : outCountryAdapter.getDataList()) {
                    if (locBean.id.equals(kLocBean.id)) {
                        kLocBean.isAdded = true;
                    }
                }
            outCountryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestRemoved(LocBean locBean, String type) {
        if (outCountryAdapter != null) {
                for (LocBean kLocBean : outCountryAdapter.getDataList()) {
                    if (locBean.id.equals(kLocBean.id)) {
                        kLocBean.isAdded = false;
                    }
                }
            outCountryAdapter.notifyDataSetChanged();
        }
    }

    private class OutCountryNameHolder extends ViewHolderBase<CountryBean> {
        private TextView contry_name;
        //private DisplayImageOptions poptions = UILUtils.getDefaultOption();
        private DisplayImageOptions poptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.desty_name, null);
            contry_name = (TextView)contentView.findViewById(R.id.contry_name);
            return contentView;
        }

        @Override
        public void showData(int position, final CountryBean itemData) {
            contry_name.setText(itemData.zhName);
            if(itemData.isSelect){
                contry_name.setTextColor(getActivity().getResources().getColor(R.color.color_text_i));
            }else{
                contry_name.setTextColor(getActivity().getResources().getColor(R.color.color_text_iii));
            }
        }
    }

    private class OutCountryViewHolder extends ViewHolderBase<LocBean> {
        private TextView sectionTv;
        private FlowLayout cityListFl;
        TextView cityNameTv;
        ImageView desBgImage;
        ImageView addIcon;
        //private DisplayImageOptions poptions = UILUtils.getDefaultOption();
        private DisplayImageOptions poptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.out_desty_city_image, null);
            cityNameTv = (TextView) contentView.findViewById(R.id.des_title);
            desBgImage = (ImageView) contentView.findViewById(R.id.des_bg_pic);
            addIcon = (ImageView) contentView.findViewById(R.id.des_selected_icon);
            return contentView;
        }

        @Override
        public void showData(int position, final LocBean itemData) {

                cityNameTv.setText(itemData.zhName);
                if (itemData.images.size() > 0) {
                    ImageLoader.getInstance().displayImage(itemData.images.get(0).url, desBgImage, poptions);
                }
                if (!itemData.isAdded) {
                    addIcon.setVisibility(View.GONE);
                } else {
                    addIcon.setVisibility(View.VISIBLE);
                }

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

}
