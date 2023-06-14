package com.xiaoxun.xun.health.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AndroidUtils;


public class HCircleProgress extends View {
    public boolean isDebug=true;
    private OnCircleProgressInter onCircleProgressInter;
    private String battery_level_str;

    public interface OnCircleProgressInter {
        void progress(float scaleProgress, float progress, float max);
    }
    private int centerX;
    private int centerY;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
    private Shader progressShader;
//    private float viewWidth;
//    private float viewHeight;

    //内圆颜色
    private int neiYuanColor;
    //圆环半径
    private int ringRadius;
    //圆环宽度
    private int ringWidth;
    //圆环颜色
    private int ringColor;
//    圆环进度颜色
    private int ringProgressColor;
    //圆环进度过度颜色
//    private int ringProgressSecondColor;
    //开始角度
    private int startAngle=-90;
    //是否顺时针
    private boolean isClockwise=true;
    //当前进度
    private float progress=10;
    //用于执行动画时的进度回调
    private float scaleProgress= progress;
    //总进度
    private float maxProgress=100;

    //用于逻辑计算的总进度(主要使动画效果更平滑)
    private final int viewMax=3600;
    //用于逻辑计算的当前进度(主要使动画效果更平滑)
    private float viewProgress=progress*viewMax/maxProgress;

    //不绘制的度数
    private int disableAngle=0;
    //圆环进度是否为圆角
    private boolean isRound=true;
    //是否设置动画
    private boolean useAnimation =true;
    //动画执行时间
    private int duration =1000;

    //进度百分比
    private int progressPercent;
    //进度百分比数值是否是小数
    private boolean isDecimal=true;
    //小数点后几位
    private int decimalPointLength=1;
    //是否显示百分比和标题
    private boolean isShowPercentText=true;
    //是否仅显示百分比
    private boolean isOnlyPercentShow=false;
    //是否仅绘制文字
    private boolean isOnlyTextShow=false;
    private String toShowStr;
    //文字颜色
    private int textColor;
    //文字大小
    private int textSize;

    private int roundArcEnd = 220;
    private int offsetCenterY = 0;
    //数字
    private String number = "--";
    //目标数字
    private String target = "目标步数：--";
    private String setting = "设置目标";
    private boolean targetSettingVisiable = true;

    public HCircleProgress(Context context) {
        super(context);
        initAttr(null);
    }
    public HCircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }
    public HCircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }
    private void initAttr(AttributeSet attrs) {
        initData();
        initPaint();
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HCircleProgress);
        neiYuanColor = typedArray.getColor(R.styleable.HCircleProgress_HneiYuanColor,getTransparentColor());
        ringRadius = (int) typedArray.getDimension(R.styleable.HCircleProgress_HringRadius, -1);
        ringWidth = (int) typedArray.getDimension(R.styleable.HCircleProgress_HringWidth, 30);
        ringColor = typedArray.getColor(R.styleable.HCircleProgress_HringColor, ContextCompat.getColor(getContext(),R.color.ring_high2));
        ringProgressColor = typedArray.getColor(R.styleable.HCircleProgress_HringProgressColor,
                ContextCompat.getColor(getContext(),R.color.ring_high1));
