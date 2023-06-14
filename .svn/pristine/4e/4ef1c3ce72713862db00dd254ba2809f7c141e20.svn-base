package com.xiaoxun.xun.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.ResponseOnTouch;

/**
 * Created by fushiqing on 2015/11/24.
 */
public class CustomSeekBar extends View {

    private int width;
    private int height;
    private int downX = 0;
    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private Paint mPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private Bitmap thumb;
    private int hotarea = 100;//点击的热区
    private int volume = 0;//0,1,2,3
    private ResponseOnTouch responseOnTouch;
    private int bitMapHeight = 38;//第一个点的起始位置起始，图片的长宽是76，所以取一半的距离
    private int textMove = 60;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int[] colors = new int[]{0xffdf5600,0x33000000,0xffb5b5b4};//进度条的橙色,进度条的灰色,字体的灰色
    private int textSize;
    private int circleRadius;
    private String[] volumeLevel;
    public CustomSeekBar(Context context) {
        this(context, null);

    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        volume = 0;
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
        thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
        bitMapHeight = thumb.getHeight()/2;
        textMove = bitMapHeight+22;
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        volumeLevel = new String[]{getResources().getString(R.string.watch_volume_silence),
                getResources().getString(R.string.watch_volume_low),
                getResources().getString(R.string.watch_volume_middle),
                getResources().getString(R.string.watch_volume_high)};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSize;
        //控件的高度
        //        height = 185;
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        width = width-30*2;
        hotarea = width/6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(textSize);
        if(volume == 0){
            mPaint.setColor(colors[1]);
            canvas.drawCircle(bitMapHeight, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + circleRadius+5, height * 2 / 3, bitMapHeight + width * 3 / 10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 3 / 10 + circleRadius+5, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 3 / 10 + 2*circleRadius+10, height * 2 / 3, bitMapHeight + width * 6 / 10 + 2*circleRadius+10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 6 / 10 + 3*circleRadius+15, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 6 / 10 + 4*circleRadius+20, height * 2 / 3, bitMapHeight + width * 9 / 10 + 4*circleRadius+20, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 9 / 10 + 5*circleRadius+25, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[2]);
            canvas.drawText(volumeLevel[0], bitMapHeight-textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[1], bitMapHeight + width * 3 / 10 + circleRadius+5 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[2], bitMapHeight + width * 6 / 10 + 3*circleRadius+15 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[3], bitMapHeight + width * 9 / 10 + 5*circleRadius+25 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawBitmap(thumb, bitMapHeight - bitMapHeight, height * 2 / 3 - bitMapHeight, mPaint);
        }else if(volume == 1){
            mPaint.setColor(colors[0]);
            canvas.drawCircle(bitMapHeight, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + circleRadius+5, height * 2 / 3, bitMapHeight + width * 3 / 10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 3 / 10 + circleRadius+5, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[1]);
            canvas.drawLine(bitMapHeight + width * 3 / 10 + 2*circleRadius+10, height * 2 / 3, bitMapHeight + width * 6 / 10 + 2*circleRadius+10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 6 / 10 + 3*circleRadius+15, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 6 / 10 + 4*circleRadius+20, height * 2 / 3, bitMapHeight + width * 9 / 10 + 4*circleRadius+20, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 9 / 10 + 5*circleRadius+25, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[2]);
            canvas.drawText(volumeLevel[0], bitMapHeight-textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[1], bitMapHeight + width * 3 / 10 + circleRadius+5 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[2], bitMapHeight + width * 6 / 10 + 3*circleRadius+15 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[3], bitMapHeight + width * 9 / 10 + 5*circleRadius+25 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawBitmap(thumb, bitMapHeight + width * 3 / 10 + circleRadius+5 - bitMapHeight, height * 2 / 3 - bitMapHeight, mPaint);
        }else if(volume == 2){
            mPaint.setColor(colors[0]);
            canvas.drawCircle(bitMapHeight, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + circleRadius+5, height * 2 / 3, bitMapHeight + width * 3 / 10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 3 / 10 + circleRadius+5, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 3 / 10 + 2*circleRadius+10, height * 2 / 3, bitMapHeight + width * 6 / 10 + 2*circleRadius+10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 6 / 10 + 3*circleRadius+15, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[1]);
            canvas.drawLine(bitMapHeight + width * 6 / 10 + 4*circleRadius+20, height * 2 / 3, bitMapHeight + width * 9 / 10 + 4*circleRadius+20, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 9 / 10 + 5*circleRadius+25, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[2]);
            canvas.drawText(volumeLevel[0], bitMapHeight-textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[1], bitMapHeight + width * 3 / 10 + circleRadius+5 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[2], bitMapHeight + width * 6 / 10 + 3*circleRadius+15 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[3], bitMapHeight + width * 9 / 10 + 5*circleRadius+25 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawBitmap(thumb, bitMapHeight + width * 6 / 10 + 3*circleRadius+15 - bitMapHeight, height * 2 / 3 - bitMapHeight, mPaint);
        }else if(volume == 3){
            mPaint.setColor(colors[0]);
            canvas.drawCircle(bitMapHeight, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + circleRadius+5, height * 2 / 3, bitMapHeight + width * 3 / 10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 3 / 10 + circleRadius+5, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 3 / 10 + 2*circleRadius+10, height * 2 / 3, bitMapHeight + width * 6 / 10 + 2*circleRadius+10, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 6 / 10 + 3*circleRadius+15, height * 2 / 3, circleRadius, mPaint);
            canvas.drawLine(bitMapHeight + width * 6 / 10 + 4*circleRadius+20, height * 2 / 3, bitMapHeight + width * 9 / 10 + 4*circleRadius+20, height * 2 / 3, mPaint);
            canvas.drawCircle(bitMapHeight + width * 9 / 10 + 5*circleRadius+25, height * 2 / 3, circleRadius, mPaint);
            mPaint.setColor(colors[2]);
            canvas.drawText(volumeLevel[0], bitMapHeight-textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[1], bitMapHeight + width * 3 / 10 + circleRadius+5 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[2], bitMapHeight + width * 6 / 10 + 3*circleRadius+15 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawText(volumeLevel[3], bitMapHeight + width * 9 / 10 + 5*circleRadius+25 - textSize/2, height * 2 / 3 - textMove, mPaint);
            canvas.drawBitmap(thumb, bitMapHeight + width * 9 / 10 + 5*circleRadius+25 - bitMapHeight, height * 2 / 3 - bitMapHeight, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_1);
                downX = (int) event.getX();
                downY = (int) event.getY();
                responseTouch(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_1);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
                upX = (int) event.getX();
                upY = (int) event.getY();
                responseTouch(upX, upY);
                responseOnTouch.onTouchResponse(volume);
                break;
        }
        return true;
    }

    //不同的热区对应不同的音量
    private void responseTouch(int x, int y) {

        if(x <= bitMapHeight + hotarea && x >= bitMapHeight - hotarea && y <= height * 2 / 3 + hotarea && y >= height * 2 / 3 - hotarea){
            //(bitMapHeight, height * 2 / 3)  静音
            volume = 0;
        }else if (x <= bitMapHeight + width * 3 / 10 + 15 + hotarea && x >= bitMapHeight + width * 3 / 10 + 15 - hotarea
                && y <= height * 2 / 3 + hotarea && y >= height * 2 / 3 - hotarea) {
            //(bitMapHeight + width * 3 / 10 + 15, height * 2 / 3)  低
            volume = 1;
        }else if (x <= bitMapHeight + width * 6 / 10 + 45 + hotarea && x >= bitMapHeight + width * 6 / 10 + 45 - hotarea
                && y <= height * 2 / 3 + hotarea && y >= height * 2 / 3 - hotarea) {
            //(bitMapHeight + width * 6 / 10 + 45, height * 2 / 3)  中
            volume = 2;
        }else if (x <= bitMapHeight + width * 9 / 10 + 75 + hotarea && x >= bitMapHeight + width * 9 / 10 + 75 - hotarea
                && y <= height * 2 / 3 + hotarea && y >= height * 2 / 3 - hotarea) {
            //(bitMapHeight + width * 9 / 10 + 75, height * 2 / 3)  高
            volume = 3;
        }
        invalidate();
    }

    //设置监听
    public void setResponseOnTouch(ResponseOnTouch response){
        responseOnTouch = response;
    }

    //设置进度
    public void setProgress(int progress){
        volume = progress;
        invalidate();
    }

}
