package com.xuejian.client.lxp.common.widget.SuperToast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuejian.client.lxp.R;


/**
 * Created by yibiao.qin on 2015/8/26.
 */
public class SuperToast {
    private static final String TAG = "SuperToast";
    private static final String ERROR_CONTEXTNULL = " - You cannot use a null context.";
    private static final String ERROR_DURATIONTOOLONG = " - You should NEVER specify a duration greater than four and a half seconds for a SuperToast.";
    private SuperToast.Animations mAnimations;
    private Context mContext;
    private int mGravity;
    private int mDuration;
    private int mTypefaceStyle;
    private int mBackground;
    private int mXOffset;
    private int mYOffset;
    private LinearLayout mRootLayout;
    private SuperToast.OnDismissListener mOnDismissListener;
    private TextView mMessageTextView;
    private View mToastView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowManagerParams;
    private int animId;
    public SuperToast(Context context) {
        this.mAnimations = SuperToast.Animations.FADE;
        this.mGravity = 81;
        this.mDuration = 2000;
        this.mXOffset = 0;
        this.mYOffset = 0;
        if(context == null) {
            throw new IllegalArgumentException("SuperToast - You cannot use a null context.");
        } else {
            this.mContext = context;
            this.mYOffset = context.getResources().getDimensionPixelSize(R.dimen.toast_hover);
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mToastView = layoutInflater.inflate(R.layout.supertoast, (ViewGroup)null);
            this.mWindowManager = (WindowManager)this.mToastView.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            this.mRootLayout = (LinearLayout)this.mToastView.findViewById(R.id.root_layout);
            this.mMessageTextView = (TextView)this.mToastView.findViewById(R.id.message_textview);
        }
    }

    public SuperToast(Context context, Style style) {
        this.mAnimations = SuperToast.Animations.FADE;
        this.mGravity = 81;
        this.mDuration = 2000;
        this.mXOffset = 0;
        this.mYOffset = 0;
        if(context == null) {
            throw new IllegalArgumentException("SuperToast - You cannot use a null context.");
        } else {
            this.mContext = context;
            this.mYOffset = context.getResources().getDimensionPixelSize(R.dimen.toast_hover);
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mToastView = layoutInflater.inflate(R.layout.supertoast, (ViewGroup)null);
            this.mWindowManager = (WindowManager)this.mToastView.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            this.mRootLayout = (LinearLayout)this.mToastView.findViewById(R.id.root_layout);
            this.mMessageTextView = (TextView)this.mToastView.findViewById(R.id.message_textview);
            this.setStyle(style);
        }
    }

    public void show() {
        this.mWindowManagerParams = new WindowManager.LayoutParams();
        this.mWindowManagerParams.height = -2;
        this.mWindowManagerParams.width = -1;
        this.mWindowManagerParams.flags = 152;
        this.mWindowManagerParams.format = -3;
        this.mWindowManagerParams.windowAnimations = this.getAnimation();
        this.mWindowManagerParams.type = 2005;
        this.mWindowManagerParams.gravity = this.mGravity;
        this.mWindowManagerParams.x = this.mXOffset;
        this.mWindowManagerParams.y = this.mYOffset;
        ManagerSuperToast.getInstance().add(this);
    }

    public void setText(CharSequence text) {
        this.mMessageTextView.setText(text);
    }

    public CharSequence getText() {
        return this.mMessageTextView.getText();
    }

    public void setTypefaceStyle(int typeface) {
        this.mTypefaceStyle = typeface;
        this.mMessageTextView.setTypeface(this.mMessageTextView.getTypeface(), typeface);
    }

    public int getTypefaceStyle() {
        return this.mTypefaceStyle;
    }

    public void setTextColor(int textColor) {
        this.mMessageTextView.setTextColor(textColor);
    }

    public int getTextColor() {
        return this.mMessageTextView.getCurrentTextColor();
    }

    public void setTextSize(int textSize) {
        this.mMessageTextView.setTextSize((float)textSize);
    }

    public float getTextSize() {
        return this.mMessageTextView.getTextSize();
    }