//        ringProgressSecondColor = typedArray.getColor(R.styleable.CircleProgress_ringProgressSecondColor, ringProgressColor);
        startAngle = typedArray.getInteger(R.styleable.HCircleProgress_HstartAngle, -90);
        isClockwise = typedArray.getBoolean(R.styleable.HCircleProgress_HisClockwise, true);
        progress = typedArray.getFloat(R.styleable.HCircleProgress_Hprogress2, 10);
        maxProgress = typedArray.getFloat(R.styleable.HCircleProgress_HmaxProgress, 100);

        if(maxProgress<=0){
            this.maxProgress=0;
        }
        if(progress> maxProgress){
            this.progress = maxProgress;
        }else if(progress<0){
            this.progress =0;
        }

        scaleProgress= progress;
        viewProgress=progress*viewMax/maxProgress;

        disableAngle = typedArray.getInteger(R.styleable.HCircleProgress_HdisableAngle, 0);
        isRound = typedArray.getBoolean(R.styleable.HCircleProgress_HisRound, true);
        useAnimation = typedArray.getBoolean(R.styleable.HCircleProgress_HuseAnimation, true);
        duration = typedArray.getInteger(R.styleable.HCircleProgress_Hduration, 1000);
        isDecimal = typedArray.getBoolean(R.styleable.HCircleProgress_HisDecimal, true);
        decimalPointLength = typedArray.getInteger(R.styleable.HCircleProgress_HdecimalPointLength, 1);
        isShowPercentText = typedArray.getBoolean(R.styleable.HCircleProgress_HisShowPercentText,true);
        isOnlyPercentShow = typedArray.getBoolean(R.styleable.HCircleProgress_HisOnlyPercentShow,false);
        textColor = typedArray.getColor(R.styleable.HCircleProgress_HtextColor, ContextCompat.getColor(getContext(),R.color.health_circle_pb_foreground));
        textSize = (int) typedArray.getDimension(R.styleable.HCircleProgress_HtextSize, getDef_TextSize());
        offsetCenterY = (int) typedArray.getDimension(R.styleable.HCircleProgress_HoffsetCenterY, 0);
        typedArray.recycle();
