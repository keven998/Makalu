package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aizou.core.http.HttpCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

/**
 * Created by xuyongchen on 15/9/21.
 */
public class EditAlbumDescActivity extends Activity{
    private ImageView myalbum;
    private EditText album_info;
    private String imageurl;
    private String imageid;
    private String userid;
    private String imageDescribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_album_desc);
        TitleHeaderBar titleHeaderBar = (TitleHeaderBar)findViewById(R.id.edit_album_title);
        titleHeaderBar.setTitleText("编辑文字描述");
        titleHeaderBar.getLeftTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlbumInfo();
            }
        });
        imageurl = getIntent().getStringExtra("imageurl");
        imageid = getIntent().getStringExtra("imageid");
        userid = AccountManager.getCurrentUserId();
        imageDescribe = getIntent().getStringExtra("caption");
        myalbum = (ImageView)findViewById(R.id.myalbum);

        DisplayImageOptions option=UILUtils.getDefaultOption();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageurl,myalbum,option);
        album_info=(EditText)findViewById(R.id.album_info);
        if(imageDescribe!=null){
            album_info.setText(imageDescribe);
        }
    }



    public  void updateAlbumInfo(){

        final String description=album_info.getText().toString().trim();
        if(description!=null && description.length()>0){
            UserApi.updateAlbumDesc(userid, imageid, description, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson<ImageBean> resultBean =CommonJson.fromJson(result,ImageBean.class);
                    if(resultBean.code==0){
                        Intent intent = new Intent();
                        intent.putExtra("thumb",imageurl);
                        intent.putExtra("caption",description);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        finish();
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }



}
