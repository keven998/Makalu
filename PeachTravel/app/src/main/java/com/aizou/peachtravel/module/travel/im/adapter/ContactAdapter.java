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
package com.aizou.peachtravel.module.travel.im.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 简单的好友Adapter实现
 *
 */
public class ContactAdapter extends ArrayAdapter<IMUser>  implements SectionIndexer{

	private LayoutInflater layoutInflater;
	private EditText query;
	private ImageButton clearSearch;
    private List<String> sections;
    private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private int res;

	public ContactAdapter(Context context, int resource, List<IMUser> objects) {
		super(context, resource, objects);
		this.res = resource;
		layoutInflater = LayoutInflater.from(context);
        initSections();
	}
	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if (position == 0) {//搜索框
//			if(convertView == null){
//				convertView = layoutInflater.inflate(R.layout.search_bar_with_padding, null);
//				query = (EditText) convertView.findViewById(R.id.query);
//				clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
//				query.addTextChangedListener(new TextWatcher() {
//					public void onTextChanged(CharSequence s, int start, int before, int count) {
//						getFilter().filter(s);
//						if (s.length() > 0) {
//							clearSearch.setVisibility(View.VISIBLE);
////							if (sidebar != null)
////								sidebar.setVisibility(View.GONE);
//						} else {
//							clearSearch.setVisibility(View.INVISIBLE);
////							if (sidebar != null)
////								sidebar.setVisibility(View.VISIBLE);
//						}
//					}
//
//					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//					}
//
//					public void afterTextChanged(Editable s) {
//					}
//				});
//				clearSearch.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//						if (((Activity) getContext()).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//							if (((Activity) getContext()).getCurrentFocus() != null)
//							manager.hideSoftInputFromWindow(((Activity) getContext()).getCurrentFocus().getWindowToken(),
//									InputMethodManager.HIDE_NOT_ALWAYS);
//						//清除搜索框文字
//						query.getText().clear();
//					}
//				});
//			}
//		}else{
			if(convertView == null){
				convertView = layoutInflater.inflate(res, null);
			}
			
			ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
			TextView unreadMsgView = (TextView) convertView.findViewById(R.id.unread_msg_number);
			TextView nameTextview = (TextView) convertView.findViewById(R.id.name);
			TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
			IMUser user = getItem(position);
			if(user == null)
				Log.d("ContactAdapter", position + "");
			String username = user.getUsername();
			String header = user.getHeader();
			if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
				if ("".equals(header)) {
					tvHeader.setVisibility(View.GONE);
				} else {
					tvHeader.setVisibility(View.VISIBLE);
					tvHeader.setText(header);
				}
			} else {
				tvHeader.setVisibility(View.GONE);
			}
			//显示申请与通知item
			if(username.equals(Constant.NEW_FRIENDS_USERNAME)){
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.new_friends_icon);
				if(user.getUnreadMsgCount() > 0){
					unreadMsgView.setVisibility(View.VISIBLE);
					unreadMsgView.setText(user.getUnreadMsgCount()+"");
				}else{
					unreadMsgView.setVisibility(View.INVISIBLE);
				}
			}else if(username.equals(Constant.GROUP_USERNAME)){
				//群聊item
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.groups_icon);
			}else{
				nameTextview.setText(user.getNick());
				if(unreadMsgView != null)
					unreadMsgView.setVisibility(View.INVISIBLE);
				avatar.setBackgroundResource(R.drawable.default_avatar);
                ImageLoader.getInstance().displayImage(user.getAvatar(),avatar, UILUtils.getDefaultOption());
			}


		return convertView;
	}
	
	@Override
	public IMUser getItem(int position) {
		return  super.getItem(position);
	}

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

    public void initSections(){
        int count = getCount();
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        sections = new ArrayList<String>();
//        positionOfSection.put(0, 0);
//        sectionOfPosition.put(0, 0);
        int section=0;
        for (int i = 0; i < count; i++) {
            String letter = getItem(i).getHeader();
            String beforeLetter ="";
            if(i>0){
                beforeLetter = getItem(i-1).getHeader();
            }
            if (letter != null && !beforeLetter.equals(letter)) {
                section++;
                sections.add(letter);
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        initSections();
    }

	public List<String> getSectionList() {
		return sections;
	}

}
