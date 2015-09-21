package com.xuejian.client.lxp.module.my;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.MatrixImageView;
import com.xuejian.client.lxp.module.my.adapter.AlbumViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuyongchen on 15/9/19.
 */
public class GalleryDetailActivity extends Activity implements MatrixImageView.OnSingleTapListener,CompoundButton.OnCheckedChangeListener{
    AlbumViewPager viewpager;//大图显示pager

    private List<LocalImageHelper.LocalFile> currentFolder=null;
    private List<LocalImageHelper.LocalFile> checkedItems=null;
    private AlbumViewPager.LocalViewPagerAdapter adapter=null;
    private TextView mCountView;
    private CheckBox checkBox;
    private TextView headerfinish;
    private RelativeLayout headerBar;
    private FrameLayout galleryBottom;
    private int currentIndex=0;
    private String folder;
    private boolean preLook=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_detail_activity);
        if(LocalImageHelper.getInstance()==null || !LocalImageHelper.getInstance().isInited()){
            finish();
            return;
        }
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        folder = getIntent().getStringExtra("folder");
        preLook = getIntent().getBooleanExtra("preLook", false);
        viewpager =(AlbumViewPager)findViewById(R.id.albumviewpager);
        viewpager.setOnSingleTapListener(this);
        mCountView = (TextView)findViewById(R.id.header_bar_photo_count);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(this);
        headerBar = (RelativeLayout)findViewById(R.id.album_item_header_bar);
        headerfinish = (TextView)findViewById(R.id.gallery_header_finish);
        galleryBottom =(FrameLayout) findViewById(R.id.gallery_bottom_panel);
        checkedItems=LocalImageHelper.getInstance().getCheckedItems();
        ((View)findViewById(R.id.header_bar_photo_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaforeBack();
            }
        });
        if(!preLook){
            if(folder==null){
                ArrayList<String> folderNames = new ArrayList<String>();
                Iterator iter = LocalImageHelper.getInstance().getFolderMap().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    folderNames.add(key);
                }
                //根据文件夹内的图片数量降序显示
                if(folderNames.size()>0){
                    Collections.sort(folderNames, new Comparator<String>() {
                        public int compare(String arg0, String arg1) {
                            Integer num1 = LocalImageHelper.getInstance().getFolder(arg0).size();
                            Integer num2 = LocalImageHelper.getInstance().getFolder(arg1).size();
                            return num2.compareTo(num1);
                        }
                    });

                    currentFolder=LocalImageHelper.getInstance().getFolderMap().get(folderNames.get(0));
                }
            }else{
                currentFolder = LocalImageHelper.getInstance().getFolder(folder);
            }

        }else{
            currentFolder =checkedItems;
        }

        if(currentFolder==null){
            currentFolder=new ArrayList<LocalImageHelper.LocalFile>();
        }
        adapter = viewpager.new LocalViewPagerAdapter(currentFolder);
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(pageChangeListener);
        showViewPager(currentIndex);
        headerfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkedItems.size() == 0) {
                    if (currentIndex >= 0 && currentIndex < currentFolder.size()) {
                        if (!checkedItems.contains(currentFolder.get(currentIndex))) {
                            checkedItems.add(currentFolder.get(currentIndex));
                        }
                    }
                }

                setResult(20);
                finish();

            }
        });
    }

    private void showViewPager(int index) {
        viewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + currentFolder.size());
        if(index==0){
            checkBox.setTag(currentFolder.get(index));
            checkBox.setChecked(checkedItems.contains(currentFolder.get(index)));
        }
    }

    @Override
    public void onBackPressed() {
        beaforeBack();
        super.onBackPressed();

    }

    public void beaforeBack(){
        setResult(RESULT_OK);
        finish();
    }
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (viewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + viewpager.getAdapter().getCount();
                currentIndex=position;
                mCountView.setText(text);
                checkBox.setTag(currentFolder.get(position));
                checkBox.setChecked(checkedItems.contains(currentFolder.get(position)));
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onSingleTap() {
        if (headerBar.getVisibility() == View.VISIBLE) {
            AlphaAnimation animation=new AlphaAnimation(1, 0);
            animation.setDuration(300);
            headerBar.startAnimation(animation);
            headerBar.setVisibility(View.GONE);

            AlphaAnimation animation2=new AlphaAnimation(1, 0);
            animation2.setDuration(300);
            galleryBottom.startAnimation(animation2);
            galleryBottom.setVisibility(View.GONE);
        }
        else {
            headerBar.setVisibility(View.VISIBLE);
            AlphaAnimation animation=new AlphaAnimation(0, 1);
            animation.setDuration(300);
            headerBar.startAnimation(animation);

            galleryBottom.setVisibility(View.VISIBLE);
            AlphaAnimation animation2=new AlphaAnimation(0, 1);
            animation2.setDuration(300);
            galleryBottom.startAnimation(animation2);
        }
    }




    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (!b) {
            if (checkedItems.contains(compoundButton.getTag())) {
                checkedItems.remove(compoundButton.getTag());
            }
        } else {
            if (!checkedItems.contains(compoundButton.getTag())) {
                checkedItems.add((LocalImageHelper.LocalFile) compoundButton.getTag());
            }
        }
        if (checkedItems.size()+ LocalImageHelper.getInstance().getCurrentSize()> 0) {
            headerfinish.setText("确定(" +checkedItems.size() + ")");
        } else {
            headerfinish.setText("确定");
        }
    }
}
