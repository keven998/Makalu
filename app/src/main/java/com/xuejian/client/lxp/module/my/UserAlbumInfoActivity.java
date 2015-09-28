package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.pagerIndicator.indicator.slidebar.ScrollBar;
import com.qiniu.android.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.MoreDialog;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.LocalImageHelper;
import com.xuejian.client.lxp.common.widget.MatrixImageView;
import com.xuejian.client.lxp.module.my.adapter.AlbumViewPager;
import com.xuejian.client.lxp.module.my.adapter.PictureAdapter;
import com.xuejian.client.lxp.module.toolbox.im.AddContactActivity;
import com.xuejian.client.lxp.module.toolbox.im.PickContactsWithCheckboxActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xuyongchen on 15/9/21.
 */

public class UserAlbumInfoActivity extends Activity{
    PictureAdapter viewpager;//大图显示pager
    private PictureAdapter.LocalViewPagerAdapter adapter=null;
    private TextView mCountView;
    private RelativeLayout headerBar;
    private FrameLayout galleryBottom;
    private ImageView photo_setting;
    private int currentIndex=0;
    private MoreDialog dialog;
    private ArrayList<ImageBean> myPictures;
    private TextView description_detail;
    private ArrayList<String> pic_ids;
    private String userid;
    private boolean isCity;
    private static final int REQUEST_EDIT_ALBUM=0x544;
    private static final int SAVE_LOCAL_SUCESS=0x545;
    private static final int SAVE_LOCAL_FAIL=0x546;

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SAVE_LOCAL_SUCESS:


                    Uri uri = (Uri)msg.obj;
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(uri);
                    UserAlbumInfoActivity.this.sendBroadcast(intent);
                    Toast.makeText(UserAlbumInfoActivity.this, "保存成功~", Toast.LENGTH_SHORT).show();
                    break;
                case SAVE_LOCAL_FAIL:
                    Toast.makeText(UserAlbumInfoActivity.this, "保存失败~", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_album_activity);

        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        myPictures = getIntent().getParcelableArrayListExtra("myPictures");
        pic_ids = getIntent().getStringArrayListExtra("pic_ids");
        isCity = getIntent().getBooleanExtra("isCity", false);
        userid = AccountManager.getCurrentUserId();
        if(myPictures==null || pic_ids==null){
            finish();
            return;
        }
        viewpager =(PictureAdapter)findViewById(R.id.albumviewpager);
        mCountView = (TextView)findViewById(R.id.page_num_info);

