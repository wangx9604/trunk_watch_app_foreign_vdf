package com.xiaoxun.xun.health.report.fragments.day;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.newcharting.charts.BarChart;
import com.github.mikephil.newcharting.charts.LineChart;
import com.github.mikephil.newcharting.components.AxisBase;
import com.github.mikephil.newcharting.components.XAxis;
import com.github.mikephil.newcharting.components.YAxis;
import com.github.mikephil.newcharting.data.BarData;
import com.github.mikephil.newcharting.data.BarDataSet;
import com.github.mikephil.newcharting.data.BarEntry;
import com.github.mikephil.newcharting.data.Entry;
import com.github.mikephil.newcharting.data.LineData;
import com.github.mikephil.newcharting.data.LineDataSet;
import com.github.mikephil.newcharting.formatter.IAxisValueFormatter;
import com.github.mikephil.newcharting.interfaces.datasets.ILineDataSet;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.health.customview.CalendarViewDialog;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayChartData;
import com.xiaoxun.xun.health.report.fragments.dataBean.DayOxyData;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private ImibabyApp mApp;
    private WatchData curWatch;
    private DayFragmentViewModel viewModel;
    private String curDate;
    private int targetStep = 0;

    TextView tv_date;
    TextView tv_steps_target;
    private BarChart chart_steps;
    private LineChart chart_heart;
    private BarChart chart_oxy;

    public DayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.scroll to view
     * @param param2 Parameter 2.
     * @return A new instance of fragment DayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayFragment newInstance(String param1, String param2,String param3) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
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
            mParam3 = getArguments().getString(ARG_PARAM3);
            targetStep = Integer.parseInt(mParam2);
            curWatch = mApp.getCurUser().queryWatchDataByEid(mParam3);
        }
        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(DayFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        curDate = TimeUtil.getDay();
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //viewModel.testData();
        view.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("c to " + mParam1);
                scrollTo(view.findViewById(R.id.sv_content),getScrollCardView(mParam1,view));
            }
        });
        viewModel.getDayDatas(mApp, curDate,curWatch.getEid());
    }

    private void initViews(View view) {
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
                        if (!curDate.equals(TimeUtil.getDay()) || targetStep == 0) {
                            tv_steps_target.setVisibility(View.INVISIBLE);
                        } else {
                            tv_steps_target.setVisibility(View.VISIBLE);
                            tv_steps_target.setText(getString(R.string.health_monitor_target_step, String.valueOf(targetStep)));
                        }
                        clearCharts();
                        viewModel.clearDatas();
                        viewModel.getDayDatas(mApp, t,curWatch.getEid());
                    }
                });
                dlg.setDate(curDate);
                dlg.show();
            }
        });

        tv_steps_target = view.findViewById(R.id.tv_steps_target);
        if (targetStep == 0) {
            tv_steps_target.setVisibility(View.INVISIBLE);
        } else {
            tv_steps_target.setText(getString(R.string.health_monitor_target_step, String.valueOf(targetStep)));
        }
        viewModel.getTargetStep().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.equals("")) {
                    String target = getString(R.string.health_monitor_target_step, s);
                    tv_steps_target.setText(target);
                } else {
                    tv_steps_target.setText("--");
                }
            }
        });
        TextView tv_steps_count = view.findViewById(R.id.tv_steps_count);
        viewModel.getDaySteps().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_steps_count.setText(s);
            }
        });
        TextView tv_cal_count = view.findViewById(R.id.tv_cal_count);
        viewModel.getDayCal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    double cal = Double.parseDouble(s);
                    tv_cal_count.setText(formatCaloris(cal));
                } else {
                    tv_cal_count.setText("--");
                }
            }
        });
        TextView tv_distance_count = view.findViewById(R.id.tv_distance_count);
        viewModel.getDayDistance().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    double dis = Double.parseDouble(s);
                    tv_distance_count.setText(formatKilometers(dis));
                } else {
                    tv_distance_count.setText("--");
                }
            }
        });
        TextView tv_heart_count = view.findViewById(R.id.tv_heart_count);
        viewModel.getLatestHeartRate().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_count.setText(String.valueOf(number.intValue()));
                } else {
                    tv_heart_count.setText("--");
                }
            }
        });
        TextView tv_heart_high_count = view.findViewById(R.id.tv_heart_high_count);
        viewModel.getHeartRateMax().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_high_count.setText(String.valueOf(number.intValue()));
                } else {
                    tv_heart_high_count.setText("--");
                }
            }
        });
        TextView tv_heart_ave_count = view.findViewById(R.id.tv_heart_ave_count);
        viewModel.getHeartRateAve().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_ave_count.setText(String.valueOf(number.intValue()));
                } else {
                    tv_heart_ave_count.setText("--");
                }
            }
        });
        TextView tv_heart_low_count = view.findViewById(R.id.tv_heart_low_count);
        viewModel.getHeartRateMin().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_low_count.setText(String.valueOf(number.intValue()));
                } else {
                    tv_heart_low_count.setText("--");
                }
            }
        });
        TextView tv_oxy_count = view.findViewById(R.id.tv_oxy_count);
        TextView tv_oxy_status = view.findViewById(R.id.tv_oxy_status);
        viewModel.getLatestOxy().observe(getViewLifecycleOwner(), new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_oxy_count.setText(String.valueOf(number.intValue()));
                    getOxyStatus(tv_oxy_status, number.intValue());
                    tv_oxy_status.setVisibility(View.VISIBLE);
                } else {
                    tv_oxy_count.setText("--");
                    tv_oxy_status.setVisibility(View.GONE);
                }
            }
        });

        chart_steps = view.findViewById(R.id.chart_steps);
        chart_heart = view.findViewById(R.id.chart_heart);
        chart_oxy = view.findViewById(R.id.chart_oxy);
        initChart();
        viewModel.getStepChartDatas().observe(getViewLifecycleOwner(), new Observer<List<DayChartData>>() {
            @Override
            public void onChanged(@Nullable List<DayChartData> dayChartData) {
                if (dayChartData != null) {
                    setStepChartData(dayChartData);
                }
            }
        });
        viewModel.getHeartRateChartDatas().observe(getViewLifecycleOwner(), new Observer<List<DayChartData>>() {
            @Override
            public void onChanged(@Nullable List<DayChartData> dayChartData) {
                if (dayChartData != null) {
                    setHeartRateChartData(dayChartData);
                }
            }
        });
        viewModel.getOxyChartDatas().observe(getViewLifecycleOwner(), new Observer<List<DayOxyData>>() {
            @Override
            public void onChanged(@Nullable List<DayOxyData> dayOxyData) {
                if (dayOxyData != null) {
                    setOxyChartData(dayOxyData);
                }
            }
        });

    }

    private String formatDurationStr(int minutes) {
        String ret = "";
        int hour = minutes / 60;
        int min = minutes % 60;
        ret = formatTwoNumber(hour) + getString(R.string.unit_hour)
                + formatTwoNumber(min) + getString(R.string.unit_minute);
        return ret;
    }

    private String formatTwoNumber(int num) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(num);
    }

    private void toPreDay() {
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
            if (!curDate.equals(TimeUtil.getDay()) || targetStep == 0) {
                tv_steps_target.setVisibility(View.INVISIBLE);
            } else {
                tv_steps_target.setVisibility(View.VISIBLE);
                tv_steps_target.setText(getString(R.string.health_monitor_target_step, String.valueOf(targetStep)));
            }
            viewModel.getDayDatas(mApp, curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void toNextDay() {
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
            if (!curDate.equals(TimeUtil.getDay()) || targetStep == 0) {
                tv_steps_target.setVisibility(View.INVISIBLE);
            } else {
                tv_steps_target.setVisibility(View.VISIBLE);
                tv_steps_target.setText(getString(R.string.health_monitor_target_step, String.valueOf(targetStep)));
            }
            viewModel.getDayDatas(mApp, curDate,curWatch.getEid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    private String getSightlvl(int n) {
        String lvl = getString(R.string.level_normal);
        switch (n) {
            case 0:
                lvl = getString(R.string.level_normal);
                break;
            case 1:
                lvl = getString(R.string.level_tiny);
                break;
            case 2:
                lvl = getString(R.string.level_a_bit);
                break;
            case 3:
                lvl = getString(R.string.level_obvious);
                break;
        }
        return lvl;
    }

    private String getBrainlvl(int n) {
        String lvl = getString(R.string.level_normal);
        switch (n) {
            case 0:
                lvl = getString(R.string.level_normal);
                break;
            case 1:
                lvl = getString(R.string.level_light);
                break;
            case 2:
                lvl = getString(R.string.level_middle);
                break;
            case 3:
                lvl = getString(R.string.level_severe);
                break;
        }
        return lvl;
    }

    private void initChart() {
        initStepChart();
        initHeartRateChart();
        initOxyChart();
    }


    private void initStepChart() {
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
        xAxis.setAxisMaximum(24f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                int value = (int) v;
                String s = "";
                if (value == 0) {
                    s = "00";
                } else if (value == 6) {
                    s = "06";
                } else if (value == 12) {
                    s = "12";
                } else if (value == 18) {
                    s = "18";
                } else if (value == 24) {
                    s = "24";
                }
                return s;
            }
        });
        YAxis yAxis = chart_steps.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(800f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(5);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String s = "";
                if (v == 0 || v == 200 || v == 400 || v == 600 || v == 800) {
                    s = String.valueOf((int) v);
                }
                return s;
            }
        });
    }

    private void initHeartRateChart() {
        chart_heart.setBackgroundColor(Color.WHITE);
        chart_heart.getDescription().setEnabled(false);
        chart_heart.setNoDataText(getString(R.string.battery_detail_chart_no_data));
        chart_heart.setTouchEnabled(false);
        chart_heart.setDrawGridBackground(true);
        chart_heart.setPinchZoom(true);
        chart_heart.getAxisLeft().setEnabled(false);
        chart_heart.getLegend().setEnabled(false);
        chart_heart.setBackgroundColor(Color.WHITE);
        chart_heart.setDrawGridBackground(false);
        chart_heart.setExtraBottomOffset(16f);

        XAxis xAxis = chart_heart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(1440f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                LogUtil.e("xAxis x = " + v);
                String s = "";
                if (v == 0) {
                    s = "00";
                } else if (v == 360) {
                    s = "06";
                } else if (v == 720) {
                    s = "12";
                } else if (v == 1080) {
                    s = "18";
                } else if (v == 1440) {
                    s = "24";
                }
                return s;
            }
        });
        YAxis yAxis = chart_heart.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(220f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(5, true);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String s = "";
                LogUtil.e("yAxis y = " + v);
                //if(v == 0 || v == 40 || v == 85 || v == 130 || v == 175 || v == 220)
                {
                    s = String.valueOf((int) v);
                }
                return s;
            }
        });
    }

    private void initOxyChart() {
        chart_oxy.setBackgroundColor(Color.WHITE);
        chart_oxy.getDescription().setEnabled(false);
        chart_oxy.setNoDataText(getString(R.string.battery_detail_chart_no_data));
        chart_oxy.setTouchEnabled(false);
        chart_oxy.setDrawGridBackground(true);
        chart_oxy.setPinchZoom(true);
        chart_oxy.getAxisLeft().setEnabled(false);
        chart_oxy.getLegend().setEnabled(false);
        chart_oxy.setBackgroundColor(Color.WHITE);
        chart_oxy.setDrawGridBackground(false);
        chart_oxy.setExtraBottomOffset(16f);

        XAxis xAxis = chart_oxy.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(1440f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);
        xAxis.setTextColor(getResources().getColor(R.color.battery_detail_tab_divide));
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                LogUtil.e("oxy xAxis x = " + v);
                String s = "";
                if (v == 0f) {
                    s = "00";
                } else if (v == 360.0f) {
                    s = "06";
                } else if (v == 720.0f) {
                    s = "12";
                } else if (v == 1080.0f) {
                    s = "18";
                } else if (v == 1440.0f) {
                    s = "24";
                }
                return s;
            }
        });
        YAxis yAxis = chart_oxy.getAxisRight();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaximum(40f);
        yAxis.setAxisMinimum(0f);
        yAxis.setLabelCount(5);
        yAxis.setTextColor(getResources().getColor(R.color.app_score_color));
        yAxis.setTextSize(12f);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String s = "";
                if (v == 10) {
                    s = "70%";
                } else if (v == 20) {
                    s = "80%";
                } else if (v == 30) {
                    s = "90%";
                } else if (v == 40) {
                    s = "100%";
                }
                return s;
            }
        });
    }

    private void setStepChartData(List<DayChartData> stepsData) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < stepsData.size(); i++) {
            int hour = Integer.parseInt(stepsData.get(i).getTimeStamp());
            int value = stepsData.get(i).getValue();
            BarEntry entry = new BarEntry(hour + 1, value);
            values.add(entry);
        }
        BarDataSet set1;
