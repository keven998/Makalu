package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CityBean;
import com.aizou.peachtravel.bean.InCityBean;
import com.aizou.peachtravel.bean.OutCountryBean;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.common.widget.TopSectionBar;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutItem;
import com.aizou.peachtravel.common.widget.expandablelayout.ExpandableLayoutListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/10/9.
 */
public class SelectDestActivity extends PeachBaseActivity {
    private ListView mInListView;
    private ExpandableLayoutListView mOutCountryListView;
    private TopSectionBar mTopSectiionBar;
    private RadioGroup inOutRg;
    private LinearLayout citysLl;
    private TextView startTv;
    private List<InCityBean> incityList;
    private List<OutCountryBean> outCountryList;
    private List<CityBean> allAddCityList = new ArrayList<CityBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(mContext,R.layout.activity_select_dest, null);
        setContentView(rootView);
        mInListView = (ListView) rootView.findViewById(R.id.lv_in_city);
        mOutCountryListView = (ExpandableLayoutListView) rootView.findViewById(R.id.lv_out_country);
        mTopSectiionBar = (TopSectionBar) rootView.findViewById(R.id.section_bar);
        inOutRg = (RadioGroup) rootView.findViewById(R.id.in_out_rg);
        citysLl = (LinearLayout) rootView.findViewById(R.id.ll_citys);
        startTv = (TextView) rootView.findViewById(R.id.tv_start);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,StrategyActivity.class);
                startActivity(intent);
            }
        });
        inOutRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_in:
                        mTopSectiionBar.setVisibility(View.VISIBLE);
                        mInListView.setVisibility(View.VISIBLE);
                        mOutCountryListView.setVisibility(View.GONE);
                        break;
                    case R.id.rb_out:
                        mTopSectiionBar.setVisibility(View.GONE);
                        mInListView.setVisibility(View.GONE);
                        mOutCountryListView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
