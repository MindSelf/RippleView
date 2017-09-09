package com.example.zhaolexi.rippleview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by ZHAOLEXI on 2017/9/9.
 */

public class RippleView extends RelativeLayout {

    private float mClickX,mClickY;

    private float mRadius;
    private int color;
    private int alpha;
    private int duration;
    private float start;
    private static final float END=1000;

    private Paint mPaint;

    private ValueAnimator mRadiusValueAnimator;

    private OnRippleCompleteListener rippleCompleteListener;
    private boolean isAnimating;

    public interface OnRippleCompleteListener {
        void onComplete(RippleView rippleView);
    }

    public void setOnRippleCompleteListener(OnRippleCompleteListener listener) {
        this.rippleCompleteListener = listener;
    }

    public RippleView(Context context) {
        this(context,null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        color = ta.getColor(R.styleable.RippleView_rv_color, Color.parseColor("#9026b7f0"));
        alpha = ta.getInt(R.styleable.RippleView_rv_alpha, 50);
        duration = ta.getInt(R.styleable.RippleView_rv_duration, 200);
        start = ta.getInteger(R.styleable.RippleView_rv_initial_radius, 60);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setAlpha(alpha);
        mRadiusValueAnimator = ValueAnimator.ofFloat(start, END).
                setDuration(duration);
        mRadiusValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRadiusValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mRadiusValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating=true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetRipple();
                isAnimating=true;
                if (rippleCompleteListener != null) {
                    rippleCompleteListener.onComplete(RippleView.this);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                resetRipple();
                isAnimating=true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        //一定要手动设置为false 因为ViewGroup默认不会调用onDraw,除非手动调用此方法或者设置背景
        setWillNotDraw(false);
    }

    private void resetRipple() {
        mRadius = 0;
        invalidate();
    }

    //重绘波纹的时机在dispatchDraw之后，之前尝试在onDraw后重绘，会导致在RecyclerView等控件中无法看到
    //绘制的效果，原因可能是后面调用了dispatchDraw之后把bitmap上的内容覆盖掉了
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isAnimating)
            canvas.drawCircle(mClickX, mClickY, mRadius, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //不拦截，子控件没有消耗点击事件再对事件进行处理
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mClickX = event.getX();
        mClickY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRadius = start;
                invalidate();
            case MotionEvent.ACTION_MOVE:
                resetRipple();
                mRadius = start;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchPointInView(this, event.getRawX(), event.getRawY()))
                    mRadiusValueAnimator.start();
                else
                    resetRipple();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetRipple();
            default:
        }
        return true;
    }

    //(x,y)是否在view的区域内
    private boolean isTouchPointInView(View view, float x, float y) {
        if(view==null)
            return false;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        RectF rect = new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
        return rect.contains(x, y);
    }
}
