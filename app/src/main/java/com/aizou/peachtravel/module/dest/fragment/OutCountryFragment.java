package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.CountryBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutItem;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutListView;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/3.
 */
public class OutCountryFragment extends PeachBaseFragment implements OnDestActionListener {
    @InjectView(R.id.lv_out_country)
    ExpandableLayoutListView mLvOutCountry;
    ListViewDataAdapter<CountryBean> outCountryAdapter;
    OnDestActionListener mOnDestActionListener;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_out_country, container, false);
        ButterKnife.inject(this, rootView);
        outCountryAdapter = new ListViewDataAdapter<CountryBean>(new ViewHolderCreator<CountryBean>() {
            @Override
            public ViewHolderBase<CountryBean> createViewHolder() {
                return new OutCountryViewHolder();
            }
        });
        mLvOutCountry.setAdapter(outCountryAdapter);
//        getOutCountryList();
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
        }
            getOutCountryList();
    }

    private void getOutCountryList(){
        TravelApi.getOutDestList(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(result, CountryBean.class);
                if (countryListResult.code == 0) {
                    bindOutView(countryListResult.result);
                    PreferenceUtils.cacheData(getActivity(), "destination_outcountry", result);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (isAdded())
                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
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
        outCountryAdapter.getDataList().addAll(result);
        outCountryAdapter.notifyDataSetChanged();
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
        private TextView nameTv,descTv;
        private ImageView imageIv;
        private FlowLayout cityListFl;
        private View contentView;
        DisplayImageOptions picOptions;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.dest_out_country, null);
            ExpandableLayoutItem itemView = (ExpandableLayoutItem) contentView.findViewById(R.id.row_country);
            RelativeLayout headRl = itemView.getHeaderRelativeLayout();
            RelativeLayout contentRl=itemView.getContentRelativeLayout();
            nameTv = (TextView) headRl.findViewById(R.id.tv_country_name);
            descTv = (TextView) headRl.findViewById(R.id.tv_country_desc);
            imageIv = (ImageView) headRl.findViewById(R.id.iv_country);
            cityListFl = (FlowLayout) contentRl.findViewById(R.id.fl_city_list);
            int width = LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(20);
            int height = width * 240 / 640;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            imageIv.setLayoutParams(lp);

            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(true)
//				.decodingOptions(D)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(2)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

            return contentView;
        }

        @Override
        public void showData(int position, final CountryBean itemData) {
//            contentView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(cityListFl.getVisibility()==View.VISIBLE){
//                        cityListFl.setVisibility(View.GONE);
//                    }else{
//                        cityListFl.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
            nameTv.setText(itemData.zhName);
//            SpannableString impress = new SpannableString("|"+itemData.desc);
//            impress.setSpan(
//                    new ForegroundColorSpan(getResources().getColor(
//                            R.color.route_price_color)), 0, impress.length(),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(15)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            nameTv.append(impress);
            if(itemData.image!=null&&itemData.image.size()>0) {
                ImageLoader.getInstance().displayImage(itemData.image.get(0).url, imageIv, picOptions);
            } else {
                imageIv.setImageDrawable(null);
            }
            descTv.setText(itemData.desc);
            cityListFl.removeAllViews();
            for(final LocBean bean:itemData.destinations){
                View view = View.inflate(getActivity(),R.layout.dest_select_city,null);
                CheckedTextView cityNameTv = (CheckedTextView) view.findViewById(R.id.tv_cell_name);
                cityNameTv.setText(bean.zhName);
                cityNameTv.setChecked(bean.isAdded);
                cityNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bean.isAdded=!bean.isAdded;
                        if(mOnDestActionListener!=null){
                            if(bean.isAdded){
                                mOnDestActionListener.onDestAdded(bean);
                            }else{
                                mOnDestActionListener.onDestRemoved(bean);
                            }
                        }
                        outCountryAdapter.notifyDataSetChanged();
                    }
                });
//                ImageView addIv = (ImageView) view.findViewById(R.id.iv_add);
//                if(bean.isAdded){
//                    addIv.setImageResource(R.drawable.ic_line_edit_delete);
//                }else{
//                    addIv.setImageResource(R.drawable.ic_view_add);
//                }
//                addIv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        bean.isAdded=!bean.isAdded;
//                        if(mOnDestActionListener!=null){
//                            if(bean.isAdded){
//                                mOnDestActionListener.onDestAdded(bean);
//                            }else{
//                                mOnDestActionListener.onDestRemoved(bean);
//                            }
//                        }
//                        outCountryAdapter.notifyDataSetChanged();
//                    }
//                });

                cityListFl.addView(view);
            }
            cityListFl.requestLayout();

        }
    }

}