//        initData();
        setting = getContext().getString(R.string.health_monitor_step_setting_title);
    }
    private int getTransparentColor(){
        return ContextCompat.getColor(getContext(),R.color.transparent);
    }

    private int getDef_TextSize(){
        return dip2px(getContext(),17);
    }

    private void initPaint() {
        mPaint=new Paint();
        mPaint2=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint2.setAntiAlias(true);
        mPaint2.setDither(true);
        mPaint2.setColor(Color.WHITE);
        mPaint2.setStyle(Paint.Style.FILL);

        mPaint3 = new Paint();
        mPaint2.setDither(true);
        mPaint2.setColor(Color.WHITE);
        mPaint3.setAntiAlias(true);
    }



    private void initData() {
        neiYuanColor=ContextCompat.getColor(getContext(),R.color.transparent);
        ringRadius =-1;
        ringWidth=30;
        ringColor= ContextCompat.getColor(getContext(),R.color.ring_high2);
        ringProgressColor=ContextCompat.getColor(getContext(),R.color.ring_high1);
//        ringProgressSecondColor=ContextCompat.getColor(getContext(),R.color.blue_00);
        textSize=dip2px(getContext(),17);
        textColor=ContextCompat.getColor(getContext(),R.color.ring_high1);

        battery_level_str = getContext().getResources().getString(R.string.battery_level_str);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth =160;
        int mHeight = 160;
        if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT&&getLayoutParams().height== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,mHeight);
        }else if(getLayoutParams().width== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(mWidth,heightSize);
        }else if(getLayoutParams().height== ViewGroup.LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(widthSize,mHeight);
        }
    }

 /*   @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth=w;
        viewHeight=h;
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int WH=Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),getHeight()-getPaddingTop()-getPaddingBottom());
        if(ringRadius<0){
            ringRadius=(WH-ringWidth)/2;
        }
        centerX = getWidth()/2;
        centerY = getHeight() - offsetCenterY;
        //绘制圆环
//        drawRing(canvas);
        drawRing2(canvas);
        //绘制内圆
        drawNeiYuan(canvas);
        //绘制进度圆环
        drawProgressRing(canvas);
        //绘制进度百分比
        if(isShowPercentText){
            drawProgressText(canvas);
        }
        if(isOnlyPercentShow){
            drawOnlyPercentShow(canvas);
        }
        if(isOnlyTextShow){
            drawOnlyPaintTextShow(canvas);
        }
        //绘制数字
        drawNumber(canvas);
    }

    private void drawOnlyPaintTextShow(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        Rect rect=new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.getTextBounds(toShowStr,0,toShowStr.length(),rect);
//        Log.e("circleprogress",rect.width()+":"+rect.height()+":"+rect.bottom+":"+rect.top+":"+centerX+":"+centerY);
        canvas.drawText(toShowStr+"",centerX-rect.width()/2,centerY + rect.height()/2 ,mPaint);
    }

    private void drawOnlyPercentShow(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        progressPercent  = AndroidUtils.chuFa(progress * 100, maxProgress, decimalPointLength);
        String percentStr=progressPercent+"%";
        if(!isDecimal){
            percentStr=((int)progressPercent)+"%";
        }
        Rect rect=new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.getTextBounds(percentStr,0,percentStr.length(),rect);
//        Log.e("circleprogress",rect.width()+":"+rect.height()+":"+rect.bottom+":"+rect.top+":"+centerX+":"+centerY);
        canvas.drawText(percentStr+"",centerX-rect.width()/2,centerY + rect.height()/2 ,mPaint);
    }

    private void drawProgressText(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        progressPercent  = AndroidUtils.chuFa(progress * 100, maxProgress, decimalPointLength);
        String percentStr=progressPercent+"%";
        if(!isDecimal){
            percentStr=((int)progressPercent)+"%";
        }
        Rect rect=new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.getTextBounds(percentStr,0,percentStr.length(),rect);

        Rect rect2=new Rect();
        mPaint2.setTextSize((float) (textSize*0.4));
        mPaint2.setColor(ContextCompat.getColor(getContext(),R.color.ring_textcolor));
        mPaint2.getTextBounds(battery_level_str,0,battery_level_str.length(),rect2);
        float baseLineHeight = Math.abs(mPaint.getFontMetrics().ascent);


        canvas.drawText(percentStr+"",centerX-rect.width()/2,centerY+baseLineHeight,mPaint);
        canvas.drawText(battery_level_str,centerX-rect2.width()/2,centerY - baseLineHeight/2,mPaint2);

    }

    private void drawNumber(Canvas canvas){
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        Rect rect=new Rect();
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.getTextBounds(number,0,number.length(),rect);
        float baseLineHeight = Math.abs(mPaint.getFontMetrics().ascent);

        Rect rect2=new Rect();
        mPaint2.setTextSize((float) (textSize*0.4));
        mPaint2.setColor(ContextCompat.getColor(getContext(),R.color.ring_textcolor));
        mPaint2.getTextBounds(target,0,target.length(),rect2);
        float targetBase = Math.abs(mPaint2.getFontMetrics().ascent);

        mPaint3.setTextSize((float) (textSize*0.4));
        mPaint3.setColor(textColor);
        Rect rect3 = new Rect();
        mPaint3.getTextBounds(setting,0,setting.length(),rect3);
        float settingBase = Math.abs(mPaint3.getFontMetrics().ascent);

        canvas.drawText(number,centerX-rect.width()/2,centerY - baseLineHeight/2,mPaint);
        canvas.drawText(target,centerX-rect2.width()/2,centerY + targetBase/2,mPaint2);
        if(targetSettingVisiable) {
            canvas.drawText(setting, centerX - rect3.width() / 2, getHeight() - settingBase, mPaint3);
        }
    }

    private void drawNeiYuan(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(neiYuanColor);
        mPaint.setStyle(Paint.Style.FILL);

        RectF rectF=new RectF(centerX-ringRadius,centerY-ringRadius,centerX+ringRadius,centerY+ringRadius);
        canvas.drawArc(rectF,startAngle,roundArcEnd,false,mPaint);
    }

    private void drawProgressRing(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(ringProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setShader(null);

        RectF rectF=new RectF(centerX-ringRadius,centerY-ringRadius,centerX+ringRadius,centerY+ringRadius);
//        mPaint.setShadowLayer(1000,1000,1000,ContextCompat.getColor(getContext(),R.color.blue_00));
       /* LinearGradient linearGradient = new LinearGradient(0,0,
                getMeasuredWidth(),getMeasuredHeight(),
                ringProgressColor,ringProgressSecondColor,
                Shader.TileMode.MIRROR);*/

