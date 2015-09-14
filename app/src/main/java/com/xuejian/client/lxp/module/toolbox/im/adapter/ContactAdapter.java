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
package com.xuejian.client.lxp.module.toolbox.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.User;

import java.util.ArrayList;
import java.util.List;


/**
 * 简单的好友Adapter实现
 */
public class ContactAdapter extends ArrayAdapter<User> implements SectionIndexer {

    private LayoutInflater layoutInflater;
//    private EditText query;
//    private ImageButton clearSearch;
    private List<String> sections;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private DisplayImageOptions picOptions;
    private ArrayList<Integer> subItemNum = new ArrayList<>();

    public ContactAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.res = resource;
        layoutInflater = LayoutInflater.from(context);
        initSections();

        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(20)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    @Override
    public int getCount() {
        return super.getCount();
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
        ViewHolder1 vh;
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
            vh = new ViewHolder1();
            vh.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
            vh.nickView = (TextView) convertView.findViewById(R.id.name);
            vh.sectionHeader = (TextView) convertView.findViewById(R.id.header);
            vh.unreadMsgView = (TextView) convertView.findViewById(R.id.non_accept_number);
            vh.dividerView = convertView.findViewById(R.id.vw_divider);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder1) convertView.getTag();
        }
        final User user = getItem(position);
        String username = user.getNickName();
        String header = user.getHeader();
        if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                vh.sectionHeader.setVisibility(View.GONE);
                vh.dividerView.setVisibility(View.GONE);
            } else {
                vh.sectionHeader.setVisibility(View.VISIBLE);
                vh.sectionHeader.setText(header);
                vh.dividerView.setVisibility(View.VISIBLE);
            }
        } else {
            vh.sectionHeader.setVisibility(View.GONE);
            vh.dividerView.setVisibility(View.GONE);
        }
        if ("item_new_friends".equals(username)) {
            vh.sectionHeader.setVisibility(View.GONE);
            vh.dividerView.setVisibility(View.GONE);
        }
        //显示申请与通知item
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            vh.nickView.setText("新朋友");
//                vh.nickView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cell_accessory, 0);
//                vh.avatarView.setImageResource(R.drawable.new_friends_icon);
            //    vh.talkView.setImageResource(R.drawable.icon_arrow_right);
            vh.avatarView.setImageResource(R.drawable.ic_contact_list_invent);
            if(IMClient.getInstance().getUnAcceptMsg()>0){
                vh.unreadMsgView.setVisibility(View.VISIBLE);
                vh.unreadMsgView.setText(IMClient.getInstance().getUnAcceptMsg()+"");
            }else{
                vh.unreadMsgView.setVisibility(View.GONE);
            }
        } else if (username.equals(Constant.GROUP_USERNAME)) {
            //群聊item
            vh.nickView.setText(user.getNickName());
            vh.avatarView.setImageResource(R.drawable.my_group);
            vh.nickView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            vh.unreadMsgView.setVisibility(View.INVISIBLE);
            if (TextUtils.isEmpty(user.getMemo())){
                vh.nickView.setText(user.getNickName());
            }else {
                vh.nickView.setText(user.getMemo()+"("+user.getNickName()+")");
            }
            vh.nickView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ImageLoader.getInstance().displayImage(user.getAvatarSmall(), vh.avatarView, picOptions);
//            vh.talkView.setVisibility(View.VISIBLE);
//            vh.talkView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra("name", user.getNickName());
//                    intent.putExtra("chatType", "single");
//                    intent.putExtra("friend_id", String.valueOf(user.getUserId()));
//                    intent.setClass(getContext(), ChatActivity.class);
//                    getContext().startActivity(intent);
//                }
//            });
//            vh.unreadMsgView.setVisibility(View.GONE);
        }
//
//
//        if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
//            if ("".equals(header)||"item_new_friends".equals(username)) {
//                vh.sectionHeader.setVisibility(View.GONE);
//                vh.dividerView.setVisibility(View.GONE);
//            } else {
//                vh.sectionHeader.setVisibility(View.VISIBLE);
//                vh.sectionHeader.setText(header);
//                vh.dividerView.setVisibility(View.VISIBLE);
//            }
//        }
//            else {
//                vh.sectionHeader.setVisibility(View.GONE);
//                vh.dividerView.setVisibility(View.GONE);
//			}
//
//			//显示申请与通知item
//			if(username.equals(Constant.NEW_FRIENDS_USERNAME)) {
//                vh.nickView.setText("好友请求");
//                vh.talkView.setImageResource(R.drawable.ic_gray_right_arrow);
//                vh.avatarView.setImageResource(R.drawable.ic_frend_request);
//                    vh.unreadMsgView.setVisibility(View.GONE);
//			} else if (username.equals(Constant.GROUP_USERNAME)){
//				//群聊item
//                vh.nickView.setText(user.getNickName());
//                vh.avatarView.setImageResource(R.drawable.my_group);
//                vh.nickView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                vh.talkView.setImageResource(R.drawable.ic_gray_right_arrow);
//                vh.unreadMsgView.setVisibility(View.GONE);
//			} else {
//                vh.nickView.setText(user.getNickName());
//                vh.nickView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                ImageLoader.getInstance().displayImage(user.getAvatarSmall(), vh.avatarView, picOptions);
//                vh.talkView.setVisibility(View.VISIBLE);
//                vh.talkView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent=new Intent();
//                        intent.putExtra("Name",user.getNickName());
//                        intent.putExtra("chatType","single");
//                        intent.putExtra("friend_id",user.getUserId()+"");
//                        intent.setClass(getContext(),ChatActivity.class);
//                        getContext().startActivity(intent);
//                    }
//                });
//                vh.unreadMsgView.setVisibility(View.GONE);
//			}


        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public Object[] getSections() {
        return sections.toArray();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    /**
     * 根据分类的首字母获取其第一次出现该首字母的位置
     */
    public int getPositionForIndex(String indexStr) {
        for (int i = 0; i < sections.size(); i++) {
            String sortStr = sections.get(i);
            if (indexStr.equals(sortStr)) {
                int all = 0;
                for (int j = 0; j < i; j++) {
                    if (j < subItemNum.size()) {
                        all += subItemNum.get(j);
                    } else {
                        all = -1;
                        break;
                    }
                }
                return all;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    public void initSections() {
        subItemNum.clear();
        int count = getCount();
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        sections = new ArrayList<String>();
//        positionOfSection.put(0, 0);
//        sectionOfPosition.put(0, 0);
        int section = 0;
        int num = 0;
        for (int i = 0; i < count; i++) {
            String letter = getItem(i).getHeader();
            String beforeLetter = "";
            if (i > 0) {
                beforeLetter = getItem(i - 1).getHeader();
            }
            if (letter != null && !beforeLetter.equals(letter)) {
                section++;
                sections.add(letter);
                positionOfSection.put(section, i);
                if (num != 0) {
                    subItemNum.add(num);
                }
                num = 1;
            } else {
                num++;
            }
            sectionOfPosition.put(i, section);
        }
        subItemNum.add(num);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        initSections();
    }

    public List<String> getSectionList() {
        return sections;
    }

    class ViewHolder1 {
        public TextView sectionHeader;
        public ImageView avatarView;
        public View dividerView;
        public TextView nickView;
        //       public ImageView talkView;
        public TextView unreadMsgView;
    }

}
