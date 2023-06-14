package com.xiaoxun.xun.health.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.health.bean.FatigueBrainBean;
import com.xiaoxun.xun.health.bean.FatigueSightBean;
import com.xiaoxun.xun.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthFatigueView extends View {

    private static final int[] SIGHT_FATIGUE_COLORS= {R.color.health_report_legend_blue,R.color.health_report_legend_purple
            ,R.color.health_report_legend_orange,R.color.health_report_legend_red};
    private static final int[] BRAIN_FATIGUE_COLORS= {R.color.health_report_legend_blue,R.color.health_report_legend_light_yellow
            ,R.color.health_report_legend_orange,R.color.health_report_legend_red};

    private static final int WEEK_BAR_WIDTH = 16;//dp
    private static final int MONTH_BAR_WIDTH = 6;//dp
    private static final int TEXT_MARGIN_TOP = 12;//dp

    private int type = 0;   //0 视疲劳 1 脑疲劳
    private int style = 0;  //0 周 1 月

    private List<FatigueSightBean> sightDatas;
    private List<FatigueBrainBean> brainDatas;

    private int mWidth = 0;
    private int mHeight = 0;
    private float unit = 2f;   //每单位时间高度
    private float textsize = 16f;
    private int lineInteval = 0;
    private int barInteval = 0;
    private int barWidth;
    private int bottomLine;

    private Paint linePaint;
    private Paint textPaint;
    private Paint normalPaint;
    private Paint tinyPaint;
    private Paint abitPaint;
    private Paint obviousPaint;

    public HealthFatigueView(Context context) {
        super(context);
        initAttrs(null);
    }
    public HealthFatigueView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        initAttrs(attrs);
    }
    public HealthFatigueView(Context context, @Nullable AttributeSet attrs,int defstyle){
        super(context, attrs,defstyle);
        initAttrs(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        LogUtil.e("HealthFatigueView onMeasure mWidth = " + mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGridYLine(canvas);
        calPositionData();
        if(type == 0 ){
            if(sightDatas == null || sightDatas.size() == 0){
                return;
            }
            if(style == 0){
                drawWeekText(canvas);
                drawWeekSight(canvas);
            }else{
                drawMonthText(canvas);
                drawMonthSight(canvas);
            }
        }else{
            if(brainDatas == null || brainDatas.size() == 0){
                return;
            }
            if(style == 0){
                drawWeekText(canvas);
                drawWeekBrain(canvas);
            }else{
                drawMonthText(canvas);
                drawMonthBrain(canvas);
            }
        }
    }

    private void initAttrs(AttributeSet attrs){
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HealthFatigueView);
        type = typedArray.getInteger(R.styleable.HealthFatigueView_ViewType,0);
        style = typedArray.getInteger(R.styleable.HealthFatigueView_ViewStyle,0);
        textsize = typedArray.getDimension(R.styleable.HealthFatigueView_ViewTextSize,11f);
        initPaints();
        initData();
    }

    private void initPaints(){
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(getResources().getColor(R.color.health_report_chart_line));
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.alipay_666666));
        textPaint.setTextSize(textsize);

        normalPaint = new Paint();
        normalPaint.setAntiAlias(true);
        tinyPaint = new Paint();
        tinyPaint.setAntiAlias(true);
        abitPaint = new Paint();
        abitPaint.setAntiAlias(true);
        obviousPaint = new Paint();
        obviousPaint.setAntiAlias(true);
        if(type == 0) {
            normalPaint.setColor(getResources().getColor(SIGHT_FATIGUE_COLORS[0]));
            tinyPaint.setColor(getResources().getColor(SIGHT_FATIGUE_COLORS[1]));
            abitPaint.setColor(getResources().getColor(SIGHT_FATIGUE_COLORS[2]));
            obviousPaint.setColor(getResources().getColor(SIGHT_FATIGUE_COLORS[3]));
        }else{
            normalPaint.setColor(getResources().getColor(BRAIN_FATIGUE_COLORS[0]));
            tinyPaint.setColor(getResources().getColor(BRAIN_FATIGUE_COLORS[1]));
            abitPaint.setColor(getResources().getColor(BRAIN_FATIGUE_COLORS[2]));
            obviousPaint.setColor(getResources().getColor(BRAIN_FATIGUE_COLORS[3]));
        }
    }

    private void initData(){
        sightDatas = new ArrayList<>();
        brainDatas = new ArrayList<>();
    }

    public void setSightDatas(List<FatigueSightBean> list){
        sightDatas = list;
        //barInteval = (mWidth - sightDatas.size() * barWidth) / (sightDatas.size()  - 1);
        invalidate();
    }
    public void setBrainDatas(List<FatigueBrainBean> list){
        brainDatas = list;
        //barInteval = (mWidth - brainDatas.size() * barWidth) / (brainDatas.size()  - 1);
        invalidate();
    }

    private void calPositionData(){
        float textHeihgt = Math.abs(textPaint.getFontMetrics().ascent);
        lineInteval = (int)(getHeight() - textHeihgt - dip2px(getContext(),TEXT_MARGIN_TOP) - 5 * dip2px(getContext(),1)) / 4;
        if(style == 0){
            barWidth = dip2px(getContext(),WEEK_BAR_WIDTH);
            barInteval = (mWidth - 7 * barWidth) / 6;
        }else{
            barWidth = dip2px(getContext(),MONTH_BAR_WIDTH);
            barInteval = (mWidth - 31 * barWidth) / (30);
        }
        unit = (4 * lineInteval) / 1440f;

        bottomLine = 4 * lineInteval;
        LogUtil.e("HealthFatigueView calSightPositionData barInteval = " + barInteval);
        LogUtil.e("HealthFatigueView calSightPositionData lineInteval = " + lineInteval + " | unit = " + unit + " | bottomLine = " + bottomLine);
    }

