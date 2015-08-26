package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.GroupLocBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.TagView.Tag;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyongchen on 15/8/14.
 */
public class SearchSomeCityActivity extends Activity {
    @InjectView(R.id.search_city_text)
    EditText searchCityText;
    @InjectView(R.id.search_city_button)
    TextView searchCityButton;
    @InjectView(R.id.search_city_item)
    ListView searchCityItem;
    @InjectView(R.id.add_poi_panel)
    HorizontalScrollView hsView;
    @InjectView(R.id.poiadd_ll)
    LinearLayout hsViewLL;
    @InjectView(R.id.search_city_cancel)
    ImageView search_back;
    private String keyWords;
    private ArrayList<LocBean> searchCities=new ArrayList<LocBean>();
    private ArrayList<CountryBean>  countris = new ArrayList<CountryBean>();
    private ArrayList<GroupLocBean> groupLocBeans = new ArrayList<GroupLocBean>();
    private ListViewDataAdapter<LocBean> cityListAdapter;
    private List<Tag> cityTags = new ArrayList<Tag>();
    ArrayList<LocBean> choosedCities = new ArrayList<LocBean>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_some_city);
        ButterKnife.inject(this);
        keyWords = getIntent().getStringExtra("keyWords");
        if(keyWords==null){
            keyWords="";
        }
        searchCityText.setText(keyWords);
        searchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(searchCityText.getText())) {
                    return;
                } else {
                    searchCities(searchCityText.getText().toString().trim());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beforeBack();
            }
        });
        searchCityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_SEND) {
                    final String keywords = searchCityText.getText().toString();
                    if (!TextUtils.isEmpty(keywords)) {
                        searchCities(keywords);
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });

        searchCityItem.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(absListView.getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        cityListAdapter = new ListViewDataAdapter<LocBean>(new ViewHolderCreator<LocBean>() {
            @Override
            public ViewHolderBase<LocBean> createViewHolder() {
                return new CityViewHolder();
            }
        });
        initData();
        searchCityItem.setAdapter(cityListAdapter);
        searchCityItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final  LocBean locBean =cityListAdapter.getDataList().get(position);

                if(!locBean.isChecked){
                    locBean.isChecked=true;
                    choosedCities.add(locBean);
                    final View layoutview = View.inflate(SearchSomeCityActivity.this, R.layout.poi_bottom_cell_with_del, null);
                    FrameLayout del_fl = (FrameLayout) layoutview.findViewById(R.id.poi_del_fl);
                    TextView location = (TextView) layoutview.findViewById(R.id.names);
                    location.setText(locBean.zhName + "," + locBean.destCountry);
                    del_fl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hsViewLL.removeView(layoutview);
                            locBean.isChecked = false;
                            choosedCities.remove(locBean);
                            if(cityListAdapter.getDataList()==null || cityListAdapter.getDataList().size()==0){
                                hsView.setVisibility(View.GONE);
                            }else{
                                boolean flag = false;
                                for(int i=0;i<cityListAdapter.getDataList().size();i++){
                                    if(cityListAdapter.getDataList().get(i).isChecked){
                                        flag=true;
                                        break;
                                    }
                                }
                                if(flag){
                                    hsView.setVisibility(View.VISIBLE);
                                }else{
                                    hsView.setVisibility(View.GONE);
                                }

                            }
                            cityListAdapter.notifyDataSetChanged();
                        }
                    });
                    hsViewLL.addView(layoutview);

                    if(cityListAdapter.getDataList()==null || cityListAdapter.getDataList().size()==0){
                        hsView.setVisibility(View.GONE);
                    }else{
                        boolean flag = false;
                        for(int i=0;i<cityListAdapter.getDataList().size();i++){
                            if(cityListAdapter.getDataList().get(i).isChecked){
                                flag=true;
                                break;
                            }
                        }
                        if(flag){
                            hsView.setVisibility(View.VISIBLE);
                        }else{
                            hsView.setVisibility(View.GONE);
                        }

                    }
                }
                cityListAdapter.notifyDataSetChanged();
            }
        });

    }



    private class CityViewHolder extends ViewHolderBase<LocBean> {
        TextView city_result;
        ImageView des_selected_icon;
        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.city_choose_layout, null);
            city_result = (TextView)contentView.findViewById(R.id.city_result);
            des_selected_icon = (ImageView)contentView.findViewById(R.id.des_selected_icon);
            return contentView;
        }

        @Override
        public void showData(int position, final LocBean itemData) {
            city_result.setText(itemData.zhName+","+itemData.destCountry);
            if (!itemData.isChecked) {
                des_selected_icon.setImageResource(R.drawable.search_add);
            } else {
                des_selected_icon.setImageResource(R.drawable.search_chart);
            }

        }
    }


    private void searchCities(String keyWords){
        cityListAdapter.getDataList().clear();
        for(LocBean locBean : searchCities){
            if((locBean.destCountry !=null && locBean.destCountry.contains(keyWords) || locBean.zhName.contains(keyWords))){
                cityListAdapter.getDataList().add(locBean);
            }
        }
        cityListAdapter.notifyDataSetChanged();
    }

    private void initData(){
        cityListAdapter.getDataList().clear();
        String data = PreferenceUtils.getCacheData(SearchSomeCityActivity.this, "destination_outcountry");
        if (!TextUtils.isEmpty(data)){
            CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(data, CountryBean.class);
            if (countryListResult.code == 0) {
                if(countryListResult.result!=null && countryListResult.result.size()>0){
                    countris.addAll(countryListResult.result);
                    for (CountryBean  countryBean:countris){
                        for(LocBean locBean : countryBean.destinations){
                            locBean.destCountry = countryBean.zhName;
                            searchCities.add(locBean);
                            if(keyWords!=null && keyWords.trim().length()>0 && (locBean.zhName.contains(keyWords)|| locBean.destCountry.contains(keyWords))){
                                cityListAdapter.getDataList().add(locBean);
                            }
                        }
                    }
                }

            }
        }

        String localCities = PreferenceUtils.getCacheData(SearchSomeCityActivity.this, "destination_indest_group");
        if (!TextUtils.isEmpty(localCities)) {
            CommonJson4List<GroupLocBean> locListResult = CommonJson4List.fromJson(localCities, GroupLocBean.class);
            if (locListResult.code == 0) {
                if(locListResult.result!=null && locListResult.result.size()>0){
                    groupLocBeans.addAll(locListResult.result);
                    for (GroupLocBean  groupLocBean:groupLocBeans){
                        for(LocBean locBean : groupLocBean.destinations){
                            locBean.destCountry = "中国";
                            searchCities.add(locBean);
                            if(keyWords!=null && keyWords.trim().length()>0 && (locBean.zhName.contains(keyWords)|| locBean.destCountry.contains(keyWords))){
                                cityListAdapter.getDataList().add(locBean);
                            }
                        }
                    }
                }

            }
        }
        cityListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        beforeBack();
        super.onBackPressed();
    }

    public void beforeBack(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("choosedCities",choosedCities);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