        headerBar = (RelativeLayout)findViewById(R.id.album_item_header_bar);
        galleryBottom =(FrameLayout) findViewById(R.id.gallery_bottom_panel);
        photo_setting = (ImageView)findViewById(R.id.photo_setting);
        if(isCity){
            photo_setting.setVisibility(View.GONE);
        }
        description_detail = (TextView) findViewById(R.id.description_detail);
        ((View)findViewById(R.id.header_bar_photo_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaforeBack();
            }
        });
        photo_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });

        adapter = viewpager.new LocalViewPagerAdapter(myPictures);
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(pageChangeListener);
        showViewPager(currentIndex);
    }

    private void showActionDialog() {
        String[] names = {"保存到手机", "编辑文字描述", "删除","取消"};
        dialog = new MoreDialog(UserAlbumInfoActivity.this);
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        dialog.setMoreStyle(false, 4, names);
        dialog.getTv1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentIndex>=0 && currentIndex<myPictures.size()){
                    saveToLocal(myPictures.get(currentIndex));

                }else{
                    Toast.makeText(UserAlbumInfoActivity.this,"相册已经为空,没有照片可保存！",Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
        dialog.getTv2().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(currentIndex>=0 && currentIndex<myPictures.size()){
                    Intent intent = new Intent(UserAlbumInfoActivity.this,EditAlbumDescActivity.class);
                    intent.putExtra("imageurl",myPictures.get(currentIndex).thumb);
                    intent.putExtra("imageid",pic_ids.get(currentIndex));
                    intent.putExtra("userid",userid);
                    String caption = myPictures.get(currentIndex).caption;
                    if(caption!=null){
                        intent.putExtra("caption",caption);
                    }
                    UserAlbumInfoActivity.this.startActivityForResult(intent, REQUEST_EDIT_ALBUM);
                }else{
                    Toast.makeText(UserAlbumInfoActivity.this,"相册已经为空,没有照片可保存！",Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentIndex>=0 && currentIndex<myPictures.size()){
                    delThisPic(pic_ids.get(currentIndex),myPictures.get(currentIndex));
                }else{
                    Toast.makeText(UserAlbumInfoActivity.this,"相册已经为空,没有照片可删除！",Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }



    public void delThisPic(final String picId, final ImageBean imageBean) {
        if (!CommonUtils.isNetWorkConnected(UserAlbumInfoActivity.this)) {
            ToastUtil.getInstance(UserAlbumInfoActivity.this).showToast("无网络连接，请检查网络");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(UserAlbumInfoActivity.this, "请稍后");
        }catch (Exception e){
            DialogManager.getInstance().dissMissLoadingDialog();
        }

        UserApi.delUserAlbumPic(String.valueOf(userid), picId, new HttpCallBack<String>() {


            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                pic_ids.remove(picId);
                myPictures.remove(imageBean);
                if (myPictures.size() == 0) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("myPictures", myPictures);
                    intent.putStringArrayListExtra("pic_ids", pic_ids);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                adapter.notifyDataSetChanged();

                viewpager.setAdapter(adapter);
                if((currentIndex-1>=0)&& (currentIndex-1)<(myPictures.size()-1)){
                    currentIndex=currentIndex-1;
                    showViewPager(currentIndex);
                }

                AccountManager.getInstance().getLoginAccountInfo().setAlbumCnt(myPictures.size());
                ToastUtil.getInstance(UserAlbumInfoActivity.this).showToast("删除成功");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(UserAlbumInfoActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }

            @Override
            public void onStart() {
            }
        });

    }

    public void saveToLocal(final  ImageBean imageBean){

        new Thread(){
            @Override
            public void run() {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                    FileOutputStream fout=null;
                    InputStream inputStream=null;
                    HttpURLConnection urlConnection=null;
                    try{
                        String filePath = Environment.getExternalStorageDirectory().toString()+"/Download";
                        URL url=new URL(imageBean.full);
                        urlConnection= (HttpURLConnection)url.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        if(urlConnection.getResponseCode()==200){

                            inputStream = urlConnection.getInputStream();
                        }


                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy_MM_dd_HH:mm:ss");
                        String filePrefix = simpleDateFormat.format(new Date());
                        String contentType = urlConnection.getContentType();
                        String lastPostFix = contentType.split("/")[1];
                        String filename = filePrefix+"."+lastPostFix;

                        File outputFile = new File(filePath,filename);
                        if(!outputFile.getParentFile().exists()){
                            outputFile.getParentFile().mkdirs();
                        }
                        if(!outputFile.exists()){
                            outputFile.createNewFile();
                        }

                        fout = new FileOutputStream(outputFile);

                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer))!=-1){
                            fout.write(buffer,0,len);
                        }
                        fout.flush();
                        fout.close();
                        try{
                            MediaStore.Images.Media.insertImage(UserAlbumInfoActivity.this.getContentResolver(),outputFile.getAbsolutePath(),filename,null);
                        }catch (Exception ex){

                        }
                        Message message = new Message();
                        message.obj=Uri.fromFile(outputFile);
                        message.what=SAVE_LOCAL_SUCESS;
                        myHandler.sendMessage(message);


                    }catch (Exception ex){
                        myHandler.sendEmptyMessage(SAVE_LOCAL_FAIL);
                    }finally {

                        if(urlConnection!=null){
                            urlConnection.disconnect();
                        }
                        if(fout!=null){
                            try{
                                fout.close();
                            }catch (Exception ex){

                            }

                        }

                        if(inputStream!=null){
                            try{
                                inputStream.close();
                            }catch (Exception ex){

                            }

                        }
                    }

                }else{
                    myHandler.sendEmptyMessage(SAVE_LOCAL_FAIL);

                }
            }
        }.start();


    }
    private void showViewPager(int index) {
        viewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + myPictures.size());
        if(currentIndex>=0 && currentIndex<myPictures.size() && myPictures.get(currentIndex).caption!=null){
            description_detail.setText(myPictures.get(currentIndex).caption);
        }
    }

    @Override
    public void onBackPressed() {
        beaforeBack();
        super.onBackPressed();

    }

    public void beaforeBack(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("myPictures",myPictures);
        intent.putStringArrayListExtra("pic_ids",pic_ids);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_EDIT_ALBUM && resultCode==RESULT_OK){

            String thumb = data.getStringExtra("thumb");
            String caption = data.getStringExtra("caption");
            if(thumb!=null){
                for(int i=0;i<myPictures.size();i++){
                    if(myPictures.get(i).thumb!=null && myPictures.get(i).thumb.equals(thumb)){
                        myPictures.get(i).caption=caption;
                    }
                }

               // viewpager.setAdapter(adapter);
            }
        }
        adapter.notifyDataSetChanged();
        if(myPictures.size()>0){
            showViewPager(currentIndex);
        }



    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (viewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + viewpager.getAdapter().getCount();
                currentIndex=position;
                mCountView.setText(text);
                if(myPictures.get(position).caption!=null){
                    description_detail.setText(myPictures.get(position).caption);
                }

            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        @Override
        public void onPageScrollStateChanged(int arg0) {


        }
    };

   /* @Override
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
    }*/

}
