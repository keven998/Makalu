/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aizou.peachtravel.module.toolbox.im;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.easemob.chat.EMChatDB;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.VoiceMessageBody;
import com.lv.bean.MessageBean;

public class VoicePlayClickListener implements View.OnClickListener {

    MessageBean message;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private String chatType;
    private String path;
    private boolean isRead;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static VoicePlayClickListener currentPlayListener = null;


	public VoicePlayClickListener(MessageBean message, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity activity,
			String username,String chatType,boolean isRead,String path) {
		this.message = message;
		this.iv_read_status = iv_read_status;
		this.adapter=adapter;
		voiceIconView = v;
		this.activity = activity;
		this.chatType =chatType ;
        this.isRead=isRead;
        this.path=path;
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		if (message.getSendType() == 1) {
			voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((ChatActivity)activity).playMsgId=null;
		adapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		((ChatActivity)activity).playMsgId=String.valueOf(message.getLocalId());
		AudioManager audioManager = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()){
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		}
		else{
			audioManager.setSpeakerphoneOn(false);//关闭扬声器
			//把声音设定成Earpiece（听筒）出来，设定为正在通话中
			 audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.release();
					mediaPlayer = null;
					stopPlayVoice(); // stop animation
				}

			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();
			try {
				//如果是接收的消息
				if (!isRead && message.getSendType() == 1) {
					isRead = true;
					if (iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
						//隐藏自己未播放这条语音消息的标志
						iv_read_status.setVisibility(View.INVISIBLE);
						//EMChatDB.getInstance().updateMessageAck(message.getMsgId(), true);
					}
					//告知对方已读这条消息
//					if(chatType != ChatType.GroupChat)
//						EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
				}
			} catch (Exception e) {
				isRead = false;
			}
		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (message.getSendType() ==1) {
				voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
				voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}
	@Override
	public void onClick(View v) {
		if (isPlaying) {
			if(((ChatActivity)activity).playMsgId !=null&&((ChatActivity)activity).playMsgId .equals(String.valueOf(message.getLocalId())))
			{
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}

		if (message.getSendType() == 0) {
			// for sent msg, we will try to play the voice file directly
			playVoice(path);
		} else {
			if(message.getStatus()==0){
				File file = new File(path);
				if (file.exists() && file.isFile()){
                    LogUtil.d("voiceFile","playVoice");
                    playVoice(path);
                }else{
                    LogUtil.d("voiceFile","file not exist");
                }

			}else if(message.getStatus()==1)
			{
				Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
			}else if(message.getStatus()==2)
			{
				Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						//EMChatManager.getInstance().asyncFetchMessage(message);
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
					}
					
				}.execute();
				
			
			}
 
		}
	}
}