package com.aizou.peachtravel.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.aizou.peachtravel.R;

/**
 * Created by Rjm on 2014/12/6.
 */
public class MaskImage extends ImageView {
    int mMaskSource=0;

    public MaskImage(Context context){
        super(context);
    }
    public MaskImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImage, 0, 0);
        mMaskSource = a.getResourceId(R.styleable.MaskImage_mask, 0);
        a.recycle();
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        Bitmap original = bm;
        int minWidth=getMeasuredWidth();
        if(minWidth>bm.getWidth()){
            int height =minWidth* bm.getHeight()/bm.getWidth();
            original = Bitmap.createScaledBitmap(bm,minWidth,height,true);
        }
        NinePatchDrawable maskDrawable= (NinePatchDrawable) getResources().getDrawable(mMaskSource);
        maskDrawable.setBounds(0, 0, original.getWidth(), original.getHeight());
        Bitmap mask =  Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas maskCancas = new Canvas(mask);
        maskDrawable.draw(maskCancas);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        // if this is used frequently, may handle bitmaps explicitly
        // to reduce the intermediate drawable object
        setImageDrawable(new BitmapDrawable(getContext().getResources(), result));
    }
}
