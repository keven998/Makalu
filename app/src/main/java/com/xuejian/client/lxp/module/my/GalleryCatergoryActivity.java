package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuyongchen on 15/9/18.
 */

/**
 * Created by linjizong on 15/6/12.
 * 本地相册
 */
public class GalleryCatergoryActivity extends Activity {
    ListView listView;
    LocalImageHelper helper;
    List<String> folderNames;
    private boolean isDisdroy=false;
    private TitleHeaderBar gallery_title;
    private static final int RQUEST_GALLERY_IMAGES=0x555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_album);
        Intent intent = new Intent(GalleryCatergoryActivity.this,CustomGalleryActivity.class);
        startActivityForResult(intent, RQUEST_GALLERY_IMAGES);
        listView = (ListView) findViewById(R.id.local_album_list);
        gallery_title = (TitleHeaderBar)findViewById(R.id.gallery_catergory_title);
        gallery_title.setTitleText("相册选择");
        gallery_title.getLeftTextView().setText("图片上传");
        gallery_title.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaforeBack();
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
        LocalImageHelper.init(BaseApplication.getContext());
        helper = LocalImageHelper.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
                LocalImageHelper.getInstance().initImage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //初始化完毕后，显示文件夹列表
                        if (!isDisdroy) {
                            initAdapter();
                        }
                    }
                });
            }
        }).start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(GalleryCatergoryActivity.this, CustomGalleryActivity.class);
                intent.putExtra("folderName", folderNames.get(i));
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        beaforeBack();
        super.onBackPressed();
    }

    public void beaforeBack(){
        setResult(20);
        finish();
    }

    public void initAdapter() {
        listView.setAdapter(new FolderAdapter(this, helper.getFolderMap()));
    }


    /**
     * 读取图片的旋转的角度，还是三星的问题，需要根据图片的旋转角度正确显示
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RQUEST_GALLERY_IMAGES && resultCode==RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }else if(requestCode==RQUEST_GALLERY_IMAGES && resultCode==20){
            setResult(20);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class FolderAdapter extends BaseAdapter {
        Map<String, List<LocalImageHelper.LocalFile>> folders;
        Context context;
        DisplayImageOptions options;

        FolderAdapter(Context context, Map<String, List<LocalImageHelper.LocalFile>> folders) {
            this.folders = folders;
            this.context = context;
            folderNames = new ArrayList<>();

            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(false)
                    .showImageForEmptyUri(R.drawable.ic_default_picture)
                    .showImageOnFail(R.drawable.ic_default_picture)
                    .showImageOnLoading(R.drawable.ic_default_picture)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .setImageSize(new ImageSize(LocalDisplay.SCREEN_WIDTH_PIXELS/4, 0))
                    .displayer(new SimpleBitmapDisplayer()).build();

            Iterator iter = folders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                folderNames.add(key);
            }
            //根据文件夹内的图片数量降序显示
            Collections.sort(folderNames, new Comparator<String>() {
                public int compare(String arg0, String arg1) {
                    Integer num1 = helper.getFolder(arg0).size();
                    Integer num2 = helper.getFolder(arg1).size();
                    return num2.compareTo(num1);
                }
            });
        }

        @Override
        public int getCount() {
            return folders.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_albumfolder, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String name = folderNames.get(i);
            List<LocalImageHelper.LocalFile> files = folders.get(name);
            viewHolder.textView.setText(name + "(" + files.size() + ")");
            if (files.size() > 0) {
                ImageLoader.getInstance().displayImage(files.get(0).getThumbnailUri(), new ImageViewAware(viewHolder.imageView), options,
                        null, null, files.get(0).getOrientation());
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    @Override
    protected void onDestroy() {
        isDisdroy=true;
        super.onDestroy();
    }
}

