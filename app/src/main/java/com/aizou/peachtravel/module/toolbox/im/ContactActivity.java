package com.aizou.peachtravel.module.toolbox.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rjm on 2015/3/30.
 */
public class ContactActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar titleBar;
    @InjectView(R.id.content_frame)
    FrameLayout contentFrame;
    private Fragment contactListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.inject(this);
        titleBar.getTitleTextView().setText("联系人");
        titleBar.getLeftTextView().setCompoundDrawables(null,null,null,null);
        titleBar.getLeftTextView().setText("取消");
        titleBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.push_bottom_out);
            }
        });
        titleBar.setRightViewImageRes(R.drawable.ic_add_friend);
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.getInstance(ContactActivity.this).showToast("添加新用户");
            }
        });
        contactListFragment = new ContactlistFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, contactListFragment).commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