//        ImageLoader.getInstance().displayImage("http://d.hiphotos.baidu.com/super/whfpf%3D425%2C260%2C50/sign=70ecd7664c4a20a4314b6f87f66fac10/d01373f082025aaf97f5f1bff8edab64024f1afa.jpg",(ImageView)rootView.findViewById(R.id.iv_test));
        initData();
    }
    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.ic_search);
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    public String sections[]={"A","B","C","D"};
    private void initData() {

        incityList = new ArrayList<InCityBean>();
        outCountryList = new ArrayList<OutCountryBean>();
        for (int i = 0; i < 4; i++) {
            InCityBean inCity = new InCityBean();
            inCity.section = sections[i];
            ArrayList<CityBean> cityBeans = new ArrayList<CityBean>();
            inCity.cityList = cityBeans;
            for (int j = 0; j < 5; j++) {
                CityBean city = new CityBean();
                city.zhName = "云南";
                city.image = "http://d.hiphotos.baidu.com/super/whfpf%3D425%2C260%2C50/sign=70ecd7664c4a20a4314b6f87f66fac10/d01373f082025aaf97f5f1bff8edab64024f1afa.jpg";
                cityBeans.add(city);
            }
            incityList.add(inCity);
        }
        for(int i=0;i<5;i++){
            OutCountryBean outCountry = new OutCountryBean();
            outCountry.name ="韩国";
            outCountry.desc ="Korea";
            outCountry.image="http://hiphotos.baidu.com/lvpics/pic/item/1c950a7b02087bf45139aa41f2d3572c10dfcf45.jpg";
            ArrayList<CityBean> cityBeans = new ArrayList<CityBean>();
            outCountry.cityList = cityBeans;
            for (int j = 0; j < 15; j++) {
                CityBean city = new CityBean();
                city.zhName = "云南";
                city.image = "http://d.hiphotos.baidu.com/super/whfpf%3D425%2C260%2C50/sign=70ecd7664c4a20a4314b6f87f66fac10/d01373f082025aaf97f5f1bff8edab64024f1afa.jpg";
                cityBeans.add(city);
            }
            outCountryList.add(outCountry);
        }
        InCityAdapter inCityAdapter = new InCityAdapter(new ViewHolderCreator<InCityBean>() {
            @Override
            public ViewHolderBase<InCityBean> createViewHolder() {
                return new InCityViewHolder();
            }
        });
        ListViewDataAdapter<OutCountryBean> outCountryAdapter = new ListViewDataAdapter<OutCountryBean>(new ViewHolderCreator<OutCountryBean>() {
            @Override
            public ViewHolderBase<OutCountryBean> createViewHolder() {
                return new OutCountryViewHolder();
            }
        });
        mInListView.setAdapter(inCityAdapter);
        inCityAdapter.getDataList().addAll(incityList);
        inCityAdapter.notifyDataSetChanged();
        mTopSectiionBar.setListView(mInListView);
        mOutCountryListView.setAdapter(outCountryAdapter);
        outCountryAdapter.getDataList().addAll(outCountryList);
        outCountryAdapter.notifyDataSetChanged();
    }

    private class  InCityAdapter  extends  ListViewDataAdapter<InCityBean> implements SectionIndexer{
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

        public void initSections(){
            int count = getCount();
            positionOfSection = new SparseIntArray();
            sectionOfPosition = new SparseIntArray();
            sections = new ArrayList<String>();
            int section=0;
            for (int i = 0; i < count; i++) {
                String letter =getItem(i).section ;
                String beforeLetter ="";
                if(i>0){
                    beforeLetter = getItem(i-1).section;
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


    private class InCityViewHolder extends ViewHolderBase<InCityBean> {
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
        public void showData(int position, final InCityBean itemData) {
            sectionTv.setText(itemData.section);
            cityListFl.removeAllViews();
            for(final CityBean bean:itemData.cityList){
                View contentView = View.inflate(mContext,R.layout.dest_select_city,null);
                TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_city_name);
                cityNameTv.setText(bean.zhName);
                ImageView addIv = (ImageView) contentView.findViewById(R.id.iv_add);
                addIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
                        citysLl.addView(cityView);
                        allAddCityList.add(bean);
                        TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
                        ImageView removeIv = (ImageView) cityView.findViewById(R.id.iv_remove);
                        cityNameTv.setText(bean.zhName);
                        removeIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int index = allAddCityList.indexOf(bean);
                                citysLl.removeViewAt(index);
                                allAddCityList.remove(bean);

                            }
                        });
                    }
                });
                cityListFl.addView(contentView);
            }



        }
    }

    private class OutCountryViewHolder extends ViewHolderBase<OutCountryBean> {
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
        public void showData(int position, final OutCountryBean itemData) {
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
            nameTv.setText(itemData.name);
            SpannableString impress = new SpannableString("|"+itemData.desc);
            impress.setSpan(
                    new ForegroundColorSpan(getResources().getColor(
                            R.color.route_price_color)), 0, impress.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            impress.setSpan(new AbsoluteSizeSpan(LocalDisplay.dp2px(15)),  0, impress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.append(impress);
            ImageLoader.getInstance().displayImage(itemData.image, imageIv, UILUtils.getRadiusOption());
            cityListFl.removeAllViews();
            int i=0;
            for(final CityBean bean:itemData.cityList){
                View view = View.inflate(mContext,R.layout.dest_select_city,null);
                TextView cityNameTv = (TextView) view.findViewById(R.id.tv_city_name);
                cityNameTv.setText(bean.zhName);
                ImageView addIv = (ImageView) view.findViewById(R.id.iv_add);
                addIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View cityView = View.inflate(mContext, R.layout.dest_add_item, null);
                        citysLl.addView(cityView);
                        allAddCityList.add(bean);
                        TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
                        ImageView removeIv = (ImageView) cityView.findViewById(R.id.iv_remove);
                        cityNameTv.setText(bean.zhName);
                        removeIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int index = allAddCityList.indexOf(bean);
                                citysLl.removeViewAt(index);
                                allAddCityList.remove(bean);

                            }
                        });
                    }
                });

                cityListFl.addView(view);
                i++;
            }
            cityListFl.requestLayout();

        }
    }



}
