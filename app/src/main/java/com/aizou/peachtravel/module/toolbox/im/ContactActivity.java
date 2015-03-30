package com.aizou.peachtravel.module.toolbox.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

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
        titleBar.enableBackKey(true);
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
