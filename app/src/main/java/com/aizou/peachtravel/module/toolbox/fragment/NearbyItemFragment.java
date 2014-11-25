package com.aizou.peachtravel.module.toolbox.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyItemFragment extends PeachBaseFragment {
    @InjectView(R.id.fragment_mainTab_item_progressBar)
    ProgressBar mFragmentMainTabItemProgressBar;
    @InjectView(R.id.fragment_mainTab_item_textView)
    TextView mFragmentMainTabItemTextView;
    private int tabIndex;
    public static final String INTENT_INT_INDEX = "intent_int_index";

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabmain_item, container,false);
        ButterKnife.inject(this,rootView);
        tabIndex = getArguments().getInt(INTENT_INT_INDEX);
        mFragmentMainTabItemTextView.setText("界面" + " " + tabIndex + " 加载完毕");
        handler.sendEmptyMessageDelayed(1, 2000);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(1);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mFragmentMainTabItemProgressBar.setVisibility(View.GONE);
            mFragmentMainTabItemTextView.setVisibility(View.VISIBLE);
        }
    };
}
