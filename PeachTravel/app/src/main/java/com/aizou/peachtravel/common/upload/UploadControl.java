package com.aizou.peachtravel.common.upload;

import android.content.Context;
import android.net.Uri;

import com.aizou.core.log.LogUtil;
import com.qiniu.auth.Authorizer;
import com.qiniu.io.IO;
import com.qiniu.resumableio.ResumableIO;
import com.qiniu.resumableio.SliceUploadTask;
import com.qiniu.rs.CallBack;
import com.qiniu.rs.CallRet;
import com.qiniu.rs.PutExtra;
import com.qiniu.rs.UploadCallRet;
import com.qiniu.rs.UploadTaskExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rjm on 2014/10/17.
 */
public class UploadControl {

    public static String uptoken = "anEC5u_72gw1kZPSy3Dsq1lo_DPXyvuPDaj4ePkN:zmaikrTu1lgLb8DTvKQbuFZ5ai0=:eyJzY29wZSI6ImFuZHJvaWRzZGsiLCJyZXR1cm5Cb2R5Ijoie1wiaGFzaFwiOlwiJChldGFnKVwiLFwia2V5XCI6XCIkKGtleSlcIixcImZuYW1lXCI6XCIgJChmbmFtZSkgXCIsXCJmc2l6ZVwiOlwiJChmc2l6ZSlcIixcIm1pbWVUeXBlXCI6XCIkKG1pbWVUeXBlKVwiLFwieDphXCI6XCIkKHg6YSlcIn0iLCJkZWFkbGluZSI6MTQ2NjIyMjcwMX0=";
    // upToken 这里需要自行获取. SDK 将不实现获取过程. 隔一段时间到业务服务器重新获取一次
    public static Authorizer auth = new Authorizer();
    {
        auth.setUploadToken(uptoken);
    }
    private static UploadControl instance;
    public static UploadControl getInstance(){
        if(instance==null){
            instance = new UploadControl();
        }
        return instance;
    }

    // 在七牛绑定的对应bucket的域名. 更换 uptoken 时同时更换为对应的空间名，
    public static String bucketName = "androidsdk";

    private void clean(){
        executor = null;
    }

    volatile boolean uploading = false;
    UploadTaskExecutor executor;
    PutExtra mExtra = new PutExtra();
    public  void uploadImage(Context context,File imageFile){
       doResumableUpload(context,imageFile,mExtra);
    }
    public void doResumableUpload(Context context,final File file, PutExtra extra) {
        final MyBlockRecord record = MyBlockRecord.genFromUri(context, file);

        String key = null;
        if(extra != null){
            extra.params = new HashMap<String, String>();
            extra.params.put("x:a", "bb");
        }
        List<SliceUploadTask.Block> blks = record.loadBlocks();
        String s = "blks.size(): " + blks.size() + " ==> ";
        for(SliceUploadTask.Block blk : blks ){
            s += blk.getIdx() + ", ";
        }
        final String pre = s + "\r\n";
        uploading = true;

        executor = ResumableIO.putFile( auth, key, file, extra, blks, new CallBack() {
            @Override
            public void onSuccess(UploadCallRet ret) {
                uploading = false;
                String key = ret.getKey();
                String redirect = "http://" + bucketName + ".qiniudn.com/" + key;
                String redirect2 = "http://" + bucketName + ".u.qiniudn.com/" + key;
                LogUtil.d("success---"+ret.getResponse());
                record.removeBlocks();
                clean();
            }

            @Override
            public void onProcess(long current, long total) {
                int percent = (int) (current * 100 / total);
                LogUtil.d("process---"+percent);
                //int i = 3/0;
            }

            @Override
            public void onBlockSuccess(SliceUploadTask.Block blk) {
                record.saveBlock(blk);
            }

            @Override
            public void onFailure(CallRet ret) {
                LogUtil.d("failure---"+ret.getResponse());
                uploading = false;
                clean();
            }
        });
    }

    static class MyBlockRecord{
        private static HashMap<String, List<SliceUploadTask.Block>> records = new HashMap<String, List<SliceUploadTask.Block>>();

        public static MyBlockRecord genFromUri(Context context, File file){
            String id = file.getPath() + context.toString();
            return new MyBlockRecord(id);
        }

        private final String id;
        private List<SliceUploadTask.Block> lastUploadBlocks;
        public MyBlockRecord(String id){
            this.id = id;
        }

        public List<SliceUploadTask.Block> loadBlocks() {
            if(lastUploadBlocks == null){
                List<SliceUploadTask.Block> t = records.get(id);
                if(t == null){
                    t = new ArrayList<SliceUploadTask.Block>();
                    records.put(id, t);
                }
                lastUploadBlocks = t;
            }
            return lastUploadBlocks;
        }

        /**
         * @param blk 断点记录， 以4M为一个断点单元
         */
        public void saveBlock(SliceUploadTask.Block blk){
            if(lastUploadBlocks != null){
                lastUploadBlocks.add(blk);
            }
        }

        public void removeBlocks(){
            records.remove(id);
        }
    }
}
