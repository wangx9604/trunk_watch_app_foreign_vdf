package com.xiaoxun.xun.health.motion.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.newcharting.charts.LineChart;
import com.github.mikephil.newcharting.components.AxisBase;
import com.github.mikephil.newcharting.components.Description;
import com.github.mikephil.newcharting.components.LimitLine;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.github.mikephil.newcharting.formatter.IAxisValueFormatter;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.networkv2.beans.MotionReportBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MotionWeekKickView extends LinearLayout {

    private TextView mTvRankValue;
    private LineChart mChart;
    private TextView mTvHighValue;
    private TextView mTvAvgValue;

    public MotionWeekKickView(Context context) {
        super(context);
        init(null, 0);
    }

    public MotionWeekKickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MotionWeekKickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MotionWeekKickView, defStyle, 0);
        Drawable mHeadIcon = getContext().getDrawable(R.drawable.icon_3d_location);
        if(a.hasValue(R.styleable.MotionWeekKickView_motion_head_icon)){
            mHeadIcon = a.getDrawable(R.styleable.MotionWeekKickView_motion_head_icon);
        }
        String mMotionHeadTitle = a.getString(R.styleable.MotionWeekKickView_motion_head_title);
        int mMotionRankColor = a.getColor(R.styleable.MotionWeekKickView_motion_head_rank_color, Color.RED);
        int mMotionChartStartColor = a.getColor(R.styleable.MotionWeekKickView_motion_chart_start_color, Color.RED);
        int mMotionChartEndColor = a.getColor(R.styleable.MotionWeekKickView_motion_chart_end_color, Color.RED);

        a.recycle();
        View view = View.inflate(getContext(), R.layout.layout_motion_week_view, null);

        ImageView mIvTitleIcon = view.findViewById(R.id.iv_title_icon);
        TextView mTvHeadTitle = view.findViewById(R.id.tv_head_title);

        mTvRankValue = view.findViewById(R.id.tv_rank_value);
        mTvHighValue = view.findViewById(R.id.tv_score_value_0);
        mTvAvgValue = view.findViewById(R.id.tv_score_value_1);
        mChart = view.findViewById(R.id.line_chare_view);

        mIvTitleIcon.setBackground(mHeadIcon);
        mTvHeadTitle.setText(mMotionHeadTitle);
        mTvRankValue.setTextColor(mMotionRankColor);
        initLineChart(mMotionChartStartColor, mMotionChartEndColor);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    private void initLineChart(int mMotionChartStartColor, int mMotionChartEndColor) {
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
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        //TODO 设置x轴坐标显示的数量  加true才能显示设置的数量 一旦加true后续的x轴数据显示有问题,
        // xAxis.setLabelCount(5,true);
//        xAxis.setLabelCount(5);
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
        //自定义x轴标签数据
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xMap.get((int)value);
            }
        });
        /********************************************************************************/

        //设置限制线 70代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(200);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setLabel("基线");
        //设置限制线为虚线
        limitLine.enableDashedLine(10f, 10f, 0f);

        //左侧Y轴属性设置
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);
        //是否绘制Y轴网格线
        leftAxis.setDrawGridLines(false);
        //左边Y轴添加限制线
        leftAxis.addLimitLine(limitLine);
        //右侧Y轴坐标
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(true);
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
        set2.setColor(Color.rgb(93,4,1));
        mChart.getLegend().setEnabled(false);

        set2.setHighLightColor(Color.rgb(244, 117, 117));
        //set2.setFillFormatter(new MyFillFormatter(900f));
//            LineData lineData = new LineData(xVals, set1);
        // create a data object with the datasets
        BarData data = new BarData(set2);
        //设置图标中显示数字的颜色
        data.setValueTextColor(Color.RED);
        data.setDrawValues(false);
        //设置图标中显示数字的大小
        data.setValueTextSize(9f);

        // set data
//        mChart.setData(data);
    }


    public void setMotionData(MotionReportBean.MotionSportBean reportByType) {


    }
}