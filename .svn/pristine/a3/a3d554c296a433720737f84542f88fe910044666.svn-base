package com.xiaoxun.xun.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liutianxiang
 */
public class CustomerPickerView extends View
{

	public static final String TAG = "PickerView";
	public static float MARGIN_ALPHA = 1.2f;
	public static final float SPEED = 5;

	private List<String> mDataList;
	private int mCurrentSelected;
	private Paint mPaint;
	private Paint mCirclePaint ;

	private float mMaxTextSize = 50;
	private float mMinTextSize = 36;
	private float mHourMinSize = 22;
	private float lineHeight = 119;
	private float mMaxTextAlpha = 255;
	private float mMinTextAlpha = 120;

	private int mColorText = 0x333333;
	private int mColorSelectText = -1;

	private int mViewHeight;
	private int mViewWidth;

	private float mLastDownY;
	private float mMoveLen = 0;
	private boolean isInit = false;
	private onSelectListener mSelectListener;
	private Timer timer;
	private Timer timer2;
	private MyTimerTask mTask;
	private MyTimerTask mTask2;

	private String sHOrM="";
	private int velocity;

	private boolean isCircle = false ;
	private VelocityTracker vtTracker;

	Handler updateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			if (Math.abs(mMoveLen) < SPEED)
			{
				mMoveLen = 0;
				if (mTask != null)
				{
					mTask.cancel();
					mTask = null;
					performSelect();
				}
			} else
				mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
			invalidate();
		}

	};

	Handler inHandler = new Handler(){
		int i =0;
		@Override
		public void handleMessage(Message msg) {
			if(i<Math.abs(velocity/500)){
				if(velocity>0){
					moveTailToHead();
					performSelect();
				}else{
					moveHeadToTail();
					performSelect();
				}
				i++;
			}else{
				i=0;
				mTask2.cancel();
			}
			invalidate();
		}

	};



	public CustomerPickerView(Context context)
	{
		super(context);
		init();
	}

	public CustomerPickerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		if (width<1080) {
			mMaxTextSize = (width * 54 / 1080);
			mMinTextSize = (width * 36 / 1080);
		}
		lineHeight = (width * 119 / 1080);
		init();
	}

	public void setOnSelectListener(onSelectListener listener)
	{
		mSelectListener = listener;
	}

	private void performSelect()
	{
		if (mSelectListener != null)
			mSelectListener.onSelect(mDataList.get(mCurrentSelected));
	}

	public void setData(List<String> datas)
	{
		mDataList = datas;
		mCurrentSelected = datas.size() / 2;
		invalidate();
	}

	public void setSelected(int selected)
	{
		mCurrentSelected = selected;
		int distance = mDataList.size() / 2 - mCurrentSelected;
		if (distance < 0)
			for (int i = 0; i < -distance; i++)
			{
				moveHeadToTail();
				mCurrentSelected--;
			}
		else if (distance > 0)
			for (int i = 0; i < distance; i++)
			{
				moveTailToHead();
				mCurrentSelected++;
			}
		invalidate();
	}

	public void setSelected(String mSelectItem){
		for(int i = 0; i < mDataList.size(); i++)
			if(mDataList.get(i).equals(mSelectItem)){
				setSelected(i);
				break;
			}
	}

	private void moveHeadToTail()
	{
		String head = mDataList.get(0);
		mDataList.remove(0);
		mDataList.add(head);
	}

	private void moveTailToHead()
	{
		String tail = mDataList.get(mDataList.size() - 1);
		mDataList.remove(mDataList.size() - 1);
		mDataList.add(0, tail);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = getMeasuredHeight();
		mViewWidth = getMeasuredWidth();
		isInit = true;
		invalidate();
	}

	private void init()
	{
		timer = new Timer();
		timer2 = new Timer();
		mDataList = new ArrayList<String>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mColorText);

		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setStrokeWidth((float) 3.0);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setColor(mColorText);
		mCirclePaint.setAlpha(100);
	}

	public void setmMaxTextSize(int mMaxSize){
		mMaxTextSize = mMaxSize;
	}

	public void setSelectTextColor(int mColor){
		mColorSelectText = mColor;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (isInit)
			drawData(canvas);
	}

	private void drawData(Canvas canvas)
	{
		float scale = parabola(mViewHeight / 4.0f, mMoveLen);
		mPaint.setTextSize(mMaxTextSize);
		if(mColorSelectText != -1){
			mPaint.setColor(mColorSelectText);
		}else {
			mPaint.setColor(0xdf5600);
		}
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
		float y = (float) (mViewHeight / 2.0 + mMoveLen);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		//float x = (float) (234);
		//float y = (float) (300);
		canvas.drawText(mDataList.get(mCurrentSelected), (float) ((mViewWidth-17)/ 2.0), baseline, mPaint);

		for (int i = 1; (mCurrentSelected - i) >= 0; i++){
			drawOtherText(canvas, i, -1 , baseline);
		}
		for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++){
			drawOtherText(canvas, i, 1 , baseline);
		}

		if(isCircle){
			canvas.drawCircle(this.getWidth()/2, this.getHeight()/2, 70, mCirclePaint);
		}

	}

	private void drawOtherText(Canvas canvas, int position, int type , float baseline)
	{
		//float x = (float) (240);
		//float y = (float) (284);

		float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen);
		float scale = parabola(mViewHeight / 4.0f, d);
		//float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
		mPaint.setTextSize(mMinTextSize);
		mPaint.setColor(mColorText);
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale/3 + mMinTextAlpha));
		//float y = (float) (mViewHeight / 2.0 + type * d);
		//FontMetricsInt fmi = mPaint.getFontMetricsInt();
		//float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		//canvas.drawText(mDataList.get(mCurrentSelected + type * position), (float) (mViewWidth / 2.0), baseline, mPaint);
		canvas.drawText(mDataList.get(mCurrentSelected + type * position), (float) ((mViewWidth-17)/2.0), baseline+lineHeight*type*position, mPaint);
	}

	private float parabola(float zero, float x)
	{
		float f = (float) (1 - Math.pow(x / zero, 2));
		return f < 0 ? 0 : f;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		this.getParent().requestDisallowInterceptTouchEvent(true);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			this.getParent().requestDisallowInterceptTouchEvent(false);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				if(vtTracker == null){
					vtTracker=VelocityTracker.obtain();
				}else {
					vtTracker.clear();
				}
				vtTracker.addMovement(event);
				doDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				vtTracker.addMovement(event);
				vtTracker.computeCurrentVelocity(500);
				doMove(event);
				break;
			case MotionEvent.ACTION_UP:
				doUp(event,vtTracker.getYVelocity());
				break;
		}
		return true;
	}

	private void doDown(MotionEvent event)
	{
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		mLastDownY = event.getY();
	}

	private void doMove(MotionEvent event)
	{

		mMoveLen += (event.getY() - mLastDownY);
		if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2)
		{
			moveTailToHead();
			mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
		} else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2)
		{
			moveHeadToTail();
			mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
		}

		mLastDownY = event.getY();
		invalidate();
	}

	private void doUp(MotionEvent event,double v)
	{
		velocity = (int) v;
		if(mTask2!=null)
			mTask2.cancel();
		mTask2 = new MyTimerTask(inHandler);
		timer2.schedule(mTask2, 0, 100);


		if (Math.abs(mMoveLen) < 0.0001)
		{
			mMoveLen = 0;
			return;
		}
		if (mTask != null)
		{
			mTask.cancel();
			mTask = null;
		}
		mTask = new MyTimerTask(updateHandler);
		timer.schedule(mTask, 0, 10);


	}

	public void release() {
		if (timer != null) timer.cancel();
		if (timer2 != null) timer2.cancel();
		if (mTask != null) mTask.cancel();
		if (mTask2 != null) mTask2.cancel();
	}

	public void setMarginAlphaValue(float value , String hOrM){
		MARGIN_ALPHA = value ;
		sHOrM = hOrM ;
	}

	public void setBackgroundCricle(boolean bSet){
		isCircle = bSet ;
	}

	class MyTimerTask extends TimerTask
	{
		Handler handler;

		public MyTimerTask(Handler handler)
		{
			this.handler = handler;
		}

		@Override
		public void run()
		{
			handler.sendMessage(handler.obtainMessage());
		}

	}

	public interface onSelectListener
	{
		void onSelect(String text);
	}
}
