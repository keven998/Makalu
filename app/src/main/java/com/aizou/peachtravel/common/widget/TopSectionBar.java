package com.aizou.peachtravel.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.module.MainActivity;
import com.aizou.peachtravel.module.toolbox.im.adapter.ContactAdapter;

/**
 * Created by Rjm on 2014/10/29.
 */
public class TopSectionBar extends Gallery {
    private ListView mListView;
    private SectionAdapter mSectionAdapter;
    private SectionIndexer indexer;
    private boolean isGalleryFocus;
    private int curIndex;
    private ViewGroup mTempView;
    private int lytNormalSize;
    private int lytSelectSize;
    private float textNormalSize;
    private float textSelectSize;

    public TopSectionBar(Context context) {
        super(context);
    }

    public TopSectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = getResources();
        lytNormalSize = res.getDimensionPixelSize(R.dimen.alpha_indexing_size_normal);
        lytSelectSize = res.getDimensionPixelSize(R.dimen.alpha_indexing_size_selected);
        textNormalSize = res.getDimensionPixelSize(R.dimen.alpha_indexing_text_size_normal);
        textSelectSize = res.getDimensionPixelSize(R.dimen.alpha_indexing_text_size_selected);
    }

    public void setListView(ListView listView) {
        mListView = listView;
        indexer = (SectionIndexer) mListView.getAdapter();
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

    public void notifyDataSetChanged(){
        if(mSectionAdapter!=null)
        mSectionAdapter.notifyDataSetChanged();
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
            if(curIndex!=indexer.getSectionForPosition(pos)){
                curIndex=indexer.getSectionForPosition(pos);
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
//            if(!isGalleryFocus) {
//                return;
//            }
            ViewGroup ctView = (ViewGroup)view;
            if (mTempView != null && mTempView != ctView) {
                AutoResizeTextView textView = new AutoResizeTextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(1);
                textView.setTextSize(textNormalSize);
                textView.setEnableSizeCache(false);
                textView.setLayoutParams(new LayoutParams(lytNormalSize, lytNormalSize));
                textView.setBackgroundResource(R.drawable.alpha_index_selector);
                textView.setTextColor(Color.WHITE);
                textView.setText(((AutoResizeTextView)mTempView.getChildAt(0)).getText());
                mTempView.removeAllViews();
                mTempView.addView(textView);
            }
            mTempView = ctView;
            AutoResizeTextView textView = new AutoResizeTextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setMaxLines(1);
            textView.setTextSize(textSelectSize);
            textView.setEnableSizeCache(false);
            textView.setLayoutParams(new LayoutParams(lytSelectSize, lytSelectSize));
            textView.setBackgroundResource(R.drawable.alpha_index_selector);
            textView.setTextColor(Color.WHITE);
            textView.setText(((AutoResizeTextView) ctView.getChildAt(0)).getText());
            textView.setChecked(true);
            ctView.removeAllViews();
            ctView.addView(textView);

            if(mListView==null) {
                return;
            }
            int i=position+1;
            if (i <= indexer.getSections().length) {
                mListView.setSelection(indexer.getPositionForSection(i));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class SectionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return indexer.getSections().length + 10;
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
            ViewGroup sectionTv = (ViewGroup)convertView;
            AutoResizeTextView textView;
            if (sectionTv == null) {
                convertView = View.inflate(getContext(), R.layout.item_section, null);
                textView = new AutoResizeTextView(getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(1);
                textView.setBackgroundResource(R.drawable.alpha_index_selector);
                textView.setLayoutParams(new LayoutParams(lytNormalSize, lytNormalSize));
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(textNormalSize);
                textView.setEnableSizeCache(false);
                ((ViewGroup) convertView).addView(textView);
            } else {
                textView = (AutoResizeTextView)sectionTv.getChildAt(0);
            }

            textView.setText("A");

            return convertView;
        }
    }


}
