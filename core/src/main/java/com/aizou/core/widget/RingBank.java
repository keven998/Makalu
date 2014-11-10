package com.aizou.core.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.io.InputStream;

/**
 * 一个主题图标+若干菜单图标 主题图标必须最先添加
 */
public class RingBank extends ViewGroup implements Runnable {
	private String TAG = "info";
	boolean isDebug = false;

	private final float angle2Radian = (float) (Math.PI / 180);
	/**
	 * startOrder是表示在child中,转圈的起始序列, =2是因为主题图标也是一个child，但是不参与转圈 另外还有中间那个地球
	 * 设置这个值的原因，是因为除了主题，可能要加更多的不参与转圈的图标
	 */
	private final int startOrder = 0; // 菜单图标的起始
	/** 转圈图标之间的角度间距 */
	private float mdAngle = 18f;
	/** 根据child的个数获得：整体的所占的角度 */
	private float angleScope = 0;
	private float innerBeginAngle = 0; // 圆环区域内径起始角度
	private float innerEndAngle = 90; // 圆环区域内径结束角度
	/** 页面显示的个数 */
	private int visibleCount = 5;

	private int viewWidth;
	private int viewHeight;

	/** 圆心X坐标 */
	public int xRef = 0;
	/** 圆心Y坐标 */
	public int yRef = 0;
	/** 圆半径 */
	public int rRef = 0;
	private int xTheme = 0;
	private int yTheme = 0;

	public double rAngle;// 转动的角度，一直记录累加
	private Event lastEvent;

	private Path ringArea = new Path();
	// private Paint ringPaint = new Paint();

	// 启动初期转动：停止条件时间到、用户点击
	private boolean isAutoRotate = true; // 用户点击则置false
	private Thread rotateSelf = new Thread(this);

	// 抬手后fling
	private Event lastDown = null;
	private Event lastUp = null;
	private boolean isTouchStop = false; // 按下后停止线程

	// 是否使用放大
	private boolean isUseScale = false;
	// private int targetViewCount = -1;
	private float ratio = 1;
	private Context mContext;
	private PointF centerF = new PointF();
	private PointF lastF = new PointF();

	public static double oneMovedx;// 一次移动的距离
	// private boolean isOnclickedble;// 是否响应点击事件
	public static double onMovedxStatic;// 一次移动的净距离

	// private boolean isStoped = false;// 是否停止转动了

	public RingBank(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * 此方法被内部外部调用，不同于onLayout被父视图调用 根据rAngle调整所有子 view的位置
	 */
	private void reLayoutChild() {

		int count = getChildCount();
		if (count <= 0)
			return;
		// 菜单图标
		angleScope = (count - startOrder) * mdAngle;
		float theta = 0, tempt = 0;
		RotateAnimation rotateAnimation = null;
		for (int i = startOrder; i < count; i++) {

			tempt = theta = (float) (mdAngle * (i - startOrder) + rAngle
					+ innerBeginAngle + mdAngle / 2); // 90是2中坐标的角度差 固定值
			View child = getChildAt(i);
			theta *= angle2Radian; // to 弧度
			int left = xRef + (int) (rRef * Math.sin(theta));
			int top = yRef - (int) (rRef * Math.cos(theta));
			left -= (child.getMeasuredWidth() >> 1);
			top -= (child.getMeasuredHeight() >> 1);
			int v = (int) (rAngle / mdAngle);
			child.setVisibility(View.VISIBLE);
			rotateAnimation = new RotateAnimation(tempt, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setDuration(0);
			rotateAnimation.setFillAfter(true);
			 child.startAnimation(rotateAnimation);//子菜单是否旋转角度
			child.layout(left, top, left + child.getMeasuredWidth(), top
					+ child.getMeasuredHeight());

		}
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		// canvas抗锯齿
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		super.dispatchDraw(canvas);
		reLayoutChild();

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// handleActionDown(e);
			lastDown = new Event(e);
			isAutoRotate = false;
			isTouchStop = true;
			onMovedxStatic = 0;
			oneMovedx = 0;
			break;
		case MotionEvent.ACTION_UP:
			lastUp = new Event(e);
			backTonormal(getNear(rAngle));
			lastEvent = null;
			break;
		case MotionEvent.ACTION_MOVE:
			lastF.set(e.getX(), e.getY());

			double distance = GeoLine.distanceOf2PointF(centerF, lastF);
			if (distance < rRef + 40)
				handleActionMove(e);

			break;
		}
		super.dispatchTouchEvent(e);
		return true;
	}

	/**
	 * 划屏停止后 菜单归位到固定卡槽的方法
	 * 
	 * @param nd
	 */
	private void backTonormal(final double nd) {
		final float dA = 1.2f;// 控制归位的速度

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				double temprAngle = rAngle;
				float i = 0;
				try {
					Thread.sleep(50); // 防止启动时候卡住
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isAutoRotate = true;
				while (isAutoRotate && i < Math.abs(nd)) { // 一个while循环视作一帧
					if (nd > 0)
						rAngle += dA; // 每过一个循环，角度 +1
					else if (nd < 0) {
						rAngle -= dA;
					}
					i += dA;// 每过一个循环，帧数 +1
					if (i >= Math.abs(nd)) {
						rAngle = temprAngle + nd;
					}
					postInvalidate();
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				isAutoRotate = false;
			}
		}).start();

	}

