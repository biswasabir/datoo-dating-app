package com.angopapo.datooapp.modules.autoimageslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.angopapo.datooapp.R;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.PageIndicatorView;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.animation.type.AnimationType;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.animation.type.BaseAnimation;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.animation.type.ColorAnimation;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.draw.data.Orientation;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.draw.data.RtlMode;
import com.angopapo.datooapp.modules.autoimageslider.IndicatorView.utils.DensityUtils;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.AntiClockSpinTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.Clock_SpinTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeInDepthTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeInRotationTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeInScalingTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeOutDepthTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeOutRotationTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.CubeOutScalingTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.DepthTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.FadeTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.FanTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.FidgetSpinTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.GateTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.HingeTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.HorizontalFlipTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.PopTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.SimpleTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.SpinnerTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.TossTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.VerticalFlipTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.VerticalShutTransformation;
import com.angopapo.datooapp.modules.autoimageslider.Transformations.ZoomOutTransformation;

import static com.angopapo.datooapp.modules.autoimageslider.IndicatorView.draw.controller.AttributeController.getRtlMode;

public class SliderView extends FrameLayout {

    public static final int AUTO_CYCLE_DIRECTION_RIGHT = 0;
    public static final int AUTO_CYCLE_DIRECTION_LEFT = 1;
    public static final int AUTO_CYCLE_DIRECTION_BACK_AND_FORTH = 2;

    private final Handler mHandler = new Handler();
    private boolean mFlagBackAndForth;
    private boolean mIsAutoCycle;
    private int mAutoCycleDirection;
    private int mScrollTimeInSec;
    private CircularSliderHandle mCircularSliderHandle;
    private PageIndicatorView mPagerIndicator;
    private DataSetObserver mDataSetObserver;
    private PagerAdapter mPagerAdapter;
    private Runnable mSliderRunnable;
    private SliderPager mSliderPager;

