package com.lv.Audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.io.File;

public class MediaRecordFunc {

    private static MediaRecordFunc mInstance;
    private boolean isRecord = false;
    public String tempPath;
    private MediaRecorder mMediaRecorder;
    Handler handler;

    private MediaRecordFunc() {
    }

    public synchronized static MediaRecordFunc getInstance() {
        if (mInstance == null)
            mInstance = new MediaRecordFunc();
        return mInstance;
    }

    public int startRecordAndFile(Handler _handler) {
        handler = _handler;
        // 判断是否有外部存储设备sdcard/
        if (AudioFileFunc.isSdcardExit()) {
            if (isRecord) {
                return 1002;
            } else {
                if (mMediaRecorder == null)
                    if (!createMediaRecord()) return 1004;
                try {
                    try {
                        mMediaRecorder.prepare();
                    } catch (Throwable e) {
                        return 1010;
                    }

                    mMediaRecorder.start();
                    // 让录制状态为true
                    isRecord = true;
                    new Thread(new Runnable() {
                        public void run() {
                            while (true) {
                                try {
                                    if (isRecord) {
                                        Message var1 = Message.obtain();
                                        var1.what = mMediaRecorder.getMaxAmplitude() * 13 / 32767;
                                        handler.sendMessage(var1);
                                        SystemClock.sleep(100L);
                                        continue;
                                    }
                                } catch (Exception var2) {
                                    var2.printStackTrace();
                                }
                                return;
                            }
                        }
                    }).start();
                    return 1000;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return 1010;
                }
            }

        } else {
            return 1003;
        }
    }

    public void startRecordNotFile() {
        // 判断是否有外部存储设备sdcard/
        if (AudioFileFunc.isSdcardExit()) {

            if (mMediaRecorder == null)
                if (!createMediaRecord()) ;
            try {
                try {
                    mMediaRecorder.prepare();
                } catch (Throwable e) {

                }

                mMediaRecorder.start();
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }

        }
    }

    private boolean createMediaRecord() {
        mMediaRecorder = new MediaRecorder();

		/* setAudioSource/setVedioSource */
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风

		/*
         * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

		/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mMediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
        mMediaRecorder.setAudioSamplingRate(8000);
        mMediaRecorder.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_8BIT);
        /* 设置输出文件的路径 */
        File file = new File(AudioFileFunc.getAMRFilePath());

        if (file.exists()) {
            file.delete();
        }
        tempPath = AudioFileFunc.getAMRFilePath();
        if (tempPath == null) return false;
        mMediaRecorder.setOutputFile(tempPath);
        return true;
    }

    public String stopRecordAndFile() {
        close();
        return tempPath;
    }

    public boolean cancleRecord() {
        close();
        File file = new File(tempPath);
        return !file.exists() || file.delete();
    }

    public long getRecordFileSize() {
        return AudioFileFunc.getFileSize(AudioFileFunc.getAMRFilePath());
    }

    public void checkOp() {

    }

    private void close() {
        try {
            if (mMediaRecorder != null) {
                isRecord = false;
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
