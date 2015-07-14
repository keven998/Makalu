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
    private TitleHeaderBar bar;
    private LinearLayout add_btn;
    private TextView saveTypeName;
    private ArrayList<String> groupIndicator=new ArrayList<String>();
    private ArrayList<ArrayList<PoiDetailBean>> content=new ArrayList<ArrayList<PoiDetailBean>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strategy = getIntent().getParcelableExtra("strategy");
        setContentView(R.layout.poi_save_activity);
        bar = (TitleHeaderBar)findViewById(R.id.poi_save_titleBar);
        saveTypeName = (TextView)findViewById(R.id.save_type_name);
        add_btn = (LinearLayout)findViewById(R.id.save_btn);
        bar.getTitleTextView().setText(getIntent().getStringExtra("title")+"收藏");
        saveTypeName.setText("收藏"+getIntent().getStringExtra("title"));



        if(getIntent().getStringExtra("title").equals("购物")){
            resizeData(strategy.shopping);
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
        int width = getWindowManager().getDefaultDisplay().getWidth();
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            eListView.setIndicatorBounds(width-60, width-20);
        } else {
            eListView.setIndicatorBoundsRelative(width-60, width-20);
        }
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

    private void resizeData(ArrayList<PoiDetailBean> bean){
        if(bean.size()>0) {
            groupIndicator.clear();
            content.clear();
            ArrayList<PoiDetailBean> newBean=new ArrayList<PoiDetailBean>();
            String placeTitle = bean.get(0).locality.zhName;
            ArrayList<PoiDetailBean> childTitle = new ArrayList<PoiDetailBean>();
            childTitle.add(bean.get(0));


            for (int i = 1; i < bean.size(); i++) {
                if (placeTitle.equals(bean.get(i).locality.zhName)) {
                    childTitle.add(bean.get(i));
                } else {
                    newBean.add(bean.get(i));
                }
            }
            
            groupIndicator.add(placeTitle);
            content.add(childTitle);

            resizeData(newBean);
            //map.put(placeTitle, childTitle);
        }else {return;}
    }


    private class PoiSaveAdapter extends BaseExpandableListAdapter{

        private TextView groupTitle,groupSum;
        private ImageView childCellImage;
        private TextView childTitle,childLevel,childTime;
        private DisplayImageOptions poptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.empty_photo)
                .showImageOnLoading(R.drawable.messages_bg_useravatar)
                .showImageForEmptyUri(R.drawable.empty_photo)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        int pos=0;
        ArrayList<PoiDetailBean> value;


        @Override
        public int getGroupCount() {
            return groupIndicator.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return content.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return groupIndicator.get(i);
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
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if(view==null){
               view= View.inflate(mContext,R.layout.poi_save_group_cell,null);
            }
            groupTitle = (TextView) view.findViewById(R.id.tv_save_group_palce);
            groupSum = (TextView) view.findViewById(R.id.tv_save_group_num);


            groupTitle.setText(groupIndicator.get(i));
            groupSum.setText(content.get(i).size() + "个收藏");
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view==null){
                view= View.inflate(mContext,R.layout.poi_save_child_cell,null);
            }
            childCellImage = (ImageView) view.findViewById(R.id.iv_poi_save_img);
            childTitle = (TextView) view.findViewById(R.id.tv_poi_save_title);
            childLevel = (TextView) view.findViewById(R.id.tv_poi_save_level);
            childTime = (TextView) view.findViewById(R.id.tv_poi_save_time);

            value=content.get(i);
            /*if(pos!=i){
                value=map1.entrySet().iterator().next().getValue();
                pos=i;
            }*/

            if(value.get(i1).images.size()==0){
                ImageLoader.getInstance().displayImage(null, childCellImage, poptions);
            }else{
                ImageLoader.getInstance().displayImage(value.get(i1).images.get(0).url, childCellImage, poptions);
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
