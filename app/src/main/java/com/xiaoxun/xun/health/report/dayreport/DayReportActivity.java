package com.xiaoxun.xun.health.report.dayreport;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.xiaoxun.xun.activitys.NormalAppCompatActivity;
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

public class DayReportActivity extends NormalAppCompatActivity {

    private ImibabyApp mApp;
    private DayReportViewModel viewModel;
    private String curDate;

    private BarChart chart_steps;
    private LineChart chart_heart;
    private BarChart chart_oxy;

    private TextView tv_date;
    TextView tv_steps_target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_report);
        mApp = (ImibabyApp) getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(DayReportViewModel.class);
        curDate = TimeUtil.getDay();

        initViews();
        viewModel.getDayReportData(mApp, curDate);
    }

    private void initViews() {
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView iv_pre = findViewById(R.id.iv_pre);
        iv_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreDay();
            }
        });
        ImageView iv_next = findViewById(R.id.iv_next);
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNextDay();
            }
        });
        LinearLayout date_ly = findViewById(R.id.date_ly);
        tv_date = findViewById(R.id.tv_date);
        tv_date.setText(getDisplayTimeText(curDate));
        date_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarViewDialog dlg = new CalendarViewDialog(DayReportActivity.this);
                dlg.setOnDateSelectListener(new CalendarViewDialog.OnDateSelectListener() {
                    @Override
                    public void onSelect(int year, int month, int day) {
                        String date = month + getString(R.string.month) + day + getString(R.string.day);
                        tv_date.setText(date);
                        String t = formatSelectDate(year, month, day);
                        curDate = t;
                        if (!curDate.equals(TimeUtil.getDay()) || viewModel.getTargetSteps().getValue() == null || viewModel.getTargetSteps().getValue().equals("0")) {
                            tv_steps_target.setVisibility(View.INVISIBLE);
                        } else {
                            tv_steps_target.setVisibility(View.VISIBLE);
                        }
                        viewModel.getDayReportData(mApp, t);
                    }
                });
                dlg.setDate(curDate);
                dlg.show();
            }
        });

        tv_steps_target = findViewById(R.id.tv_steps_target);
        viewModel.getTargetSteps().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null && !s.equals("")) {
                    tv_steps_target.setText(getString(R.string.health_monitor_target_step, s));
                } else {
                    tv_steps_target.setVisibility(View.INVISIBLE);
                }
            }
        });
        TextView tv_steps_count = findViewById(R.id.tv_steps_count);
        viewModel.getDaySteps().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_steps_count.setText(s);
            }
        });
        TextView tv_cal_count = findViewById(R.id.tv_cal_count);
        viewModel.getDayCal().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_cal_count.setText(s);
            }
        });
        TextView tv_distance_count = findViewById(R.id.tv_distance_count);
        viewModel.getDayDistance().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_distance_count.setText(s);
            }
        });
        ConstraintLayout ly_sedentary = findViewById(R.id.ly_sedentary);
        TextView tv_sedentary_count = findViewById(R.id.tv_sedentary_count);
        viewModel.getDaySedentary().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s == null) {
                    ly_sedentary.setVisibility(View.GONE);
                } else {
                    tv_sedentary_count.setText(s);
                }
            }
        });
        TextView tv_heart_count = findViewById(R.id.tv_heart_count);
        viewModel.getLatestHeartRate().observe(this, new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_count.setText(String.valueOf(number));
                }
            }
        });
        TextView tv_heart_high_count = findViewById(R.id.tv_heart_high_count);
        viewModel.getHeartRateMax().observe(this, new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_high_count.setText(String.valueOf(number));
                }
            }
        });
        TextView tv_heart_ave_count = findViewById(R.id.tv_heart_ave_count);
        viewModel.getHeartRateAve().observe(this, new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_ave_count.setText(String.valueOf(number));
                }
            }
        });
        TextView tv_heart_low_count = findViewById(R.id.tv_heart_low_count);
        viewModel.getHeartRateMin().observe(this, new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_heart_low_count.setText(String.valueOf(number));
                }
            }
        });
        TextView tv_oxy_count = findViewById(R.id.tv_oxy_count);
        viewModel.getLatestOxy().observe(this, new Observer<Number>() {
            @Override
            public void onChanged(@Nullable Number number) {
                if (number != null) {
                    tv_oxy_count.setText(String.valueOf(number));
                }
            }
        });

        initCharts();
        viewModel.getStepChartDatas().observe(this, new Observer<List<DayChartData>>() {
            @Override
            public void onChanged(@Nullable List<DayChartData> dayChartData) {
                if (dayChartData != null) {
                    setStepChartData(dayChartData);
                }
            }
        });
        viewModel.getHeartRateChartDatas().observe(this, new Observer<List<DayChartData>>() {
            @Override
            public void onChanged(@Nullable List<DayChartData> dayChartData) {
                if (dayChartData != null) {
                    setHeartChartData(dayChartData);
                }
            }
        });
        viewModel.getOxyChartDatas().observe(this, new Observer<List<DayOxyData>>() {
            @Override
            public void onChanged(@Nullable List<DayOxyData> dayOxyData) {
                if (dayOxyData != null) {
                    setOxyChartData(dayOxyData);
                }
            }
        });

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

    private void initCharts() {
        initStepchart();
        initHeartRateChart();
        initOxyChart();
    }

    private void initStepchart() {
        chart_steps = findViewById(R.id.chart_steps);
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
                LogUtil.e("xAxis x = " + v);
                String s = "";
                if (v == 0) {
                    s = "00";
                } else if (v == 6) {
                    s = "06";
                } else if (v == 12) {
                    s = "12";
                } else if (v == 18) {
                    s = "18";
                } else if (v == 24) {
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
                if (v == 200 || v == 400 || v == 600 || v == 800) {
                    s = String.valueOf(v);
                }
                return s;
            }
        });
    }

    private void initHeartRateChart() {
        chart_heart = findViewById(R.id.chart_heart);
        chart_heart.getDescription().setEnabled(false);
        chart_heart.setNoDataText(getString(R.string.battery_detail_chart_no_data));
        chart_heart.setTouchEnabled(false);
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
                //if(v == 0 || v == 40 || v == 85 || v == 130 || v == 175 || v == 220)
                {
                    s = String.valueOf(v);
                }
                return s;
            }
        });
    }

    private void initOxyChart() {
        chart_oxy = findViewById(R.id.chart_oxy);
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
            BarEntry entry = new BarEntry(hour, value);
            values.add(entry);
        }
        BarDataSet set1;
        if (chart_steps.getData() != null &&
                chart_steps.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart_steps.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart_steps.getData().notifyDataChanged();
            chart_steps.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setColor(getResources().getColor(R.color.health_monitor_str_color));
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            BarData barData = new BarData(set1);
            chart_steps.setData(barData);
        }
        chart_steps.invalidate();
    }

    private void setHeartChartData(List<DayChartData> heartRateChartData) {
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
        }
        LineDataSet set1;
        if (chart_heart.getData() != null &&
                chart_heart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart_heart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart_heart.getData().notifyDataChanged();
            chart_heart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "HeartRate");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            set1.setDrawCircleHole(false);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1f);
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
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
        BarDataSet setNormal, setLight, setMiddle, setSevere;
        if (chart_oxy.getData() != null && chart_oxy.getData().getDataSetCount() > 0) {
            setNormal = (BarDataSet) chart_oxy.getData().getDataSetByIndex(0);
            setLight = (BarDataSet) chart_oxy.getData().getDataSetByIndex(1);
            setMiddle = (BarDataSet) chart_oxy.getData().getDataSetByIndex(2);
            setSevere = (BarDataSet) chart_oxy.getData().getDataSetByIndex(3);
            setNormal.setValues(valuesNormal);
            setLight.setValues(valuesLight);
            setMiddle.setValues(valuesMiddle);
            setSevere.setValues(valuesSevere);
            chart_oxy.getData().notifyDataChanged();
            chart_oxy.notifyDataSetChanged();
        } else {
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
            if (!curDate.equals(TimeUtil.getDay()) || viewModel.getTargetSteps().getValue() == null || viewModel.getTargetSteps().getValue().equals("0")) {
                tv_steps_target.setVisibility(View.INVISIBLE);
            } else {
                tv_steps_target.setVisibility(View.VISIBLE);
            }
            viewModel.getDayReportData(mApp, curDate);
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
            if (!curDate.equals(TimeUtil.getDay()) || viewModel.getTargetSteps().getValue() == null || viewModel.getTargetSteps().getValue().equals("0")) {
                tv_steps_target.setVisibility(View.INVISIBLE);
            } else {
                tv_steps_target.setVisibility(View.VISIBLE);
            }
            viewModel.getDayReportData(mApp, curDate);
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
            String ret = month + getString(R.string.month) + day + getString(R.string.day);
            return ret;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}