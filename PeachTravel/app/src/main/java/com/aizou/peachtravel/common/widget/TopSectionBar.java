package com.aizou.peachtravel.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.module.travel.im.adapter.ContactAdapter;

/**
 * Created by Rjm on 2014/10/29.
 */
public class TopSectionBar extends Gallery {
    private ListView mListView;
    private SectionAdapter mSectionAdapter;
    private ContactAdapter mContatctAdapter;
    private boolean isGalleryFocus;
    private int curIndex;

    public TopSectionBar(Context context) {
        super(context);
    }

    public TopSectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListView(ListView listView) {
        mListView = listView;
        mContatctAdapter = (ContactAdapter) mListView.getAdapter();
        mListView.setOnScrollListener(new SectionScrollListener());
        mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isGalleryFocus =false;
                return false;
            }
        });
        mSectionAdapter = new SectionAdapter();
        this.setAdapter(mSectionAdapter);
        this.setOnItemSelectedListener(new SectionOnItemSelectedListener());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isGalleryFocus = true;
                return false;
            }
        });
    }

    private class SectionScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(isGalleryFocus)
                return;
            int pos = view.getFirstVisiblePosition();
            if(curIndex!=mContatctAdapter.getSectionForPosition(pos)){
                curIndex=mContatctAdapter.getSectionForPosition(pos);
                int i=curIndex-1;
                if(i>=0){
                    setSelection(i);
                }

            }

        }
    }

    private class SectionOnItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(!isGalleryFocus)
                return;
            if(mListView==null)
                return;
            int i=position+1;
            if(i<=mContatctAdapter.getSectionList().size()){
                mListView.setSelection(mContatctAdapter.getPositionForSection(i));
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



    private class SectionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mContatctAdapter.getSectionList().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView sectionTv;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_section, null);
            }
            sectionTv = (TextView) convertView.findViewById(R.id.tv_section);
            sectionTv.setText(mContatctAdapter.getSectionList().get(position));
            return convertView;
        }
    }


}
