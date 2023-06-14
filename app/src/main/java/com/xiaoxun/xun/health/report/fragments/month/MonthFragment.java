package com.xiaoxun.xun.health.report.fragments.month;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.newcharting.charts.BarChart;
import com.github.mikephil.newcharting.charts.CandleStickChart;
import com.github.mikephil.newcharting.components.AxisBase;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.github.mikephil.newcharting.data.CandleData;
import com.github.mikephil.newcharting.data.CandleDataSet;
import com.github.mikephil.newcharting.data.CandleEntry;
import com.github.mikephil.newcharting.formatter.IAxisValueFormatter;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.CalendarViewDialog;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayChartData;
import com.xiaoxun.xun.health.report.fragments.dataBean.KChartData;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImibabyApp mApp;
    private WatchData curWatch;
    private MonthFragmentViewModel viewModel;

    private String curDate;

    private TextView tv_date;
    private BarChart chart_steps;
    private CandleStickChart chart_heart;
    private CandleStickChart chart_oxy;

    private Comparator<CandleEntry> comparator = new Comparator<CandleEntry>() {
        @Override
        public int compare(CandleEntry integer, CandleEntry t1) {
            if(integer.getX() > t1.getX()){
                return 1;
            }else if(integer.getX() < t1.getX()){
                return -1;
            }
            return 0;
        }
    };

    public MonthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthFragment newInstance(String param1, String param2) {
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ImibabyApp) getActivity().getApplication();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            curWatch = mApp.getCurUser().queryWatchDataByEid(mParam2);
        }
        viewModel = new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(MonthFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        curDate = TimeUtil.getMonth();
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getMonthData(mApp,curDate,curWatch.getEid());
    }

    private void initViews(View view){
        ImageView iv_pre = view.findViewById(R.id.iv_pre);
        iv_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreMonth();
            }
        });
        ImageView iv_next = view.findViewById(R.id.iv_next);
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNextMonth();
            }
        });
        LinearLayout date_ly = view.findViewById(R.id.date_ly);
        tv_date = view.findViewById(R.id.tv_date);
        tv_date.setText(getDisplayTimeText(curDate));
        date_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarViewDialog dlg = new CalendarViewDialog(getContext());
                dlg.setOnDateSelectListener(new CalendarViewDialog.OnDateSelectListener() {
                    @Override
                    public void onSelect(int year, int month, int day) {
                        String date = year + getString(R.string.year) + month + getString(R.string.month);
                        tv_date.setText(date);
                        String t = formatSelectDate(year,month,day);
                        curDate = t;
                        clearCharts();
                        viewModel.getMonthData(mApp,t,curWatch.getEid());
                    }
                });
                dlg.setDate(curDate);
                dlg.show();
            }
        });

        TextView tv_steps_count = view.findViewById(R.id.tv_steps_count);
        viewModel.getTotalSteps().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    tv_steps_count.setText(s);
                }else{
                    tv_steps_count.setText("--");
                }
            }
        });
        TextView tv_steps_ave_count = view.findViewById(R.id.tv_steps_ave_count);
        viewModel.getAveSteps().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    tv_steps_ave_count.setText(s);
                }else{
                    tv_steps_ave_count.setText("--");
                }
            }
        });
        TextView tv_cal_count = view.findViewById(R.id.tv_cal_count);
        viewModel.getTotalCal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    double cal = Double.parseDouble(s);
                    tv_cal_count.setText(formatCaloris(cal));
                }else{
                    tv_cal_count.setText("--");
                }
            }
        });
        TextView tv_distance_count = view.findViewById(R.id.tv_distance_count);
        viewModel.getTotalDistance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null) {
                    double dis = Double.parseDouble(s);
                    tv_distance_count.setText(formatKilometers(dis));
                }else{
                    tv_distance_count.setText("--");
                }
            }
        });
        TextView tv_heart_count = view.findViewById(R.id.tv_heart_count);
        viewModel.getLatestHeartRate().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null) {
                    tv_heart_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_heart_count.setText("--");
                }
            }
        });
        TextView tv_heart_high_count = view.findViewById(R.id.tv_heart_high_count);
        viewModel.getHighHeartRate().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null){
                    tv_heart_high_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_heart_high_count.setText("--");
                }
            }
        });
        TextView tv_heart_ave_count = view.findViewById(R.id.tv_heart_ave_count);
        viewModel.getAveHeartRate().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null){
                    tv_heart_ave_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_heart_ave_count.setText("--");
                }
            }
        });
        TextView tv_heart_low_count = view.findViewById(R.id.tv_heart_low_count);
        viewModel.getLowHeartRate().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null){
                    tv_heart_low_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_heart_low_count.setText("--");
                }
            }
        });
        TextView tv_oxy_count = view.findViewById(R.id.tv_oxy_count);
        TextView tv_oxy_status = view.findViewById(R.id.tv_oxy_status);
        viewModel.getLatestOxy().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null){
                    int value = number.intValue();
                    tv_oxy_count.setText(String.valueOf(value));
                    int status = viewModel.getOxyStatusLvl(value);
                    if(status == 0){
                        tv_oxy_status.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_normal));
                    }else if(status == 1){
                        tv_oxy_status.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_light));
                    }else if(status == 2){
                        tv_oxy_status.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_middle));
                    }else{
                        tv_oxy_status.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_severe));
                    }
                    tv_oxy_status.setVisibility(View.VISIBLE);
                }else {
                    tv_oxy_count.setText("--");
                    tv_oxy_status.setVisibility(View.GONE);
                }
            }
        });
        TextView tv_oxy_high_count = view.findViewById(R.id.tv_oxy_high_count);
        viewModel.getHighOxy().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null) {
                    tv_oxy_high_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_oxy_high_count.setText("--");
                }
            }
        });
        TextView tv_oxy_ave_count = view.findViewById(R.id.tv_oxy_ave_count);
        viewModel.getAveOxy().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number!=null){
                    tv_oxy_ave_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_oxy_ave_count.setText("--");
                }
            }
        });
        TextView tv_oxy_low_count = view.findViewById(R.id.tv_oxy_low_count);
        viewModel.getLowOxy().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if(number != null){
                    tv_oxy_low_count.setText(String.valueOf(number.intValue()));
                }else {
                    tv_oxy_low_count.setText("--");
                }
            }
        });


        chart_steps = view.findViewById(R.id.chart_steps);
        chart_heart = view.findViewById(R.id.chart_heart);
        chart_oxy = view.findViewById(R.id.chart_oxy);
        initCharts();
        viewModel.getStepsDatas().observe(getViewLifecycleOwner(), new Observer<List<DayChartData>>() {
            @Override
            public void onChanged(@Nullable List<DayChartData> dayChartData) {
                if(dayChartData != null){
                    setStepData(dayChartData);
                }
            }
        });
        viewModel.getHeartRateDatas().observe(getViewLifecycleOwner(), new Observer<List<KChartData>>() {
            @Override
            public void onChanged(@Nullable List<KChartData> list) {
                if(list != null){
                    setHeartData(list);
                }
            }
        });
        viewModel.getOxyDatas().observe(getViewLifecycleOwner(), new Observer<List<KChartData>>() {
            @Override
            public void onChanged(@Nullable List<KChartData> list) {
                if(list != null){
                    setOxyData(list);
                }
            }
        });

    }

    private void toPreMonth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MONTH,-1);
            cal.set(Calendar.DAY_OF_MONTH,1);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String dateStr = year + getString(R.string.year) + month + getString(R.string.month);
            LogUtil.e("toPreMonth : " + dateStr);
            tv_date.setText(dateStr);
            clearCharts();
            viewModel.getMonthData(mApp,curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void toNextMonth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            Calendar calNow = Calendar.getInstance();

            if(cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH)){
                return;
            }
            cal.add(Calendar.MONTH,1);
            cal.set(Calendar.DAY_OF_MONTH,1);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String dateStr = year + getString(R.string.year) + month + getString(R.string.month);
            LogUtil.e("toNextMonth : " + dateStr);
            tv_date.setText(dateStr);
            clearCharts();
            viewModel.getMonthData(mApp,curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initCharts(){
        initStepChart();
        initHeartRateChart();
        initOxyChart();
    }
    private void initStepChart(){
        chart_steps.setBackgroundColor(Color.WHITE);
        chart_steps.getDescription().setEnabled(false);
        chart_steps.setNoDataText(getString(R.string.battery_detail_chart_no_data));
        chart_steps.setTouchEnabled(false);
        chart_steps.setDrawGridBackground(true);
        chart_steps.setPinchZoom(true);
        chart_steps.getAxisLeft().setEnabled(false);
        chart_steps.getLegend().setEnabled(false);
        chart_steps.setBackgroundColor(Color.WHITE);
        chart_steps.setDrawGridBackground(false);
        chart_steps.setExtraBottomOffset(16f);

        XAxis xAxis = chart_steps.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(32f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setLabelCount(7);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                LogUtil.e("xAxis x = " + v);
                String s = "";
                if(v == 1){
                    s = "1";
                }else if(v == 5){
                    s = "5";
                }else if(v == 10){
                    s = "10";
                }else if(v == 15){
                    s = "15";
                }else if(v == 20){
                    s = "20";
                }else if(v == 25){
                    s = "25";
                }else if(v == 30){
                    s = "30";
                }
                return s;
            }
        });
        YAxis yAxis = chart_steps.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(12000f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(5);
        yAxis.setGranularity(3000f);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return String.valueOf((int)v);
            }
        });
    }
    private void initHeartRateChart(){
        chart_heart.setDrawGridBackground(false);
        chart_heart.setNoDataText(getString(R.string.heart_rate_no_data_txt));
        chart_heart.setTouchEnabled(true);
        chart_heart.setScaleEnabled(true);
        chart_heart.setPinchZoom(true);
        chart_heart.getLegend().setEnabled(false);
        chart_heart.getAxisLeft().setEnabled(false);
        chart_heart.getDescription().setEnabled(false);
        chart_heart.setExtraBottomOffset(16f);

        XAxis xAxis = chart_heart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(32f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setLabelCount(7);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                LogUtil.e("xAxis x = " + v);
                String s = "";
                if(v == 1){
                    s = "1";
                }else if(v == 5){
                    s = "5";
                }else if(v == 10){
                    s = "10";
                }else if(v == 15){
                    s = "15";
                }else if(v == 20){
                    s = "20";
                }else if(v == 25){
                    s = "25";
                }else if(v == 30){
                    s = "30";
                }
                return s;
            }
        });
        YAxis yAxis = chart_heart.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(220f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(5);
        yAxis.setGranularity(40);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return String.valueOf((int)v);
            }
        });
    }

    private void initOxyChart(){
        chart_oxy.setDrawGridBackground(false);
        chart_oxy.setNoDataText(getString(R.string.heart_rate_no_data_txt));
        chart_oxy.setTouchEnabled(true);
        chart_oxy.setScaleEnabled(true);
        chart_oxy.setPinchZoom(true);
        chart_oxy.getLegend().setEnabled(false);
        chart_oxy.getDescription().setEnabled(false);
        chart_oxy.getAxisLeft().setEnabled(false);
        chart_oxy.setExtraBottomOffset(16f);

        XAxis xAxis = chart_oxy.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(32f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setLabelCount(7);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                LogUtil.e("xAxis x = " + v);
                String s = "";
                if(v == 1){
                    s = "1";
                }else if(v == 5){
                    s = "5";
                }else if(v == 10){
                    s = "10";
                }else if(v == 15){
                    s = "15";
                }else if(v == 20){
                    s = "20";
                }else if(v == 25){
                    s = "25";
                }else if(v == 30){
                    s = "30";
                }
                return s;
            }
        });
        YAxis yAxis = chart_oxy.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(40f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(4);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String s = "";
                if(v == 10){
                    s = "70%";
                }else if(v == 20){
                    s = "80%";
                }else if(v == 30){
                    s = "90%";
                }else if(v == 40){
                    s = "100%";
                }
                return s;
            }
        });
    }

    private void setStepData(List<DayChartData> list){
        ArrayList<BarEntry> values = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String t = list.get(i).getTimeStamp();
            int v = list.get(i).getValue();
            int d = getMonthDayFromTimeStamp(t);
            if(d != -1){
                BarEntry entry = new BarEntry(d + 1,v);
                values.add(entry);
            }
        }
        BarDataSet set1;
        if(chart_steps.getData() != null && chart_steps.getData().getDataSetCount() > 0){
            set1 = (BarDataSet) chart_steps.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart_steps.getData().notifyDataChanged();
            chart_steps.notifyDataSetChanged();
        }else{
            set1 = new BarDataSet(values,"steps");
            set1.setColor(getResources().getColor(R.color.health_report_legend_dark_orange));
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            BarData barData = new BarData(set1);
            chart_steps.setData(barData);
        }
        chart_steps.invalidate();
    }

    private void setHeartData(List<KChartData> list){
        ArrayList<CandleEntry> values = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            int high = list.get(i).getHigh();
            int low = list.get(i).getLow();
            String timeStamp = list.get(i).getTimeStamp();
            int d = getMonthDayFromTimeStamp(timeStamp);
            if(d != -1){
                CandleEntry entry = new CandleEntry(d + 1,high,low,high,low);
                values.add(entry);
            }
        }
        if(values.size() == 0){
            return;
        }
        Collections.sort(values,comparator);
        CandleDataSet set1 = new CandleDataSet(values,"heart");
        set1.setDrawIcons(false);
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setDecreasingColor(getResources().getColor(R.color.health_report_chart_candle_color));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.health_report_chart_candle_color));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        CandleData candleData = new CandleData(set1);
        chart_heart.setData(candleData);
        chart_heart.invalidate();
    }
    private void setOxyData(List<KChartData> list){
        ArrayList<CandleEntry> values = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            int high = list.get(i).getHigh();
            int low = list.get(i).getLow();
            String timeStamp = list.get(i).getTimeStamp();
            int d = getMonthDayFromTimeStamp(timeStamp);
            if(d != -1){
                CandleEntry entry = new CandleEntry(d + 1,high - 60,low - 60,high - 60,low - 60);
                values.add(entry);
            }
        }
        if(values.size() == 0){
            return;
        }
        Collections.sort(values,comparator);
        CandleDataSet set1 = new CandleDataSet(values,"heart");
        set1.setDrawIcons(false);
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setDecreasingColor(getResources().getColor(R.color.health_report_chart_candle_color));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.health_report_chart_candle_color));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        CandleData candleData = new CandleData(set1);
        chart_oxy.setData(candleData);
        chart_oxy.invalidate();
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
    private String formatSelectDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        String time = format.format(calendar.getTime());
        return time;
    }
    private String getDisplayTimeText(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String ret = year + getString(R.string.year) + month + getString(R.string.month);
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void clearCharts(){
        chart_steps.clear();
        chart_heart.clear();
        chart_oxy.clear();
    }

    private String formatKilometers(double s) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(s);
    }

    private String formatCaloris(double s){
        DecimalFormat format = new DecimalFormat("0");
        return format.format(s);
    }
}