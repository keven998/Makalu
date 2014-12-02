package com.aizou.peachtravel.module.toolbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.widget.prv.PullToRefreshListView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/1.
 */
public class FavListActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.fav_lv)
    PullToRefreshListView mFavLv;
    @InjectView(R.id.edit_btn)
    Button mEditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

        TitleHeaderBar thb = (TitleHeaderBar)findViewById(R.id.title_bar);
        thb.getTitleTextView().setText("收藏夹");

        mFavLv.getRefreshableView().setAdapter(new CustomAdapter());
    }

    private void initView() {
        setContentView(R.layout.activity_fav_list);
        ButterKnife.inject(this);
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
                view.setTag(vh);
            } else {
                vh = (ViewHolder)view.getTag();
            }

            //TEST
            vh.imgView.setImageResource(R.drawable.ic_launcher);
            vh.titleView.setText("黄果树瀑布");
            vh.tvLocal.setText("安顺");
            vh.typeView.setText("景点");
            vh.descView.setText("不久前，巴萨主帅恩里克因战绩不佳陷入而信任危机，俱乐部高层对其执教能力信心不足，西班牙《机密报》披露，由于担心被死敌皇马长期压制，巴萨正在考虑未来由穆里尼奥替代恩里克的可能性，这或许在一定程度上促使了切尔西尽快着手续约事宜。\n" +
                    "\n" +
                    "　　穆里尼奥与切尔西的合同将于2017年6月到期，此前穆帅已多次在公开场合强调，只要俱乐部愿意一直聘用他，他永远都不会离开斯坦福桥。在接受英国广播公司采访时，穆帅说：“俱乐部知道我不想走。我也不会去想下一步打算，因为我根本没有这方面的考虑");
            vh.flagView.setImageResource(R.drawable.ic_gender_lady);

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

}