//        mPaint.setShader(linearGradient);
        if(progressShader!=null){
            mPaint.setShader(progressShader);
        }else{
            mPaint.setShader(null);
        }

        if(isRound){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        int angle =  AndroidUtils.chuFa(viewProgress*getEffectiveDegree(),viewMax,2);
        if(!isClockwise){
            angle=-1*angle;
        }
        canvas.drawArc(rectF,startAngle,angle,false,mPaint);
        mPaint.reset();//如果不reset，setShader导致设置进度无效果
    }

    private void drawRing2(Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(ringColor);
        RectF rectF=new RectF(centerX-ringRadius,centerY-ringRadius,centerX+ringRadius,centerY+ringRadius);
        if(isRound){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        float angle = getEffectiveDegree();

        if(!isClockwise){
            angle=-1*angle;
        }
        canvas.drawArc(rectF,startAngle,angle,false,mPaint);
    }
    private void drawRing(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(ringColor);
        canvas.drawCircle(centerX,centerY, ringRadius,mPaint);
    }
    /*******************************get*set********************************************/
    public int getEffectiveDegree(){
        return roundArcEnd-disableAngle;
    }
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if(progress > 100f){
            progress = 100f;
        }
        setProgress(progress,useAnimation);
    }

    public int getDisableAngle() {
        return disableAngle;
    }

    public void setDisableAngle(int disableAngle) {
        int beforeDisableAngle=this.disableAngle;
        if(disableAngle>360){
            this.disableAngle=360;
        }else if(disableAngle<0){
            this.disableAngle=0;
        }else{
            this.disableAngle = disableAngle;
        }
        if(useAnimation){
            ValueAnimator valueAnimator =ValueAnimator.ofInt(beforeDisableAngle,disableAngle);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    HCircleProgress.this.disableAngle= (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            invalidate();
        }
    }

    public int getStartAngle() {
        return startAngle;
    }

    public HCircleProgress setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        invalidateCircleProgress();
        return this;
    }

    public HCircleProgress setTextRemind(String battery_level_str) {
        this.battery_level_str = battery_level_str;
        invalidateCircleProgress();
        return this;
    }

    public void setProgress(float progress, boolean useAnimation) {
        float beforeProgress=viewProgress;
        if(progress>maxProgress){
            this.progress=maxProgress;
        }else if(progress<0){
            this.progress=0;
        }else{
            this.progress = progress;
        }
        viewProgress = progress * viewMax / maxProgress;
        if(useAnimation){
            ValueAnimator valueAnimator =ValueAnimator.ofFloat(beforeProgress,viewProgress);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    HCircleProgress.this.viewProgress = (float) animation.getAnimatedValue();
                    HCircleProgress.this.scaleProgress = HCircleProgress.this.viewProgress*HCircleProgress.this.maxProgress/viewMax;
                    invalidate();
                    setCircleProgress(HCircleProgress.this.scaleProgress,HCircleProgress.this.progress,HCircleProgress.this.maxProgress);
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }else{
            HCircleProgress.this.scaleProgress=this.progress;
            invalidate();
            setCircleProgress(HCircleProgress.this.scaleProgress,HCircleProgress.this.progress,HCircleProgress.this.maxProgress);
        }
    }

    public int getNeiYuanColor() {
        return neiYuanColor;
    }

    public HCircleProgress setNeiYuanColor(@ColorInt int neiYuanColor) {
        this.neiYuanColor = neiYuanColor;
        invalidateCircleProgress();
        return this;
    }

    public int getRingRadius() {
        return ringRadius;
    }

    public HCircleProgress setRingRadius(int ringRadius) {
        this.ringRadius = ringRadius;
        invalidateCircleProgress();
        return this;
    }

    public int getRingWidth() {
        return ringWidth;
    }

    public HCircleProgress setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
        invalidateCircleProgress();
        return this;
    }

    public int getRingColor() {
        return ringColor;
    }

    public HCircleProgress setRingColor(@ColorInt int ringColor) {
        this.ringColor = ringColor;
        invalidateCircleProgress();
        return this;
    }


    public int getRingProgressColor() {
        return ringProgressColor;
    }

    public HCircleProgress setRingProgressColor(@ColorInt int ringProgressColor) {
        this.ringProgressColor = ringProgressColor;
        invalidateCircleProgress();
        return this;
    }

    /*public int getRingProgressSecondColor() {
        return ringProgressSecondColor;
    }

    public CircleProgress setRingProgressSecondColor(@ColorInt int ringProgressSecondColor) {
        this.ringProgressSecondColor = ringProgressSecondColor;
        invalidateCircleProgress();
        return this;
    }
*/

    public boolean isClockwise() {
        return isClockwise;
    }

    public HCircleProgress setClockwise(boolean clockwise) {
        isClockwise = clockwise;
        invalidateCircleProgress();
        return this;
    }

    public Shader getProgressShader() {
        return progressShader;
    }

    public HCircleProgress setProgressShader(Shader progressShader) {
        this.progressShader = progressShader;
        invalidateCircleProgress();
        return this;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public HCircleProgress setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        invalidateCircleProgress();
        return this;
    }

    public boolean isRound() {
        return isRound;
    }

    public HCircleProgress setRound(boolean round) {
        isRound = round;
        invalidateCircleProgress();
        return this;
    }

    public boolean isUseAnimation() {
        return useAnimation;
    }

    public HCircleProgress setUseAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
        invalidateCircleProgress();
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public HCircleProgress setDuration(int duration) {
        this.duration = duration;
        invalidateCircleProgress();
        return this;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public HCircleProgress setDecimal(boolean decimal) {
        isDecimal = decimal;
        invalidateCircleProgress();
        return this;
    }

    public int getDecimalPointLength() {
        return decimalPointLength;
    }

    public HCircleProgress setDecimalPointLength(int decimalPointLength) {
        this.decimalPointLength = decimalPointLength;
        invalidateCircleProgress();
        return this;
    }

    public boolean isShowPercentText() {
        return isShowPercentText;
    }

    public HCircleProgress setShowPercentText(boolean showPercentText) {
        isShowPercentText = showPercentText;
        invalidateCircleProgress();
        return this;
    }

    public boolean isOnlyTextShow() {
        return isOnlyTextShow;
    }

    public void setOnlyTextShow(boolean onlyTextShow, String showStr) {
        isOnlyTextShow = onlyTextShow;
        toShowStr = showStr;
        invalidateCircleProgress();
    }

    public boolean isOnlyPercentShow() {
        return isOnlyPercentShow;
    }

    public HCircleProgress setOnlyPercentShow(boolean onlyPercentShow) {
        isOnlyPercentShow = onlyPercentShow;
        invalidateCircleProgress();
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public HCircleProgress setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
        invalidateCircleProgress();
        return this;
    }

    public int getTextSize() {
        return textSize;
    }

    public HCircleProgress setTextSize(int textSize) {
        this.textSize = textSize;
        invalidateCircleProgress();
        return this;
    }

    public OnCircleProgressInter getOnCircleProgressInter() {
        return onCircleProgressInter;
    }
    public HCircleProgress setOnCircleProgressInter(OnCircleProgressInter onCircleProgressInter) {
        this.onCircleProgressInter = onCircleProgressInter;
        return this;
    }
    private void setCircleProgress(float scaleProgress,float progress,float max) {
        if(onCircleProgressInter !=null){
            onCircleProgressInter.progress(scaleProgress,progress,max);
        }
    }

    public void setNumber(String n){
        number = n;
        invalidate();
    }

    public void setTarget(String target) {
        this.target = getResources().getString(R.string.health_monitor_target_step,target);
        invalidate();
    }

    public void setTargetSettingVisiable(boolean enable){
        targetSettingVisiable = enable;
        invalidate();
    }

    public int getTarget(){
        String ret = "8000";
        String[] num = target.split("：");
        if(num[1].equals("--")){
            ret ="8000";
        }else {
            ret = num[1].substring(0, num[1].length() - 1);
        }
        return Integer.parseInt(ret);
    }

    private void invalidateCircleProgress(){
        invalidate();
    }

    private int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

}
