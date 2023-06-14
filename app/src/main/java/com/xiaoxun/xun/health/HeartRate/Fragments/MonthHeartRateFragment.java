package com.xiaoxun.xun.health.HeartRate.Fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.calendar.calendarView;
import com.xiaoxun.xun.health.HeartRate.Data.DataStruct;
import com.xiaoxun.xun.health.HeartRate.HeartRateModel;
import com.xiaoxun.xun.securityarea.WaitingDialog;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MonthHeartRateFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    CandleStickChart chart;
    TextView tv_pre_month;
    TextView tv_next_month;
    TextView tv_date;
    LinearLayout date_ly;
    TextView tv_aver_rate;
    TextView tv_high;
    TextView tv_low;
    TextView tv_rhr;
    RelativeLayout top_ly;
    calendarView calendarView_t;
    TextView tv_summary;

    HeartRateModel model;
    Observable<DataStruct.MonthData> heartRateDataObservable;
    DataStruct.MonthData curMonthData;
    String curSelectDate;
    String searchDate;
    boolean canClickNextMonth = false;

    WaitingDialog pd;

    public MonthHeartRateFragment() {

    }

    public MonthHeartRateFragment newInstance() {
        MonthHeartRateFragment fragment = new MonthHeartRateFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = HeartRateModel.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.heart_rate_month_fragment_ly, container, false);
        initViews(view);
        initDate();
        createHeaartRateDataObservable();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.e("xxxx MonthHeartRateFragment onViewCreated.");
        subscribeObservable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(pd != null){
            pd.cancel();
            pd = null;
        }
    }

    private void initDate() {
        curSelectDate = TimeUtil.getReversedTimeByTime(TimeUtil.getDay());
        searchDate = curSelectDate;
        tv_date.setText(getString(R.string.the_current_month));
    }

    private void initViews(View view) {
        pd = new WaitingDialog(getContext(), R.style.Theme_DataSheet);
        pd.enableKeyBack(true);
        chart = view.findViewById(R.id.monthChart);
        tv_pre_month = view.findViewById(R.id.preMonth);
        tv_pre_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreMonth();
            }
        });
        tv_next_month = view.findViewById(R.id.nextMonth);
        tv_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canClickNextMonth) {
                    return;
                }
                toNextMonth();
            }
        });
        tv_date = view.findViewById(R.id.tv_date);
        date_ly = view.findViewById(R.id.date_ly);
        tv_aver_rate = view.findViewById(R.id.tv_aver_rate);
        tv_high = view.findViewById(R.id.tv_high);
        tv_low = view.findViewById(R.id.tv_low);
        tv_rhr = view.findViewById(R.id.tv_rhr);
        top_ly = view.findViewById(R.id.top_ly);
        tv_summary = view.findViewById(R.id.tv_summary);

        initChart();
    }

    private void initChart() {
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.setNoDataText(getString(R.string.heart_rate_no_data_txt));
        chart.setNoDataTextDescription(getString(R.string.heart_rate_no_data_description));
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setVisibleYRange(100f, YAxis.AxisDependency.LEFT);
        chart.setPinchZoom(true);
        chart.getLegend().setEnabled(false);
        chart.setHighlightEnabled(false);

        XAxis xAxis;
        xAxis = chart.getXAxis();
        chart.getAxisRight().setEnabled(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(5);

        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMaxValue(200f);
        yAxis.setAxisMinValue(0f);
        yAxis.setLabelCount(4);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return String.valueOf((int) v);
            }
        });
    }

    private void createHeaartRateDataObservable() {
        heartRateDataObservable = Observable.create(new ObservableOnSubscribe<DataStruct.MonthData>() {
            @Override
            public void subscribe(ObservableEmitter<DataStruct.MonthData> e) throws Exception {
                LogUtil.e("MonthHeartRateFragment Observable");
                DataStruct.MonthData monthData = model.getMonthDataByDate(searchDate);
                //DataStruct.MonthData monthData = model.simulateMonthData(curSelectDate);
                if (monthData != null) {
                    e.onNext(monthData);
                } else {
                    curMonthData = null;
                    curSelectDate = searchDate;
                    emptyDataAndViewInvalid();
                }
            }
        });
    }

    private void subscribeObservable() {
        if(ActivityUtils.getTopActivity().isDestroyed()) return;
        if (getUserVisibleHint() && pd != null && !pd.isShowing()) {
            pd.normalShow();
        }
        Observer<DataStruct.MonthData> observer = new Observer<DataStruct.MonthData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DataStruct.MonthData monthData) {
                curMonthData = monthData;
                curSelectDate = searchDate;
                setChartData();
                if(ActivityUtils.getTopActivity().isDestroyed()) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setViewData();
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        LogUtil.e("MonthHeartRateFragment subscribeObservable");
        heartRateDataObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(observer);
    }

    private void setChartData() {
        ArrayList<CandleEntry> values = new ArrayList<>();
        ArrayList<String> xnames = new ArrayList<>();

        int index = 0;
        ArrayList<DataStruct.DayData> dayDatas = curMonthData.getMonthValue();
        int monthDayCount = getCurMonthDaysCount();
        Calendar monthDay = getCurMonthFirstDay();
        for (int i = 0; i < monthDayCount; i++) {
            String time = TimeUtil.getDayTimeStrFromDate(monthDay.getTime());
            String reversedTime = TimeUtil.getReversedTimeByTime(time);
            CandleEntry entry;
            if (index > dayDatas.size() - 1) {
                entry = new CandleEntry(i, -1, -1, -1, -1);
            } else {
                String valueDay = dayDatas.get(index).getDate();
                DataStruct.DayData item = dayDatas.get(index);
                if (reversedTime.equals(valueDay)) {
                    entry = new CandleEntry(i, item.getHighRate(), item.getLowRate(), item.getHighRate(), item.getLowRate());
                    index++;
                } else {
                    entry = new CandleEntry(i, -1, -1, -1, -1);
                }
            }
            Date d = TimeUtil.getDateFromOrderTimeStr(reversedTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String day = cal.get(Calendar.DAY_OF_MONTH) + getString(R.string.day);
            xnames.add(day);
            values.add(i, entry);
            monthDay.add(Calendar.DAY_OF_MONTH, 1);
        }
//        for(DataStruct.DayData item : dayDatas){
//            CandleEntry entry = new CandleEntry(index,item.getHighRate(),item.getLowRate(),item.getHighRate(),item.getLowRate());
//            Date d = TimeUtil.getDateFromOrderTimeStr(item.getDate());
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(d);
//            String day = cal.get(Calendar.DAY_OF_MONTH) + getString(R.string.day);
//            xnames.add(day);
//            values.add(index,entry);
//            index++;
//        }

        CandleDataSet set1 = new CandleDataSet(values, "");
        set1.setDrawValues(false);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.RED);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setBodySpace(0.2f);
        set1.setHighLightColor(Color.YELLOW);
        CandleData candleData = new CandleData(xnames, set1);
        chart.setData(candleData);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void setViewData() {
        tv_aver_rate.setText(getString(R.string.heart_rate_count, String.valueOf(curMonthData.getMonthAverageRate())));
        tv_high.setText(getString(R.string.heart_rate_count, String.valueOf(curMonthData.getMonthHighRate())));
        tv_low.setText(getString(R.string.heart_rate_count, String.valueOf(curMonthData.getMonthLowRate())));
        tv_rhr.setText(getString(R.string.heart_rate_count, String.valueOf(curMonthData.getMonthRestingRate())));

        DataStruct.DayData dayData = getSelectDayData();
        String curdate = TimeUtil.getReversedTimeByTime(curSelectDate);
        String content = getString(R.string.heart_rate_summary_title, TimeUtil.getSlashFormatTimeFromTimeStr(curdate),
                String.valueOf(dayData.getAverageRate()));
        ToolUtils.setTVColorText(tv_summary, content, String.valueOf(dayData.getAverageRate()), getResources().getColor(R.color.heart_rate_summary_average_color));

        Date selectDate = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectDate);
        Calendar calNow = Calendar.getInstance();
        if (cal.get(Calendar.MONTH) != calNow.get(Calendar.MONTH)) {
            setNextEnable(true);
            tv_date.setText(getString(R.string.heart_rate_month_tv_date, String.valueOf(cal.get(Calendar.MONTH) + 1)));
        } else {
            setNextEnable(false);
            tv_date.setText(getString(R.string.the_current_month));
        }
    }

    private void setNextEnable(boolean next_enable) {
        canClickNextMonth = next_enable;
        if (next_enable) {
            tv_next_month.setTextColor(getResources().getColor(R.color.txt_coupon_color));
        } else {
            tv_next_month.setTextColor(getResources().getColor(R.color.color_content_hint));
        }
    }

    private DataStruct.DayData getSelectDayData() {
        DataStruct.DayData curSeleceDayData = null;
        ArrayList<DataStruct.DayData> monthdays = curMonthData.getMonthValue();
        for (DataStruct.DayData dayData : monthdays) {
            if (dayData.getDate().equals(curSelectDate)) {
                curSeleceDayData = dayData;
                break;
            }
        }
        if (curSeleceDayData != null) {
            return curSeleceDayData;
        } else {
            return monthdays.get(0);
        }
    }

    private void toPreMonth() {
        Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date preMonthday = cal.getTime();
        String tempDay = TimeUtil.getReversedTimeByTime(TimeUtil.getDayTimeStrFromDate(preMonthday));
        String[] firstAndLast = TimeUtil.getFirstDayAndLastDayOfMonthByDate(tempDay);
        searchDate = firstAndLast[0];
        subscribeObservable();
    }

    private void toNextMonth() {
        Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date nextMonthday = cal.getTime();
        String tempDay = TimeUtil.getReversedTimeByTime(TimeUtil.getDayTimeStrFromDate(nextMonthday));
        String[] firstAndLast = TimeUtil.getFirstDayAndLastDayOfMonthByDate(tempDay);
        searchDate = firstAndLast[0];
        subscribeObservable();
    }

    private void emptyDataAndViewInvalid() {
        chart.clearValues();
        chart.notifyDataSetChanged();
        chart.invalidate();

        if(ActivityUtils.getTopActivity().isDestroyed()) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                //tv_date.setText(getString(R.string.heart_rate_month_tv_date,String.valueOf(cal.get(Calendar.MONTH)+1)));
                tv_aver_rate.setText("--");
                tv_high.setText("--");
                tv_low.setText("--");
                tv_rhr.setText("--");

                Calendar calNow = Calendar.getInstance();
                if (cal.get(Calendar.MONTH) != calNow.get(Calendar.MONTH)) {
                    setNextEnable(true);
                    tv_date.setText(getString(R.string.heart_rate_month_tv_date, String.valueOf(cal.get(Calendar.MONTH) + 1)));
                } else {
                    setNextEnable(false);
                    tv_date.setText(getString(R.string.the_current_month));
                }

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    private int getCurMonthDaysCount() {
        int year = curMonthData.getYear();
        int mon = curMonthData.getMonth();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, mon);
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return count;
    }

    private Calendar getCurMonthFirstDay() {
        int year = curMonthData.getYear();
        int mon = curMonthData.getMonth();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, mon);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        LogUtil.e("weekFragment onValueSelected : highlight = " + highlight.getXIndex() + "Xvals size : " + chart.getCandleData().getXVals().size());
        if (highlight.getXIndex() >= chart.getCandleData().getXVals().size()) {
            return;
        }
        String time = chart.getCandleData().getXVals().get(highlight.getXIndex());
        String dateStr = getTouchDayDateStr(time);
        for (DataStruct.DayData item : curMonthData.getMonthValue()) {
            if (item.getDate().equals(dateStr)) {
                curSelectDate = item.getDate();
                break;
            }
        }
//        curSelectDate = curMonthData.getMonthValue().get(highlight.getXIndex()).getDate();
        LogUtil.e("weekFragment onValueSelected : selectDate = " + curSelectDate);
        setViewData();
    }

    @Override
    public void onNothingSelected() {

    }

    private String getTouchDayDateStr(String sourceStr) {
        String temp = sourceStr.replace("日", "");
        int dayOfMonth = Integer.parseInt(temp);
        int month = curMonthData.getMonth();
        int year = curMonthData.getYear();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date d = cal.getTime();
        String orderStr = TimeUtil.getDayTimeStrFromDate(d);
        String reStr = TimeUtil.getReversedTimeByTime(orderStr);
        return reStr;
    }
}
