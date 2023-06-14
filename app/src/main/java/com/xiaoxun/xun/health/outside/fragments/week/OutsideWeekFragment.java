package com.xiaoxun.xun.health.outside.fragments.week;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.github.mikephil.newcharting.components.AxisBase;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.github.mikephil.newcharting.data.Entry;
import com.github.mikephil.newcharting.formatter.IAxisValueFormatter;
import com.github.mikephil.newcharting.highlight.Highlight;
import com.github.mikephil.newcharting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.newcharting.listener.OnChartValueSelectedListener;
import com.github.mikephil.newcharting.utils.ColorTemplate;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.CalendarViewDialog;
import com.xiaoxun.xun.health.outside.bean.OutSideChartWeekBean;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutsideWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutsideWeekFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImibabyApp mApp;
    WatchData curWatch;
    OutsideWeekModel viewModel;

    TextView tv_date;
    BarChart chart_outside;

    private String curDate;

    public OutsideWeekFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutsideWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutsideWeekFragment newInstance(String param1, String param2) {
        OutsideWeekFragment fragment = new OutsideWeekFragment();
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
        viewModel = factory.create(OutsideWeekModel.class);//new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(OutsideWeekModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_outside_week, container, false);
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
                LogUtil.e("onViewCreated test");
                //viewModel.testData(mApp,curDate);
                viewModel.getWeekData(mApp,curDate,curWatch.getEid());
            }
        },100);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("Outside Fragment week onResume");
        if(viewModel.getChartDatas().getValue() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("onResume test");
                    clearCharts();
                    setBarChartData(viewModel.getChartDatas().getValue());
                }
            },200);
        }
    }

    private void initViews(View view){
        ImageView iv_pre = view.findViewById(R.id.iv_pre);
        iv_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreWeek();
            }
        });
        ImageView iv_next = view.findViewById(R.id.iv_next);
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNextWeek();
            }
        });
        LinearLayout date_ly = view.findViewById(R.id.date_ly);
        tv_date = view.findViewById(R.id.tv_date);
        String[] monSun = viewModel.getMonAndSunbyReserveDate(curDate);
        tv_date.setText(getDisplayTimeText(monSun[0]) + "-" + getDisplayTimeText(monSun[1]));
        date_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarViewDialog dlg = new CalendarViewDialog(getContext());
                dlg.setOnDateSelectListener(new CalendarViewDialog.OnDateSelectListener() {
                    @Override
                    public void onSelect(int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month - 1);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
                        String dateStr = format.format(calendar.getTime());
                        String[] monAndSun = viewModel.getMonAndSunbyReserveDate(dateStr);
                        String mon = getDisplayTimeText(monAndSun[0]);
                        String sun = getDisplayTimeText(monAndSun[1]);
                        String date = mon + "-" + sun;
                        tv_date.setText(date);
                        curDate = dateStr;
                        clearCharts();
                        viewModel.clearDatas();
                        viewModel.getWeekData(mApp,curDate,curWatch.getEid());
                    }
                });
                dlg.setDate(curDate);
                dlg.show();
            }
        });

        //选中图表中的具体某条Bar的信息
        TextView tv_selected_day = view.findViewById(R.id.tv_selected_day);
        TextView tv_outside_dura = view.findViewById(R.id.tv_outside_dura);
        TextView tv_selected_lvl = view.findViewById(R.id.tv_selected_lvl);
        String deflvl = "--" + getString(R.string.unit_level);
        tv_selected_lvl.setText(deflvl);
        TextView tv_selected_lvl_dura = view.findViewById(R.id.tv_selected_lvl_dura);

        //当周统计数据
        TextView tv_ave_dura = view.findViewById(R.id.tv_ave_dura);
        TextView tv_round_lvl = view.findViewById(R.id.tv_round_lvl);

        //图表数据
        chart_outside = view.findViewById(R.id.chart_outside);
        initCharts();

        viewModel.getSelectOutsideTime().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null){
                    tv_selected_day.setText(s);
                }else{
                    tv_selected_day.setText("--");
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
                    tv_selected_lvl.setText(getString(R.string.health_outside_selected_lvl,integer));
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
        viewModel.getAveDura().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != null){
                    int ret = integer / 60;
                    tv_ave_dura.setText(String.valueOf(ret));
                }else{
                    tv_ave_dura.setText("--");
                }
            }
        });
        viewModel.getRoundLvl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String integer) {
                if(!TextUtils.isEmpty(integer)){
                    tv_round_lvl.setText(integer);
                }else{
                    tv_round_lvl.setText("--");
                }
            }
        });
        viewModel.getChartDatas().observe(getViewLifecycleOwner(), new Observer<List<OutSideChartWeekBean>>() {
            @Override
            public void onChanged(List<OutSideChartWeekBean> outSideChartBeans) {
                if(outSideChartBeans != null){
                    setBarChartData(outSideChartBeans);
                }
            }
        });
    }

    private void initCharts(){
        initBarChart();
    }

    private void initBarChart(){
        chart_outside.getDescription().setEnabled(false);
        chart_outside.setMaxVisibleValueCount(40);
        chart_outside.setPinchZoom(false);
        chart_outside.setDrawGridBackground(false);
        chart_outside.setDrawBarShadow(false);
        chart_outside.setDrawValueAboveBar(false);
        chart_outside.setHighlightFullBarEnabled(false);
        chart_outside.getLegend().setEnabled(false);
        chart_outside.getAxisLeft().setEnabled(false);

        XAxis xAxis = chart_outside.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(7f);
        xAxis.setLabelCount(7);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String s = "";
                if(v == 0){
                    s = getString(R.string.week_1);
                }else if(v == 1){
                    s = getString(R.string.week_2);
                }else if(v == 2){
                    s = getString(R.string.week_3);
                }else if(v == 3){
                    s = getString(R.string.week_4);
                }else if(v == 4){
                    s = getString(R.string.week_5);
                }else if(v == 5){
                    s = getString(R.string.week_6);
                }else if(v == 6){
                    s = getString(R.string.week_0);
                }
                return s;
            }
        });

        YAxis leftAxis = chart_outside.getAxisRight();
        leftAxis.setGranularity(15f);
        //leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1440f);
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
                    viewModel.getSelectOutsideTime().postValue(viewModel.getTimeFromChartSelect(mApp,x,curDate));

                    Log.e("VAL SELECTED", "highlight: " + h.toString());
                    if(h.getStackIndex() >= 0) {
                        Log.e("VAL SELECTED", "Y Value: " + entry.getYVals()[h.getStackIndex()]);
                        int index = -1;
                        for(int i=0;i<viewModel.chartDatas.getValue().size();i++){
                            OutSideChartWeekBean weekBean = viewModel.chartDatas.getValue().get(i);
                            if(weekBean.getWeekDay() == x+1){
                                index = i;
                            }
                        }
                        if(index == -1){
                            return;
                        }
                        OutSideChartWeekBean bean = viewModel.chartDatas.getValue().get(index);
                        viewModel.getSelectOutsideDura().postValue(bean.getOutside_week_all_dura());
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

    private void setBarChartData(List<OutSideChartWeekBean> outSideChartBeans){
        ArrayList<BarEntry> values = new ArrayList<>();
        for(int i=0;i < outSideChartBeans.size();i++){
            OutSideChartWeekBean bean = outSideChartBeans.get(i);
            bean.initChartValueBean();
            LogUtil.e("outside week setBarChartData : " + bean.printValues());
            float lvl1Dura = (float) bean.getLvl1_chart_value() / 60;
            float lvl2Dura = (float) bean.getLvl2_chart_value() / 60;
            float lvl3Dura = (float) bean.getLvl3_chart_value() / 60;
            float lvl4Dura = (float) bean.getLvl4_chart_value() / 60;
            float lvl5Dura = (float) bean.getLvl5_chart_value() / 60;
            values.add(new BarEntry(bean.getWeekDay()-1,new float[]{lvl1Dura,lvl2Dura,lvl3Dura,lvl4Dura,lvl5Dura}," day"));
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

    private void toPreWeek(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_YEAR,-7);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            String[] monAndSun = viewModel.getMonAndSunbyReserveDate(curDate);
            String monday = getDisplayTimeText(monAndSun[0]);
            String sunday = getDisplayTimeText(monAndSun[1]);
            String dateStr = monday + "-" + sunday ;
            LogUtil.e("toPreWeek : " + dateStr);
            tv_date.setText(dateStr);
            clearCharts();
            viewModel.clearDatas();
            viewModel.getWeekData(mApp,curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void toNextWeek(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format.parse(curDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            Calendar calNow = Calendar.getInstance();

            if(cal.get(Calendar.WEEK_OF_YEAR) == calNow.get(Calendar.WEEK_OF_YEAR)){
                return;
            }
            cal.add(Calendar.DAY_OF_YEAR,7);
            Date preday = cal.getTime();
            curDate = format.format(preday);
            String[] monAndSun = viewModel.getMonAndSunbyReserveDate(curDate);
            String monday = getDisplayTimeText(monAndSun[0]);
            String sunday = getDisplayTimeText(monAndSun[1]);
            String dateStr = monday + "-" + sunday ;
            LogUtil.e("toNextWeek : " + dateStr);
            tv_date.setText(dateStr);
            clearCharts();
            viewModel.clearDatas();
            viewModel.getWeekData(mApp,curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private String getDisplayTimeText(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format1.parse(time);
            if(d != null) {
                return format.format(d);
            }else{
                return "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getSelectBarTime(int index){
        String[] weekdays = viewModel.getAllWeekDaysByDate(curDate);
        String day = weekdays[index];
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        try {
            Date d = format.parse(day);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int month = cal.get(Calendar.MONTH) + 1;
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            return month + getString(R.string.month) + dayOfMonth + getString(R.string.day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "--";
    }

    private void clearCharts(){
        chart_outside.clear();
    }

    private int[] getColors(){
        int[] colors = new int[]{ColorTemplate.rgb("#ABF268"),ColorTemplate.rgb("#FFD108")
                ,ColorTemplate.rgb("#FEA40C"),ColorTemplate.rgb("#FF5C73"),ColorTemplate.rgb("#DC76F2")};
        return colors;
    }


}