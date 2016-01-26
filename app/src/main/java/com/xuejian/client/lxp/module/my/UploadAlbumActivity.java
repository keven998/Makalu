package com.xuejian.client.lxp.module.my;

/**
 * Created by xuyongchen on 15/9/19.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.UploadTokenBean;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UploadAlbumActivity extends Activity {


    private EditText mContent;//动态内容编辑框
    private InputMethodManager imm;//软键盘管理
    private TextView textRemain;//字数提示
    private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();//图片路径数组
    View editContainer;//动态编辑部分


    //显示大图的viewpager 集成到了Actvity中 下面是和viewpager相关的控件
    int size;//小图大小
    int padding;//小图间距
    DisplayImageOptions options;
    private TitleHeaderBar post_album_title;
    private GridView image_to_upload;
    private UpLoadImageAdapter adapter;
    private static final int REFRESH_CURRENT_IMAGES = 0x434;
    private static final int UPLOAD_ONE_IMAGE = 0x435;
    private static final int REQUEST_CATEGORY = 0x444;
    private static final int REFRESHADAPTER = 0x445;
    private LocalImageHelper.LocalFile addFile;
    private int currentUpload = 0;
    private String info;

    private static class MyHandler extends Handler {

        private final WeakReference<UploadAlbumActivity> mActivity;

        public MyHandler(UploadAlbumActivity activity) {
            mActivity = new WeakReference<UploadAlbumActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            UploadAlbumActivity activity = mActivity.get();

            if (activity != null) {
                switch (msg.what) {
                    case UPLOAD_ONE_IMAGE:
                        if (activity.currentUpload >= 0 && activity.currentUpload < activity.pictures.size() - 1) {
                            activity.adapter.notifyDataSetChanged();

                            if (!"addfile".equals(activity.pictures.get(activity.currentUpload).getThumbnailUri())) {
                                int position =activity.currentUpload;
                                activity.uploadAvatar(activity.pictures.get(activity.currentUpload), activity.info,position);
                            }
                        } else {
                            Toast.makeText(activity, "上传成功~", Toast.LENGTH_SHORT).show();
                            if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
                                LocalImageHelper.getInstance().getCheckedItems().clear();
                            }
                            activity.setResult(RESULT_OK);
                            activity.finish();
                        }
                        break;
                    case REFRESHADAPTER:
                        /*int position = msg.arg1;
                        if(activity.image_to_upload.getFirstVisiblePosition()>=position && position<=activity.image_to_upload.getLastVisiblePosition()){
                            activity.image_to_upload.getChildAt(position).findViewById(R.id.);
                        }*/
                        activity.adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        }
    }

    private MyHandler myHandler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_album_activity);
        boolean comment = getIntent().getBooleanExtra("comment",false);

        if (!comment) {
            Intent intent = new Intent(UploadAlbumActivity.this, GalleryCatergoryActivity.class);
            startActivityForResult(intent, REQUEST_CATEGORY);
        }

        addFile = new LocalImageHelper.LocalFile();
        addFile.setThumbnailUri("addfile");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //设置ImageLoader参数
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.ic_default_picture)
                .showImageOnFail(R.drawable.ic_default_picture)
                .showImageOnLoading(R.drawable.ic_default_picture)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        initViews();
    }


    /**
     * @Description： 初始化Views
     */
    private void initViews() {
        // TODO Auto-generated method stub
        post_album_title = (TitleHeaderBar) findViewById(R.id.post_album_title);
        post_album_title.getLeftTextView().setText("取消");
        post_album_title.getLeftTextView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeBack();
            }
        });
        post_album_title.getRightTextView().setText("上传");
        post_album_title.getRightTextView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                info = mContent.getText().toString().trim();
                if (!TextUtils.isEmpty(info)) {
                    post_album_title.getRightTextView().setEnabled(false);
                    for (int i = 0; i < pictures.size(); i++) {
                        if ("addfile".equals(pictures.get(i).getThumbnailUri())) {
                            pictures.get(i).setIsupLoading(false);
                        } else {
                            pictures.get(i).setIsupLoading(true);
                        }
                    }

                    if (currentUpload >= 0 && currentUpload < pictures.size()) {
                        if (!"addfile".equals(pictures.get(currentUpload).getThumbnailUri())) {
                            uploadAvatar(pictures.get(currentUpload), info,currentUpload);
                        }
                    }
                } else {
                    Toast.makeText(UploadAlbumActivity.this, "图片描述不能为空哦！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mContent = (EditText) findViewById(R.id.post_content);
        textRemain = (TextView) findViewById(R.id.post_text_remain);
        editContainer = findViewById(R.id.post_edit_container);
        image_to_upload = (GridView) findViewById(R.id.image_to_upload);
        if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null && LocalImageHelper.getInstance().getCheckedItems().size() > 0) {
            pictures.addAll(LocalImageHelper.getInstance().getCheckedItems());
        }
        if (!pictures.contains(addFile)) {
            pictures.add(addFile);
        }

        adapter = new UpLoadImageAdapter(this, pictures);
        image_to_upload.setAdapter(adapter);
        image_to_upload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pictures != null) {
                    if (pictures.get(position).getThumbnailUri() != null && !pictures.get(position).getThumbnailUri().equals("addfile")) {
                        Intent intent = new Intent(UploadAlbumActivity.this, GalleryDetailActivity.class);
                        intent.putExtra("preLook", true);
                        intent.putExtra("currentIndex", position);
                        startActivityForResult(intent, REFRESH_CURRENT_IMAGES);
                    } else {
                        Intent intent = new Intent(UploadAlbumActivity.this, GalleryCatergoryActivity.class);
                        startActivityForResult(intent, REQUEST_CATEGORY);
                    }
                }

            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable content) {
                textRemain.setText(content.toString().length() + "/150");
            }
        });
    }

    private void uploadAvatar(final LocalImageHelper.LocalFile localFile, final String info,final int myposition) {
        String filepath = null;
        if (localFile != null) {
            try {
                String[] project = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = UploadAlbumActivity.this.getContentResolver().query(Uri.parse(localFile.getOriginalUri()), project, null, null, null);
                if (cursor!=null)cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filepath = cursor.getString(columnIndex);
                cursor.close();
            } catch (Exception ex) {

            }

            if (filepath == null) {
                filepath = "";
            }
            final File file = new File(filepath);
            if (file != null) {
                OtherApi.getAvatarAlbumUploadToken(new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result, UploadTokenBean.class);
                        if (tokenResult.code == 0) {
                            String token = tokenResult.result.uploadToken;
                            String key = tokenResult.result.key;
                            UploadManager uploadManager = new UploadManager();
                            uploadManager.put(file, key, token,
                                    new UpCompletionHandler() {
                                        @Override
                                        public void complete(String key, ResponseInfo info, JSONObject response) {
                                            localFile.setIsupLoading(false);
                                            Message message = new Message();
                                            currentUpload++;
                                            if (info.isOK()) {
                                                message.arg1 = 1;
                                            }
                                            message.what = UPLOAD_ONE_IMAGE;
                                            myHandler.sendMessage(message);
                                        }
                                    }, new UploadOptions(null, null, false,
                                            new UpProgressHandler() {
                                                public void progress(String key, double percent) {
                                                    try {
                                                        localFile.setCurrentProgress((int)(percent * 100));
                                                        myHandler.sendEmptyMessage(REFRESHADAPTER);
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }, null));
                        } else {
                            if (currentUpload == pictures.size() - 1) {
                                if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
                                    LocalImageHelper.getInstance().getCheckedItems().clear();
                                }
                                finish();
                                return;
                            }
                            currentUpload++;
                            localFile.setIsupLoading(false);
                            myHandler.sendEmptyMessage(UPLOAD_ONE_IMAGE);
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        if (currentUpload == pictures.size() - 1) {
                            if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
                                LocalImageHelper.getInstance().getCheckedItems().clear();
                            }
                            finish();
                            return;
                        }
                        currentUpload++;
                        localFile.setIsupLoading(false);
                        myHandler.sendEmptyMessage(UPLOAD_ONE_IMAGE);
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {
                        if (currentUpload == pictures.size() - 1) {
                            if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
                                LocalImageHelper.getInstance().getCheckedItems().clear();
                            }
                            finish();
                            return;
                        }
                        currentUpload++;
                        localFile.setIsupLoading(false);
                        adapter.notifyDataSetChanged();
                        myHandler.sendEmptyMessage(UPLOAD_ONE_IMAGE);
                    }
                }, info);
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        beforeBack();
    }

    public void beforeBack() {
        if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
            LocalImageHelper.getInstance().getCheckedItems().clear();
        }
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFRESH_CURRENT_IMAGES) {
            if (LocalImageHelper.getInstance() != null) {
                pictures.clear();
                if (LocalImageHelper.getInstance().getCheckedItems() != null && LocalImageHelper.getInstance().getCheckedItems().size() > 0) {
                    pictures.addAll(LocalImageHelper.getInstance().getCheckedItems());
                }
                if (!pictures.contains(addFile)) {
                    pictures.add(addFile);
                }
                image_to_upload.setAdapter(adapter);
            }
        } else if (requestCode == REQUEST_CATEGORY && resultCode == RESULT_OK) {
            if (LocalImageHelper.getInstance() != null && LocalImageHelper.getInstance().getCheckedItems() != null) {
                LocalImageHelper.getInstance().getCheckedItems().clear();
            }
            finish();
        } else if (requestCode == REQUEST_CATEGORY && resultCode == 20) {

            if (LocalImageHelper.getInstance() != null) {
                pictures.clear();
                if (LocalImageHelper.getInstance().getCheckedItems() != null && LocalImageHelper.getInstance().getCheckedItems().size() > 0) {
                    pictures.addAll(LocalImageHelper.getInstance().getCheckedItems());
                }
                if (!pictures.contains(addFile)) {
                    pictures.add(addFile);
                }
                adapter.notifyDataSetChanged();
                image_to_upload.setAdapter(adapter);
            }
        }

    }

    class UpLoadImageAdapter extends BaseAdapter {
        private Context context;
        private List<LocalImageHelper.LocalFile> uploadImages;
        private LayoutInflater inflater;
        private ImageLoader imageLoader;

        public UpLoadImageAdapter(Context context, List<LocalImageHelper.LocalFile> uploadImages) {
            this.context = context;
            this.uploadImages = uploadImages;
            this.inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return  Math.min(uploadImages.size(),10);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.upload_grid_item, null);
            FrameLayout gallery_frame = (FrameLayout) convertView.findViewById(R.id.gallery_frame);
            ImageView imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
            ProgressBar image_progress = (ProgressBar) convertView.findViewById(R.id.image_progress);
            FrameLayout overLay = (FrameLayout) convertView.findViewById(R.id.overLay);
            TextView progress_info = (TextView) convertView.findViewById(R.id.progress_info);
            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - (3 * LocalDisplay.dp2px(4) + LocalDisplay.dp2px(20))) / 4;
            AbsListView.LayoutParams lytp = new AbsListView.LayoutParams(width, width);
            gallery_frame.setLayoutParams(lytp);
            if (uploadImages.get(position).getThumbnailUri() != null && uploadImages.get(position).getThumbnailUri().equals("addfile")) {
                imgQueue.setImageResource(R.drawable.add_pictures);
            } else {
                ImageLoader.getInstance().displayImage(uploadImages.get(position).getThumbnailUri(), new ImageViewAware(imgQueue), options,
                        null, null, uploadImages.get(position).getOrientation());
            }
            if (uploadImages.get(position).isupLoading()) {

                if(overLay.getVisibility()!=View.VISIBLE){
                    overLay.setVisibility(View.VISIBLE);
                }
                progress_info.setText(uploadImages.get(position).getCurrentProgress() + "%");
            } else {
                overLay.setVisibility(View.GONE);
            }


            return convertView;
        }


        @Override
        public Object getItem(int position) {
            return uploadImages.get(position);
        }
    }
}
