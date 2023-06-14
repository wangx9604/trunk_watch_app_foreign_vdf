package com.xiaoxun.test;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoxun.xun.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by xilvkang on 2017/1/6.
 */

public class DrawPathView extends View {

    private int screenW;
    private int screenH;
    private int origin_x = 0;
    private int origin_y = 0;

    private Paint paint;
    private Paint origin_p;
    private Paint dott_p;

    public ArrayList<DrawPathActivity.pathInfo> drawList = new ArrayList<>();

    public DrawPathView(Context context) {
        super(context);
        init();

        initPaint();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        origin_p = new Paint();
        origin_p.setColor(Color.BLUE);
        origin_p.setAntiAlias(true);
        dott_p = new Paint();
        dott_p.setColor(Color.BLUE);
        dott_p.setAntiAlias(true);
        dott_p.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinates(canvas);
        drawPath(canvas);
        if(istest) {
            //drawPoint(canvas);
            canvas.drawPath(line, p2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
        origin_x = screenW/2;
        origin_y = screenH/2;
    }

    public void onViewDestroy(){
        if(drawList != null){
            drawList.clear();
        }
    }

    public void onViewResume(){

    }

    public void updateList(DrawPathActivity.pathInfo item){
        drawList.add(item);
        invalidate();
    }

    private void drawCoordinates(Canvas ca){
        ca.drawCircle(screenW/2,screenH/2,4,origin_p);

        PathEffect effects = new DashPathEffect(new float[]{8,8,8,8},1);
        dott_p.setPathEffect(effects);
        Path ph = new Path();
        ph.moveTo(0-screenW,screenH/2);
        ph.lineTo(screenW*2,screenH/2);
        ca.drawPath(ph,dott_p);
        Path pv = new Path();
        pv.moveTo(screenW/2,0-screenH);
        pv.lineTo(screenW/2,screenH*2);
        ca.drawPath(pv,dott_p);
    }

    private void drawPath(Canvas ca){
        for(int i=0;i<drawList.size();i++){
            int x = drawList.get(i).x * 4;
            int y = 0 - drawList.get(i).y * 4;
            ca.drawCircle(origin_x + x,origin_y + y,4,paint);
        }
    }

    public void clearView(){
        drawList.clear();

        cleartest();

        invalidate();
    }



    //test
    private class pos{
        public float x = 0;
        public float y = 0;
    }
    public boolean istest = false;
    private Paint p1,p2;
    private Path line;
    private float lenth = 0;
    private ArrayList<pos> pointlist = new ArrayList<>();
    private void initPoint(){
        for(int i=0;i<drawList.size();i++){
            pos item = new pos();
            item.x = drawList.get(i).x * 4;
            item.y = 0 - drawList.get(i).y * 4;
            pointlist.add(item);
        }
    }
    private void initPaint(){
        p1 = new Paint();
        p1.setAntiAlias(true);
        p1.setColor(Color.RED);
        p2 = new Paint();
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.STROKE);
        p2.setColor(Color.BLUE);
        p2.setStrokeWidth(10);
    }
    public void initPath(){
        if(pointlist == null ||pointlist.size() <= 0){
            return;
        }
        initPoint();
        line = new Path();
        line.moveTo(origin_x + pointlist.get(0).x,origin_y + pointlist.get(0).y);
        for(int i=1;i<pointlist.size();i++){
            line.lineTo(origin_x + pointlist.get(i).x,origin_y + pointlist.get(i).y);
        }
        PathMeasure measure = new PathMeasure(line,false);
        lenth = measure.getLength();

        ObjectAnimator obj = ObjectAnimator.ofFloat(DrawPathView.this,"xxxx",0.0f,1.0f);
        obj.setDuration(3000);
        obj.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float)animation.getAnimatedValue();
                LogUtil.e("onAnimationUpdate :" + p);
                setphase(p);
            }
        });
        obj.start();
    }
    private void cleartest(){
        pointlist.clear();
    }
    private void setphase(float phase){
        p2.setPathEffect(new DashPathEffect(new float[]{lenth,lenth},lenth-lenth*phase));
        invalidate();
    }
    private void drawPoint(Canvas ca){
        for(int i=0;i<pointlist.size();i++){
            ca.drawCircle(origin_x + pointlist.get(i).x,origin_y + pointlist.get(i).y,5,p1);
        }
    }
}
