package com.xuejian.client.lxp.module;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aizou.core.utils.SharePrefUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.utils.UpdateUtil;
import com.xuejian.client.lxp.module.my.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends PeachBaseActivity implements OnPageChangeListener {
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private String type;
//    private DotView dotView;

	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

    //引导页第二页小图动画index
    private int guide2IvIndex;
    //引导页第三页小图动画index
    private int guide3IvIndex;


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
		for (int i = 0; i <3; i++) {
			if (i == 0) {
				View view = View.inflate(this, R.layout.guide_1, null);
                ImageView earthIv = (ImageView) view.findViewById(R.id.iv_guide_earth);
                Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_guide_earth);
                earthIv.startAnimation(operatingAnim);
                views.add(view);
			} else if(i == 1) {
                View view = View.inflate(this,R.layout.guide_2,null);
                views.add(view);
			} else if(i == 2) {
                View view = View.inflate(this,R.layout.guide_3,null);
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
//		initDots();

	}


//	private void initDots() {
//        dotView = (DotView) findViewById(R.id.dot_view);
//        dotView.setNum(views.size());
//
//		currentIndex = 0;
//		dotView.setSelected(currentIndex);
//	}


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
                Intent mainActivity = new Intent(GuideActivity.this, LoginActivity.class);
                startActivityWithNoAnim(mainActivity);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else if (type.equals("setting")) {

			}
			finish();
		}

	}

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        SharePrefUtil.saveBoolean(GuideActivity.this, "hasLoad_" + UpdateUtil.getVerName(GuideActivity.this), true);
        Intent mainActivity = new Intent(GuideActivity.this, LoginActivity.class);
        startActivityWithNoAnim(mainActivity);
//        Intent storyIntent = new Intent(GuideActivity.this, StoryActivity.class);
//        startActivityWithNoAnim(storyIntent);
    }

    // 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
        currentIndex=arg0;
//		dotView.setSelected(arg0);
        if(arg0==0){
            ImageView earthIv = (ImageView) views.get(arg0).findViewById(R.id.iv_guide_earth);
            Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_guide_earth);
            earthIv.startAnimation(operatingAnim);
        }else if(arg0==1){
            View view = views.get(arg0);
            guide2IvIndex=1;
            final ImageView dis1Iv = (ImageView) view.findViewById(R.id.iv_guide2_1);
            final ImageView dis2Iv = (ImageView) view.findViewById(R.id.iv_guide2_2);
            final ImageView dis3Iv = (ImageView) view.findViewById(R.id.iv_guide2_3);
            final ImageView dis4Iv = (ImageView) view.findViewById(R.id.iv_guide2_4);
            final ImageView dis5Iv = (ImageView) view.findViewById(R.id.iv_guide2_5);
            final ImageView dis6Iv = (ImageView) view.findViewById(R.id.iv_guide2_6);
            final ImageView dis7Iv = (ImageView) view.findViewById(R.id.iv_guide2_7);
            final ImageView dis8Iv = (ImageView) view.findViewById(R.id.iv_guide2_8);
            dis1Iv.setBackgroundResource(R.drawable.ic_guide2_1_normal);
            dis2Iv.setBackgroundResource(R.drawable.ic_guide2_2_normal);
            dis3Iv.setBackgroundResource(R.drawable.ic_guide2_3_normal);
            dis4Iv.setBackgroundResource(R.drawable.ic_guide2_4_normal);
            dis5Iv.setBackgroundResource(R.drawable.ic_guide2_5_normal);
            dis6Iv.setBackgroundResource(R.drawable.ic_guide2_6_normal);
            dis7Iv.setBackgroundResource(R.drawable.ic_guide2_7_normal);
            dis8Iv.setBackgroundResource(R.drawable.ic_guide2_8_normal);

            final Animation disAnim = AnimationUtils.loadAnimation(mContext,R.anim.scale_as_event);
            disAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(guide2IvIndex==1){
                        guide2IvIndex=2;
                        dis1Iv.setBackgroundResource(R.drawable.ic_guide2_1_select);
                        dis1Iv.setAnimation(null);
                        dis2Iv.startAnimation(disAnim);

                    }else if(guide2IvIndex==2){
                        guide2IvIndex=3;
                        dis2Iv.setBackgroundResource(R.drawable.ic_guide2_2_select);
                        dis2Iv.setAnimation(null);
                        dis3Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==3){
                        guide2IvIndex=4;
                        dis3Iv.setBackgroundResource(R.drawable.ic_guide2_3_select);
                        dis3Iv.setAnimation(null);
                        dis4Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==4){
                        guide2IvIndex=5;
                        dis4Iv.setBackgroundResource(R.drawable.ic_guide2_4_select);
                        dis4Iv.setAnimation(null);
                        dis5Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==5){
                        guide2IvIndex=6;
                        dis5Iv.setBackgroundResource(R.drawable.ic_guide2_5_select);
                        dis5Iv.setAnimation(null);
                        dis6Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==6){
                        guide2IvIndex=7;
                        dis6Iv.setBackgroundResource(R.drawable.ic_guide2_6_select);
                        dis6Iv.setAnimation(null);
                        dis7Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==7){
                        guide2IvIndex=8;
                        dis7Iv.setBackgroundResource(R.drawable.ic_guide2_7_select);
                        dis7Iv.setAnimation(null);
                        dis8Iv.startAnimation(disAnim);
                    }else if(guide2IvIndex==8){
                        dis8Iv.setBackgroundResource(R.drawable.ic_guide2_8_select);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            dis1Iv.startAnimation(disAnim);

        }else if(arg0==2){
            View view = views.get(arg0);
            guide3IvIndex=1;
            final ImageView talkIv1= (ImageView) view.findViewById(R.id.iv_talk_1);
            final ImageView talkIv2= (ImageView) view.findViewById(R.id.iv_talk_2);
            final ImageView talkIv3= (ImageView) view.findViewById(R.id.iv_talk_3);
            talkIv1.setVisibility(View.INVISIBLE);
            talkIv2.setVisibility(View.INVISIBLE);
            talkIv3.setVisibility(View.INVISIBLE);
            final Animation alphaAnimation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
            alphaAnimation.setDuration(600);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if(guide3IvIndex==1){
                        talkIv1.setVisibility(View.VISIBLE);
                    }else if(guide3IvIndex==2){
                        talkIv2.setVisibility(View.VISIBLE);
                    }else if(guide3IvIndex==3){
                        talkIv3.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(guide3IvIndex==1){
                        guide3IvIndex=2;
                        talkIv1.setAnimation(null);
                        talkIv2.startAnimation(alphaAnimation);
                    }else if(guide3IvIndex==2){
                        guide3IvIndex=3;
                        talkIv2.setAnimation(null);
                        talkIv3.startAnimation(alphaAnimation);
                    }else if(guide3IvIndex==3){
                        guide3IvIndex=1;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            talkIv1.startAnimation(alphaAnimation);
        }
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