	private void handleActionMove(MotionEvent e) {
		if (null == lastEvent) {
			lastEvent = new Event(e);
			return;
		}
		// 计算2点对(xRef,yRef)的张角，然后设置到rAngle
		PointF center = new PointF(xRef, yRef);
		PointF lastP = new PointF(lastEvent.x, lastEvent.y);

		PointF nowP = new PointF(e.getX(), e.getY());

		double angle0 = GeoLine.fieldAngleToPoint(center, lastP);
		double angle1 = GeoLine.fieldAngleToPoint(center, nowP);

		lastEvent = new Event(e);

		double dAngle = angle1 - angle0; // dAngle有时会接近+/-360 时发生突变，要修改
		if (dAngle > 280) { // 280的原因是经过测试，突变角度最大在80度以内(280+80=360)
			dAngle -= 360;
		} else if (dAngle < -280) {
			dAngle += 360;
		}
		oneMovedx += Math.abs(dAngle);
		onMovedxStatic += dAngle;
		if (Math.abs(onMovedxStatic) < 3) {
			return;
		}

		rAngle += dAngle;
		if (rAngle > mdAngle * 2) {
			rAngle -= dAngle;
			return;
		}
		if (rAngle + innerBeginAngle < innerEndAngle
				- (mdAngle * (getChildCount() - startOrder)) - mdAngle * 2) {
			rAngle -= dAngle;
			return;
		}

		reLayoutChild();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private boolean isLoaded = false;// 已加载过了

	@Override
	public void onMeasure(int widthSpec, int heightSpec) {
		viewWidth = MeasureSpec.getSize(widthSpec);
		viewHeight = MeasureSpec.getSize(heightSpec);
		setMeasuredDimension(viewWidth, viewHeight);
		// 对child进行measure
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			measureChild(child, widthSpec, heightSpec);// 考虑了margin,
		}
		if (!isLoaded) {
			initGeo();
			isLoaded = true;
		}
	}

	private void initGeo() {
		// int w = getWidth();
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		// int h = getHeight();
		// int min = Math.min(w, h);

		int h1 = h / 4;
		// xRef = (int) (w * 0.5);
		xRef = 0;
		if (h != 0) {
			// rRef = h / 10 + (5 * w * w) / (8 * h);
			// rRef = h / 50 + ( w * w) / (8 * h);
			rRef = (int) (h * 0.85);
			// yRef = h + rRef - h1;
			yRef = h;

			centerF.set(xRef, yRef);

			xTheme = (int) (w * 0.05f); // theme的left/top
			yTheme = (int) (h * 0.20f);
			ringArea.reset();
			int w1 = (int) (w * 0.15f);
			ringArea.addCircle(xRef, yRef, rRef - w1, Direction.CW);
			ringArea.addCircle(xRef, yRef, rRef + w1, Direction.CCW);
			setInitVisibleAngle();
			// 放大图标
			// int maxChildH = 0;
			// int maxChildW = 0;
			// int count = getChildCount();
			// for (int i = startOrder; i < count; i++) {
			// int ch = getChildAt(i).getMeasuredHeight();
			// int cw = getChildAt(i).getMeasuredWidth();
			// if (ch > maxChildH)
			// maxChildH = ch;
			// if (cw > maxChildW)
			// maxChildW = cw;
			// }
			// if (0 != maxChildH && 0 != maxChildW) { // 2张图片
			// originalBmp = Bitmap.createBitmap(maxChildW, maxChildH,
			// Bitmap.Config.ARGB_8888);
			// Log.d("info",
			// "originalBmp.getHeight()=="+originalBmp.getHeight()+"  originalBmp.getWidth()=="+originalBmp.getWidth());
			// originalCanvas = new Canvas(originalBmp);
			// scaledBmp = Bitmap.createBitmap(
			// (int) (maxChildW * scaledRatio),
			// (int) (maxChildH * scaledRatio),
			// Bitmap.Config.ARGB_8888);
			// scaledCanvas = new Canvas(scaledBmp);
			// Log.d("info",
			// "scaledBmp.getHeight()=="+scaledBmp.getHeight()+"  scaledBmp.getWidth()=="+scaledBmp.getWidth());
			// scaledMatrix = new Matrix();
			// scaledMatrix.postScale(scaledRatio, scaledRatio);
			// }
		}

	}

