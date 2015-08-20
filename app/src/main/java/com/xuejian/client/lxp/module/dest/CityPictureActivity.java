package com.xuejian.client.lxp.module.dest;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.HackyViewPager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.LocAlbum;
import com.xuejian.client.lxp.bean.UploadTokenBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.CustomLoadingDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ImageZoomAnimator2;
import com.xuejian.client.lxp.common.utils.SelectPicUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/19.
 */
public class CityPictureActivity extends PeachBaseActivity {
    @ViewInject(R.id.gv_city_pic)
    private GridView mCityPicGv;
    @ViewInject(R.id.zoom_container)
    private RelativeLayout zoomContainer;
    @ViewInject(R.id.vp_zoom_pic)
    private HackyViewPager zoomPicVp;
    @ViewInject(R.id.tv_title_bar_edit)
    private CheckedTextView editPics;
    private PicAdapter picAdapter;
    private ImageZoomAnimator2 zoomAnimator;
    private String id;
    private boolean isUserPics;
    private boolean isTalentAlbum;
    private ArrayList<ImageBean> userPics = new ArrayList<ImageBean>();
    private File tempImage;
    private ArrayList<String> pic_ids = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        id = getIntent().getStringExtra("id");
        initData(id);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_profile_album");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("page_profile_album");
        MobclickAgent.onPause(this);
    }

    private void initView() {
        setContentView(R.layout.activity_city_picture);
        ViewUtils.inject(this);
        isUserPics = getIntent().getBooleanExtra("isUserPics", false);
        isTalentAlbum = getIntent().getBooleanExtra("isTalentAlbum", false);

        final TextView ltv = (TextView) findViewById(R.id.tv_title_bar_left);
        ltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.tv_title_bar_title);
        if (isUserPics) {
            titleView.setText(getIntent().getStringExtra("user_name"));
            editPics.setVisibility(View.VISIBLE);
            editPics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean isChecked = !editPics.isChecked();
                    editPics.setChecked(isChecked);
                    if (isChecked) {
                        editPics.setText("完成");
                        ltv.setVisibility(View.GONE);
                    } else {
                        editPics.setText("编辑");
                        ltv.setVisibility(View.VISIBLE);
                    }
                    picAdapter.notifyDataSetChanged();
                }
            });
        } else {
            titleView.setText(getIntent().getStringExtra("title") + "图集");
        }


    }

    private void initData(String id) {

        if (isUserPics || isTalentAlbum) {
            UserApi.getUserPicAlbumn(String.valueOf(id), new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 0) {
                            JSONArray object = jsonObject.getJSONArray("result");
                            for (int i = 0; i < object.length(); i++) {
                                JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                                pic_ids.add(object.getJSONObject(i).getString("id"));
                                ImageBean ib = new ImageBean();
                                ib.url = imgArray.getJSONObject(0).getString("url");
                                userPics.add(ib);
                            }
                            AccountManager.getInstance().getLoginAccountInfo().setAlbumCnt(object.length());
                            picAdapter = new PicAdapter(userPics);
                            mCityPicGv.setAdapter(picAdapter);
                            ArrayList<ImageBean> newUserPics = new ArrayList<ImageBean>();
                            if (isUserPics) {
                                newUserPics.add(new ImageBean());
                            }
                            for (int k = 0; k < userPics.size(); k++) {
                                newUserPics.add(userPics.get(k));
                            }
                            zoomAnimator = new ImageZoomAnimator2(mContext, mCityPicGv, zoomContainer, newUserPics);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void doFailure(Exception error, String msg, String method) {
                    ToastUtil.getInstance(CityPictureActivity.this).showToast("好像没有网络额~");
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        } else {
            TravelApi.getCityGalley(id, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson<LocAlbum> imageReuslt = CommonJson.fromJson(result, LocAlbum.class);
                    if (imageReuslt.code == 0) {
                        picAdapter = new PicAdapter(imageReuslt.result.album);
                        mCityPicGv.setAdapter(picAdapter);
                        zoomAnimator = new ImageZoomAnimator2(mContext, mCityPicGv, zoomContainer, imageReuslt.result.album);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing()) {
                        ToastUtil.getInstance(CityPictureActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().stop();
        zoomContainer = null;
        picAdapter = null;
        zoomPicVp = null;
        mCityPicGv = null;
    }

    @Override
    public void finish() {
        super.finishWithNoAnim();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (zoomContainer.getVisibility() == View.VISIBLE) {
            zoomAnimator.transformOut(zoomPicVp.getCurrentItem());
        } else {
            super.onBackPressed();
        }
    }

    private class PicAdapter extends BaseAdapter {
        private List<ImageBean> imageBeanList;
        private DisplayImageOptions picOptions;
        private ImageLoader imageLoader;

        public PicAdapter(List<ImageBean> imageBeanList) {
            this.imageBeanList = imageBeanList;
            picOptions = UILUtils.getDefaultOption();
            imageLoader = ImageLoader.getInstance();
        }
        public void addAll(List<ImageBean> list){
            imageBeanList.clear();
            imageBeanList.addAll(list);
        }

        @Override
        public int getCount() {
            if (isUserPics) {
                return imageBeanList.size() + 1;
            } else {
                return imageBeanList.size();
            }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mContext, R.layout.all_pics_cell, null);
            AbsListView.LayoutParams lytp = new AbsListView.LayoutParams((LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(4)) / 3,
                    (LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(4)) / 3);

            FrameLayout cell_fl = (FrameLayout) view.findViewById(R.id.all_pics_cell_fl);
            ImageView cell_pic = (ImageView) view.findViewById(R.id.all_pics_cell_id);
            ImageView del_cell_pic = (ImageView) view.findViewById(R.id.all_pics_cell_del);

            cell_fl.setLayoutParams(lytp);
            cell_fl.setPadding(1, 1, 1, 1);
            cell_pic.setBackgroundResource(R.drawable.frame_cell_image_frame);
            cell_pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cell_pic.setImageDrawable(null);

            if (isUserPics) {
                if (position == 0) {
                    cell_pic.setBackgroundResource(R.drawable.picture_add);
                    //cell_pic.setImageDrawable(getResources().getDrawable(R.drawable.add_pictuer));
                    del_cell_pic.setVisibility(View.GONE);
                } else {
                    ImageBean itemData = imageBeanList.get(position - 1);
                    imageLoader.displayImage(itemData.url, cell_pic, picOptions);
                    if (editPics.isChecked()) {
                        del_cell_pic.setVisibility(View.VISIBLE);
                    } else {
                        del_cell_pic.setVisibility(View.GONE);
                    }
                }
            } else {
                ImageBean itemData = imageBeanList.get(position);
                imageLoader.displayImage(itemData.url, cell_pic, picOptions);
            }

            del_cell_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
                    dialog.setTitle("提示");
                    dialog.setMessage("确定删除该图片吗？");
                    dialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            delThisPic(pic_ids.get(position - 1), position - 1);

                        }
                    });
                    dialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUserPics) {
                        if (position == 0) {
                            showSelectPicDialog();
                        } else {
                            zoomAnimator.transformIn(position);
                        }
                    } else {
                        zoomAnimator.transformIn(position);
                    }
                }
            });
            view.setTag(position);
            return view;
        }
    }

    private void showSelectPicDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this,
                R.layout.dialog_select_pic, null);
        Button cameraBtn = (Button) contentView
                .findViewById(R.id.btn_camera);
        Button localBtn = (Button) contentView.findViewById(R.id.btn_local);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectPicFromCamera(CityPictureActivity.this);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectZoomPicFromLocal(CityPictureActivity.this);
                dialog.dismiss();

            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        // dialog.setView(contentView);
        // dialog.setContentView(contentView);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    public void delThisPic(String picId, final int pic_index) {
        if (!CommonUtils.isNetWorkConnected(mContext)) {
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }

        UserApi.delUserAlbumPic(String.valueOf(id), picId, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                pic_ids.remove(pic_index);
                userPics.remove(pic_index);
                picAdapter.notifyDataSetChanged();
                AccountManager.getInstance().getLoginAccountInfo().setAlbumCnt(userPics.size());
                ToastUtil.getInstance(mContext).showToast("删除成功");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });

    }

    private void uploadAvatar(final File file) {
        final CustomLoadingDialog progressDialog = DialogManager.getInstance().showLoadingDialog(mContext, "0%");
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
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    if (info.isOK()) {
                                        LogUtil.d(response.toString());
                                        userPics.clear();
                                        initData(id);
//                                        ImageBean ib = new ImageBean();
//                                        ib.url = Uri.fromFile(file).toString();
//                                        //上传图片，最新的图片置首位显示处理
//                                        ArrayList<ImageBean> newUserPics = new ArrayList<ImageBean>();
//                                        for (int i = 0; i < userPics.size(); i++) {
//                                            newUserPics.add(userPics.get(i));
//                                        }
//                                        userPics.clear();
//                                        for (int j = 0; j <= newUserPics.size(); j++) {
//                                            if (j == 0) {
//                                                userPics.add(ib);
//                                            } else {
//                                                userPics.add(newUserPics.get(j - 1));
//                                            }
//                                        }
//                                        AccountManager.getInstance().getLoginAccountInfo().setAlbumCnt(userPics.size());
//                                        //userPics.add(ib);
//                                        try {
//                                            //上传图片，最新的图片置首位显示处理
//                                            ArrayList<String> newPicId = new ArrayList<String>();
//                                            for (int i = 0; i < pic_ids.size(); i++) {
//                                                newPicId.add(pic_ids.get(i));
//                                            }
//                                            pic_ids.clear();
//                                            for (int j = 0; j <= newPicId.size(); j++) {
//                                                if (j == 0) {
//                                                    pic_ids.add(response.getString("id"));
//                                                } else {
//                                                    pic_ids.add(newPicId.get(j - 1));
//                                                }
//                                            }
//
//                                            //pic_ids.add(response.getString("id"));
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        picAdapter.addAll(userPics);
//                                        picAdapter.notifyDataSetChanged();
                                    }

                                }
                            }, new UploadOptions(null, null, false,
                                    new UpProgressHandler() {
                                        public void progress(String key, double percent) {
                                            progressDialog.setContent((int) (percent * 100) + "%");
                                            LogUtil.d("progress", percent + "");
                                        }
                                    }, null));
                } else {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(CityPictureActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SelectPicUtils.REQUEST_CODE_LOCAL_ZOOM) {
            Uri uri=null;
            String path;
            boolean flag=false;
            if(data!=null){
                if(data.getData()!=null){
                    uri=data.getData();
                    flag=true;
                }else{
                    if(data.getAction()!=null){
                        uri=Uri.parse(data.getAction());
                        flag=true;
                    }else{
                        flag=false;
                    }
                }
            }

            if(uri!=null && flag==true){
                Cursor cursor = getContentResolver().query(uri,new String[]{MediaStore.Images.Media.DATA},null,null,null);
                if(cursor!=null){
                    cursor.moveToFirst();
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                    File pathFile = new File(path);
                    pathFile.getParentFile().mkdirs();
                    uploadAvatar(new File(path));
                }
            }else{
                if (tempImage != null) {
                    uploadAvatar(tempImage);

                }
            }

        } else if (requestCode == SelectPicUtils.REQUEST_CODE_CAMERA) {
            if (tempImage != null) {
                uploadAvatar(tempImage);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
