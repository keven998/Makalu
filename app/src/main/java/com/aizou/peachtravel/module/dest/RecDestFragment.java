package com.aizou.peachtravel.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.RecDestBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.common.widget.freeflow.core.FreeFlowContainer;
import com.aizou.peachtravel.common.widget.freeflow.core.FreeFlowItem;
import com.aizou.peachtravel.common.widget.freeflow.core.Section;
import com.aizou.peachtravel.common.widget.freeflow.core.SectionedAdapter;
import com.aizou.peachtravel.common.widget.freeflow.layouts.FreeFlowLayout;
import com.aizou.peachtravel.common.widget.freeflow.layouts.FreeFlowLayoutBase;
import com.aizou.peachtravel.common.widget.freeflow.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Rjm on 2014/10/9.
 */
public class RecDestFragment extends PeachBaseFragment {
    private FreeFlowContainer recDestContainer;
    private WantToLayout wantToLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rec_dest, null);
        recDestContainer = (FreeFlowContainer) rootView.findViewById(R.id.rec_dest_container);
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) rootView.findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.getTitleTextView().setText("桃子旅行");
        titleHeaderBar.getRightTextView().setText("新Memo");
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectDestActivity.class);
                startActivity(intent);
            }
        });
        titleHeaderBar.enableBackKey(false);
