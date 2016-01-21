package com.lv.utils;

import android.os.Environment;

/**
 * Created by q on 2015/4/27.
 */
public class Config {
 //   public static final String HOST = "http://hedy-dev.lvxingpai.com";
    public static final String HOST = "http://hedy.lvxingpai.com";
    public static final String GET_GROUP = HOST+"/groups/";
    public static final String SEND_URL = HOST+"/chats";
    public static final String ACK_URL = HOST+"/users/";
    public static final String CONS_URL="/users/%s/conversations?targetIds=";
    public static final String CON_URL="/users/%s/conversations/";
    public static final String MSG_DBNAME = "IM_SDK.db";
    public static final String USER_DBNAME = "USER.db";
    public static final String DB_PATH = "/data/data/com.xuejian.client.lxp/";
    public final static String AUDIO_AMR_FILENAME = "Audio.amr";
    public static final String LOGIN_URL=HOST+"/users/login";
    public static final String FETCH_URL=HOST+"/chats/";
    public static final int DOWNLOAD_SUCCESS= 1000;
    public static final int DOWNLOAD_FILED= 1001;
    public static final int STATUS_SUCCESS= 0;
    public static final int STATUS_SENDING= 1;
    public static final int STATUS_FAILED= 2;
    public static final int STATUS_DEFAULT= 3;
    public static final int TEXT_MSG= 0;
    public static final int AUDIO_MSG= 1;
    public static final int IMAGE_MSG= 2;
    public static final int LOC_MSG= 3;
    public static final int QA_MEG= 17;
    public static final int H5_MEG= 18;
    public static final int TIP_MSG= 200;
    public static final int GONGLVE_MSG= 10;
    public static final int CMD_MSG= 100;
    public static final int TYPE_SEND= 0;
    public static final int TYPE_RECEIVE= 1;
    public static final int CHOOSE_IMAGE_CODE = 1;
    public static final String ACTION_START="ACTION.IMSDK.STARTDOWNLOAD";
    public static final String DownLoadImage_path= Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/Download/image/";
    public static final String DownLoadAudio_path= Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/Download/audio/";
    public static final String DownLoadMap_path= Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/Download/map/";
    public static final String imagepath = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/image/" ;
    public static final String mapPath = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/map/";
    public static final String audioPath = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/audio/";
    public static final String TAG="lvFM";
    public static final boolean isDebug=true;
}
