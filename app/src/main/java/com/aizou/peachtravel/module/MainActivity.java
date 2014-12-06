package com.aizou.peachtravel.module;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.core.utils.AssetUtils;
import com.aizou.core.widget.FragmentTabHost;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.TestBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.module.dest.RecDestFragment;
import com.aizou.peachtravel.module.my.MyFragment;
import com.aizou.peachtravel.module.toolbox.ToolboxFragment;
import com.google.gson.Gson;


public class MainActivity extends PeachBaseActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {ToolboxFragment.class, RecDestFragment.class, MyFragment.class,};

    //定义数组来存放按钮图片
//    private int mImageViewArray[] = {R.drawable.tab_home_btn,R.drawable.tab_message_btn,R.drawable.tab_selfinfo_btn,
//            R.drawable.tab_square_btn,R.drawable.tab_more_btn};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "目的地", "我的"};
    //Tab选项Tag
    private String mTagArray[] = {"Home", "Loc", "My"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTagArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

//        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
//        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }
    @Override
    protected void showConflictDialog(){
        AccountManager.getInstance().logout(this,null);
        try {
            if (conflictBuilder == null)
                conflictBuilder = new MaterialDialog.Builder(this);
            conflictBuilder.title("下线通知");
            conflictBuilder.content(R.string.connect_conflict);
            conflictBuilder.positiveText(R.string.ok);
            conflictBuilder.callback(new MaterialDialog.Callback() {
                @Override
                public void onNegative(MaterialDialog dialog) {


                }

                @Override
                public void onPositive(MaterialDialog dialog) {
                    dialog.dismiss();
                    conflictBuilder = null;
                    MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag("My");
                    if(myFragment!=null){
                        myFragment.refresh();
                    }

                }
            });
            conflictBuilder.cancelable(false);
            conflictBuilder.show();
            isConflict = true;
        } catch (Exception e) {
            Log.e("###", "---------color conflictBuilder error" + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