	class Event {
		float x;
		float y;
		float t;

		public Event() {
		}

		public Event(MotionEvent e) {
			x = e.getX();
			y = e.getY();
			t = e.getEventTime();
		}

		public String toString() {
			return "x=" + x + ",y=" + y + ",t=" + t;
		}
	}

	/**
	 * 计算初始可视角度范围
	 */
	private void setInitVisibleAngle() {
		// PointF center = new PointF(xRef, yRef); // 圆心点
		// GeoLine horizontalBar = new GeoLine(1, 0, -getWidth()); // x = w
		// GeoLine verticalBar = new GeoLine(1, 0, 0); // x=0
		// List<PointF> hPoints = GeoLine.intersectionWithCircle(center,
		// horizontalBar, rRef); // 计算xbar 与 innerRing的交点
		// List<PointF> vPoints = GeoLine.intersectionWithCircle(center,
		// verticalBar, rRef); // 计算ybar 与 innerRing的交点int hPointsNum = 0;
		// int vPointsNum = 0;
		// int hPointsNum = 0;
		// if (null != hPoints)
		// hPointsNum = hPoints.size();
		// if (null != vPoints)
		// vPointsNum = vPoints.size();
		// if (null != hPoints && null != vPoints)
		// if (hPointsNum > 1 && vPointsNum > 1) {
		// PointF pY1 = vPoints.get(1); // x =0的 焦点
		// PointF pY2 = hPoints.get(1); // x = w 的焦点
		// innerBeginAngle = GeoLine.fieldAngleToPoint(center, pY1);
		// }
		// // 计算中以原点,x正向为0度(3点方向)
		// // 测试发现,系统以12点为0度，因此进行修正
		// innerBeginAngle += 90;
		// innerEndAngle = -innerBeginAngle;
		// mdAngle = (innerEndAngle - innerBeginAngle) / visibleCount;
		// // if (BTCCMWApplication.getInstance().isAnimation()) {
		// // rAngle = -(getChildCount() - startOrder) * mdAngle;
		// // } else {
		rAngle = 0;
		// }
	}

	@Override
	public void run() {

		// AnimationSet as = new AnimationSet(true);
		try {
			Thread.sleep(100); // 防止启动时候卡住
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		float i = 0;
		float dA = 1.3f;
		while (isAutoRotate && i < (getChildCount() - startOrder - 2) * mdAngle
				&& null == lastEvent) {
			rAngle += dA;
			i += dA;
			postInvalidate();

			try {
				// Thread.sleep(LayoutValue.ANIM_FARME_INTERVAL);
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// dA = 0.5f; // 每次旋转的角度
		// i = 0;
		// while (isAutoRotate
		// && i < mdAngle/2
		// && null == lastEvent) {
		// rAngle += dA;
		// i += dA;
		// postInvalidate();
		//
		// try {
		// // Thread.sleep(LayoutValue.ANIM_FARME_INTERVAL);
		// Thread.sleep(20);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		dA = -0.5f; // 每次旋转的角度
		i = 0;
		while (isAutoRotate && i < mdAngle * 2 / 3 && null == lastEvent) {
			rAngle += dA;
			i -= dA;
			postInvalidate();

			try {
				// Thread.sleep(LayoutValue.ANIM_FARME_INTERVAL);
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isAutoRotate = false;
	}

	public void startRun() {
		isAutoRotate = true;
		if (isAutoRotate && !rotateSelf.isAlive()) {
			rotateSelf.start();
		}
	}

	public void stopRun() {
		isAutoRotate = false;
	}

	/**
	 * 获取旋转角度 离固定中心分布点最近的那个点的角度
	 * 
	 * @param x
	 * @return
	 */
	private double getNear(double x) {

		if (rAngle > mdAngle / 2) {

			return -rAngle;
		}
		if (rAngle < -(mdAngle * (getChildCount() - startOrder - visibleCount))) {

			return -rAngle
					- (mdAngle * (getChildCount() - startOrder - visibleCount));
		}
		double rd = 0;

		int count = getChildCount();
		for (int i = -count; i < count; i++) {
			if (x > mdAngle * i && x < mdAngle * (i + 1)) {
				if ((x - mdAngle * i) > mdAngle / 2) {
					rd = mdAngle * (i + 1) - x;
					break;
				} else {
					rd = mdAngle * i - x;
					break;
				}
			}
		}
		return rd;

	}

	public void clearContext() {
		mContext = null;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}
