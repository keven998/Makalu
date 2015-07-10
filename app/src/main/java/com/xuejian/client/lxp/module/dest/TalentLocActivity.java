package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lxp_dqm07 on 2015/7/8.
 */
public class TalentLocActivity extends PeachBaseActivity implements AbsListView.OnScrollListener {

    @InjectView(R.id.talent_loc_list)
    ListView listView;
    @InjectView(R.id.talent_loc_title_bar)
    TitleHeaderBar titleHeaderBar;
    private TalentLocAdapter adapter;
    private ArrayList<Integer> headerPos = new ArrayList<Integer>();
    int lastPos=0;
    String[] delta={"亚洲","欧洲","美洲","非洲","大洋洲"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talentloc);
        ButterKnife.inject(this);
        titleHeaderBar.getTitleTextView().setText("");
        titleHeaderBar.enableBackKey(true);
        adapter=new TalentLocAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        getHeaderPos();
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

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        LogUtil.d("i="+i);
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        int pos=listView.getFirstVisiblePosition();
        //lastPos = pos;
        for(int j=0;j<headerPos.size();j++){
            if(j<headerPos.size()-1){
                if(pos>headerPos.get(j)&&pos<headerPos.get(j+1)) {
                    if(!titleHeaderBar.getTitleTextView().getText().equals(delta[j])) {
                        titleHeaderBar.getTitleTextView().setText(delta[j]);
                        if(pos>lastPos) {
                            titleHeaderBar.getTitleTextView().startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom));
                        }else{
                            titleHeaderBar.getTitleTextView().startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
                        }
                        lastPos=pos;
                    }
                }
            }else{
                if(pos>headerPos.get(j)){
                    if(!titleHeaderBar.getTitleTextView().getText().equals(delta[j])) {
                        titleHeaderBar.getTitleTextView().setText(delta[j]);
                        titleHeaderBar.getTitleTextView().startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom));
                        lastPos=pos;
                    }
                }
            }
        }
    }

    public void getHeaderPos(){
        int pos=0;
        headerPos.add(pos);
        for(int i=0;i<adapter.getSectionCount()-1;i++) {
            pos += adapter.getCountInSection(i) + 1;
            headerPos.add(pos);
        }
    }

    public class TalentLocAdapter extends BaseSectionAdapter {

        private ImageView bgImage;
        private TextView numSum;
        private TextView loc;
        private TextView header;
        private DisplayImageOptions poptions = UILUtils.getDefaultOption();

        @Override
        public int getContentItemViewType(int section, int position) {
            return 0;
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
            return null;
        }

        @Override
        public long getItemId(int section, int position) {
            return getGlobalPositionForItem(section,position);
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=View.inflate(mContext,R.layout.talent_loc_cell_content,null);
            }
            bgImage = (ImageView) convertView.findViewById(R.id.talent_loc_img);
            numSum = (TextView) convertView.findViewById(R.id.talent_loc_num);
            loc = (TextView) convertView.findViewById(R.id.talent_loc_city);

            ImageLoader.getInstance().displayImage("http://images.taozilvxing.com/06ba9e1897fe8a2da0114ea7e6b0fcd8?imageView2/2/w/960", bgImage, poptions);
            numSum.setText("99人");
            loc.setText("~派派.美国.达人~");

            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=View.inflate(mContext,R.layout.talent_loc_cell_head,null);
            }
            header = (TextView) convertView.findViewById(R.id.talent_loc_head);
            header.setText(delta[section]);
            return convertView;
        }

        @Override
        public int getSectionCount() {
            return delta.length;
        }

        @Override
        public int getCountInSection(int section) {
            return (section+1)*2;
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
}
