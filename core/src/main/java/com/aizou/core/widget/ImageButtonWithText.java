package com.aizou.core.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lxp_dqm07 on 2015/4/21.
 */
public class ImageButtonWithText extends LinearLayout{

    private ImageButton imageViewbutton;

    private  TextView   textView;

    public ImageButtonWithText(Context context,AttributeSet attrs) {
        super(context,attrs);
        // TODO Auto-generated constructor stub

        imageViewbutton = new ImageButton(context, attrs);

        imageViewbutton.setPadding(10, 0, 0, 0);

        textView =new TextView(context, attrs);
        //水平居中
        textView.setGravity(Gravity.CENTER);

        textView.setPadding(0, 0, 0, 0);

        setClickable(true);

        setFocusable(true);

        setBackgroundResource(android.R.drawable.screen_background_light_transparent);

        setOrientation(LinearLayout.HORIZONTAL);

        setGravity(Gravity.CENTER_VERTICAL);

        addView(textView);

        addView(imageViewbutton);
    }

    public void setImageDrawable(Drawable drawable){

      imageViewbutton.setImageDrawable(drawable);
    }
}
