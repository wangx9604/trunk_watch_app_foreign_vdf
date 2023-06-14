package com.xiaoxun.xun.health.outside.fragments.day;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.newcharting.charts.BarChart;
import com.github.mikephil.newcharting.charts.PieChart;
import com.github.mikephil.newcharting.components.AxisBase;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.github.mikephil.newcharting.data.Entry;
import com.github.mikephil.newcharting.data.PieData;
import com.github.mikephil.newcharting.data.PieDataSet;
import com.github.mikephil.newcharting.data.PieEntry;
import com.github.mikephil.newcharting.formatter.IAxisValueFormatter;
import com.github.mikephil.newcharting.formatter.PercentFormatter;
import com.github.mikephil.newcharting.highlight.Highlight;
import com.github.mikephil.newcharting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.newcharting.listener.OnChartValueSelectedListener;
import com.github.mikephil.newcharting.utils.ColorTemplate;
import com.github.mikephil.newcharting.utils.MPPointF;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.CalendarViewDialog;
import com.xiaoxun.xun.health.outside.bean.OutSideChartDayBean;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutsideDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutsideDayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImibabyApp mApp;
    WatchData curWatch;
    OutsideDayModel viewModel;

    TextView tv_date;
    BarChart chart_outside;
    PieChart chart_outside_pie;

    private String curDate;

    public OutsideDayFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutsideDayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutsideDayFragment newInstance(String param1, String param2) {
        OutsideDayFragment fragment = new OutsideDayFragment();
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
        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        viewModel = factory.create(OutsideDayModel.class);//new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(OutsideDayModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outside_day, container, false);
        curDate = TimeUtil.getDay();
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearCharts();
                viewModel.clearDatas();
                //viewModel.testData();
                viewModel.getDayData(mApp,curDate,curWatch.getEid());
                viewModel.sendUploadMsg(mApp,curDate,curWatch.getEid());
            }
        },300);
    }

    private void initViews(View view){
        ImageView iv_pre = view.findViewById(R.id.iv_pre);
        iv_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreDay();
            }
        });
        ImageView iv_next = view.findViewById(R.id.iv_next);
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNextDay();
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
                        String date = month + getString(R.string.month) + day + getString(R.string.day);
                        tv_date.setText(date);
                        String t = formatSelectDate(year, month, day);
                        curDate = t;
                        clearCharts();
                        viewModel.clearDatas();
                        viewModel.getDayData(mApp, t,curWatch.getEid());
                    }
                });
                dlg.setDate(curDate);
                dlg.show();
            }
        });

        //选中图表中的具体某条Bar的信息
        TextView tv_selected_hour = view.findViewById(R.id.tv_selected_hour);
        TextView tv_outside_dura = view.findViewById(R.id.tv_outside_dura);
        TextView tv_selected_lvl = view.findViewById(R.id.tv_selected_lvl);
        String defLvl = "--" + getString(R.string.unit_level);
        tv_selected_lvl.setText(defLvl);
        TextView tv_selected_lvl_dura = view.findViewById(R.id.tv_selected_lvl_dura);

        //当天统计数据
        TextView tv_all_dura = view.findViewById(R.id.tv_all_dura);
        TextView tv_highest_lvl = view.findViewById(R.id.tv_highest_lvl);
        TextView tv_pie_lvl1 = view.findViewById(R.id.tv_pie_lvl1);
        TextView tv_pie_lvl2 = view.findViewById(R.id.tv_pie_lvl2);
        TextView tv_pie_lvl3 = view.findViewById(R.id.tv_pie_lvl3);
        TextView tv_pie_lvl4 = view.findViewById(R.id.tv_pie_lvl4);
        TextView tv_pie_lvl5 = view.findViewById(R.id.tv_pie_lvl5);

        //图表数据
        chart_outside = view.findViewById(R.id.chart_outside);
        chart_outside_pie = view.findViewById(R.id.chart_outside_pie);
        initCharts();

        viewModel.getSelectOutsideTime().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null){
                    tv_selected_hour.setText(s);
                }else{
                    tv_selected_hour.setText("--");
                }
            }
        });
        viewModel.getSelectOutsideDura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    int ret = integer / 60;
                    tv_outside_dura.setText(String.valueOf(ret));
                }else{
                    tv_outside_dura.setText("--");
                }
            }
        });
        viewModel.getSelectOutsideLvl().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_selected_lvl.setText(getString(R.string.health_outside_selected_lvl,integer.intValue()));
                }else{
                    String em = "--" + getString(R.string.unit_level) + "：";
                    tv_selected_lvl.setText(em);
                }
            }
        });
        viewModel.getSelectOutsideLvlDura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    int ret = integer / 60;
                    tv_selected_lvl_dura.setText(String.valueOf(ret));
                }else{
                    tv_selected_lvl_dura.setText("--");
                }
            }
        });

        viewModel.getAllDura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_all_dura.setText(String.valueOf(integer));
                }else{
                    tv_all_dura.setText("--");
                }
            }
        });
        viewModel.getHighestLvl().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    tv_highest_lvl.setText(String.valueOf(integer));
                }else{
                    tv_highest_lvl.setText("--");
                }
            }
        });
        viewModel.getLvl1Dura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    String ret = "(" + integer + "%)";
                    tv_pie_lvl1.setText(ret);
                }else{
                    tv_pie_lvl1.setText("--");
                }
            }
        });
        viewModel.getLvl2Dura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    String ret = "(" + integer + "%)";
                    tv_pie_lvl2.setText(ret);
                }else{
                    tv_pie_lvl2.setText("--");
                }
            }
        });
        viewModel.getLvl3Dura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    String ret = "(" + integer + "%)";
                    tv_pie_lvl3.setText(ret);
                }else{
                    tv_pie_lvl3.setText("--");
                }
            }
        });
        viewModel.getLvl4Dura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    String ret = "(" + integer + "%)";
                    tv_pie_lvl4.setText(ret);
                }else{
                    tv_pie_lvl4.setText("--");
                }
            }
        });
        viewModel.getLvl5Dura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    String ret = "(" + integer + "%)";
                    tv_pie_lvl5.setText(ret);
                }else{
                    tv_pie_lvl5.setText("--");
                }
            }
        });
        viewModel.getCharDatas().observe(getViewLifecycleOwner(), new Observer<List<OutSideChartDayBean>>() {
            @Override
            public void onChanged(List<OutSideChartDayBean> outSideChartBeans) {
                if(outSideChartBeans != null){
                    setBartChartData(outSideChartBeans);
                }else{

                }
            }
        });
        viewModel.getPieChartData().observe(getViewLifecycleOwner(), new Observer<OutsideDayModel.OutSidePieChartBean>() {
            @Override
            public void onChanged(OutsideDayModel.OutSidePieChartBean outSidePieChartBean) {
                if(outSidePieChartBean != null){
                    setPieChartData(outSidePieChartBean);
                }
            }
        });
    }

    private void initCharts(){
        initBarChart();
        initPieChart();
    }

    private void initBarChart(){
        chart_outside.getDescription().setEnabled(false);
        chart_outside.setMaxVisibleValueCount(100);
        chart_outside.setPinchZoom(false);
        chart_outside.setDrawGridBackground(false);
        chart_outside.setDrawBarShadow(false);
        chart_outside.setDrawValueAboveBar(false);
        chart_outside.setHighlightFullBarEnabled(false);
        chart_outside.getAxisLeft().setEnabled(false);
        chart_outside.getLegend().setEnabled(false);
        chart_outside.setNoDataText(getString(R.string.battery_detail_chart_no_data));

        XAxis xAxis = chart_outside.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(24f);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                int hour = (int)v;
                return String.valueOf(hour) + ":00";
            }
        });

        YAxis leftAxis = chart_outside.getAxisRight();
        leftAxis.setGranularity(15f);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(60f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return String.valueOf((int)v);
            }
        });

        chart_outside.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                BarEntry entry = (BarEntry) e;
                if(entry != null) {
                    Log.e("VAL SELECTED", "X Value: " + entry.getX());
                    int x = (int) entry.getX();
                    viewModel.getSelectOutsideTime().postValue(viewModel.getTimeFromChartSelect(x));

                    if(h.getStackIndex() >= 0) {
                        Log.i("VAL SELECTED", "segment: " + h.getStackIndex());
                        Log.e("VAL SELECTED", "Y Value: " + entry.getYVals()[h.getStackIndex()]);
                        int index = -1;
                        for(int i=0;i<viewModel.chartDatas.getValue().size();i++){
                            OutSideChartDayBean bean = viewModel.chartDatas.getValue().get(i);
                            if(bean.getHour() == x){
                                index = i;
                            }
                        }
                        if(index == -1){
                            return;
                        }
                        OutSideChartDayBean bean = viewModel.chartDatas.getValue().get(index);
                        viewModel.getSelectOutsideDura().postValue(bean.getOutside_day_all_dura()       );
                        int segment = h.getStackIndex();
                        if (segment == 0) {
                            viewModel.getSelectOutsideLvl().postValue(1);
                            viewModel.getSelectOutsideLvlDura().postValue(bean.getLvl1_dura());
                        } else if (segment == 1) {
                            viewModel.getSelectOutsideLvl().postValue(2);
                            viewModel.getSelectOutsideLvlDura().postValue(bean.getLvl2_dura());
                        } else if (segment == 2) {
                            viewModel.getSelectOutsideLvl().postValue(3);
                            viewModel.getSelectOutsideLvlDura().postValue(bean.getLvl3_dura());
                        } else if (segment == 3) {
                            viewModel.getSelectOutsideLvl().postValue(4);
                            viewModel.getSelectOutsideLvlDura().postValue(bean.getLvl4_dura());
                        } else if (segment == 4) {
                            viewModel.getSelectOutsideLvl().postValue(5);
                            viewModel.getSelectOutsideLvlDura().postValue(bean.getLvl5_dura());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initPieChart(){
        chart_outside_pie.setBackgroundColor(Color.WHITE);
        chart_outside_pie.setUsePercentValues(true);
        chart_outside_pie.setNoDataText(getString(R.string.battery_detail_chart_no_data));
        chart_outside_pie.getDescription().setEnabled(false);
        chart_outside_pie.setExtraOffsets(5, 10, 5, 5);
        chart_outside_pie.setDragDecelerationFrictionCoef(0.95f);
        chart_outside_pie.setDrawHoleEnabled(true);
        chart_outside_pie.setDrawCenterText(true);
        chart_outside_pie.setCenterText(getString(R.string.health_outside_duration_percent));
        chart_outside_pie.setCenterTextSize(10f);
        chart_outside_pie.setRotationEnabled(true);
        chart_outside_pie.getLegend().setEnabled(false);
    }

    private void setBartChartData(List<OutSideChartDayBean> outSideChartBeans){
        ArrayList<BarEntry> values = new ArrayList<>();
        for(int i=0;i < outSideChartBeans.size();i++){
            OutSideChartDayBean bean = outSideChartBeans.get(i);
            bean.initChartValueBean();
            LogUtil.e("Outside day setBartChartData : " + bean.printValues());
            float lvl1Dura = (float) bean.getLvl1_dura() / 60;
            float lvl2Dura = (float) bean.getLvl2_dura() / 60;
            float lvl3Dura = (float) bean.getLvl3_dura() / 60;
            float lvl4Dura = (float) bean.getLvl4_dura() / 60;
            float lvl5Dura = (float) bean.getLvl5_dura() / 60;
            if(lvl1Dura != 0.0f || lvl2Dura != 0.0f || lvl3Dura != 0.0f || lvl4Dura != 0.0f || lvl5Dura != 0.0f) {//整天无紫外线数据，不在图表上显示
                BarEntry entry = new BarEntry(bean.getHour(), new float[]{lvl1Dura, lvl2Dura, lvl3Dura, lvl4Dura, lvl5Dura}, bean.getHour() + " entry");
                values.add(entry);
            }
        }
        BarDataSet set;
        if (chart_outside.getData() != null &&
                chart_outside.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart_outside.getData().getDataSetByIndex(0);
            set.setValues(values);
            chart_outside.getData().notifyDataChanged();
            chart_outside.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "outside BarChart");
            set.setDrawIcons(false);
            set.setDrawValues(false);
            set.setColors(getColors());
            set.setStackLabels(new String[]{"lvl1", "lvl2","lvl3","lvl4","lvl5"});
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);

            BarData data = new BarData(dataSets);
            chart_outside.setData(data);
        }

        chart_outside.setFitBars(true);
        chart_outside.invalidate();
    }

    private void setPieChartData(OutsideDayModel.OutSidePieChartBean outSideChartBeans){
        ArrayList<PieEntry> entries = new ArrayList<>();
        int lvl1All=outSideChartBeans.lvl1;
        int lvl2All=outSideChartBeans.lvl2;
        int lvl3All=outSideChartBeans.lvl3;
        int lvl4All=outSideChartBeans.lvl4;
        int lvl5All=outSideChartBeans.lvl5;

        entries.add(new PieEntry(lvl1All,""));
        entries.add(new PieEntry(lvl2All,""));
        entries.add(new PieEntry(lvl3All,""));
        entries.add(new PieEntry(lvl4All,""));
        entries.add(new PieEntry(lvl5All,""));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(getColors());
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setDrawValues(false);
        chart_outside_pie.setData(data);
        chart_outside_pie.highlightValues(null);
        chart_outside_pie.invalidate();
    }

    private void toPreDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String dateStr = month + getString(R.string.month) + day + getString(R.string.day);
            LogUtil.e("toPreDay : " + dateStr);
            tv_date.setText(dateStr);
            clearCharts();
            viewModel.clearDatas();
            viewModel.getDayData(mApp, curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void toNextDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            if (TimeUtil.isTheSameDay(d, Calendar.getInstance().getTime())) {
                return;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String dateStr = month + getString(R.string.month) + day + getString(R.string.day);
            tv_date.setText(dateStr);
            LogUtil.e("toNextDay : " + dateStr);
            clearCharts();
            viewModel.clearDatas();
            viewModel.getDayData(mApp, curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private String getDisplayTimeText(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
            String ret = month + getString(R.string.month) + day + getString(R.string.day) + " " + getWeekDayStr(weekDay);
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    private String getWeekDayStr(int weekDay){
        String ret = "";
        switch (weekDay){
            case Calendar.MONDAY:
                ret = getString(R.string.week_1);
                break;
            case Calendar.TUESDAY:
                ret = getString(R.string.week_2);
                break;
            case Calendar.WEDNESDAY:
                ret = getString(R.string.week_3);
                break;
            case Calendar.THURSDAY:
                ret = getString(R.string.week_4);
                break;
            case Calendar.FRIDAY:
                ret = getString(R.string.week_5);
                break;
            case Calendar.SATURDAY:
                ret = getString(R.string.week_6);
                break;
            case Calendar.SUNDAY:
                ret = getString(R.string.week_0);
                break;
        }
        return ret;
    }
    private String formatSelectDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String time = format.format(calendar.getTime());
        return time;
    }

//    private int getDaysForMonth() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        try {
//            Date d = format.parse(curDate);
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(d);
//            int year = cal.get(Calendar.YEAR);
//            int month = cal.get(Calendar.MONTH);
//            // month is 0-based
//            if (month == 1) {
//                boolean is29Feb = false;
//
//                if (year < 1582)
//                    is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
//                else if (year > 1582)
//                    is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
//
//                return is29Feb ? 29 : 28;
//            }
//
//            if (month == 3 || month == 5 || month == 8 || month == 10)
//                return 30;
//            else
//                return 31;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 30;
//    }

    private void clearCharts() {
        chart_outside.clear();
        chart_outside_pie.clear();
    }

    private String toPercent(float d){
        NumberFormat nt = NumberFormat.getPercentInstance();
        return nt.format(d);
    }

    private static final int[] COLORS = {ColorTemplate.rgb("#ABF268"),ColorTemplate.rgb("#FFD108")
            ,ColorTemplate.rgb("#FEA40C"),ColorTemplate.rgb("#FF5C73"),ColorTemplate.rgb("#DC76F2")};
    private int[] getColors(){
        int[] colors = new int[5];
        System.arraycopy(COLORS, 0, colors, 0, 5);
        return colors;
    }

}