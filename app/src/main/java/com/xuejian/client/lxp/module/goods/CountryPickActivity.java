package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.widget.SideBar;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CountryCodeBean;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/24.
 */
public class CountryPickActivity extends PeachBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_country_list);
        findViewById(R.id.iv_nav_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ListView listView = (ListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        SideBar indexBar = (SideBar) findViewById(R.id.sb_index);
        TextView indexDialogTv = (TextView) findViewById(R.id.dialog);
        indexBar.setTextView(indexDialogTv);
        indexBar.setTextColor(getResources().getColor(R.color.app_theme_color));
        final ArrayList <CountryCodeBean> contactList =  CommonUtils.parserCountryCodeJson(mContext);
        final ContactAdapter adapter = new ContactAdapter(this, R.layout.item_country_code, contactList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        indexBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForIndex(s.charAt(0) + "".toUpperCase());
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (position>=0&&position<=contactList.size()-1){
                  Intent intent = new Intent();
                  intent.putExtra("dialCode",Integer.parseInt(contactList.get(position).dialCode));
                  setResult(RESULT_OK, intent);
                  finish();
              }
            }
        });

    }

    public class ContactAdapter extends ArrayAdapter<CountryCodeBean> implements SectionIndexer {

        private LayoutInflater layoutInflater;
        //    private EditText query;
//    private ImageButton clearSearch;
        private List<String> sections;
        private SparseIntArray positionOfSection;
        private SparseIntArray sectionOfPosition;
        private int res;
        private ArrayList<Integer> subItemNum = new ArrayList<>();

        public ContactAdapter(Context context, int resource, List<CountryCodeBean> objects) {
            super(context, resource, objects);
            this.res = resource;
            layoutInflater = LayoutInflater.from(context);
            initSections();
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder1 vh;
            if (convertView == null) {
                convertView = layoutInflater.inflate(res, null);
                vh = new ViewHolder1();
                vh.nickView = (TextView) convertView.findViewById(R.id.name);
                vh.sectionHeader = (TextView) convertView.findViewById(R.id.header);
                vh.dividerView = convertView.findViewById(R.id.vw_divider);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder1) convertView.getTag();
            }
            vh.dividerView.setVisibility(View.GONE);
            final CountryCodeBean bean = getItem(position);
            String header = bean.header.charAt(0)+"";
            if (position == 0 || header != null && !header.equals(getItem(position - 1).header.charAt(0)+"")) {
                if ("".equals(header)) {
                    vh.sectionHeader.setVisibility(View.GONE);
                    vh.dividerView.setVisibility(View.GONE);
                } else {
                    vh.sectionHeader.setVisibility(View.VISIBLE);
                    vh.sectionHeader.setText(header);
                }
            } else {
                vh.sectionHeader.setVisibility(View.GONE);
                vh.dividerView.setVisibility(View.GONE);
            }

            vh.nickView.setText(bean.countryName);

            return convertView;
        }

        @Override
        public CountryCodeBean getItem(int position) {
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
                if (indexStr.equals(sortStr.toUpperCase())) {
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
                String letter = getItem(i).header.charAt(0)+"".toUpperCase();
                String beforeLetter = "";
                if (i > 0) {
                    beforeLetter = getItem(i - 1).header.charAt(0)+"".toUpperCase();
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
            public View dividerView;
            public TextView nickView;
        }

    }

}
