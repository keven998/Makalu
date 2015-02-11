package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.SideBar;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.aizou.core.widget.section.SectionListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.InDestBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.widget.DynamicBox;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.aizou.peachtravel.module.dest.SelectDestActivity;
import com.easemob.util.HanziToPinyin;

import org.apache.http.Header;

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
    @InjectView(R.id.lv_in_city)
    ListView mLvInCity;
    @InjectView(R.id.sb_index)
    SideBar mSbIndex;
    @InjectView(R.id.dialog)
    TextView mDialog;

    DynamicBox box;
    private List<InDestBean> incityList = new ArrayList<InDestBean>();
    InCityAdapter inCityAdapter;


    OnDestActionListener mOnDestActionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_dest, container, false);
        ButterKnife.inject(this, rootView);
        box = new DynamicBox(getActivity(), mLvInCity);
        inCityAdapter = new InCityAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new InCityViewHolder();
            }
        });
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(70)));
        mLvInCity.addFooterView(view);
        mLvInCity.setAdapter(inCityAdapter);
        mSbIndex.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = inCityAdapter.getPositionForIndex(s);
                if (position != -1) {
                    mLvInCity.setSelection(position);
                }
            }
        });
        mSbIndex.setTextView(mDialog);
        mSbIndex.setTextColor(getResources().getColor(R.color.app_theme_color_secondary));
        initData();
        return rootView;
    }

    private void initData() {
        String data = PreferenceUtils.getCacheData(getActivity(), "destination_indest");
        if (!TextUtils.isEmpty(data)) {
            CommonJson4List<LocBean> locListResult = CommonJson4List.fromJson(data, LocBean.class);
            if (locListResult.code == 0) {
                bindInView(locListResult.result);
            }
        }else{
            box.showLoadingLayout();
        }
        getInLocList();
    }

    private void getInLocList() {

        String lastModify = PreferenceUtils.getCacheData(getActivity(), "indest_last_modify");
        TravelApi.getInDestList(lastModify, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {

            }

            @Override
            public void doSucess(String result, String method, Header[] headers) {
                CommonJson4List<LocBean> locListResult = CommonJson4List.fromJson(result, LocBean.class);
                box.hideAll();
                if (locListResult.code == 0) {
                    bindInView(locListResult.result);
                    PreferenceUtils.cacheData(getActivity(), "destination_indest", result);
                    PreferenceUtils.cacheData(getActivity(), "indest_last_modify", CommonUtils.getLastModifyForHeader(headers));
                    LogUtil.d("last_modify", CommonUtils.getLastModifyForHeader(headers));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                box.hideAll();
//                if (isAdded()) {
//                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
//                }
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
        ArrayList<String> sections = new ArrayList<>();
        ArrayList<LocBean> allSelectLoc = null;
        if (getActivity() != null) {
            allSelectLoc = ((SelectDestActivity) getActivity()).getAllSelectedLoc();
        }
        for (LocBean locBean : result) {
            if (allSelectLoc != null && allSelectLoc.contains(locBean)) {
                locBean.isAdded = true;
            }
            if (Character.isDigit(locBean.zhName.charAt(0))) {
                locBean.header = "#";
            } else {
                if (TextUtils.isEmpty(locBean.pinyin)) {
                    locBean.header = HanziToPinyin.getInstance().get(locBean.zhName.substring(0, 1)).get(0).target.substring(
                            0, 1).toUpperCase();
                } else {
                    locBean.header = locBean.pinyin.substring(0, 1).toUpperCase();
                }

                char header = locBean.header.toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    locBean.header = "#";
                }
            }
            sections.add(locBean.header);
            if (locMap.get(locBean.header) != null) {
                locMap.get(locBean.header).add(locBean);
            } else {
                List<LocBean> locList = new ArrayList<LocBean>();
                locList.add(locBean);
                locMap.put(locBean.header, locList);
            }

        }
        incityList.clear();
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
        inCityAdapter.getDataList().clear();
        inCityAdapter.getDataList().addAll(incityList);
        inCityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestAdded(LocBean locBean) {
        for (InDestBean inDestBean : incityList) {
            for (LocBean kLocBean : inDestBean.locList) {
                if (locBean.id.equals(kLocBean.id)) {
                    kLocBean.isAdded = true;
                }
            }
        }
        inCityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestRemoved(LocBean locBean) {
        for (InDestBean inDestBean : incityList) {
            for (LocBean kLocBean : inDestBean.locList) {
                if (locBean.id.equals(kLocBean.id)) {
                    kLocBean.isAdded = false;
                }
            }
        }
        inCityAdapter.notifyDataSetChanged();
    }

    private class InCityAdapter2 extends BaseSectionAdapter {
        private List<InDestBean> mInDestBeanList;

        public InCityAdapter2(List<InDestBean> inDestBeanList) {
            mInDestBeanList = inDestBeanList;
        }


        @Override
        public int getContentItemViewType(int section, int position) {
            return 1;
        }

        @Override
        public int getHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 1;
        }

        @Override
        public int getHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public Object getItem(int section, int position) {
            return mInDestBeanList.get(section).locList.get(position);
        }

        @Override
        public long getItemId(int section, int position) {
            return section * 10000 + position;
        }

        @Override
        public String getSectionStr(int section) {
            return mInDestBeanList.get(section).section;
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.dest_in_item, null);
            }
            FlowLayout cityListFl = (FlowLayout) convertView.findViewById(R.id.fl_city_list);
            cityListFl.removeAllViews();
            InDestBean itemData = mInDestBeanList.get(section);
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
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_indest_section, null);
            }
            TextView sectionTv = (TextView) convertView.findViewById(R.id.tv_section);
            sectionTv.setText(mInDestBeanList.get(section).section);
            return convertView;
        }

        @Override
        public int getSectionCount() {
            return mInDestBeanList.size();
        }

        /**
         * 根据分类的首字母获取其第一次出现该首字母的位置
         */
        public int getPositionForIndex(String indexStr) {
            for (int i = 0; i < getSectionCount(); i++) {
                String sortStr = mInDestBeanList.get(i).section;
                if (indexStr.equals(sortStr)) {
                    return getGlobalPositionForHeader(i);
                }
            }
            return -1;
        }

        @Override
        public int getCountInSection(int section) {
            return 1;
        }

        @Override
        public boolean doesSectionHaveHeader(int section) {
            return true;
        }

        @Override
        public boolean shouldListHeaderFloat(int headerIndex) {
            return false;
        }
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

        /**
         * 根据分类的首字母获取其第一次出现该首字母的位置
         */
        public int getPositionForIndex(String indexStr) {
            for (int i = 0; i < sections.size(); i++) {
                String sortStr = sections.get(i);
                if (indexStr.equals(sortStr)) {
                    return i;
                }
            }
            return -1;
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
