package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.video.Utils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxp_dqm07 on 2015/7/11.
 */
public class PoiSaveActivity extends PeachBaseActivity {

    public final static int ADD_SHOPPING_REQUEST_CODE=103;
    public final static int ADD_REST_REQUEST_CODE=102;
    private ExpandableListView eListView;
    private PoiSaveAdapter adapter;
    private StrategyBean strategy;
    private ArrayList<LocBean> destinations;
    private TitleHeaderBar bar;
    private LinearLayout add_btn;
    private TextView saveTypeName;
    private ArrayList<ArrayList<PoiDetailBean>> content=new ArrayList<ArrayList<PoiDetailBean>>();
    private String type;
    private int requestType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strategy = getIntent().getParcelableExtra("strategy");
        destinations = getIntent().getParcelableArrayListExtra("destinations");
        setContentView(R.layout.poi_save_activity);
        bar = (TitleHeaderBar)findViewById(R.id.poi_save_titleBar);
        saveTypeName = (TextView)findViewById(R.id.save_type_name);
        add_btn = (LinearLayout)findViewById(R.id.save_btn);
        bar.getTitleTextView().setText(getIntent().getStringExtra("title")+"收藏");
        saveTypeName.setText("收藏"+getIntent().getStringExtra("title"));



        if(getIntent().getStringExtra("title").equals("购物")){
            resizeData(strategy.shopping);
            type=TravelApi.PeachType.SHOPPING;
            requestType=ADD_SHOPPING_REQUEST_CODE;
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PoiSaveActivity.this, PoiListActivity.class);
                    intent.putExtra("type", TravelApi.PeachType.SHOPPING);
                    intent.putExtra("canAdd", true);
                    intent.putExtra("strategy",strategy);
                    startActivityForResult(intent, ADD_SHOPPING_REQUEST_CODE);
                }
            });
            bar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("newStrategy", strategy.shopping);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

        }else {
            resizeData(strategy.restaurant);
            type=TravelApi.PeachType.RESTAURANTS;
            requestType=ADD_REST_REQUEST_CODE;
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PoiSaveActivity.this, PoiListActivity.class);
                    intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
                    intent.putExtra("canAdd", true);
                    intent.putExtra("strategy",strategy);
                    startActivityForResult(intent, ADD_REST_REQUEST_CODE);
                }
            });
            bar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("newStrategy", strategy.restaurant);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
        eListView = (ExpandableListView) findViewById(R.id.poi_save_list);
        eListView.setGroupIndicator(null);
        adapter = new PoiSaveAdapter();
        eListView.setAdapter(adapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_SHOPPING_REQUEST_CODE){
                strategy.shopping = data.getParcelableArrayListExtra("poiList");
                resizeData(strategy.shopping);
                adapter.notifyDataSetChanged();
            }else if(requestCode==ADD_REST_REQUEST_CODE){
                strategy.restaurant = data.getParcelableArrayListExtra("poiList");
                resizeData(strategy.restaurant);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void resizeData(ArrayList<PoiDetailBean> bean) {
        content.clear();
        for (int i = 0; i < destinations.size(); i++) {
            ArrayList<PoiDetailBean> childTitle = new ArrayList<PoiDetailBean>();
            for (int j = 0; j < bean.size(); j++) {
                if (destinations.get(i).zhName.equals(bean.get(j).locality.zhName)) {
                    childTitle.add(bean.get(j));
                }
            }
            content.add(childTitle);
        }
    }

    private class PoiSaveAdapter extends BaseExpandableListAdapter{

        private TextView groupTitle,groupSum,save_btn;
        private ImageView childCellImage;
        private TextView childTitle,childLevel,childTime;
        private DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.empty_photo)
                .showImageOnLoading(R.drawable.messages_bg_useravatar)
                .showImageForEmptyUri(R.drawable.empty_photo)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        ArrayList<PoiDetailBean> value;


        @Override
        public int getGroupCount() {
            return destinations.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return content.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return destinations.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return content.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            int sum=0;
            for(int j=0;j<i;j++){
                sum+=content.get(j).size();
            }
            return sum+i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
            if(view==null){
               view= View.inflate(mContext,R.layout.poi_save_group_cell,null);
            }
            groupTitle = (TextView) view.findViewById(R.id.tv_save_group_palce);
            groupSum = (TextView) view.findViewById(R.id.tv_save_group_num);
            save_btn = (TextView) view.findViewById(R.id.poi_save_btn);

            groupTitle.setText(destinations.get(i).zhName);
            groupSum.setText("("+content.get(i).size() + ")");

            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<LocBean> list=new ArrayList<LocBean>();
                    list.add(destinations.get(i));

                    Intent intent = new Intent(PoiSaveActivity.this, PoiListActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("canAdd", true);
                    intent.putExtra("strategy", strategy);
                    intent.putParcelableArrayListExtra("locList", list);
                    startActivityForResult(intent, requestType);
                }
            });

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view==null){
                view= View.inflate(mContext,R.layout.item_plan_day_detil,null);
            }
            childCellImage = (ImageView) view.findViewById(R.id.iv_poi_img);
            childTitle = (TextView) view.findViewById(R.id.tv_poi_title);
            childLevel = (TextView) view.findViewById(R.id.tv_poi_level);
            childTime = (TextView) view.findViewById(R.id.tv_poi_time);

            value=content.get(i);

            if(value.get(i1).images.size()==0){
                ImageLoader.getInstance().displayImage(null, childCellImage, options);
            }else{
                ImageLoader.getInstance().displayImage(value.get(i1).images.get(0).url, childCellImage, options);
            }
            childTitle.setText(value.get(i1).zhName);
            childLevel.setText(value.get(i1).getFormatRank());
            childTime.setText(value.get(i1).address);
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
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
    public void finish() {
        super.finish();
    }
}
