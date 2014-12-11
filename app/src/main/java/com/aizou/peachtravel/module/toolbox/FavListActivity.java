package com.aizou.peachtravel.module.toolbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class FavListActivity extends PeachBaseActivity {
    public final static int CONST_TYPE_SPOT = 1;
    public final static int CONST_TYPE_FOOD = 2;
    public final static int CONST_TYPE_SHOP = 3;
    public final static int CONST_TYPE_STAY = 4;
    public final static int CONST_TYPE_NOTE = 5;

    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.fav_lv)
    PullToRefreshListView mFavLv;
    @InjectView(R.id.edit_btn)
    CheckedTextView mEditBtn;

    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
        initData();

        mTitleBar.getTitleTextView().setText("收藏夹");
        mTitleBar.enableBackKey(true);
        mTitleBar.setRightViewImageRes(R.drawable.selecter_ic_nav_filter);
        mTitleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        mFavLv.getRefreshableView().setAdapter(mAdapter = new CustomAdapter());
    }

    private void initView() {
        setContentView(R.layout.activity_fav_list);
        ButterKnife.inject(this);

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEditable = mEditBtn.isChecked();
                mEditBtn.setChecked(!isEditable);
                if (isEditable) {
                    //TODO
                } else {
                    //TODO
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {

    }

    class CustomAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public CustomAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View contentView, ViewGroup viewGroup) {
            View view = contentView;
            ViewHolder vh;
            if (view == null) {
                view = inflater.inflate(R.layout.favorite_list_item, null);
                vh = new ViewHolder();
                vh.imgView = (ImageView)view.findViewById(R.id.stand_img);
                vh.titleView = (TextView)view.findViewById(R.id.tv_title);
                vh.tvLocal = (TextView)view.findViewById(R.id.tv_local);
                vh.typeView = (TextView)view.findViewById(R.id.tv_type);
                vh.descView = (ExpandableTextView)view.findViewById(R.id.expand_text_view);
                vh.flagView = (ImageView)view.findViewById(R.id.iv_flag);

//                int width = LocalDisplay.SCREEN_WIDTH_PIXELS - LocalDisplay.dp2px(20);
//                int height = width * 260 / 640;
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
//                vh.imgView.setLayoutParams(lp);

                view.setTag(vh);
            } else {
                vh = (ViewHolder)view.getTag();
                if (!vh.descView.isCollpased()) {
                    vh.descView.reset();
                }
            }

            //TEST
            vh.imgView.setImageResource(R.drawable.guide_1);
            vh.titleView.setText("黄果树瀑布");
            vh.tvLocal.setText("安顺");

            vh.descView.setText("不久前，巴萨主帅恩里克因战绩不佳陷入而信任危机，俱乐部高层对其执教能力信心不足，西班牙《机密报》披露，由于担心被死敌皇马长期压制，巴萨正在考虑未来由穆里尼奥替代恩里克的可能性，这或许在一定程度上促使了切尔西尽快着手续约事宜。\n" +
                    "\n" +
                    "　　穆里尼奥与切尔西的合同将于2017年6月到期，此前穆帅已多次在公开场合强调，只要俱乐部愿意一直聘用他，他永远都不会离开斯坦福桥。在接受英国广播公司采访时，穆帅说：“俱乐部知道我不想走。我也不会去想下一步打算，因为我根本没有这方面的考虑");

            int type = 1;
            int res = 0;
            String typeText = "";
            switch (type) {
                case CONST_TYPE_FOOD:
                    typeText = "美食";
                    res = R.drawable.ic_standard_food;
                    break;

                case CONST_TYPE_NOTE:
                    typeText = "游记";
                    res = R.drawable.ic_standard_tnote;
                    break;

                case CONST_TYPE_SHOP:
                    typeText = "购物";
                    res = R.drawable.ic_standard_shopping;
                    break;

                case CONST_TYPE_STAY:
                    typeText = "酒店";
                    res = R.drawable.ic_standard_food;
                    break;

                case CONST_TYPE_SPOT:
                    typeText = "景点";
                    res = R.drawable.ic_standard_spot;
                    break;

                default:
                    break;
            }
            vh.typeView.setText(typeText);
            vh.flagView.setImageResource(res);

            return view;
        }

    }

    class ViewHolder {
        ImageView imgView;
        TextView  titleView;
        TextView  tvLocal;
        ImageView flagView;
        TextView  typeView;
        ExpandableTextView descView;
    }

    class FavoriteItem {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_to_right);
    }

}
