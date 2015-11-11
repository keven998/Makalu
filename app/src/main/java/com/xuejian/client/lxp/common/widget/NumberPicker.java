package com.xuejian.client.lxp.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

/**
 * Created by yibiao.qin on 2015/11/9.
 */
public class NumberPicker extends FrameLayout {
    OnButtonClick listenr ;

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_number_picker, this);
        ImageView reduce = (ImageView) findViewById(R.id.reduce);
        ImageView add = (ImageView) findViewById(R.id.add);
        final TextView number = (TextView) findViewById(R.id.num_text);
        int num = 0;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker);
            num = array.getInteger(R.styleable.NumberPicker_default_num, 0);
            array.recycle();
        }
        number.setText(String.valueOf(num));
        reduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(number.getText().toString());
                if (current > 0) {
                    number.setText(String.valueOf(current - 1));
                    if (listenr!=null){
                        listenr.OnValueChange(current - 1);
                    }
                }
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(number.getText().toString());
                number.setText(String.valueOf(current + 1));
                if (listenr!=null){
                    listenr.OnValueChange(current + 1);
                }
            }
        });
    }

    public void setListenr(OnButtonClick listenr) {
        this.listenr = listenr;
    }

    public interface OnButtonClick {
        void OnValueChange(int value);
    }
}
