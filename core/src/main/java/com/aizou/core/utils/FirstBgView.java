package com.aizou.core.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.aizou.core.constant.LayoutValue;


public class FirstBgView extends View {

	/*
	 * 自定义控件一般写两个构造方法 CoordinatesView(Context context)用于java硬编码创建控件
	 * 如果想要让自己的控件能够通过xml来产生就必须有第2个构造方法 CoordinatesView(Context context,
	 * AttributeSet attrs) 因为框架会自动调用具有AttributeSet参数的这个构造方法来创建继承自View的控件
	 */
	public FirstBgView(Context context) {
		super(context, null);
	}

	public FirstBgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	 * 自定义控件一般都会重载onDraw(Canvas canvas)方法，来绘制自己想要的图形
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStrokeWidth(3);
		paint.setColor(Color.RED);
		int width = getWidth();
		int hight = getHeight();
		// 画坐标轴
		if (canvas != null) {
			// 画直线
			canvas.drawLine((int) (width * (1 - LayoutValue.FIRST_RIGHT_H_PERCENT - LayoutValue.FIRST_RIGHT_X_PERCENT_WIDTH)),
					hight, (int) (width * (1 - LayoutValue.FIRST_RIGHT_H_PERCENT)),
					(int) (hight * (1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH)), paint);
			canvas.drawLine((int) (width * (1 - LayoutValue.FIRST_RIGHT_H_PERCENT)),
					(int) (hight * ((1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH))), width ,
					(int) (hight * ((1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH))), paint);
//			canvas.drawCircle(0, (int) (hight * (1.5f)), hight, paint);
			canvas.drawLine(0,
					(int) (hight * ((1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH))), width*LayoutValue.FIRST_RIGHT_H_PERCENT ,
					(int) (hight * ((1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH))), paint);
			
			canvas.drawLine( width*LayoutValue.FIRST_RIGHT_H_PERCENT,
					(int) (hight * (1- LayoutValue.FIRST_RIGHT_X_PERCENT_HEIGTH)), (int) (width * ( LayoutValue.FIRST_RIGHT_H_PERCENT+LayoutValue.FIRST_RIGHT_X_PERCENT_WIDTH)),
					hight, paint);
		}

	}

}