//        initData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void initData() {

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
                   bindView(destResult.result);
                }


            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    public void reloadData() {
        if (recDestContainer.getAdapter() == null || recDestContainer.getAdapter().getNumberOfSections() == 0) {
            initData();
        }
    }

    private void bindView(List<RecDestBean> recDestList){
        wantToLayout = new WantToLayout();
        recDestContainer.setEdgeEffectsEnabled(false);
        wantToLayout.setLayoutParams(new LayoutParams(LocalDisplay.SCREEN_WIDTH_PIXELS, LocalDisplay.dp2px(40)));
        recDestContainer.setLayout(wantToLayout);
        recDestContainer.setAdapter(new RecDestAdapter(getActivity(), recDestList));

    }


    private class RecDestAdapter implements SectionedAdapter{
        private Context context;
        private ArrayList<Section> sections = new ArrayList<Section>();
        public RecDestAdapter(Context context,List<RecDestBean> destBeanList){
            this.context = context;
            Section section;
            for(RecDestBean recDestBean:destBeanList){
                section = new Section();
                section.setHeaderData(recDestBean.title);
                for(RecDestBean.RecDestItem item : recDestBean.contents){
                    section.getData().add(item);
                }
                sections.add(section);

            }
        }

        @Override
        public long getItemId(int section, int position) {
            return section*1000+position;
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = View.inflate(context,R.layout.row_rec_dest_item, null);
            }
            RecDestBean.RecDestItem itemData = (RecDestBean.RecDestItem) sections.get(section).getDataAtIndex(position);
            TextView nameTv = (TextView) convertView.findViewById(R.id.tv_dest_name);
            TextView descTv = (TextView) convertView.findViewById(R.id.tv_dest_desc);
            ImageView imageIv = (ImageView) convertView.findViewById(R.id.iv_dest_pic);
            nameTv.setText(TextUtils.isEmpty(itemData.zhName) ? itemData.enName : itemData.zhName);
            descTv.setText(itemData.desc);
            ImageLoader.getInstance().displayImage(itemData.cover, imageIv, UILUtils.getRadiusOption());
            return convertView;
        }

        @Override
        public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = View.inflate(context,R.layout.row_rec_dest_header,null);
            }
            TextView titleTv = (TextView) convertView.findViewById(R.id.tv_rec_title);
            String title= (String) sections.get(section).getHeaderData();
            titleTv.setText(title);
            return convertView;
        }

        @Override
        public int getNumberOfSections() {
            return sections.size();
        }

        @Override
        public Section getSection(int index) {
            if (index < sections.size() && index >= 0)
                return sections.get(index);

            return null;
        }

        @Override
        public Class[] getViewTypes() {
            return new Class[]{LinearLayout.class,LinearLayout.class};
        }

        @Override
        public Class getViewType(FreeFlowItem proxy) {
            return LinearLayout.class;
        }

        @Override
        public boolean shouldDisplaySectionHeaders() {
            return true;
        }
    }

    private class WantToLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

        private static final String TAG = "WantToLayout";

        private int oneThirdItemSide;
        private int oneTwoItemSide;
        protected int headerWidth = -1;
        protected int headerHeight = -1;
        protected FreeFlowLayoutParams layoutParams;


        @Override
        public void setDimensions(int measuredWidth, int measuredHeight) {
            super.setDimensions(measuredWidth, measuredHeight);
            oneTwoItemSide  = measuredWidth / 2;
            oneThirdItemSide = measuredWidth / 3;

        }

        private HashMap<Object, FreeFlowItem> map=new HashMap<>();

        @Override
        public void prepareLayout() {
            Log.d(TAG, "prepare layout!!!");
            map.clear();
            int topStart = 0;
            int rowCnt = itemsAdapter.getNumberOfSections();
            for (int i = 0; i < rowCnt; i++) {
                Section s = itemsAdapter.getSection(i);
                if (itemsAdapter.shouldDisplaySectionHeaders()) {
                    FreeFlowItem header = new FreeFlowItem();
                    Rect hframe = new Rect();
                    header.itemSection = i;
                    header.itemIndex = -1;
                    header.isHeader = true;
                    hframe.left = 0;
                    hframe.top = topStart;
                    hframe.right = headerWidth;
                    hframe.bottom = hframe.top + headerHeight;
                    header.frame = hframe;
                    header.data = s.getHeaderData();
                    map.put(header.data, header);
                    topStart += headerHeight;
                }
                int curRowIndex = 0;
                if (i == 0) {
                    for (int j = 0; j < s.getDataCount(); j++) {
                        FreeFlowItem p = new FreeFlowItem();
                        p.isHeader = false;
                        p.itemIndex = j;
                        p.itemSection = i;
                        p.data = s.getDataAtIndex(j);
                        Rect r = new Rect();
                        int nextRowIndex = curRowIndex;
                        if (j == 0) {
                            r.left = 0;
                            r.top = topStart;
                            r.right = oneThirdItemSide * 2;
                            r.bottom = r.top + LocalDisplay.dp2px(150);
                            if (j == s.getDataCount() - 1) {
                                nextRowIndex++;
                            }
                        } else if (j == 1) {
                            r.left = oneThirdItemSide * 2;
                            r.right = oneThirdItemSide * 2 + oneThirdItemSide;
                            r.top = topStart;
                            r.bottom = r.top + LocalDisplay.dp2px(150);
                            nextRowIndex++;
                        } else {
                            int cols = 3;
                            int gridCount = j - 2;
                            r.left = (gridCount % cols) * oneThirdItemSide;
                            r.top = topStart;
                            r.right = r.left + oneThirdItemSide;
                            r.bottom = r.top + oneThirdItemSide;
                            if (gridCount % cols == cols - 1 || j == s.getDataCount() - 1) {
                                nextRowIndex++;
                            }
                        }
                        p.frame = r;
                        map.put(p.data, p);
                        if (curRowIndex != nextRowIndex) {
                            if (j == 0 || j == 1) {
                                topStart += LocalDisplay.dp2px(150);
                            } else {
                                topStart += oneThirdItemSide;
                            }
                            curRowIndex = nextRowIndex;
                        }

                    }
                } else {
                    int cols = 2;
                    for (int j = 0; j < s.getDataCount(); j++) {
                        int nextRowIndex = curRowIndex;
                        FreeFlowItem p = new FreeFlowItem();
                        p.isHeader = false;
                        p.itemIndex = j;
                        p.itemSection = i;
                        p.data = s.getDataAtIndex(j);
                        Rect r = new Rect();
                        r.left = (j % cols) * oneTwoItemSide;
                        r.top = topStart;
                        r.right = r.left + oneTwoItemSide;
                        r.bottom = r.top + oneTwoItemSide;
                        p.frame = r;
                        p.data = s.getDataAtIndex(j);
                        map.put(p.data, p);
                        if (j % cols == cols - 1) {
                            nextRowIndex++;
                        }
                        if (curRowIndex != nextRowIndex || j == s.getDataCount() - 1) {
                            topStart += oneTwoItemSide;
                            curRowIndex = nextRowIndex;
                        }

                    }

                }


            }

        }

        @Override
        public HashMap<Object, FreeFlowItem> getItemProxies(
                int viewPortLeft, int viewPortTop) {

            Rect viewport = new Rect(viewPortLeft,
                    viewPortTop,
                    viewPortLeft + width,
                    viewPortTop + height);

//            LogUtil.d("freeflow", "Viewport: " + viewPortLeft + ", " + viewPortTop + ", " + viewport.width() + "," + viewport.height());
            HashMap<Object, FreeFlowItem> ret = new HashMap<Object, FreeFlowItem>();

            Iterator<Map.Entry<Object, FreeFlowItem>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, FreeFlowItem> pairs = it.next();
                FreeFlowItem p = (FreeFlowItem) pairs.getValue();
                if (Rect.intersects(p.frame, viewport)) {
                    ret.put(pairs.getKey(), p);
                }
            }
            return ret;

        }

        @Override
        public FreeFlowItem getFreeFlowItemForItem(Object item) {
            Log.d(TAG, " returing item: " + map.get(item));
            return map.get(item);
        }

        @Override
        public int getContentWidth() {
            return 0;
        }

        @Override
        public int getContentHeight() {
            if (itemsAdapter == null || itemsAdapter.getNumberOfSections() <= 0) {
                return 0;
            }

            int sectionIndex = itemsAdapter.getNumberOfSections() - 1;
            Section s = itemsAdapter.getSection(sectionIndex);

            if (s.getDataCount() == 0)
                return 0;

            Object lastFrameData = s.getDataAtIndex(s.getDataCount() - 1);
            FreeFlowItem fd = map.get(lastFrameData);
            if (fd == null) {
                return 0;
            }
            return (fd.frame.top + fd.frame.height());
        }

        @Override
        public FreeFlowItem getItemAt(float x, float y) {
            return (FreeFlowItem) ViewUtils.getItemAt(map, (int) x, (int) y);
        }

        @Override
        public void setLayoutParams(FreeFlowLayoutParams params) {
            if (params.equals(this.layoutParams)) {
                return;
            }
            LayoutParams lp = (LayoutParams) params;
            this.headerWidth = lp.headerWidth;
            this.headerHeight = lp.headerHeight;
        }

        @Override
        public boolean verticalScrollEnabled() {
            return true;
        }

        @Override
        public boolean horizontalScrollEnabled() {
            return false;
        }


    }
    public  class LayoutParams extends FreeFlowLayout.FreeFlowLayoutParams {
        public int headerWidth = 0;
        public int headerHeight = 0;


        public LayoutParams(int headerWidth, int headerHeight) {
            this.headerWidth = headerWidth;
            this.headerHeight = headerHeight;
        }

    }

}
