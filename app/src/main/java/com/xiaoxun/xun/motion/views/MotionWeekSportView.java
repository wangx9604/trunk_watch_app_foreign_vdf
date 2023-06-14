package com.xiaoxun.xun.motion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.newcharting.charts.BarChart;
import com.github.mikephil.newcharting.components.Description;
import com.github.mikephil.newcharting.components.LimitLine;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.networkv2.beans.MotionReportBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MotionWeekSportView extends LinearLayout {

    private BarChart mChart;
    private TextView mTvHighValue;
    private TextView mTvAvgValue;
    private  TextView mTvHeadTitle;

    public MotionWeekSportView(Context context) {
        super(context);
        init(null, 0);
    }

    public MotionWeekSportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MotionWeekSportView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.motion_weeK_sport, defStyle, 0);

        int mMotionChartStartColor = a.getColor(R.styleable.motion_weeK_sport_motion_chart_color, Color.RED);
        String mMotionHint0 = a.getString(R.styleable.motion_weeK_sport_motion_bottom_0);
        String mMotionHint1 = a.getString(R.styleable.motion_weeK_sport_motion_bottom_1);

        a.recycle();
        View view = View.inflate(getContext(), R.layout.layout_motion_sport_view, null);

        TextView mTvBottom0 = view.findViewById(R.id.tv_score_title_0);
        TextView mTvBottom1 = view.findViewById(R.id.tv_score_title_1);
        mTvBottom0.setText(mMotionHint0);
        mTvBottom1.setText(mMotionHint1);
//
        mTvHighValue = view.findViewById(R.id.tv_score_value_0);
        mTvAvgValue = view.findViewById(R.id.tv_score_value_1);
        mTvHeadTitle = view.findViewById(R.id.tv_avg_info);
        mChart = view.findViewById(R.id.bar_chare_view);

        initBarChart(mMotionChartStartColor);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    private void initBarChart(int mMotionChartStartColor) {
        // no description text
        // mChart.getDescription().setEnabled(false);
        //设置是否绘制chart边框的线
        mChart.setDrawBorders(false);
        //设置chart边框线颜色
        mChart.setBorderColor(Color.RED);
        //设置chart边框线宽度
        mChart.setBorderWidth(1f);
        //设置chart是否可以触摸
        mChart.setTouchEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.9f);

        //设置是否可以拖拽
        mChart.setDragEnabled(false);
        //设置是否可以缩放 x和y，默认true
        mChart.setScaleEnabled(false);
        //设置是否可以通过双击屏幕放大图表。默认是true
        mChart.setDoubleTapToZoomEnabled(false);
        //是否启用网格背景
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        //设置chart动画 x轴y轴都有动画
        //mChart.animateXY(2000, 2000);
        // false代表缩放时X与Y轴分开缩放,true代表同时缩放
        mChart.setPinchZoom(false);
        // set an alternative background color
        //图表背景颜色的设置
        mChart.setBackgroundColor(Color.LTGRAY);
        //图表进入的动画时间
        mChart.animateX(2500);

        //描述信息
        Description description = new Description();
        description.setText("");
        //设置描述信息
        mChart.setDescription(description);
        //设置没有数据时显示的文本
        mChart.setNoDataText("没有数据");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(8f);
        //X轴显示的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        //X轴
        xAxis.setSpaceMin(0.5f);
        //X轴数据的颜色
        xAxis.setTextColor(Color.BLUE);
        //是否绘制X轴的网格线
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        //TODO 设置x轴坐标显示的数量  加true才能显示设置的数量 一旦加true后续的x轴数据显示有问题,
        // xAxis.setLabelCount(5,true);
        xAxis.setLabelCount(5);
        //设置竖线为虚线样子
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        //图表第一个和最后一个label数据不超出左边和右边的Y轴
         xAxis.setAvoidFirstLastClipping(true);

        /********************************************************************************/
        final Map<Integer, String> xMap = new HashMap<>();
        final String[] valueArry = {"周一", "周二", "周三", "周四", "周五","周六", "周日"};
        for (int i = 0; i < 7; i++) {
            xMap.put(i, valueArry[i]);
        }
        /********************************************************************************/

        //设置限制线 70代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(70);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("我是基线，也可以叫我水位线");
        //设置限制线为虚线
        limitLine.enableDashedLine(10f, 10f, 0f);

        //左侧Y轴属性设置
        YAxis leftAxis = mChart.getAxisLeft();
        //Y轴数据的字体颜色
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        //左侧Y轴最大值
        leftAxis.setAxisMaximum(200f);
        //左侧Y轴最小值
        leftAxis.setAxisMinimum(0f);
        //是否绘制Y轴网格线
        leftAxis.setDrawGridLines(false);
        //TODO 什么属性?
        leftAxis.setGranularityEnabled(true);
        //左边Y轴添加限制线
        leftAxis.addLimitLine(limitLine);
        //右侧Y轴坐标
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaximum(900);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        //是否绘制等0线
        rightAxis.setDrawZeroLine(true);
        rightAxis.setGranularityEnabled(false);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 7; i++) {
            float mult = 100;
            float val = (float) (Math.random() * mult) + 450;
            yVals1.add(new BarEntry(i, val));
//            if(i == 10) {
//                yVals2.add(new Entry(i, val + 50));
//            }
        }

        // create a dataset and give it a type
        BarDataSet set2 = new BarDataSet(yVals1, "数据2");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);

        set2.setHighLightColor(Color.rgb(244, 117, 117));
        //set2.setFillFormatter(new MyFillFormatter(900f));
//            LineData lineData = new LineData(xVals, set1);
        // create a data object with the datasets
        BarData data = new BarData(set2);
        //设置图标中显示数字的颜色
        data.setValueTextColor(Color.RED);
        //设置图标中显示数字的大小
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }


    public void setMotionData(MotionReportBean.MotionStepBean stepBean) {
        //1:基本视图
        mTvHighValue.setText(stepBean.getCompare()+"");
        mTvAvgValue.setText(stepBean.getAvg()+"");
        mTvHeadTitle.setText(stepBean.getTarget()+"");
        //2:图标视图

    }
}