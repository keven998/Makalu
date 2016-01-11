package com.xuejian.client.lxp.common.widget.circleMenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xuejian.client.lxp.R;


/**
 * Created by yibiao.qin on 2015/11/5.
 */
public class CircleItem extends LinearLayout {

    // Angle is used for the positioning on the circle
    private float angle = 0;
    // Position represents the index of this view in the viewgroups children array
    private int position = 0;
    // The name of the view
    private String name;

    /**
     * Return the angle of the view.
     * @return Returns the angle of the view in degrees.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Set the angle of the view.
     * @param angle The angle to be set for the view.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Return the position of the view.
     * @return Returns the position of the view.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the position of the view.
     * @param position The position to be set for the view.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Return the name of the view.
     * @return Returns the name of the view.
     */
    public String getName(){
        return name;
    }

    /**
     * Set the name of the view.
     * @param name The name to be set for the view.
     */
    public void setName(String name){
        this.name = name;
    }


    public CircleItem(Context context) {
        this(context, null);
    }

    public CircleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CircleItem);

            this.name = array.getString(R.styleable.CircleItem_item_name);
        }
    }

    public CircleItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
}
