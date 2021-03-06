package com.lv.Audio;

import android.media.MediaRecorder;
import android.os.Environment;

import com.lv.utils.Config;
import com.lv.utils.TimeUtils;

import java.io.File;

public class AudioFileFunc {
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    public final static int AUDIO_SAMPLE_RATE = 44100;

    // 44.1KHz,普遍使用的频率
    // 录音输出文件
    //private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    //private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
    public static boolean isSdcardExit() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAMRFilePath() {
        File sd = Environment.getExternalStorageDirectory();
        String path = Config.audioPath;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        String mAudioAMRPath = "";
        if (isSdcardExit()) {
            mAudioAMRPath = Config.audioPath+ TimeUtils.getTimestamp() + "_" + Config.AUDIO_AMR_FILENAME;
        }
        return mAudioAMRPath;
    }

    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }
}
