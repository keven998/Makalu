package com.xuejian.client.lxp.common.task;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yibiao.qin on 2015/5/29.
 */
public class DownloadVoice {
    private String url;
    private String filename;

    public DownloadVoice(String url, String fileName) {
        this.url = url;
        this.filename = fileName;
    }

    public interface DownloadListener {
        void onSuccess();

        void onProgress(int progress);

        void onFail();
    }

    public void download(final DownloadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myURL = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    int fileSize = conn.getContentLength();//根据响应获取文件大小
                    if (fileSize <= 0){
                        // throw new RuntimeException("无法获知文件大小 ");
                        listener.onFail();
                    }
                    if (is == null){
                        //  throw new RuntimeException("stream is null");
                        listener.onFail();
                    }
                    String path = filename.substring(0, filename.lastIndexOf("/"));
                    File file = new File(path);
                    if (!file.exists()) file.mkdirs();
                    FileOutputStream fos = new FileOutputStream(filename);
                    //把数据存入路径+文件名
                    byte buf[] = new byte[1024];
                    int downLoadFileSize = 0;
                    do {
                        //循环读取
                        int numread = is.read(buf);
                        if (numread == -1) {
                            break;
                        }
                        fos.write(buf, 0, numread);
                        downLoadFileSize += numread;
                        int result = downLoadFileSize * 100 / fileSize;
                        listener.onProgress(result);//更新进度条
                    } while (true);
                    try {
                        is.close();
                    } catch (Exception ex) {
                        Log.e("tag", "error: " + ex.getMessage(), ex);
                    }
                    listener.onSuccess();//通知下载完成
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFail();
                }
            }
        }).start();
    }

}