    public SliderView(Context context) {
        super(context);
        setupSlideView(context);
    }

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupSlideView(context);
        setUpAttributes(context, attrs);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupSlideView(context);
        setUpAttributes(context, attrs);
    }

    private void setUpAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0);

        int indicatorOrientation = typedArray.getInt(R.styleable.SliderView_sliderIndicatorOrientation, Orientation.HORIZONTAL.ordinal());
        Orientation orientation;
        if (indicatorOrientation == 0) {
            orientation = Orientation.HORIZONTAL;
        } else {
            orientation = Orientation.VERTICAL;
        }
        int indicatorRadius = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorRadius, DensityUtils.dpToPx(2));
        int indicatorPadding = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorPadding, DensityUtils.dpToPx(3));
        int indicatorMargin = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMargin, DensityUtils.dpToPx(12));
        int indicatorMarginLeft = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginLeft, DensityUtils.dpToPx(12));
        int indicatorMarginTop = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginTop, DensityUtils.dpToPx(12));
        int indicatorMarginRight = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginRight, DensityUtils.dpToPx(12));
        int indicatorMarginBottom = (int) typedArray.getDimension(R.styleable.SliderView_sliderIndicatorMarginBottom, DensityUtils.dpToPx(12));
        int indicatorGravity = typedArray.getInt(R.styleable.SliderView_sliderIndicatorGravity, Gravity.CENTER | Gravity.BOTTOM);
        int indicatorUnselectedColor = typedArray.getColor(R.styleable.SliderView_sliderIndicatorUnselectedColor, Color.parseColor(ColorAnimation.DEFAULT_UNSELECTED_COLOR));
        int indicatorSelectedColor = typedArray.getColor(R.styleable.SliderView_sliderIndicatorSelectedColor, Color.parseColor(ColorAnimation.DEFAULT_SELECTED_COLOR));
        int indicatorAnimationDuration = typedArray.getInt(R.styleable.SliderView_sliderIndicatorAnimationDuration, BaseAnimation.DEFAULT_ANIMATION_TIME);
        int indicatorRtlMode = typedArray.getInt(R.styleable.SliderView_sliderIndicatorRtlMode, RtlMode.Off.ordinal());
        RtlMode rtlMode = getRtlMode(indicatorRtlMode);
        int sliderAnimationDuration = typedArray.getInt(R.styleable.SliderView_sliderAnimationDuration, SliderPager.DEFAULT_SCROLL_DURATION);
        int sliderScrollTimeInSec = typedArray.getInt(R.styleable.SliderView_sliderScrollTimeInSec, 2);
        boolean sliderCircularHandlerEnabled = typedArray.getBoolean(R.styleable.SliderView_sliderCircularHandlerEnabled, true);
        boolean sliderAutoCycleEnabled = typedArray.getBoolean(R.styleable.SliderView_sliderAutoCycleEnabled, true);
        boolean sliderStartAutoCycle = typedArray.getBoolean(R.styleable.SliderView_sliderStartAutoCycle, false);
        int sliderAutoCycleDirection = typedArray.getInt(R.styleable.SliderView_sliderAutoCycleDirection, AUTO_CYCLE_DIRECTION_RIGHT);

        setIndicatorOrientation(orientation);
        setIndicatorRadius(indicatorRadius);
        setIndicatorPadding(indicatorPadding);
        setIndicatorMargin(indicatorMargin);
        if(R.styleable.SliderView_sliderIndicatorMargin == 0){
            setIndicatorMarginCustom(indicatorMarginLeft,indicatorMarginTop,indicatorMarginRight,indicatorMarginBottom);
        }
        setIndicatorGravity(indicatorGravity);
        setIndicatorUnselectedColor(indicatorUnselectedColor);
        setIndicatorSelectedColor(indicatorSelectedColor);
        setIndicatorAnimationDuration(indicatorAnimationDuration);
        setIndicatorRtlMode(rtlMode);
        setSliderAnimationDuration(sliderAnimationDuration);
        setScrollTimeInSec(sliderScrollTimeInSec);
        setCircularHandlerEnabled(sliderCircularHandlerEnabled);
        setAutoCycle(sliderAutoCycleEnabled);
        setAutoCycleDirection(sliderAutoCycleDirection);
        if (sliderStartAutoCycle) {
            startAutoCycle();
        }

        typedArray.recycle();
    }

    private void setupSlideView(Context context) {

        View wrapperView = LayoutInflater
                .from(context)
                .inflate(R.layout.slider_view, this, true);

        mSliderPager = wrapperView.findViewById(R.id.vp_slider_layout);
        mCircularSliderHandle = new CircularSliderHandle(mSliderPager);
        mSliderPager.addOnPageChangeListener(mCircularSliderHandle);
        mSliderPager.setOffscreenPageLimit(4);

        mPagerIndicator = wrapperView.findViewById(R.id.pager_indicator);
        mPagerIndicator.setViewPager(mSliderPager);
    }

    public void setOnIndicatorClickListener(DrawController.ClickListener listener) {
        mPagerIndicator.setClickListener(listener);
    }

    public void setCurrentPageListener(CircularSliderHandle.CurrentPageListener listener) {
        mCircularSliderHandle.setCurrentPageListener(listener);
    }

    public void setSliderAdapter(final PagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
        //set slider adapter
        //registerAdapterDataObserver();
        mSliderPager.setAdapter(pagerAdapter);
        //setup with indicator
        mPagerIndicator.setCount(getAdapterItemsCount());
        mPagerIndicator.setDynamicCount(true);
    }

    public PagerAdapter getSliderAdapter() {
        return mPagerAdapter;
    }

    private void registerAdapterDataObserver() {

        if (mDataSetObserver != null) {
            mPagerAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mSliderPager.setOffscreenPageLimit(getAdapterItemsCount() - 1);
            }
        };

        mPagerAdapter.registerDataSetObserver(mDataSetObserver);
    }

    public boolean isAutoCycle() {
        return mIsAutoCycle;
    }

    public void setAutoCycle(boolean autoCycle) {
        this.mIsAutoCycle = autoCycle;
        if (!mIsAutoCycle && mSliderRunnable != null) {
            mHandler.removeCallbacks(mSliderRunnable);
            mSliderRunnable = null;
        }
    }

    public void setOffscreenPageLimit(int limit) {
        mSliderPager.setOffscreenPageLimit(limit);
    }

    public void setCircularHandlerEnabled(boolean enable) {
        mSliderPager.clearOnPageChangeListeners();
        if (enable) {
            mSliderPager.addOnPageChangeListener(mCircularSliderHandle);
        }
    }

    public int getScrollTimeInSec() {
        return mScrollTimeInSec;
    }

    public void setScrollTimeInSec(int time) {
        mScrollTimeInSec = time;
    }

    public void setSliderTransformAnimation(SliderAnimations animation) {

        switch (animation) {
            case ANTICLOCKSPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new AntiClockSpinTransformation());
                break;
            case CLOCK_SPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new Clock_SpinTransformation());
                break;
            case CUBEINDEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInDepthTransformation());
                break;
            case CUBEINROTATIONTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInRotationTransformation());
                break;
            case CUBEINSCALINGTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeInScalingTransformation());
                break;
            case CUBEOUTDEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutDepthTransformation());
                break;
            case CUBEOUTROTATIONTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutRotationTransformation());
                break;
            case CUBEOUTSCALINGTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new CubeOutScalingTransformation());
                break;
            case DEPTHTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new DepthTransformation());
                break;
            case FADETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FadeTransformation());
                break;
            case FANTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FanTransformation());
                break;
            case FIDGETSPINTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new FidgetSpinTransformation());
                break;
            case GATETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new GateTransformation());
                break;
            case HINGETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new HingeTransformation());
                break;
            case HORIZONTALFLIPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new HorizontalFlipTransformation());
                break;
            case POPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new PopTransformation());
                break;
            case SIMPLETRANSFORMATION:
                mSliderPager.setPageTransformer(false, new SimpleTransformation());
                break;
            case SPINNERTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new SpinnerTransformation());
                break;
            case TOSSTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new TossTransformation());
                break;
            case VERTICALFLIPTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new VerticalFlipTransformation());
                break;
            case VERTICALSHUTTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new VerticalShutTransformation());
                break;
            case ZOOMOUTTRANSFORMATION:
                mSliderPager.setPageTransformer(false, new ZoomOutTransformation());
                break;
            default:
                mSliderPager.setPageTransformer(false, new SimpleTransformation());

        }

    }

    public void setCustomSliderTransformAnimation(ViewPager.PageTransformer animation) {
        mSliderPager.setPageTransformer(false, animation);
    }

    public void setSliderAnimationDuration(int duration) {
        mSliderPager.setScrollDuration(duration);
    }

    public void setCurrentPagePosition(int position) {

        if (getSliderAdapter() != null) {
            mSliderPager.setCurrentItem(position, true);
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    public int getCurrentPagePosition() {

        if (getSliderAdapter() != null) {
            return mSliderPager.getCurrentItem();
        } else {
            throw new NullPointerException("Adapter not set");
        }
    }

    public void setIndicatorAnimationDuration(long duration) {
        mPagerIndicator.setAnimationDuration(duration);
    }

    public void setIndicatorGravity(int gravity) {
        LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.gravity = gravity;
        mPagerIndicator.setLayoutParams(layoutParams);
    }

    public void setIndicatorPadding(int padding) {
        mPagerIndicator.setPadding(padding);
    }

    public void setIndicatorOrientation(Orientation orientation) {
        mPagerIndicator.setOrientation(orientation);
    }

    public void setIndicatorAnimation(IndicatorAnimations animations) {

        switch (animations) {
            case DROP:
                mPagerIndicator.setAnimationType(AnimationType.DROP);
                break;
            case FILL:
                mPagerIndicator.setAnimationType(AnimationType.FILL);
                break;
            case NONE:
                mPagerIndicator.setAnimationType(AnimationType.NONE);
                break;
            case SWAP:
                mPagerIndicator.setAnimationType(AnimationType.SWAP);
                break;
            case WORM:
                mPagerIndicator.setAnimationType(AnimationType.WORM);
                break;
            case COLOR:
                mPagerIndicator.setAnimationType(AnimationType.COLOR);
                break;
            case SCALE:
                mPagerIndicator.setAnimationType(AnimationType.SCALE);
                break;
            case SLIDE:
                mPagerIndicator.setAnimationType(AnimationType.SLIDE);
                break;
            case SCALE_DOWN:
                mPagerIndicator.setAnimationType(AnimationType.SCALE_DOWN);
                break;
            case THIN_WORM:
                mPagerIndicator.setAnimationType(AnimationType.THIN_WORM);
                break;
        }
    }

    public void setIndicatorVisibility(boolean visibility) {
        if (visibility) {
            mPagerIndicator.setVisibility(VISIBLE);
        } else {
            mPagerIndicator.setVisibility(GONE);
        }
    }

    private int getAdapterItemsCount() {
        try {
            return getSliderAdapter().getCount();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void startAutoCycle() {

        if (mSliderRunnable != null) {
            mHandler.removeCallbacks(mSliderRunnable);
            mSliderRunnable = null;
        }

        mSliderRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    // check is on auto scroll mode
                    if (!mIsAutoCycle) {
                        return;
                    }

                    int currentPosition = mSliderPager.getCurrentItem();

                    if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_BACK_AND_FORTH) {
                        if (currentPosition == 0) {
                            mFlagBackAndForth = true;
                        }
                        if (currentPosition == getAdapterItemsCount() - 1) {
                            mFlagBackAndForth = false;
                        }
                        if (mFlagBackAndForth) {
                            mSliderPager.setCurrentItem(++currentPosition, true);
                        } else {
                            mSliderPager.setCurrentItem(--currentPosition, true);
                        }
                    } else if (mAutoCycleDirection == AUTO_CYCLE_DIRECTION_LEFT) {
                        if (currentPosition == 0) {
                            mSliderPager.setCurrentItem(getAdapterItemsCount() - 1, true);
                        } else {
                            mSliderPager.setCurrentItem(--currentPosition, true);
                        }
                    } else {
                        if (currentPosition == getAdapterItemsCount() - 1) {
                            // if is last item return to the first position
                            mSliderPager.setCurrentItem(0, true);
                        } else {
                            // continue smooth transition between pager
                            mSliderPager.setCurrentItem(++currentPosition, true);
                        }
                    }


                } finally {
                    mHandler.postDelayed(this, mScrollTimeInSec * 1000);
                }

            }
        };

        //Run the loop for the first time
        mHandler.postDelayed(mSliderRunnable, mScrollTimeInSec * 1000);
    }

    public void setAutoCycleDirection(int direction) {
        mAutoCycleDirection = direction;
    }

    public int getAutoCycleDirection() {
        return mAutoCycleDirection;
    }

    public int getIndicatorRadius() {
        return mPagerIndicator.getRadius();
    }

    public void setIndicatorRtlMode(RtlMode rtlMode) {
        mPagerIndicator.setRtlMode(rtlMode);
    }

    public void setIndicatorRadius(int pagerIndicatorRadius) {
        this.mPagerIndicator.setRadius(pagerIndicatorRadius);
    }

    public void setIndicatorMargin(int margin) {
        LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.setMargins(margin, margin, margin, margin);
        mPagerIndicator.setLayoutParams(layoutParams);
    }
    
    public void setIndicatorMarginCustom(int left,int top,int right,int bottom) {
        LayoutParams layoutParams = (LayoutParams) mPagerIndicator.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        mPagerIndicator.setLayoutParams(layoutParams);
    }

    public void setIndicatorSelectedColor(int color) {
        this.mPagerIndicator.setSelectedColor(color);
    }

    public int getIndicatorSelectedColor() {
        return this.mPagerIndicator.getSelectedColor();
    }

    public void setIndicatorUnselectedColor(int color) {
        this.mPagerIndicator.setUnselectedColor(color);
    }

    public int getIndicatorUnselectedColor() {
        return this.mPagerIndicator.getUnselectedColor();
    }

}
