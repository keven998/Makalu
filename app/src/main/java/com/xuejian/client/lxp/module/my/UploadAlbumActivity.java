package com.xuejian.client.lxp.module.my;

/**
 * Created by xuyongchen on 15/9/19.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
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
import com.xuejian.client.lxp.common.dialog.CustomLoadingDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadAlbumActivity extends Activity{


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
    private int currentPosition=-1;
    private static final int REFRESH_CURRENT_IMAGES=0x434;
    private static final int UPLOAD_ONE_IMAGE=0x435;
    private static final int REQUEST_CATEGORY=0x444;
    private LocalImageHelper.LocalFile addFile;
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPLOAD_ONE_IMAGE:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_album_activity);
        Intent intent = new Intent(UploadAlbumActivity.this,GalleryCatergoryActivity.class);
        startActivityForResult(intent,REQUEST_CATEGORY);
        addFile = new LocalImageHelper.LocalFile();
        addFile.setThumbnailUri("addfile");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //设置ImageLoader参数
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.pic_loadfail)
                .showImageOnFail(R.drawable.pic_loadfail)
                .showImageOnLoading(R.drawable.pic_loadfail)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        initViews();
    }


    /**
     * @Description： 初始化Views
     */
    private void initViews() {
        // TODO Auto-generated method stub
        post_album_title = (TitleHeaderBar)findViewById(R.id.post_album_title);
        post_album_title.getLeftTextView().setText("取消");
        post_album_title.getLeftTextView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        post_album_title.getRightTextView().setText("上传");
        post_album_title.getRightTextView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                post_album_title.getRightTextView().setEnabled(false);
            }
        });
        mContent = (EditText) findViewById(R.id.post_content);
        textRemain = (TextView) findViewById(R.id.post_text_remain);
        editContainer = findViewById(R.id.post_edit_container);
        image_to_upload = (GridView)findViewById(R.id.image_to_upload);
        if(LocalImageHelper.getInstance()!=null && LocalImageHelper.getInstance().getCheckedItems()!=null && LocalImageHelper.getInstance().getCheckedItems().size()>0){
            pictures.addAll(LocalImageHelper.getInstance().getCheckedItems());
        }
        if(!pictures.contains(addFile)){
            pictures.add(addFile);
        }

        adapter = new UpLoadImageAdapter(this,pictures);
        image_to_upload.setAdapter(adapter);
        image_to_upload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(pictures!=null){
                    if(pictures.get(position).getThumbnailUri()!=null && !pictures.get(position).getThumbnailUri().equals("addfile")){
                        Intent intent = new Intent(UploadAlbumActivity.this, GalleryDetailActivity.class);
                        intent.putExtra("preLook", true);
                        intent.putExtra("currentIndex",position);
                        startActivityForResult(intent, REFRESH_CURRENT_IMAGES);
                    }else{
                        Intent intent = new Intent(UploadAlbumActivity.this,GalleryCatergoryActivity.class);
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

    @Override
    public void onBackPressed() {

    }


    public void uploadImages(){

    }

    private void uploadAvatar(final File file) {

        OtherApi.getAvatarAlbumUploadToken(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                Log.e("uploadresult", result + "-----------------------");
                CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result, UploadTokenBean.class);
                if (tokenResult.code == 0) {
                    String token = tokenResult.result.uploadToken;
                    String key = tokenResult.result.key;
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(file, key, token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    Log.e("uploadresultInfo", info.isOK() + "----------------------");
                                    Message message = new Message();
                                    if (info.isOK()) {
                                        message.arg1=1;
                                    }
                                    message.what=UPLOAD_ONE_IMAGE;
                                    myHandler.sendMessage(message);
                                }
                            }, new UploadOptions(null, null, false,
                                    new UpProgressHandler() {
                                        public void progress(String key, double percent) {
                                            try {
                                                //progressDialog.setContent((int) (percent * 100) + "%");
                                            } catch (Exception e) {
                                            }
                                        }
                                    }, null));
                } else {
                    myHandler.sendEmptyMessage(UPLOAD_ONE_IMAGE);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(UploadAlbumActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }










    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

           // case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
               /* if (LocalImageHelper.getInstance().isResultOk()) {
                    LocalImageHelper.getInstance().setResultOk(false);
                    //获取选中的图片
                    List<LocalImageHelper.LocalFile> files = LocalImageHelper.getInstance().getCheckedItems();
                    for (int i = 0; i < files.size(); i++) {
                        LayoutParams params = new LayoutParams(size, size);
                        params.rightMargin = padding;
                        FilterImageView imageView = new FilterImageView(this);
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(files.get(i).getThumbnailUri(), new ImageViewAware(imageView), options,
                                null, null, files.get(i).getOrientation());
                        imageView.setOnClickListener(this);
                        pictures.add(files.get(i));
                        if (pictures.size() == 9) {
                            add.setVisibility(View.GONE);
                        } else {
                            add.setVisibility(View.VISIBLE);
                        }
                        picContainer.addView(imageView, picContainer.getChildCount() - 1);
                        picRemain.setText(pictures.size() + "/9");
                        LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                    }
                    //清空选中的图片
                    files.clear();
                    //设置当前选中的图片数量
                    LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                    //延迟滑动至最右边
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 50L);
                }
                //清空选中的图片
                LocalImageHelper.getInstance().getCheckedItems().clear();*/
                //break;
            if(requestCode==REFRESH_CURRENT_IMAGES) {
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
            }else if(requestCode == REQUEST_CATEGORY && resultCode==RESULT_OK){
                if(LocalImageHelper.getInstance()!=null && LocalImageHelper.getInstance().getCheckedItems()!=null){
                    LocalImageHelper.getInstance().getCheckedItems().clear();
                }
                finish();
            }else if(requestCode == REQUEST_CATEGORY && resultCode==20){
                Log.e("dfssd","sdfsfsafasfasfasflasfaslfjkaskf");
                if (LocalImageHelper.getInstance() != null) {
                    pictures.clear();
                    if (LocalImageHelper.getInstance().getCheckedItems() != null && LocalImageHelper.getInstance().getCheckedItems().size() > 0) {
                        pictures.addAll(LocalImageHelper.getInstance().getCheckedItems());
                    }
                    if (!pictures.contains(addFile)) {
                        pictures.add(addFile);
                    }
                    Log.e("picture.size",pictures.size()+"--------------------------");
                    adapter.notifyDataSetChanged();
                    image_to_upload.setAdapter(adapter);
                }
            }

    }

    class UpLoadImageAdapter extends BaseAdapter{
        private Context context;
        private List<LocalImageHelper.LocalFile> uploadImages;
        private LayoutInflater inflater;
        private ImageLoader imageLoader;
        public UpLoadImageAdapter(Context context,List<LocalImageHelper.LocalFile> uploadImages){
            this.context = context;
            this.uploadImages = uploadImages;
            this.inflater =LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return uploadImages.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.upload_grid_item,null);
            FrameLayout gallery_frame = (FrameLayout) convertView.findViewById(R.id.gallery_frame);
            ImageView imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
            ProgressBar image_progress = (ProgressBar)convertView.findViewById(R.id.image_progress);
            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - (3*LocalDisplay.dp2px(4)+LocalDisplay.dp2px(20))) / 4;
            AbsListView.LayoutParams lytp = new AbsListView.LayoutParams(width,width);
            gallery_frame.setLayoutParams(lytp);
            if(currentPosition ==position){
                image_progress.setVisibility(View.VISIBLE);
            }else{
                image_progress.setVisibility(View.GONE);
            }
            if(uploadImages.get(position).getThumbnailUri()!=null && uploadImages.get(position).getThumbnailUri().equals("addfile")){
                imgQueue.setImageResource(R.drawable.add_pictures);
            }else{
                ImageLoader.getInstance().displayImage(uploadImages.get(position).getThumbnailUri(), new ImageViewAware(imgQueue), options,
                        null, null, uploadImages.get(position).getOrientation());
            }

            return convertView;
        }


        @Override
        public Object getItem(int position) {
            return uploadImages.get(position);
        }
    }
}
