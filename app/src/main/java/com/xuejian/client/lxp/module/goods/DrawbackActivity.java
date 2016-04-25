package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.db.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/19.
 */
public class DrawbackActivity extends PeachBaseActivity {

    String [] reasons = new String[]{"我想重新下单","我的旅行计划有所改变","我不想体验这个项目了","其他"};
    String currentReason ;
    UserAdapter a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawback);
        final long orderId = getIntent().getLongExtra("orderId",-1);
        final double price = getIntent().getDoubleExtra("amount",0.00);
        final EditText editText = (EditText) findViewById(R.id.et_memo);
        ListView reasonList = (ListView) findViewById(R.id.lv_reason);
        //reasonList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        a = new UserAdapter(mContext, true);
        reasonList.setAdapter(a);
        setListViewHeightBasedOnChildren(reasonList);
        findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundOrder(orderId, editText.getText().toString(), price);
            }
        });
        reasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        currentReason = reasons[0];
    }

    private void refundOrder(long orderId,String memo,double price) {
        JSONObject object = new JSONObject();
        User user = AccountManager.getInstance().getLoginAccount(this);

        currentReason = reasons[a.getLastId()];
        try {
            object.put("userId",user.getUserId());
            object.put("memo",memo);
            object.put("reason",currentReason);
            object.put("amount",price);
            object.put("type","apply");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        TravelApi.editOrderStatus(orderId, "refundApply", object, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                Toast.makeText(DrawbackActivity.this,"退款申请已提交",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                Toast.makeText(DrawbackActivity.this,"退款申请提交失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public class UserAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Boolean> status;
        private int lastId;

        public UserAdapter(Context c, boolean selected) {
            mContext = c;

        }

        public int getLastId(){
            return lastId;
        }
        @Override
        public int getCount() {
            return reasons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder1 viewHolder1;

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_reason, null);
                viewHolder1 = new ViewHolder1();
                viewHolder1.username = (CheckedTextView) convertView.findViewById(R.id.ctv_reason);
                convertView.setTag(viewHolder1);

            } else {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
            viewHolder1.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastId = position;
                    notifyDataSetChanged();
                }
            });
            viewHolder1.username.setText(reasons[position]);
            viewHolder1.username.setChecked(position == lastId);
            if (viewHolder1.username.isChecked()){
                viewHolder1.username.setTextColor(getResources().getColor(R.color.app_theme_color));
            }else {
                viewHolder1.username.setTextColor(getResources().getColor(R.color.color_text_ii));
            }
            return convertView;
        }

        class ViewHolder1 {
            private CheckedTextView username;
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