//    private void calBrainPositionData(){
//        float textHeihgt = Math.abs(textPaint.getFontMetrics().ascent);
//        lineInteval = (int)(getHeight() - textHeihgt - dip2px(getContext(),TEXT_MARGIN_TOP) - 5 * dip2px(getContext(),1)) / 4;
//        if(style == 0){
//            barWidth = dip2px(getContext(),WEEK_BAR_WIDTH);
//            barInteval = (mWidth - 7 * barWidth) / (6);
//        }else{
//            barWidth = dip2px(getContext(),MONTH_BAR_WIDTH);
//            barInteval = (mWidth - 31 * barWidth) / (30);
//        }
//        unit = (4 * lineInteval) / 1440f;
//
//        bottomLine = 4 * lineInteval;
//
//        LogUtil.e("HealthFatigueView calBrainPositionData barInteval = " + barInteval);
//        LogUtil.e("HealthFatigueView calBrainPositionData lineInteval = " + lineInteval + " | unit = " + unit + " | bottomLine = " + bottomLine);
//    }

    private void drawGridYLine(Canvas canvas){
        float stopx = (float)getWidth();
        for(int i=0;i<5;i++) {
            canvas.drawLine(0f, i * lineInteval, stopx, i * lineInteval,linePaint);
        }
    }
    private void drawWeekSight(Canvas canvas){
        for(int i=0;i<7;i++){
            int index = -1;
            for(int j =0;j<sightDatas.size();j++){
                int day = getWeekDayFromTimeStamp(sightDatas.get(j).getDate());
                if(i == day){
                    index = j;
                    break;
                }
            }
            if(index != -1) {
                FatigueSightBean bean = sightDatas.get(index);
                int normal = bean.getNormal().getDuration();
                int tiny = bean.getTiny().getDuration();
                int abit = bean.getAbit().getDuration();
                int obvious = bean.getObvious().getDuration();

                int leftx = i * (barWidth + barInteval);
                int rightx = i * (barWidth + barInteval) + barWidth;

                int nbottomy = bottomLine;
                int ntopy = bottomLine - (int) (unit * normal);

                int tbottomy = ntopy;
                int ttopy = ntopy - (int) (unit * tiny);

                int abottomy = ttopy;
                int atopy = abottomy - (int) (unit * abit);

                int obottomy = atopy;
                int otopy = obottomy - (int) (unit * obvious);

                canvas.drawRect(leftx, ntopy, rightx, nbottomy, normalPaint); // nromal rect
                canvas.drawRect(leftx, ttopy, rightx, tbottomy, tinyPaint); //tiny rect
                canvas.drawRect(leftx, atopy, rightx, abottomy, abitPaint); // abit rect
                canvas.drawRect(leftx, otopy, rightx, obottomy, obviousPaint);// obvious rect
            }
        }
    }
    private void drawMonthSight(Canvas canvas){
        for(int i=1;i<32;i++){
            int index = -1;
            for(int j =0;j<sightDatas.size();j++){
                int day = getMonthDayFromTimeStamp(sightDatas.get(j).getDate());
                if(i == day){
                    index = j;
                    break;
                }
            }
            if(index != -1) {
                FatigueSightBean bean = sightDatas.get(index);
                int normal = bean.getNormal().getDuration();
                int tiny = bean.getTiny().getDuration();
                int abit = bean.getAbit().getDuration();
                int obvious = bean.getObvious().getDuration();

                int leftx = i * (barWidth + barInteval);
                int rightx = i * (barWidth + barInteval) + barWidth;

                int nbottomy = bottomLine;
                int ntopy = bottomLine - (int) (unit * normal);

                int tbottomy = ntopy;
                int ttopy = ntopy - (int) (unit * tiny);

                int abottomy = ttopy;
                int atopy = abottomy - (int) (unit * abit);

                int obottomy = atopy;
                int otopy = obottomy - (int) (unit * obvious);

                canvas.drawRect(leftx, ntopy, rightx, nbottomy, normalPaint); // nromal rect
                canvas.drawRect(leftx, ttopy, rightx, tbottomy, tinyPaint); //tiny rect
                canvas.drawRect(leftx, atopy, rightx, abottomy, abitPaint); // abit rect
                canvas.drawRect(leftx, otopy, rightx, obottomy, obviousPaint);// obvious rect
            }
        }
    }
    private void drawWeekBrain(Canvas canvas){
        for(int i=0;i<7;i++){
            int index = -1;
            for(int j =0;j<brainDatas.size();j++){
                int day = getWeekDayFromTimeStamp(brainDatas.get(j).getDate());
                if(i == day){
                    index = j;
                    break;
                }
            }
            if(index != -1) {
                FatigueBrainBean bean = brainDatas.get(index);
                int normal = bean.getNormal().getDuration();
                int tiny = bean.getLight().getDuration();
                int abit = bean.getMiddle().getDuration();
                int obvious = bean.getSevere().getDuration();

                int leftx = i * (barWidth + barInteval);
                int rightx = i * (barWidth + barInteval) + barWidth;

                int nbottomy = bottomLine;
                int ntopy = bottomLine - (int) (unit * normal);

                int tbottomy = ntopy;
                int ttopy = ntopy - (int) (unit * tiny);

                int abottomy = ttopy;
                int atopy = abottomy - (int) (unit * abit);

                int obottomy = atopy;
                int otopy = obottomy - (int) (unit * obvious);

                canvas.drawRect(leftx, ntopy, rightx, nbottomy, normalPaint); // nromal rect
                canvas.drawRect(leftx, ttopy, rightx, tbottomy, tinyPaint); //tiny rect
                canvas.drawRect(leftx, atopy, rightx, abottomy, abitPaint); // abit rect
                canvas.drawRect(leftx, otopy, rightx, obottomy, obviousPaint);// obvious rect
            }
        }
    }
    private void drawMonthBrain(Canvas canvas){
        for(int i=1;i<32;i++){
            int index = -1;
            for(int j =0;j<brainDatas.size();j++){
                int day = getMonthDayFromTimeStamp(brainDatas.get(j).getDate());
                if(i == day){
                    index = j;
                    break;
                }
            }
            if(index != -1) {
                FatigueBrainBean bean = brainDatas.get(index);
                int normal = bean.getNormal().getDuration();
                int tiny = bean.getLight().getDuration();
                int abit = bean.getMiddle().getDuration();
                int obvious = bean.getSevere().getDuration();

                int leftx = i * (barWidth + barInteval);
                int rightx = i * (barWidth + barInteval) + barWidth;

                int nbottomy = bottomLine;
                int ntopy = bottomLine - (int) (unit * normal);

                int tbottomy = ntopy;
                int ttopy = ntopy - (int) (unit * tiny);

                int abottomy = ttopy;
                int atopy = abottomy - (int) (unit * abit);

                int obottomy = atopy;
                int otopy = obottomy - (int) (unit * obvious);

                canvas.drawRect(leftx, ntopy, rightx, nbottomy, normalPaint); // nromal rect
                canvas.drawRect(leftx, ttopy, rightx, tbottomy, tinyPaint); //tiny rect
                canvas.drawRect(leftx, atopy, rightx, abottomy, abitPaint); // abit rect
                canvas.drawRect(leftx, otopy, rightx, obottomy, obviousPaint);// obvious rect
            }
        }
    }
    private void drawWeekText(Canvas canvas){
        for(int i=0;i<7;i++){
            int x = i * (barWidth + barInteval);
            int y = getHeight();
            LogUtil.e("drawWeekText x = " + x + " y = " + y);
            canvas.drawText(formatWeekDayStr(i),x,y,textPaint);
        }
    }
    private void drawMonthText(Canvas canvas){
        int interval = (getWidth() - 40) / 6;
        for(int i =0;i<7;i++){
            int x = i * interval;
            int y = getHeight();
            LogUtil.e("drawMonthText x = " + x + " y = " + y);
            canvas.drawText(formatMonthDayStr(i),x,y,textPaint);
        }
    }

    private String formatWeekDayStr(int num){
        String ret = "";
        switch(num){
            case 0:
                ret = getContext().getString(R.string.week_1);
                break;
            case 1:
                ret = getContext().getString(R.string.week_2);
                break;
            case 2:
                ret = getContext().getString(R.string.week_3);
                break;
            case 3:
                ret = getContext().getString(R.string.week_4);
                break;
            case 4:
                ret = getContext().getString(R.string.week_5);
                break;
            case 5:
                ret = getContext().getString(R.string.week_6);
                break;
            case 6:
                ret = getContext().getString(R.string.week_0);
                break;
        }
        return ret;
    }

    private String formatMonthDayStr(int num){
        String ret = "";
        switch(num){
            case 0:
                ret = "1";
                break;
            case 1:
                ret = "5";
                break;
            case 2:
                ret = "10";
                break;
            case 3:
                ret = "15";
                break;
            case 4:
                ret = "20";
                break;
            case 5:
                ret = "25";
                break;
            case 6:
                ret = "30";
                break;
        }
        return ret;
    }

    private int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

