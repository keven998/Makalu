package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aizou.core.constant.LayoutValue;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.AssetUtils;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.FixedGridView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.CityBean;
import com.aizou.peachtravel.bean.InCityBean;
import com.aizou.peachtravel.bean.OutCountryBean;
import com.aizou.peachtravel.bean.RecDestBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.ExpandGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/10/9.
 */
public class RecDestFragment extends PeachBaseFragment {
    private ListView recDestLv;
    private ListViewDataAdapter<RecDestBean> recClassifyAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rec_dest, null);
        recDestLv = (ListView) rootView.findViewById(R.id.lv_rec_dest);
        initData();
        return rootView;
    }

    private void initData() {

         recClassifyAdapter = new ListViewDataAdapter<RecDestBean>(new ViewHolderCreator<RecDestBean>() {
            @Override
            public ViewHolderBase<RecDestBean> createViewHolder() {
                return new RecClassifyViewHolder();

            }
        });
        recDestLv.setAdapter(recClassifyAdapter);
        getRecDestData();
//        String dataJson= AssetUtils.getFromAssets(getActivity(),"recdest.json");
//        Gson gson = new Gson();
//        Type listType = new TypeToken<ArrayList<RecDestBean>>() {
//        }.getType();
//        ArrayList<RecDestBean> dataList =  gson.fromJson(dataJson, listType);
//        recClassifyAdapter.getDataList().addAll(dataList);
//        recClassifyAdapter.notifyDataSetChanged();
    }

    private void getRecDestData(){
        TravelApi.getRecDest(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<RecDestBean> destResult = CommonJson4List.fromJson(result,RecDestBean.class);
                if(destResult.code==0){
                    recClassifyAdapter.getDataList().clear();
                    recClassifyAdapter.getDataList().addAll(destResult.result);
                    recClassifyAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }


    private class RecClassifyViewHolder extends ViewHolderBase<RecDestBean> {
        private TextView nameTv;
        private FixedGridView cityListGv;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.row_rec_dest, null);
            nameTv = (TextView) contentView.findViewById(R.id.tv_rec_title);
            cityListGv = (FixedGridView) contentView.findViewById(R.id.gv_rec_dest);

            return contentView;
        }

        @Override
        public void showData(int position, RecDestBean itemData) {
            nameTv.setText(itemData.type.name);
            final ListViewDataAdapter<RecDestBean.RecDestItem> adapter = new ListViewDataAdapter<RecDestBean.RecDestItem>(new ViewHolderCreator<RecDestBean.RecDestItem>() {
                @Override
                public ViewHolderBase<RecDestBean.RecDestItem> createViewHolder() {
                    return new RecDestViewHolder();
                }
            });
            cityListGv.setAdapter(adapter);
            cityListGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),CityDetailActivity.class);
                    intent.putExtra("id",adapter.getItem(position).id);
                    startActivity(intent);
                }
            });
            adapter.getDataList().addAll(itemData.localities);
            adapter.notifyDataSetChanged();

        }
    }

    private class RecDestViewHolder extends ViewHolderBase<RecDestBean.RecDestItem> {
        private TextView nameTv,descTv;
        private ImageView imageIv;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.row_rec_dest_item, null);
            nameTv = (TextView) contentView.findViewById(R.id.tv_dest_name);
            descTv = (TextView) contentView.findViewById(R.id.tv_dest_desc);
            imageIv = (ImageView) contentView.findViewById(R.id.iv_dest_pic);
            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS-LocalDisplay.dp2px(28))/2;
            int height = width * 3 / 4;
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    width, height);
            imageIv.setLayoutParams(lp);
            return contentView;
        }

        @Override
        public void showData(int position, RecDestBean.RecDestItem itemData) {
            nameTv.setText(TextUtils.isEmpty(itemData.zhName) ? itemData.enName : itemData.zhName);
            descTv.setText(itemData.desc);
            ImageLoader.getInstance().displayImage(itemData.cover, imageIv, UILUtils.getDefaultOption());

        }
    }



}