    public void setDuration(int duration) {
        if(duration > 4500) {
            Log.e("SuperToast", "SuperToast - You should NEVER specify a duration greater than four and a half seconds for a SuperToast.");
            this.mDuration = 4500;
        } else {
            this.mDuration = duration;
        }

    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setIcon(int iconResource, SuperToast.IconPosition iconPosition) {
        if(iconPosition == SuperToast.IconPosition.BOTTOM) {
            this.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, (Drawable)null, (Drawable)null, this.mContext.getResources().getDrawable(iconResource));
        } else if(iconPosition == SuperToast.IconPosition.LEFT) {
            this.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(this.mContext.getResources().getDrawable(iconResource), (Drawable)null, (Drawable)null, (Drawable)null);
        } else if(iconPosition == SuperToast.IconPosition.RIGHT) {
            this.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, (Drawable)null, this.mContext.getResources().getDrawable(iconResource), (Drawable)null);
        } else if(iconPosition == SuperToast.IconPosition.TOP) {
            this.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, this.mContext.getResources().getDrawable(iconResource), (Drawable)null, (Drawable)null);
        }

    }

    public void setBackground(int background) {
        this.mBackground = background;
        this.mRootLayout.setBackgroundResource(background);
    }

    public int getBackground() {
        return this.mBackground;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mGravity = gravity;
        this.mXOffset = xOffset;
        this.mYOffset = yOffset;
    }

    public void setAnimations(SuperToast.Animations animations) {
        this.mAnimations = animations;
    }
    public void setAnimations(int animations) {
        this.animId = animations;
    }
    public SuperToast.Animations getAnimations() {
        return this.mAnimations;
    }

    public void setOnDismissListener(SuperToast.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public SuperToast.OnDismissListener getOnDismissListener() {
        return this.mOnDismissListener;
    }

    public void dismiss() {
        ManagerSuperToast.getInstance().removeSuperToast(this);
    }

    public TextView getTextView() {
        return this.mMessageTextView;
    }

    public View getView() {
        return this.mToastView;
    }

    public boolean isShowing() {
        return this.mToastView != null && this.mToastView.isShown();
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public WindowManager.LayoutParams getWindowManagerParams() {
        return this.mWindowManagerParams;
    }

    private int getAnimation() {
        if (animId!=0)return animId;
        return this.mAnimations == SuperToast.Animations.FLYIN?16973827:(this.mAnimations == SuperToast.Animations.SCALE?16973826:(this.mAnimations == SuperToast.Animations.POPUP?16973910:16973828));
    }

    private void setStyle(Style style) {
      //  this.setAnimations(style.animations);
        this.setTypefaceStyle(style.typefaceStyle);
        this.setTextColor(style.textColor);
        this.setBackground(style.background);
    }

    public static SuperToast create(Context context, CharSequence textCharSequence, int durationInteger) {
        SuperToast superToast = new SuperToast(context);
        superToast.setText(textCharSequence);
        superToast.setDuration(durationInteger);
        return superToast;
    }

    public static SuperToast create(Context context, CharSequence textCharSequence, int durationInteger, SuperToast.Animations animations) {
        SuperToast superToast = new SuperToast(context);
        superToast.setText(textCharSequence);
        superToast.setDuration(durationInteger);
        superToast.setAnimations(animations);
        return superToast;
    }

    public static SuperToast create(Context context, CharSequence textCharSequence, int durationInteger, Style style) {
        SuperToast superToast = new SuperToast(context);
        superToast.setText(textCharSequence);
        superToast.setDuration(durationInteger);
        superToast.setStyle(style);
        return superToast;
    }

    public static void cancelAllSuperToasts() {
        ManagerSuperToast.getInstance().cancelAllSuperToasts();
    }

    public static enum IconPosition {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

        private IconPosition() {
        }
    }

    public static enum Type {
        STANDARD,
        PROGRESS,
        PROGRESS_HORIZONTAL,
        BUTTON;

        private Type() {
        }
    }

    public static class TextSize {
        public static final int EXTRA_SMALL = 12;
        public static final int SMALL = 14;
        public static final int MEDIUM = 16;
        public static final int LARGE = 18;

        public TextSize() {
        }
    }

    public static class Duration {
        public static final int VERY_SHORT = 1500;
        public static final int SHORT = 2000;
        public static final int MEDIUM = 2750;
        public static final int LONG = 3500;
        public static final int EXTRA_LONG = 4500;

        public Duration() {
        }
    }


    public static enum Animations {
        FADE,
        FLYIN,
        SCALE,
        POPUP;

        private Animations() {
        }
    }

    public static class Background {
        public static final int BLACK = Style.getBackground(0);
        public static final int BLUE = Style.getBackground(1);
        public static final int GRAY = Style.getBackground(2);
        public static final int GREEN = Style.getBackground(3);
        public static final int ORANGE = Style.getBackground(4);
        public static final int PURPLE = Style.getBackground(5);
        public static final int RED = Style.getBackground(6);
        public static final int WHITE = Style.getBackground(7);

        public Background() {
        }
    }

    public interface OnDismissListener {
        void onDismiss(View var1);
    }

    public interface OnClickListener {
        void onClick(View var1, Parcelable var2);
    }
}