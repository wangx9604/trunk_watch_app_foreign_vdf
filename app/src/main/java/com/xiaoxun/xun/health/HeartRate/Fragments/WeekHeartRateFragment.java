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

public class WeekHeartRateFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    CandleStickChart chart;
    TextView tv_pre_week;
    TextView tv_next_week;
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
    Observable<DataStruct.WeekData> heartRateDataObservable;
    DataStruct.WeekData curWeekData;
    String curSelectDate;
    String searchDate;
    boolean canClickNextWeek = false;

    WaitingDialog pd;

    public WeekHeartRateFragment() {

    }

    public WeekHeartRateFragment newInstance() {
        WeekHeartRateFragment fragment = new WeekHeartRateFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = HeartRateModel.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.heart_rate_week_fragment_ly, container, false);
        initViews(view);
        initDate();
        createHeaartRateDataObservable();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.e("xxxx WeekHeartRateFragment onViewCreated.");
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

    private void initViews(View view) {
        pd = new WaitingDialog(getContext(), R.style.Theme_DataSheet);
        pd.enableKeyBack(true);
        chart = view.findViewById(R.id.weekChart);
        tv_pre_week = view.findViewById(R.id.preWeek);
        tv_pre_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreWeek();
            }
        });
        tv_next_week = view.findViewById(R.id.nextWeek);
        tv_next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canClickNextWeek) {
                    return;
                }
                toNextWeek();
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
        //xAxis.setValues();

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

    private void initDate() {
        curSelectDate = TimeUtil.getReversedTimeByTime(TimeUtil.getDay());
        searchDate = curSelectDate;
        tv_date.setText(getString(R.string.the_current_week));
    }

    private void createHeaartRateDataObservable() {
        heartRateDataObservable = Observable.create(new ObservableOnSubscribe<DataStruct.WeekData>() {
            @Override
            public void subscribe(ObservableEmitter<DataStruct.WeekData> e) throws Exception {
                DataStruct.WeekData weekData = model.getWeekDataByDate(searchDate);
                //DataStruct.WeekData weekData = model.simulateWeekData();
                if (weekData != null) {
                    e.onNext(weekData);
                } else {
                    curWeekData = null;
                    curSelectDate = searchDate;
                    emptyDataAndViewInvalid();
                }
            }
        });
    }

    private void subscribeObservable() {
        if(ActivityUtils.getTopActivity().isDestroyed()) return;
        if (getUserVisibleHint() && pd != null && !pd.isShowing() ) {
            pd.normalShow();
        }
        Observer<DataStruct.WeekData> observer = new Observer<DataStruct.WeekData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DataStruct.WeekData weekData) {
                curWeekData = weekData;
                curSelectDate = searchDate;
                setChartData();
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
        heartRateDataObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(observer);
    }

    private void setChartData() {
        ArrayList<CandleEntry> values = new ArrayList<>();
        ArrayList<String> xnames = new ArrayList<>();

        int index = 0;
        ArrayList<DataStruct.DayData> dayDatas = curWeekData.getDaysValue();
        ArrayList<String> weekDates = curWeekData.getWeekDates();
        for (int i = 0; i < weekDates.size(); i++) {
            CandleEntry entry;
            String date = weekDates.get(i);
            if (index > dayDatas.size() - 1) {
                entry = new CandleEntry(i, -1, -1, -1, -1);
            } else {
                if (!dayDatas.get(index).getDate().equals(date)) {
                    entry = new CandleEntry(i, -1, -1, -1, -1);
                } else {
                    DataStruct.DayData item = dayDatas.get(index);
                    entry = new CandleEntry(i, item.getHighRate(), item.getLowRate(), item.getHighRate(), item.getLowRate());
                    index++;
                }
            }
            Date d = TimeUtil.getDateFromOrderTimeStr(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String week = getDayOfWeekStr(cal.get(Calendar.DAY_OF_WEEK));
            xnames.add(week);
            values.add(i, entry);
        }

//        for(DataStruct.DayData item : dayDatas){
//            CandleEntry entry = new CandleEntry(index,item.getHighRate(),item.getLowRate(),item.getHighRate(),item.getLowRate());
//            Date d = TimeUtil.getDateFromOrderTimeStr(item.getDate());
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(d);
//            String week = getDayOfWeekStr(cal.get(Calendar.DAY_OF_WEEK));
//            xnames.add(week);
//            values.add(index,entry);
//            index++;
//        }

        CandleDataSet set1 = new CandleDataSet(values, "");
        set1.setDrawValues(false);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.RED);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setBodySpace(0.4f);
        set1.setHighLightColor(Color.YELLOW);
        CandleData candleData = new CandleData(xnames, set1);
        chart.setData(candleData);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void setViewData() {
        tv_aver_rate.setText(getString(R.string.heart_rate_count, String.valueOf(curWeekData.getWeekAverageRate())));
        tv_high.setText(getString(R.string.heart_rate_count, String.valueOf(curWeekData.getWeekHighRate())));
        tv_low.setText(getString(R.string.heart_rate_count, String.valueOf(curWeekData.getWeekLowRate())));
        tv_rhr.setText(getString(R.string.heart_rate_count, String.valueOf(curWeekData.getWeekRestingRate())));

        boolean isCurWeek = TimeUtil.dateIsCurrentWeek(curSelectDate);
        if (!isCurWeek) {
            String startDay = curWeekData.getWeekDates().get(0);
            String endDay = curWeekData.getWeekDates().get(6);
            LogUtil.e("WeekHRFragment setViewData : " + startDay + " | " + endDay);
            tv_date.setText(getString(R.string.heart_rate_date_str, TimeUtil.getSlashDayStrFromReverseTimeStr(startDay),
                    TimeUtil.getSlashDayStrFromReverseTimeStr(endDay)));
        } else {
            tv_date.setText(getString(R.string.the_current_week));
        }
        setNextEnable(!isCurWeek);

        DataStruct.DayData dayData = getSelectDayData();
        String curdate = TimeUtil.getReversedTimeByTime(curSelectDate);
        String content = getString(R.string.heart_rate_summary_title, TimeUtil.getSlashFormatTimeFromTimeStr(curdate),
                String.valueOf(dayData.getAverageRate()));
        ToolUtils.setTVColorText(tv_summary, content, String.valueOf(dayData.getAverageRate()), getResources().getColor(R.color.heart_rate_summary_average_color));
    }

    private void toPreWeek() {
        LogUtil.e("WeekHRFragment toPreWeek : curSelectDate" + curSelectDate);
        Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date preWeekday = cal.getTime();

        String tempDay = TimeUtil.getReversedTimeByTime(TimeUtil.getDayTimeStrFromDate(preWeekday));
        String[] monAndSun = TimeUtil.getMonAndSunbyReserveDate(tempDay);
        LogUtil.e("WeekHRFragment toPreWeek : monAndSun = " + monAndSun[0] + " | " + monAndSun[1]);
        searchDate = monAndSun[0];   //周一作为默认选择的日期
        subscribeObservable();
    }

    private void toNextWeek() {
        Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date nextWeekday = cal.getTime();

        String tempDay = TimeUtil.getReversedTimeByTime(TimeUtil.getDayTimeStrFromDate(nextWeekday));
        String[] monAndSun = TimeUtil.getMonAndSunbyReserveDate(tempDay);
        searchDate = monAndSun[0];   //周一作为默认选择的日期
        subscribeObservable();
    }

    private void setNextEnable(boolean next_enable) {
        canClickNextWeek = next_enable;
        if (next_enable) {
            tv_next_week.setTextColor(getResources().getColor(R.color.txt_coupon_color));
        } else {
            tv_next_week.setTextColor(getResources().getColor(R.color.color_content_hint));
        }
    }

    private DataStruct.DayData getSelectDayData() {
        DataStruct.DayData curSeleceDayData = null;
        ArrayList<DataStruct.DayData> weekdays = curWeekData.getDaysValue();
        for (DataStruct.DayData dayData : weekdays) {
            if (dayData.getDate().equals(curSelectDate)) {
                curSeleceDayData = dayData;
                break;
            }
        }
        if (curSeleceDayData != null) {
            return curSeleceDayData;
        } else {
            return weekdays.get(0);
        }
    }

    private String getDayOfWeekStr(int d) {
        String ret = "";
        switch (d) {
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

    private void emptyDataAndViewInvalid() {
        chart.clearValues();
        chart.notifyDataSetChanged();
        chart.invalidate();

        if(ActivityUtils.getTopActivity().isDestroyed()) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] monAndSun = TimeUtil.getMonAndSunbyReserveDate(curSelectDate);
                tv_date.setText(getString(R.string.heart_rate_date_str, TimeUtil.getSlashDayStrFromReverseTimeStr(monAndSun[0]),
                        TimeUtil.getSlashDayStrFromReverseTimeStr(monAndSun[1])));
                tv_aver_rate.setText("--");
                tv_high.setText("--");
                tv_low.setText("--");
                tv_rhr.setText("--");
                boolean isCurWeek = TimeUtil.dateIsCurrentWeek(curSelectDate);
                setNextEnable(!isCurWeek);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
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
        LogUtil.e("weekFragment onValueSelected : highlight = " + highlight.getXIndex());
        if (highlight.getXIndex() >= curWeekData.getWeekDates().size()) {
            return;
        }
        curSelectDate = curWeekData.getWeekDates().get(highlight.getXIndex());
        LogUtil.e("weekFragment onValueSelected : selectDate = " + curSelectDate);
        setViewData();
    }

    @Override
    public void onNothingSelected() {

    }
}
