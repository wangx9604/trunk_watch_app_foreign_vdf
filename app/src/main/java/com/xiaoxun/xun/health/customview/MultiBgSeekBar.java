package com.xiaoxun.xun.health.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;

@SuppressLint("AppCompatCustomView")
public class MultiBgSeekBar extends SeekBar {

    private static final int COLORS[] = {R.color.health_report_legend_yellow,R.color.health_report_legend_green,R.color.health_report_legend_red};

    private Paint paint;
    private Paint textPaint;
    private int mMulticlourCount = 3;
    private int mMulticlourColor = Color.WHITE;

    public MultiBgSeekBar(Context context) {
        super(context);
        init();
    }

    public MultiBgSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiBgSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MultiBgSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        paint = new Paint();
        paint.setColor(mMulticlourColor);
        paint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(22f);
        setSplitTrack(false);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (getWidth() <= 0 || mMulticlourCount <= 0) {
            return;
        }
        int height = getHeight();

        int length = (getWidth() - getPaddingLeft() - getPaddingRight()) / mMulticlourCount;
        int rulerTop = getHeight() / 2 - getMinimumHeight() / 2;
        int rulerBottom = rulerTop + getMinimumHeight();
        Rect thumbRect = null;
        if (getThumb() != null) {
            LogUtil.e("seekbar thumb");
            thumbRect = getThumb().getBounds();
        }else{
            thumbRect = getContext().getDrawable(R.drawable.thumb_yellow).getBounds();
        }
        for (int i = 0; i < mMulticlourCount; i++) {
            int left = getPaddingLeft() + i * length;
            int right = left + length;
            if (i % mMulticlourCount == 0) {
                paint.setColor(getResources().getColor(COLORS[0]));
            } else if (i % mMulticlourCount == 1) {
                paint.setColor(getResources().getColor(COLORS[1]));
            } else {
                paint.setColor(getResources().getColor(COLORS[2]));
            }
            if(thumbRect != null && thumbRect.left > left && thumbRect.left < right){
                textPaint.setColor(paint.getColor());
            }else{
                textPaint.setColor(getResources().getColor(R.color.health_record_step_setting_text_normal));
            }
            if (i == 0) {
                canvas.drawCircle(getPaddingLeft() + 10, height/2f, 10, paint);
                left += 10;
                canvas.drawRect(left, height/2f - 10, right, height/2f + 10, paint);
                //drawIntroText(canvas,left,60,right,getResources().getString(R.string.health_monitor_step_setting_light));
            } else if (i == mMulticlourCount - 1) {
                right -= 10;
                canvas.drawRect(left, height/2f - 10, right, height/2f + 10, paint);
                canvas.drawCircle(right, height/2f, 10, paint);
                //drawIntroText(canvas,left,60,right,getResources().getString(R.string.health_monitor_step_setting_heavy));
            } else {
                canvas.drawRect(left, height/2f - 10, right, height/2f + 10, paint);
                //drawIntroText(canvas,left,60,right,getResources().getString(R.string.health_monitor_step_setting_middle));
            }
        }
        super.onDraw(canvas);
    }

    private void drawIntroText(Canvas canvas,int left,int top,int right,String t){
        LogUtil.e("drawIntroText left = " + left);
        Rect rect = new Rect();
        textPaint.getTextBounds(t,0,t.length(),rect);
        float textBase = Math.abs(textPaint.getFontMetrics().ascent);
        int start = (right - left - rect.width()) / 2;
        canvas.drawText(t,start,top + textBase,textPaint);
    }
}
