package com.xuejian.client.lxp.module.my;

/**
 * Created by xuyongchen on 15/9/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import com.aizou.core.base.BaseApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.CustomGalleryBean;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.my.adapter.GalleryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CustomGalleryActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    GridView gridGallery;
    Handler handler;
    GalleryAdapter adapter;
    TextView btnGalleryOk;
    TextView prey_check;
    private String photoPath;
    private final int REFRESH_CURRENT_ACTIVITY=35;
    private TitleHeaderBar gallery_title;
    private ArrayList<String> folderNames;
    LocalImageHelper helper;
    private String folderName=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.grid_gallery);
        folderNames = new ArrayList<String>();
        LocalImageHelper.init(BaseApplication.getContext());
        helper = LocalImageHelper.getInstance();
        folderName = getIntent().getStringExtra("folderName");
        gallery_title = (TitleHeaderBar)findViewById(R.id.gallery_title);
        if(folderName!=null){
            gallery_title.setTitleText(folderName);
        }else{
            gallery_title.setTitleText("手机相册");
        }

        gallery_title.getLeftTextView().setText("相册");
        gallery_title.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gallery_title.getRightTextView().setText("取消");
        gallery_title.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        init();
        if(null != savedInstanceState){
            if(null != savedInstanceState.getString("photoPath")){
                photoPath =savedInstanceState.getString("photoPath");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("photoPath",photoPath);
        super.onSaveInstanceState(outState);
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.grid_Gallery);
        btnGalleryOk = (TextView) findViewById(R.id.btnGalleryOk);
        prey_check = (TextView)findViewById(R.id.prey_check);
        adapter = new GalleryAdapter(CustomGalleryActivity.this,btnGalleryOk);

        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),true, true);
        gridGallery.setOnScrollListener(listener);

        findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(mItemMulClickListener);
        prey_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomGalleryActivity.this, GalleryDetailActivity.class);
                if (folderName != null) {
                    intent.putExtra("folder", folderName);
                }
                intent.putExtra("preLook", true);
                startActivityForResult(intent, REFRESH_CURRENT_ACTIVITY);
            }
        });

        btnGalleryOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(20);
                finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
                LocalImageHelper.getInstance().initImage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(folderName==null){
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
                                        Integer num1 = helper.getFolder(arg0).size();
                                        Integer num2 = helper.getFolder(arg1).size();
                                        return num2.compareTo(num1);
                                    }
                                });

                                adapter.addAll(LocalImageHelper.getInstance().getFolderMap().get(folderNames.get(0)));
                            }

                        }else{
                            adapter.addAll(LocalImageHelper.getInstance().getFolderMap().get(folderName));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        List<LocalImageHelper.LocalFile> checkedItems = LocalImageHelper.getInstance().getCheckedItems();
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
            btnGalleryOk.setText("确定(" + checkedItems.size() + ")");
            btnGalleryOk.setEnabled(true);
            prey_check.setText("预览(" + checkedItems.size() + ")");
            prey_check.setEnabled(true);
        } else {
            prey_check.setText("预览");
            prey_check.setEnabled(false);
            btnGalleryOk.setText("确定");
            btnGalleryOk.setEnabled(false);
        }
    }


    public void refreshCheckedImages(){
        List<LocalImageHelper.LocalFile> checkedItems = LocalImageHelper.getInstance().getCheckedItems();
        if (checkedItems.size()+ LocalImageHelper.getInstance().getCurrentSize()> 0) {
            btnGalleryOk.setText("确定(" + checkedItems.size() + ")");
            btnGalleryOk.setEnabled(true);
            prey_check.setText("预览(" + checkedItems.size() + ")");
            prey_check.setEnabled(true);
        } else {
            prey_check.setText("预览");
            prey_check.setEnabled(false);
            btnGalleryOk.setText("确定");
            btnGalleryOk.setEnabled(false);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode==REFRESH_CURRENT_ACTIVITY && resultCode==RESULT_OK){
            refreshCheckedImages();
             adapter.notifyDataSetChanged();
             gridGallery.setAdapter(adapter);
         }else if(requestCode==REFRESH_CURRENT_ACTIVITY && resultCode==20){
             setResult(20);
             finish();
         }
        super.onActivityResult(requestCode, resultCode, data);
    }


    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {

            Intent intent= new Intent(CustomGalleryActivity.this,GalleryDetailActivity.class);
            intent.putExtra("currentIndex",position);
            if(folderName!=null){
                intent.putExtra("folder",folderName);
            }
            startActivityForResult(intent, REFRESH_CURRENT_ACTIVITY);
        }
    };

    private ArrayList<CustomGalleryBean> getGalleryPhotos() {
        ArrayList<CustomGalleryBean> galleryList = new ArrayList<CustomGalleryBean>();
        try {
            final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    MediaStore.Images.Media.SIZE+">0", null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()) {
                    CustomGalleryBean item = new CustomGalleryBean();
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if(!imagecursor.getString(dataColumnIndex).contains("tataufo/history/")  && !imagecursor.getString(dataColumnIndex).contains("tataufo/selfinfo/") && !imagecursor.getString(dataColumnIndex).contains("tataufo/friends/")){
                        item.sdcardPath = imagecursor.getString(dataColumnIndex);
                        galleryList.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

}