//    private List<FatigueSightBean> addEmtySightWeekDay(List<FatigueSightBean> list){
//        ArrayList<FatigueSightBean> retlist = new ArrayList<>();
//        if(list.size() == 7){
//            retlist.addAll(list);
//        }else {
//            String d1 = list.get(0).getDate();
//            String[] weekdays = getWeekDaysFromTimeStamp(d1);
//            for (int i = 0; i < weekdays.length; i++) {
//                boolean containDay = false;
//                for (int j = 0; j < list.size(); j++) {
//                    if (weekdays[i].equals(list.get(j).getDate())) {
//                        containDay = true;
//                        FatigueSightBean bean = new FatigueSightBean(list.get(j).getDate());
//                        bean.setNormal(list.get(j).getNormal());
//                        bean.setTiny(list.get(j).getTiny());
//                        bean.setAbit(list.get(j).getAbit());
//                        bean.setObvious(list.get(j).getObvious());
//                        retlist.add(bean);
//                        break;
//                    }
//                }
//                if (!containDay) {
//                    retlist.add(new FatigueSightBean(weekdays[i]));
//                }
//            }
//        }
//        return retlist;
//    }

    private int getWeekDayFromTimeStamp(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.setTime(d);
            LogUtil.e("getWeekDayFromTimeStamp time = " + time + " day = " + cal.get(Calendar.DAY_OF_WEEK));
            if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
                return 0;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
                return 1;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
                return 2;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
                return 3;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
                return 4;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                return 5;
            }else if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                return 6;
            }
            return -1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getMonthDayFromTimeStamp(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void clearChart(){
        if(sightDatas != null && sightDatas.size() != 0) {
            sightDatas.clear();
        }
        if(brainDatas != null && brainDatas.size() != 0){
            brainDatas.clear();
        }
        invalidate();
    }
}