//        if(chart_steps.getData() != null &&
//                chart_steps.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) chart_steps.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            chart_steps.getData().notifyDataChanged();
//            chart_steps.notifyDataSetChanged();
//        }else
        {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set1.setColor(getResources().getColor(R.color.health_monitor_str_color));
            BarData barData = new BarData(set1);
            chart_steps.setData(barData);
        }
        chart_steps.invalidate();
    }

    private void setHeartRateChartData(List<DayChartData> heartRateChartData) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < heartRateChartData.size(); i++) {
            String dataTime = heartRateChartData.get(i).getTimeStamp();
            int value = heartRateChartData.get(i).getValue();
            Date date = TimeUtil.unixTimeToDate(dataTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int mins = hour * 60 + minute;
            Entry entry = new Entry(mins, value);
            values.add(entry);
            LogUtil.e("DayFragment setHeartRateChartData list min = " + mins + " | value = " + value);
        }
        LineDataSet set1;
//        if (chart_heart.getData() != null &&
//                chart_heart.getData().getDataSetCount() > 0) {
//            set1 = (LineDataSet) chart_heart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            set1.notifyDataSetChanged();
//            chart_heart.getData().notifyDataChanged();
//            chart_heart.notifyDataSetChanged();
//        } else
        {
            set1 = new LineDataSet(values, "HeartRate");
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setDrawCircleHole(false);
            set1.setDrawFilled(true);
            set1.setLineWidth(1f);
            set1.setDrawCircles(false);
            set1.setColor(getResources().getColor(R.color.health_report_chart_line_color));
            Drawable drawable = getResources().getDrawable(R.drawable.fade_linechart_red);
            set1.setFillDrawable(drawable);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart_heart.setData(data);
        }
        chart_heart.invalidate();
    }

    private void setOxyChartData(List<DayOxyData> oxyChartData) {
        ArrayList<BarEntry> valuesNormal = new ArrayList<>();
        ArrayList<BarEntry> valuesLight = new ArrayList<>();
        ArrayList<BarEntry> valuesMiddle = new ArrayList<>();
        ArrayList<BarEntry> valuesSevere = new ArrayList<>();
        for (int i = 0; i < oxyChartData.size(); i++) {
            String dataTime = oxyChartData.get(i).getTimeStamp();
            int value = oxyChartData.get(i).getValue();
            int level = oxyChartData.get(i).getLevel();
            Date date = TimeUtil.unixTimeToDate(dataTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            LogUtil.e("oxy : " + cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND));
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int mins = hour * 60 + minute;
            BarEntry entry = new BarEntry(mins, value);
            if (level == 0) {
                valuesNormal.add(entry);
            } else if (level == 1) {
                valuesLight.add(entry);
            } else if (level == 2) {
                valuesMiddle.add(entry);
            } else {
                valuesSevere.add(entry);
            }
        }
        LogUtil.e("oxy normal = " + valuesNormal.size() + " light = " + valuesLight.size() + " middle = " + valuesMiddle.size()
                + " severs = " + valuesSevere.size());
        BarDataSet setNormal, setLight, setMiddle, setSevere;
//        if(chart_oxy.getData() != null && chart_oxy.getData().getDataSetCount() > 0){
//            setNormal = (BarDataSet)chart_oxy.getData().getDataSetByIndex(0);
//            setLight = (BarDataSet)chart_oxy.getData().getDataSetByIndex(1);
//            setMiddle = (BarDataSet)chart_oxy.getData().getDataSetByIndex(2);
//            setSevere = (BarDataSet)chart_oxy.getData().getDataSetByIndex(3);
//            setNormal.setValues(valuesNormal);
//            setLight.setValues(valuesLight);
//            setMiddle.setValues(valuesMiddle);
//            setSevere.setValues(valuesSevere);
//            chart_oxy.getData().notifyDataChanged();
//            chart_oxy.notifyDataSetChanged();
//        }else
        {
            setNormal = new BarDataSet(valuesNormal, "Normal");
            setNormal.setColor(getResources().getColor(R.color.health_report_legend_green));
            setNormal.setDrawIcons(false);
            setNormal.setDrawValues(false);
            setNormal.setAxisDependency(YAxis.AxisDependency.RIGHT);
            setLight = new BarDataSet(valuesLight, "Light");
            setLight.setColor(getResources().getColor(R.color.health_report_legend_light_yellow));
            setLight.setDrawIcons(false);
            setLight.setDrawValues(false);
            setLight.setAxisDependency(YAxis.AxisDependency.RIGHT);
            setMiddle = new BarDataSet(valuesMiddle, "Middle");
            setMiddle.setColor(getResources().getColor(R.color.health_report_legend_orange));
            setMiddle.setDrawValues(false);
            setMiddle.setDrawIcons(false);
            setMiddle.setAxisDependency(YAxis.AxisDependency.RIGHT);
            setSevere = new BarDataSet(valuesSevere, "Severe");
            setSevere.setColor(getResources().getColor(R.color.health_report_legend_red));
            setSevere.setDrawIcons(false);
            setSevere.setDrawValues(false);
            setSevere.setAxisDependency(YAxis.AxisDependency.RIGHT);
            BarData barData = new BarData(setNormal, setLight, setMiddle, setSevere);
            chart_oxy.setData(barData);
        }
        chart_oxy.invalidate();
    }

    private String getDisplayTimeText(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date d = format.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String ret = month + getString(R.string.month) + day + getString(R.string.day);
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getOxyStatus(TextView view, int value) {
        if (value >= 90) {
            view.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_normal));
            view.setText("正常");
        } else if (value >= 80) {
            view.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_light));
            view.setText("轻度缺氧");
        } else if (value >= 70) {
            view.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_middle));
            view.setText("中度缺氧");
        } else {
            view.setBackground(getResources().getDrawable(R.drawable.shape_health_oxy_severe));
            view.setText("重度缺氧");
        }
    }

    private void clearCharts() {
        chart_steps.clear();
        chart_heart.clear();
        chart_oxy.clear();
    }


    private String formatKilometers(double s) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(s);
    }

    private String formatCaloris(double s) {
        DecimalFormat format = new DecimalFormat("0");
        return format.format(s);
    }

    private void scrollTo(ScrollView sv,CardView cv){
        LogUtil.e("scrollTo cv top=" + cv.getTop());
        sv.smoothScrollTo(0,cv.getTop());
    }

    private CardView getScrollCardView(String sign,View root){
        CardView cv = root.findViewById(R.id.ly_steps);
        switch (sign){
            case "step":
                cv = root.findViewById(R.id.ly_steps);
                break;
            case "heart":
                cv = root.findViewById(R.id.ly_heart);
                break;
            case "oxy":
                cv = root.findViewById(R.id.ly_oxy);
                break;

        }
        return cv;
    }
}
