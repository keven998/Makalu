package com.lv.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.lv.utils.Config;
import com.lv.utils.CryptUtils;
import com.lv.bean.Message;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by q on 2015/5/4.
 */
public class DownloadService extends Service {
    private HashMap<String, Message> downlaodMap=new HashMap<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  if (Config.ACTION_START.equals(intent.getAction())) {
            try {
                Message message = (Message)intent.getSerializableExtra("msg");
                //    if (!downlaodMap.containsKey(message.getUrl())) {
                new DownloadTask1().execute(message);
                downlaodMap.put(message.getUrl(),message);
                if (Config.isDebug){
                    Log.i(Config.TAG,"开始下载！ "+message.getUrl());
//                }
//            }
//            else {
//                notice(message,newfilename);
//                if (Config.isDebug){
//                    Log.i(Config.TAG,"已下载 ");
//                }
                    //           }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadTask1 extends AsyncTask<Object,Void,Void>{
        private String url;
        private Message msg;
        private int msgType;
        @Override
        protected Void doInBackground(Object[] params) {

            msg= (Message) params[0];
            url= msg.getUrl();
            msgType=msg.getMsgType();
            HttpURLConnection conn = null;
            //OutputStream output = null;
            String newfilename=null;
            FileOutputStream output=null;
            try {
                URL downloadUrl = new URL(url);
                conn = (HttpURLConnection) downloadUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (Config.isDebug){
                    Log.i(Config.TAG,"downlaod code: "+conn.getResponseCode());
                }
                if (conn.getResponseCode() == 200) {
                    int length = conn.getContentLength();
                    if (length < 0) {
                        return null;
                    }
                    File path = null;
                    File file= null;
                    StringBuffer sb = new StringBuffer();
                    String user=CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId());
                    String name=CryptUtils.getMD5String(msg.getUrl());
                    switch (msgType) {
                        case 1:
                            path = new File(Config.DownLoadAudio_path +user);
                            file = new File(path, name+ ".amr");
                            newfilename=Config.DownLoadAudio_path +user+"/"+name+ ".amr";
                            break;
                        case 2:
                            path = new File(Config.DownLoadImage_path +user);
                            file = new File(path, name+ ".jpeg");
                            newfilename=Config.DownLoadImage_path +user+"/"+name+ ".jpeg";
                            break;
                        case 3:
                            path = new File(Config.DownLoadMap_path +user);
                            file = new File(path, name+ ".png");
                            newfilename=Config.DownLoadMap_path +user+"/"+name+ ".png";
                            break;
                        default:
                            break;
                    }

                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    if (file.exists()){
                        file.delete();
                      //  notice(msg);
                     //   if (Config.isDebug){
                      //      Log.i(Config.TAG,"已下载");
                     //   }
                    //    return null;
                    }
                    File newfile = new File(newfilename);
                    newfile.createNewFile();
                    InputStream input = null;
                    input = conn.getInputStream();
                    if (msgType==1){
                        output = new FileOutputStream(newfile);
                        byte buffer[] = new byte[1024];
                        while(input.read(buffer) != -1){
                            output.write(buffer);
                        }
                        output.flush();

                    }
                    if (msgType==2) {
                        Bitmap bm = BitmapFactory.decodeStream(input);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newfile));
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                        bm.recycle();
                    }
                    if (msgType==3) {
                        Bitmap bm = BitmapFactory.decodeStream(input);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newfile));
                        bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
                        bos.flush();
                        bos.close();
                        bm.recycle();
                    }
                    if (Config.isDebug){
                        Log.i(Config.TAG, "下载完成");
                    }
                    notice(msg,newfilename);
                }else {
                    msg.setStatus(1);
                    notice(msg,newfilename);
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.setStatus(1);
                notice(msg,newfilename);
                if (Config.isDebug){
                    Log.i(Config.TAG,"下载失败");
                }
            }finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
    private void notice(Message msg,String path){
        if (msg.getMsgType()==1){
            try {
                JSONObject o=new JSONObject(msg.getContents());
                o.put("path",path);
                o.put("isRead",false);
                msg.setContents(o.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (msg.getMsgType()==2){
            try {
                JSONObject o=new JSONObject(msg.getContents());
                String full=o.getString("full");
                String localPath=Config.DownLoadImage_path + CryptUtils.getMD5String(IMClient.getInstance().getCurrentUserId()) + "/" +CryptUtils.getMD5String(full) + ".jpeg";
                o.put("localPath",localPath);
                o.put("thumbPath",path);
                msg.setContents(o.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (msg.getMsgType()==3){
            try {
                JSONObject o=new JSONObject(msg.getContents());
                o.put("path",path);
                msg.setContents(o.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //IMClient.getInstance().updateMessage();
        android.os.Message message= android.os.Message.obtain();
        message.obj=msg;
        message.what=Config.DOWNLOAD_SUCCESS;
        HandleImMessage.handler.sendMessage(message);
    }
}