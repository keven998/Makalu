package com.xuejian.client.lxp.common.widget.SuperToast;

import android.os.Build;

import com.xuejian.client.lxp.R;

/**
 * Created by yibiao.qin on 2015/8/26.
 */
public class Style {
    public static final int BLACK = 0;
    public static final int BLUE = 1;
    public static final int GRAY = 2;
    public static final int GREEN = 3;
    public static final int ORANGE = 4;
    public static final int PURPLE = 5;
    public static final int RED = 6;
    public static final int WHITE = 7;
    public SuperToast.Animations animations;
    public int background;
    public int typefaceStyle;
    public int textColor;
    public int dividerColor;
    public int buttonTextColor;

    public Style() {
        this.animations = SuperToast.Animations.FADE;
        this.background = getBackground(2);
        this.typefaceStyle = 0;
        this.textColor = -1;
        this.dividerColor = -1;
        this.buttonTextColor = -3355444;
    }

    public static Style getStyle(int styleType) {
        Style style = new Style();
        switch(styleType) {
            case 0:
                style.textColor = -1;
                style.background = getBackground(0);
                style.dividerColor = -1;
                return style;
            case 1:
                style.textColor = -1;
                style.background = getBackground(1);
                style.dividerColor = -1;
                return style;
            case 2:
                style.textColor = -1;
                style.background = getBackground(2);
                style.dividerColor = -1;
                style.buttonTextColor = -7829368;
                return style;
            case 3:
                style.textColor = -1;
                style.background = getBackground(3);
                style.dividerColor = -1;
                return style;
            case 4:
                style.textColor = -1;
                style.background = getBackground(4);
                style.dividerColor = -1;
                return style;
            case 5:
                style.textColor = -1;
                style.background = getBackground(5);
                style.dividerColor = -1;
                return style;
            case 6:
                style.textColor = -1;
                style.background = getBackground(6);
                style.dividerColor = -1;
                return style;
            case 7:
                style.textColor = -12303292;
                style.background = getBackground(7);
                style.dividerColor = -12303292;
                style.buttonTextColor = -7829368;
                return style;
            default:
                style.textColor = -1;
                style.background = getBackground(2);
                style.dividerColor = -1;
                return style;
        }
    }

    public static Style getStyle(int styleType, SuperToast.Animations animations) {
        Style style = new Style();
        style.animations = animations;
        switch(styleType) {
            case 0:
                style.textColor = -1;
                style.background = getBackground(0);
                style.dividerColor = -1;
                return style;
            case 1:
                style.textColor = -1;
                style.background = getBackground(1);
                style.dividerColor = -1;
                return style;
            case 2:
                style.textColor = -1;
                style.background = getBackground(2);
                style.dividerColor = -1;
                style.buttonTextColor = -7829368;
                return style;
            case 3:
                style.textColor = -1;
                style.background = getBackground(3);
                style.dividerColor = -1;
                return style;
            case 4:
                style.textColor = -1;
                style.background = getBackground(4);
                style.dividerColor = -1;
                return style;
            case 5:
                style.textColor = -1;
                style.background = getBackground(5);
                style.dividerColor = -1;
                return style;
            case 6:
                style.textColor = -1;
                style.background = getBackground(6);
                style.dividerColor = -1;
                return style;
            case 7:
                style.textColor = -12303292;
                style.background = getBackground(7);
                style.dividerColor = -12303292;
                style.buttonTextColor = -7829368;
                return style;
            default:
                style.textColor = -1;
                style.background = getBackground(2);
                style.dividerColor = -1;
                return style;
        }
    }

    public static int getBackground(int style) {
        if(Build.VERSION.SDK_INT >= 19) {
            switch(style) {
                case 0:
                    return R.drawable.background_kitkat_black;
                case 1:
                    return  R.drawable.background_kitkat_blue;
                case 2:
                    return  R.drawable.background_kitkat_gray;
                case 3:
                    return  R.drawable.background_kitkat_green;
                case 4:
                    return  R.drawable.background_kitkat_orange;
                case 5:
                    return  R.drawable.background_kitkat_purple;
                case 6:
                    return  R.drawable.background_kitkat_red;
                case 7:
                    return  R.drawable.background_kitkat_white;
                default:
                    return  R.drawable.background_kitkat_gray;
            }
        } else {
            switch(style) {
                case 0:
                    return  R.drawable.background_standard_black;
                case 1:
                    return  R.drawable.background_standard_blue;
                case 2:
                    return  R.drawable.background_standard_gray;
                case 3:
                    return  R.drawable.background_standard_green;
                case 4:
                    return  R.drawable.background_standard_orange;
                case 5:
                    return  R.drawable.background_standard_purple;
                case 6:
                    return  R.drawable.background_standard_red;
                case 7:
                    return  R.drawable.background_standard_white;
                default:
                    return  R.drawable.background_standard_gray;
            }
        }
    }
}

