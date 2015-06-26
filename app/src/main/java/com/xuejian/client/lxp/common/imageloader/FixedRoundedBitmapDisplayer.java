package com.xuejian.client.lxp.common.imageloader;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class FixedRoundedBitmapDisplayer implements BitmapDisplayer {

    protected final int topLeftRadius;
    protected final int topRightRadius;
    protected final int bottomLeftRadius;
    protected final int bottomRightRadius;
    protected final int margin;

    public FixedRoundedBitmapDisplayer(int radius) {
        this(radius,radius,radius,radius, 0);
    }
    public FixedRoundedBitmapDisplayer(int topLeftRadius, int topRightRadius,int bottomLeftRadius, int bottomRightRadius) {
        this(topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius, 0);
    }

    public FixedRoundedBitmapDisplayer(int topLeftRadius, int topRightRadius,int bottomLeftRadius, int bottomRightRadius,int marginPixels) {
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius=topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius= bottomRightRadius;
        this.margin = marginPixels;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageDrawable(new RoundedDrawable(bitmap, topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius, margin));
    }

    public static class RoundedDrawable extends Drawable {

        protected final int topLeftRadius;
        protected final int topRightRadius;
        protected final int bottomLeftRadius;
        protected final int bottomRightRadius;
        protected final int margin;

        protected final RectF mRect = new RectF(),
                mBitmapRect;
        protected final BitmapShader bitmapShader;
        private RoundRectShape mRoundRectShape;
        protected final Paint paint;

        public RoundedDrawable(Bitmap bitmap,int topLeftRadius, int topRightRadius,int bottomLeftRadius, int bottomRightRadius, int margin) {
            this.topLeftRadius = topLeftRadius;
            this.topRightRadius=topRightRadius;
            this.bottomLeftRadius = bottomLeftRadius;
            this.bottomRightRadius= bottomRightRadius;
            this.margin = margin;

            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
            mRoundRectShape = new RoundRectShape(new float[]{
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
            }, null, null);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);

        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(shaderMatrix);
            mRoundRectShape.resize(mRect.width(), mRect.height());


        }

        @Override
        public void draw(Canvas canvas) {

            canvas.drawRect(mRect,paint);
            drawLeftBottom(canvas);
            drawLeftTop(canvas);
            drawRightBottom(canvas);
            drawLeftBottom(canvas);
        }

        private void drawLeftTop(Canvas canvas) {
            Path path = new Path();
            path.moveTo(0, topLeftRadius);
            path.lineTo(0, 0);
            path.lineTo(topLeftRadius, 0);
            path.arcTo(new RectF(
                            0,
                            0,
                            topLeftRadius*2,
                            topLeftRadius*2),
                    -90,
                    -90);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawLeftBottom(Canvas canvas) {
            Path path = new Path();
            path.moveTo(0, mRect.height()-bottomLeftRadius);
            path.lineTo(0, mRect.height());
            path.lineTo(bottomLeftRadius, mRect.height());
            path.arcTo(new RectF(
                            0,
                            mRect.height()-bottomLeftRadius*2,
                            bottomLeftRadius*2,
                            mRect.height()),
                    90,
                    90);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawRightBottom(Canvas canvas) {
            Path path = new Path();
            path.moveTo(mRect.width()-bottomRightRadius,  mRect.height());
            path.lineTo(mRect.width(), mRect.height());
            path.lineTo(mRect.width(),  mRect.height()-bottomRightRadius);
            path.arcTo(new RectF(
                    mRect.width()-bottomRightRadius*2,
                    mRect.height()-bottomRightRadius*2,
                    mRect.width(),
                    mRect.height()), 0, 90);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawRightTop(Canvas canvas) {
            Path path = new Path();
            path.moveTo(mRect.width(), topRightRadius);
            path.lineTo(mRect.width(), 0);
            path.lineTo(mRect.width()-topRightRadius, 0);
            path.arcTo(new RectF(
                            mRect.width()-topRightRadius*2,
                            0,
                            mRect.width(),
                            topRightRadius*2),
                    -90,
                    90);
            path.close();
            canvas.drawPath(path, paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }
    }
}
