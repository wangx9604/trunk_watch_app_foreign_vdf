package com.xiaoxun.xun.health.motion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoxun.xun.R;

public class TriangleIndicatorView extends View {

    private float mBmiValue = 0f;
    private int mWidth;
    private int mHeight;
    private int offset = 30;
    private int mSegmentWidth;

    private Path mPath1;
    private Path mPath2;
    private Path mPath3;
    private Path mPath4;
    private Path mPath5;

    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint mPaint4;

    public TriangleIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public TriangleIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TriangleIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TriangleIndicatorView, defStyle, 0);

        a.recycle();
        initCanvas();
    }

    private void initCanvas() {
        mPaint1 = new Paint();
        mPaint2 = new Paint();
        mPaint3 = new Paint();
        mPaint4 = new Paint();
        mPath1 = new Path();
        mPath2 = new Path();
        mPath3 = new Path();
        mPath4 = new Path();
        mPath5 = new Path();

        mPaint1.setColor(0xFF288EFF);
        mPaint1.setAntiAlias(true);
        mPaint2.setColor(0xFF67FF4C);
        mPaint2.setAntiAlias(true);
        mPaint3.setColor(0xFFFFDD20);
        mPaint3.setAntiAlias(true);
        mPaint4.setColor(0xFFFF4545);
        mPaint4.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mWidth = getWidth();
        initPath();
    }

    private void initPath() {
        mSegmentWidth = (mWidth-12)/4;
        int mRadius = (mHeight-offset)/2;
        mPath1.addCircle(mRadius,mRadius+offset,mRadius, Path.Direction.CW);
        mPath1.addRect(mRadius,offset,mSegmentWidth,mHeight, Path.Direction.CW);
        mPath2.addRect(mSegmentWidth+4,offset,mSegmentWidth*2+4,mHeight, Path.Direction.CW);
        mPath3.addRect(mSegmentWidth*2+8,offset,mSegmentWidth*3+8,mHeight, Path.Direction.CW);
        mPath4.addRect(mSegmentWidth*3+12,offset,mSegmentWidth*4+12-mRadius,mHeight, Path.Direction.CW);
        mPath4.addCircle(mSegmentWidth*4+12-mRadius,mRadius+offset,mRadius, Path.Direction.CW);

        updateIndicatorPath();
    }

    private void updateIndicatorPath() {
        float mIndicatorStartPoint = 0;
        if(mBmiValue < 18.5){
            mIndicatorStartPoint = mBmiValue*10/185*mSegmentWidth;
        }else if(mBmiValue < 24.9){
            mIndicatorStartPoint = (float) (mSegmentWidth+4+(mBmiValue-18.5)*10/64*mSegmentWidth);
        }else if(mBmiValue < 29.9){
            mIndicatorStartPoint = mSegmentWidth*2+4*2+(float) ((mBmiValue-25)*10/49*mSegmentWidth);
        }else{
            mIndicatorStartPoint = mSegmentWidth*3+4*3+(float) ((mBmiValue-30)*10/50*mSegmentWidth);
            if(mIndicatorStartPoint >= mSegmentWidth*4+12){
                mIndicatorStartPoint = mSegmentWidth*4+12-offset/2;
            }
        }
        mPath5.reset();
        mPath5.moveTo(mIndicatorStartPoint-offset/2,0);
        mPath5.lineTo(mIndicatorStartPoint+offset/2,0);
        mPath5.lineTo(mIndicatorStartPoint,offset);
        mPath5.close();

    }

    public void setmBmiValue(float mBmiValue) {
        this.mBmiValue = mBmiValue;
        updateIndicatorPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xFFFFFFFF);

        canvas.drawPath(mPath1,mPaint1);
        canvas.drawPath(mPath2,mPaint2);
        canvas.drawPath(mPath3,mPaint3);
        canvas.drawPath(mPath4,mPaint4);
        canvas.drawPath(mPath5,getPaintByType());
    }

    private Paint getPaintByType() {
        if(mBmiValue < 18.5)
            return mPaint1;
        else if(mBmiValue < 24.9)
            return mPaint2;
        else if(mBmiValue < 29.9)
            return mPaint3;
        else
            return mPaint4;
    }
}