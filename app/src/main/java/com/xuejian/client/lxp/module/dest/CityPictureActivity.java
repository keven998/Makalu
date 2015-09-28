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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
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
import com.xuejian.client.lxp.common.utils.SelectPicUtils;
import com.xuejian.client.lxp.module.my.UploadAlbumActivity;
import com.xuejian.client.lxp.module.my.UserAlbumInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/19.
 */
public class CityPictureActivity extends PeachBaseActivity {
    @InjectView(R.id.gv_city_pic)
    GridView mCityPicGv;
    @InjectView(R.id.tv_title_bar_edit)
    ImageView editPics;
    private PicAdapter picAdapter;
    private String id;
    private boolean isUserPics;
    private boolean isTalentAlbum;
    private ArrayList<ImageBean> userPics = new ArrayList<ImageBean>();
    private File tempImage;
    private ArrayList<String> pic_ids = new ArrayList<String>();
    private static final int REQUEST_IMAGE_CHOOSER = 0x322;
    private static final int REFRESH_IMAGE=0x321;
    private static final int REQUEST_BIGPIC=0x320;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        id = AccountManager.CurrentUserId;
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
        ButterKnife.inject(this);
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
            //titleView.setText(getIntent().getStringExtra("user_name"));
            titleView.setText("我的图集");
            editPics.setVisibility(View.VISIBLE);
            editPics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //tempImage = SelectPicUtils.getInstance().selectZoomPicFromLocal(CityPictureActivity.this);
                   /* Intent intent = new Intent(
                            "lvxingpai.ACTION_MULTIPLE_PICK");
                    CityPictureActivity.this.startActivityForResult(
                            intent, REQUEST_IMAGE_CHOOSER);*/

                    Intent intent = new Intent(CityPictureActivity.this, UploadAlbumActivity.class);
                    startActivityForResult(intent, REFRESH_IMAGE);
                    //showSelectPicDialog();

                }
            });
        } else {
            titleView.setText(getIntent().getStringExtra("title") + "图集");
        }
        mCityPicGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CityPictureActivity.this, UserAlbumInfoActivity.class);
                    intent.putExtra("currentIndex",position);
                    intent.putParcelableArrayListExtra("myPictures", userPics);
                    intent.putStringArrayListExtra("pic_ids", pic_ids);
                    intent.putExtra("userid", id);
                    startActivityForResult(intent, REQUEST_BIGPIC);
            }
        });

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
                            userPics.clear();
                            pic_ids.clear();
                            for (int i = 0; i < object.length(); i++) {
                                JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                                pic_ids.add(object.getJSONObject(i).getString("id"));
                                ImageBean ib = new ImageBean();
                                ib.url = imgArray.getJSONObject(0).getString("url");
                                ib.full=imgArray.getJSONObject(0).getString("full");
                                ib.thumb=imgArray.getJSONObject(0).getString("thumb");
                                ib.caption=imgArray.getJSONObject(0).getString("caption");

                                userPics.add(ib);
                            }
                            AccountManager.getInstance().getLoginAccountInfo().setAlbumCnt(object.length());
                            picAdapter = new PicAdapter(userPics);
                            mCityPicGv.setAdapter(picAdapter);
                            ArrayList<ImageBean> newUserPics = new ArrayList<ImageBean>();
                            /* if (isUserPics) {
                                newUserPics.add(new ImageBean());
                            }*/
                            for (int k = 0; k < userPics.size(); k++) {
                                newUserPics.add(userPics.get(k));
                            }

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
        picAdapter = null;
        mCityPicGv = null;
    }

    @Override
    public void finish() {
        super.finishWithNoAnim();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
            return imageBeanList.size();
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
            int width = (LocalDisplay.SCREEN_WIDTH_PIXELS - (5*LocalDisplay.dp2px(4))) / 4;
            AbsListView.LayoutParams lytp = new AbsListView.LayoutParams(width,width);

            FrameLayout cell_fl = (FrameLayout) view.findViewById(R.id.all_pics_cell_fl);
            ImageView cell_pic = (ImageView) view.findViewById(R.id.all_pics_cell_id);
            ImageView del_cell_pic = (ImageView) view.findViewById(R.id.all_pics_cell_del);

            cell_fl.setLayoutParams(lytp);
            cell_pic.setBackgroundResource(R.drawable.frame_cell_image_frame);
            cell_pic.setImageDrawable(null);
            ImageBean itemData = imageBeanList.get(position);
            imageLoader.displayImage(itemData.url, cell_pic, picOptions);
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }else if(requestCode==REQUEST_BIGPIC && resultCode==RESULT_OK){
            ArrayList<ImageBean> tempDatas = data.getParcelableArrayListExtra("myPictures");
            ArrayList<String> tempId = data.getStringArrayListExtra("pic_ids");
            if(tempDatas!=null){
                userPics.clear();
                if(tempDatas.size()>0){
                    userPics.addAll(tempDatas);
                }
                picAdapter.notifyDataSetChanged();
                mCityPicGv.setAdapter(picAdapter);
            }

            if(tempId!=null){
                pic_ids.clear();
                pic_ids.addAll(tempId);
            }
        }else if(requestCode==REFRESH_IMAGE && resultCode==RESULT_OK){
            initData(id);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
