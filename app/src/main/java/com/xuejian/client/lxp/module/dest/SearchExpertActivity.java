package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/9/2.
 */
public class SearchExpertActivity extends PeachBaseActivity {
    @InjectView(R.id.search_city_cancel)
    ImageView iv_cancleSearch;
    @InjectView(R.id.search_city_text)
    EditText et_search;
    @InjectView(R.id.search_city_button)
    TextView tv_search;
    @InjectView(R.id.search_city_bar)
    LinearLayout searchExpertBar;
    @InjectView(R.id.search_city_item)
    ListView searchCityItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_some_city);
        ButterKnife.inject(this);
        iv_cancleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
