package com.aizou.peachtravel.module;

import java.util.ArrayList;
import java.util.List;

import com.aizou.core.utils.SharePrefUtil;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.pagetransformer.ZoomOutTranformer;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.utils.UpdateUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnPageChangeListener {
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private String type;
    private DotView dotView;

	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	// 引导图片资源
	private static final int[] pics = {
            R.drawable.guide_1,
            R.drawable.guide_2,
			R.drawable.guide_3,
            R.drawable.guide_4 };

	// private static final int[] backgrouds = { R.drawable.guide_1_bg,
	// R.drawable.guide_2_bg, R.drawable.guide_3_bg, R.drawable.guide_4_bg };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().clearMemoryCache();
		initView();
	}

	protected void initView() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_guide);
		views = new ArrayList<View>();
		type = getIntent().getStringExtra("type");
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			if (i == 3) {
				View view = View.inflate(this, R.layout.guide_last, null);
				Button btn = (Button) view.findViewById(R.id.btn_goto);
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(type)) {
							SharePrefUtil.saveBoolean(GuideActivity.this, "hasLoad_" + UpdateUtil.getVerName(GuideActivity.this), true);
                            Intent mainActivity = new Intent(GuideActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            Intent storyIntent = new Intent(GuideActivity.this, StoryActivity.class);
                            startActivity(storyIntent);
                            overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_to_left);
						} else if (type.equals("setting")) {

						}
						finish();
					}
				});
				views.add(view);
			} else {
				View view = View.inflate(this, R.layout.item_guide, null);
				// view.setBackgroundResource(backgrouds[i]);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_guide);
				iv.setImageResource(pics[i]);
				views.add(view);
			}

		}

		vp = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
        if (Build.VERSION.SDK_INT >= 11) {
            vp.setPageTransformer(true, new ZoomOutPageTransformer());
        }
		// 初始化底部小点
		initDots();

	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	private void initDots() {
        dotView = (DotView) findViewById(R.id.dot_view);
        dotView.setNum(pics.length);

		currentIndex = 0;
		dotView.setSelected(currentIndex);
	}


	boolean isScrolling;

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (arg0 == 1) {
			isScrolling = true;
		} else {
			isScrolling = false;
		}

	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (arg0 == this.views.size() - 1 && arg1 == 0
				&& arg2 == 0 && isScrolling) {
			if (TextUtils.isEmpty(type)) {
				SharePrefUtil.saveBoolean(GuideActivity.this, "hasLoad_" + UpdateUtil.getVerName(GuideActivity.this), true);
                Intent mainActivity = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(mainActivity);
                Intent storyIntent = new Intent(GuideActivity.this, StoryActivity.class);
                startActivity(storyIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			} else if (type.equals("setting")) {

			}
			finish();
		}

	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
        currentIndex=arg0;
		dotView.setSelected(arg0);
	}

	class ViewPagerAdapter extends PagerAdapter {

		// 界面列表
		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

}

class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.85f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0.0f);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0.0f);
        }
    }
}