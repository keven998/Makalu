package com.lv.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.lv.Listener.UploadListener;
import com.lv.Utils.Config;
import com.lv.Utils.PictureUtil;
import com.lv.Utils.TimeUtils;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Random;



public class UploadUtils {

    private UploadUtils() {
    }
    private static UploadUtils UploadUitls = null;

    private UploadManager uploadManager = new UploadManager();

    public static UploadUtils getInstance() {
        if (UploadUitls == null) {
            UploadUitls = new UploadUtils();
        }
        return UploadUitls;
    }

    public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath) {
        return PictureUtil.saveBitmapToJpegFile(bitmap, filePath, 75);
    }
    public String uploadImageByUrl(Bitmap bitmap, String sender, String receive, int msgType, long localId, UploadListener listener,String chatType) {
        File path = new File(Config.imagepath);
        if (!path.exists()) path.mkdirs();
        String imagepath1 = Config.imagepath + TimeUtils.getTimestamp() + "_image.jpeg";
        if (saveBitmapToJpegFile(bitmap, imagepath1))
            upload(imagepath1, sender, receive, msgType, localId, listener,chatType);
        else if (Config.isDebug){
            Log.i(Config.TAG, "文件出错！ ");
        }
        return imagepath1;
    }
    public String handleImage(Bitmap bitmap){
        File path = new File(Config.imagepath);
        if (!path.exists()) path.mkdirs();
        String imagePath= Config.imagepath + TimeUtils.getTimestamp() + "_image.jpeg";
        saveBitmapToJpegFile(bitmap, imagePath);
        return imagePath;
    }
    public void uploadImage( MessageBean messageBean,String sender, String receive, int msgType, long localId, UploadListener listener,String chatType) {
        String path=null;
        try {
            JSONObject object=new JSONObject(messageBean.getMessage());
            path=object.getString("localPath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        upload(path, sender, receive, msgType, localId, listener,chatType);
    }
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    public void uploadFile(File file, UploadListener listener) {
        // upload(file.getAbsolutePath(), listener);
    }

    public void upload(final String filePath, final String sender, final String receive, final int msgType, final long localId, final UploadListener listener,String chatType) {
        System.out.println("filePath:" + filePath);
        HttpUtils.getToken(new HttpUtils.tokenget() {
            @Override
            public void OnSuccess(String key, String token) {
                if (token == null) {
                    if (listener != null) {
                        listener.onError(-1, "token is null");
                    }
                    return;
                }
                HashMap<String, String> pamas = new HashMap<>();
                pamas.put("x:sender", sender);
                pamas.put("x:chatType", chatType);
                pamas.put("x:msgType", msgType + "");
                pamas.put("x:receiver", receive);
                uploadManager.put(filePath, key, token, new UpCompletionHandler() {
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        System.out.println("debug:info = " + info + ",response = " + response);
                        if (info != null && info.statusCode == 200) {// 上传成功
                            String conversation = null;
                            try {
                                JSONObject obj = response.getJSONObject("result");
                                conversation = obj.get("conversation").toString();
                                String msgId = obj.get("msgId").toString();
                                long timestamp = (Double.valueOf(obj.get("timestamp").toString())).longValue();
                                IMClient.getInstance().setLastMsg(receive, Integer.parseInt(msgId));
                                IMClient.getInstance().updateMessage(receive, localId, msgId, conversation, timestamp, Config.STATUS_SUCCESS,null);
                                if(Config.isDebug){
                                    Log.i(Config.TAG,"发送成功，消息更新！");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (listener != null) {
                                listener.onSucess(null);
                            }
                        } else {
                            IMClient.getInstance().updateMessage(receive, localId, null, null, 0, Config.STATUS_FAILED,null);
                            if (listener != null) {
                                if (info!=null)
                                listener.onError(info.statusCode, info.error);
                            }
                        }
                    }
                }, new UploadOptions(pamas, null, false, new UpProgressHandler() {
                    public void progress(String key, double percent) {
                        if (listener != null) {
                            listener.onProgress((int) (percent * 100));
                        }
                    }
                }, null));
            }
        });


    }

    /**
     * 生成远程文件路径（全局唯一）
     *
     * @return filePath
     */
    private String getFileUrlUUID() {
        String filePath = android.os.Build.MODEL + "__" + System.currentTimeMillis() + "__" + (new Random().nextInt(500000))
                + "_" + (new Random().nextInt(10000));
        return filePath.replace(".", "0");
    }

    private String getRealUrl(String fileUrlUUID) {
        String filePath = "http://7xiktj.com1.z0.glb.clouddn.com/" + fileUrlUUID;
        return filePath;
    }

}


