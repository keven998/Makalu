package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.InDestBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TopSectionBar;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.easemob.util.HanziToPinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/3.
 */
public class InDestFragment extends PeachBaseFragment implements OnDestActionListener {
    @InjectView(R.id.section_bar)
    TopSectionBar mSectionBar;
    @InjectView(R.id.lv_in_city)
    ListView mLvInCity;
    private List<InDestBean> incityList = new ArrayList<InDestBean>();
    InCityAdapter inCityAdapter;
    OnDestActionListener mOnDestActionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_dest, container, false);
        ButterKnife.inject(this, rootView);
        inCityAdapter = new InCityAdapter(new ViewHolderCreator<InDestBean>() {
            @Override
            public ViewHolderBase<InDestBean> createViewHolder() {
                return new InCityViewHolder();
            }
        });
//        LinearLayout footer = new LinearLayout(getActivity());
//        footer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(64)));
//        footer.setBackgroundColor(getResources().getColor(R.color.app_backgroud));
//        ViewGroup.LayoutParams params = footer.getLayoutParams();
//        params.height = LocalDisplay.dp2px(64);
//        footer.setLayoutParams(params);
//        mLvInCity.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.padding_footer, null));

        mLvInCity.setAdapter(inCityAdapter);
        mSectionBar.setListView(mLvInCity);
        getInLocList();
        return rootView;
    }

    private void getInLocList() {
        TravelApi.getDestList(0, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<LocBean> locListResult = CommonJson4List.fromJson(result, LocBean.class);
                if (locListResult.code == 0) {
                    bindInView(locListResult.result);
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

    private void bindInView(List<LocBean> result) {
        HashMap<String, List<LocBean>> locMap = new HashMap<String, List<LocBean>>();
        for (LocBean locBean : result) {
            if (Character.isDigit(locBean.zhName.charAt(0))) {
                locBean.header = "#";
            } else {
                locBean.header = HanziToPinyin.getInstance().get(locBean.zhName.substring(0, 1)).get(0).target.substring(
                        0, 1).toUpperCase();
                char header = locBean.header.toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    locBean.header = "#";
                }
            }
            if (locMap.get(locBean.header) != null) {
                locMap.get(locBean.header).add(locBean);
            } else {
                List<LocBean> locList = new ArrayList<LocBean>();
                locList.add(locBean);
                locMap.put(locBean.header, locList);
            }
        }
        for (Map.Entry<String, List<LocBean>> entry : locMap.entrySet()) {
            InDestBean inDestBean = new InDestBean();
            inDestBean.section = entry.getKey();
            inDestBean.locList = entry.getValue();
            incityList.add(inDestBean);
        }
        // 排序
        Collections.sort(incityList, new Comparator<InDestBean>() {

            @Override
            public int compare(InDestBean lhs, InDestBean rhs) {
                return lhs.section.compareTo(rhs.section);
            }
        });
        inCityAdapter.getDataList().addAll(incityList);
        inCityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestAdded(LocBean locBean) {
        for(InDestBean inDestBean:inCityAdapter.getDataList()){
            for(LocBean kLocBean:inDestBean.locList){
                if(locBean.id.equals(kLocBean.id)){
                    kLocBean.isAdded=true;
                }
            }
        }
        inCityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        for(InDestBean inDestBean:inCityAdapter.getDataList()){
            for(LocBean kLocBean:inDestBean.locList){
                if(locBean.id.equals(kLocBean.id)){
                    kLocBean.isAdded=false;
                }
            }
        }
        inCityAdapter.notifyDataSetChanged();

    }

    private class InCityAdapter extends ListViewDataAdapter<InDestBean> implements SectionIndexer {
        private List<String> sections;
        private SparseIntArray positionOfSection;
        private SparseIntArray sectionOfPosition;

        /**
         * @param viewHolderCreator The view holder creator will create a View Holder that extends {@link com.aizou.core.widget.listHelper.ViewHolderBase}
         */
        public InCityAdapter(ViewHolderCreator viewHolderCreator) {
            super(viewHolderCreator);
            initSections();
        }

        @Override
        public Object[] getSections() {
            return sections.toArray();
        }

        @Override
        public int getPositionForSection(int section) {
            return positionOfSection.get(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            return sectionOfPosition.get(position);
        }

        public void initSections() {
            int count = getCount();
            positionOfSection = new SparseIntArray();
            sectionOfPosition = new SparseIntArray();
            sections = new ArrayList<String>();
            int section = 0;
            for (int i = 0; i < count; i++) {
                String letter = getItem(i).section;
                String beforeLetter = "";
                if (i > 0) {
                    beforeLetter = getItem(i - 1).section;
                }
                if (letter != null && !beforeLetter.equals(letter)) {
                    section++;
                    sections.add(letter);
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            initSections();
            if (mSectionBar != null) {
                mSectionBar.notifyDataSetChanged();
            }

        }

    }


    private class InCityViewHolder extends ViewHolderBase<InDestBean> {
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
        public void showData(int position, final InDestBean itemData) {
            sectionTv.setText(itemData.section);
            cityListFl.removeAllViews();
            for (final LocBean bean : itemData.locList) {
                View contentView = View.inflate(getActivity(), R.layout.dest_select_city, null);
                CheckedTextView cityNameTv = (CheckedTextView) contentView.findViewById(R.id.tv_cell_name);
                cityNameTv.setText(bean.zhName);
                cityNameTv.setChecked(bean.isAdded);
                cityNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bean.isAdded = !bean.isAdded;
                        if (mOnDestActionListener != null) {
                            if (bean.isAdded) {
                                mOnDestActionListener.onDestAdded(bean);
                            } else {
                                mOnDestActionListener.onDestRemoved(bean);
                            }
                        }
                        inCityAdapter.notifyDataSetChanged();
                    }
                });

                cityListFl.addView(contentView);
            }
        }
    }
}
