package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutItem;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutListView;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.nostra13.universalimageloader.core.ImageLoader;

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
        getOutCountryList();
        return rootView;
    }

    private void getOutCountryList(){
        TravelApi.getDestList(1, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(result, CountryBean.class);
                if (countryListResult.code == 0) {
                    bindOutView(countryListResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

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
            int height = width * 220 / 600;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    width, height);
            imageIv.setLayoutParams(lp);
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
            nameTv.setText("");
            nameTv.setText(itemData.zhName);
            SpannableString impress = new SpannableString("|"+itemData.desc);
            impress.setSpan(
                    new ForegroundColorSpan(getResources().getColor(
                            R.color.route_price_color)), 0, impress.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(15)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.append(impress);
            if(itemData.image!=null&&itemData.image.size()>0)
            ImageLoader.getInstance().displayImage(itemData.image.get(0).url, imageIv, UILUtils.getRadiusOption());
            cityListFl.removeAllViews();
            int i=0;
            for(final LocBean bean:itemData.destinations){
                View view = View.inflate(getActivity(),R.layout.dest_select_city,null);
                TextView cityNameTv = (TextView) view.findViewById(R.id.tv_city_name);
                cityNameTv.setText(bean.zhName);
                ImageView addIv = (ImageView) view.findViewById(R.id.iv_add);
                if(bean.isAdded){
                    addIv.setImageResource(R.drawable.ic_line_edit_delete);
                }else{
                    addIv.setImageResource(R.drawable.ic_view_add);
                }
                addIv.setOnClickListener(new View.OnClickListener() {
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

                cityListFl.addView(view);
                i++;
            }
            cityListFl.requestLayout();

        }
    }

}
