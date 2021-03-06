/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.common.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.lv.utils.Config;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.ImageUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;

import java.io.File;
import java.util.ArrayList;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
    private ImageView iv = null;
    String localFullSizePath = null;
    String thumbnailPath = null;
    String remotePath = null;
    MessageBean message = null;
    String chatType;
    Activity activity;
    String chatId;
    private TextView tv = null;

    @Override
    protected Bitmap doInBackground(Object... args) {
        thumbnailPath = (String) args[0];
        localFullSizePath = (String) args[1];
        remotePath = (String) args[2];
        chatType = (String) args[3];
        iv = (ImageView) args[4];
        // if(args[2] != null) {
        activity = (Activity) args[5];
        // }
        message = (MessageBean) args[6];
        tv = (TextView) args[7];
        chatId = (String)args[8];
        File file = new File(thumbnailPath);
        if (message.getType() == Config.LOC_MSG) {
            if (file.exists()) {
                return ImageUtils.decodeScaleImage(thumbnailPath, 320, 320);
            } else return null;
        } else if (file.exists()) {
            return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
        } else {
            return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
        }
    }

    protected void onPostExecute(final Bitmap image) {
        if (message.getType() == Config.LOC_MSG) {
            if (image != null) {
                tv.setBackgroundDrawable(new BitmapDrawable(image));
                ImageCache.getInstance().put(thumbnailPath, image);
            }
        } else if (image != null) {
            iv.setImageBitmap(image);
            ImageCache.getInstance().put(thumbnailPath, image);
            iv.setClickable(true);
            iv.setTag(thumbnailPath);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> pics = IMClient.getInstance().getPics(chatId);
                    int pos = 0;
                    if (message.getSendType() == 1) {
                        pos = pics.indexOf(remotePath);
                    } else {
                        pos = pics.indexOf("file://"+localFullSizePath);
                    }
                    IntentUtils.intentToPicGallery2(activity, pics, pos);


//                    if (thumbnailPath != null) {
//
//                        Intent intent = new Intent(activity, ShowBigImage.class);
//                        File file = new File(localFullSizePath);
//                        if (file.exists()) {
//                            Uri uri = Uri.fromFile(file);
//                            intent.putExtra("uri", uri);
//                        } else {
//                            intent.putExtra("downloadFilePath", localFullSizePath);
//                            intent.putExtra("remotepath", remotePath);
//                        }
////						if (message.getChatType() != ChatType.Chat) {
////							// delete the image from server after download
////						}
////						if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
////							message.isAcked = true;
////							try {
////								// 看了大图后发个已读回执给对方
////								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
////							} catch (Exception e) {
////								e.printStackTrace();
////							}
////						}
//                        ((BaseActivity) activity).startActivityWithNoAnim(intent);
//                    }
                }
            });
        } else {
            if (message.getStatus() == 2) {
//				if (CommonUtils.isNetWorkConnected(activity)) {
//					new Thread(new Runnable() {
//
//						@Override
//						public void run() {
//							EMChatManager.getInstance().asyncFetchMessage(message);
//						}
//					}).start();
//				}
            }

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
