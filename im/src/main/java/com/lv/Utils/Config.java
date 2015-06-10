package com.lv.Utils;

import android.os.Environment;

/**
 * Created by q on 2015/4/27.
 */
public class Config {
    public static final String HOST = "http://hedy.zephyre.me";
    public static final String GET_GROUP = "http://hedy.zephyre.me/groups/";
    public static final String SEND_URL = "http://hedy.zephyre.me/chats";
    public static final String ACK_URL = "http://hedy.zephyre.me/chats/";
    public static final String MSG_DBNAME = "IM_SDK.db";
    public static final String USER_DBNAME = "USER.db";
    public static final String DB_PATH = "/data/data/com.xuejian.client.lxp/";
    public final static String AUDIO_AMR_FILENAME = "Audio.amr";
    public static final String LOGIN_URL="http://hedy.zephyre.me/users/login";
    public static final String FETCH_URL="http://hedy.zephyre.me/chats/";
    public static final int DOWNLOAD_SUCCESS= 1000;
    public static final int DOWNLOAD_FILED= 1001;
    public static final int STATUS_SUCCESS= 0;
    public static final int STATUS_SENDING= 1;
    public static final int STATUS_FAILED= 2;
    public static final int TEXT_MSG= 0;
    public static final int AUDIO_MSG= 1;
    public static final int IMAGE_MSG= 2;
    public static final int LOC_MSG= 4;
    public static final int GONGLVE_MSG= 10;
    public static final int CMD_MSG= 100;
    public static final int TYPE_SEND= 0;
    public static final int TYPE_RECEIVE= 1;
    public static final int CHOOSE_IMAGE_CODE = 1;
    public static final String ACTION_START="ACTION.IMSDK.STARTDOWNLOAD";
    public static final String DownLoadImage_path= Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/Download/image/";
    public static final String DownLoadAudio_path= Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/Download/audio/";
    public static final String imagepath = Environment.getExternalStorageDirectory().getPath()+"/lvxingpai/image/" ;
    public static final String TAG="lvFM";
    public static final boolean isDebug=true;
}