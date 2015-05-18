package com.aizou.peachtravel.module.toolbox;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class HisMainPageActivity extends PeachBaseActivity implements View.OnClickListener {

    @ViewInject(R.id.his_add_friend)
    TextView add_friend;
    @ViewInject(R.id.his_query)
    TextView query;
    @ViewInject(R.id.tv_his_brithday)
    TextView age;
    @ViewInject(R.id.tv_his_resident)
    TextView resident;
    @ViewInject(R.id.tv_his_sign)
    TextView sign;
    @ViewInject(R.id.tv_his_foot_print)
    TextView foot_print;
    @ViewInject(R.id.his_destination)
    FlowLayout his_destinations;
    @ViewInject(R.id.ll_his_trip_plan)
    TextView his_trip_plan;
    @ViewInject(R.id.tv_his_status)
    TextView his_status;
    @ViewInject(R.id.tv_his_id)
    TextView his_id;
    @ViewInject(R.id.user_his_xingzuo)
    TextView xingzuo;
    @ViewInject(R.id.iv_his_gender)
    ImageView his_gender;
    @ViewInject(R.id.user_his_level)
    TextView his_level;
    @ViewInject(R.id.tv_his_nickname)
    TextView his_name;
    @ViewInject(R.id.iv_his_avatar)
    ImageView his_avatar;
    @ViewInject(R.id.tv_his_title_bar_title)
    TextView title_name;
    @ViewInject(R.id.all_his_pics_sv)
    HorizontalScrollView his_pics_sv;


    private ImageView my_pics_cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        ViewUtils.inject(this);
        initFlDestion();
        initScrollView();
    }

    public void initFlDestion(){
        his_destinations.removeAllViews();
        String[] names={"美国","日本","澳大利亚","乌兹别克斯坦","墨西哥"};
        for(int j=0;j<5;j++){
            View contentView = View.inflate(HisMainPageActivity.this, R.layout.des_text_style, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(names[j]);
            his_destinations.addView(contentView);
        }
    }

    public void initScrollView(){
        his_pics_sv.removeAllViews();
        LinearLayout llPics=new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for(int i=0;i<3;i++){
            View view=View.inflate(HisMainPageActivity.this,R.layout.my_all_pics_cell,null);
            my_pics_cell=(ImageView)view.findViewById(R.id.my_pics_cell);
            if(i==2){
                my_pics_cell.setImageResource(R.drawable.smiley_add_btn);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(HisMainPageActivity.this).showToast("添加图片");
                    }
                });
            }
            else{
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(HisMainPageActivity.this).showToast("show pics");
                    }
                });

            }
            llPics.addView(view);
        }
        his_pics_sv.addView(llPics);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
        public void finish() {
            super.finish();
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

    @Override
    public void onClick(View v) {

    }
}
