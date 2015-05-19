package com.aizou.peachtravel.module.dest.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.InDestBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import static com.aizou.peachtravel.R.layout;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */

@SuppressLint("ValidFragment")
public class ExpertFragment extends PeachBaseFragment {

    public int pos;
    private FlowLayout fl;
    private ExpertDesAdapter expertDesAdapter;

    public ExpertFragment(int pos){
        this.pos=pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.expert_des_box,container,false);
        fl = (FlowLayout)rootView.findViewById(R.id.fl_expert_city_list);
        /*expertDesAdapter = new ExpertDesAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new InExpertDesHolder();
            }
        });*/
        initData();
        return rootView;
    }

    public void initData(){
        String[] in={"北京","北京","北京","北京","北京","北京","北京"};
        String[] out={"美国","美国","美国","美国","美国","美国","美国"};
        fl.removeAllViews();

        if(pos==0){
            for (int i=0;i<in.length;i++) {
            View contentView = View.inflate(getActivity(), R.layout.des_text_style, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(in[i]+"(30)");
            cityNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //进入达人列表
                    getActivity().finish();
                }
            });
            fl.addView(contentView);
            }
        }else if(pos==1){
            for (int i=0;i<out.length;i++) {
                View contentView = View.inflate(getActivity(), layout.des_text_style, null);
                final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
                cityNameTv.setText(out[i]+"(25)");
                cityNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //进入达人列表
                        getActivity().finish();
                    }
                });
                fl.addView(contentView);
            }
        }
    }

    private class ExpertDesAdapter extends ListViewDataAdapter<InDestBean> implements SectionIndexer {

        private List<String> sections;
        private SparseIntArray positionOfSection;
        private SparseIntArray sectionOfPosition;


        /**
         * @param viewHolderCreator The view holder creator will create a View Holder that extends {@link com.aizou.core.widget.listHelper.ViewHolderBase}
         */
        public ExpertDesAdapter(ViewHolderCreator viewHolderCreator) {

            super(viewHolderCreator);
            initSections();
        }



        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            //initSections();
        }

        public void initSections() {
            int count = 1;//getCount();
            positionOfSection = new SparseIntArray();
            sectionOfPosition = new SparseIntArray();
            sections = new ArrayList<String>();
            int section = 0;
            for (int i = 0; i < count; i++) {
                String letter = getItem(i).section;
                String beforeLetter = "";
                if (i > 0) {
                    beforeLetter = getItem(i - 1).section;
                }
                if (letter != null && !beforeLetter.equals(letter)) {
                    section++;
                    sections.add(letter);
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
        }

        @Override
        public Object[] getSections() {
            return sections.toArray();
        }

        @Override
        public int getPositionForSection(int section) {
            return positionOfSection.get(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            return sectionOfPosition.get(position);
        }
    }


    private class InExpertDesHolder extends ViewHolderBase<InDestBean> {
        //InDestBean作为返回的数据传入
        String[] in={"北京","北京","北京","北京","北京","北京","北京"};
        String[] out={"美国","美国","美国","美国","美国","美国","美国"};


        @Override
        public View createView(LayoutInflater layoutInflater) {
            return fl;
        }

        @Override
        public void showData(int position, final InDestBean itemData) {
            fl.removeAllViews();
            //for (final LocBean bean : itemData.locList) {
                View contentView = View.inflate(getActivity(), R.layout.des_text_style, null);
                final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
                if(pos==0){

                    cityNameTv.setText(in[position]);
                }else if(pos==1){
                    cityNameTv.setText(out[position]);
                }

                    //cityNameTv.setChecked(bean.isAdded);

                    /// 这一步必须要做,否则不会显示.
                    cityNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //进入达人列表
                        }
                    });


                fl.addView(contentView);
           // }
        }
    }

}
