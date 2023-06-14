package com.xiaoxun.xun.health.HeartRate.Fragments;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.calendar.CustomDate;
import com.xiaoxun.xun.calendar.calendarView;
import com.xiaoxun.xun.health.HeartRate.Data.DataStruct;
import com.xiaoxun.xun.health.HeartRate.HeartRateModel;
import com.xiaoxun.xun.securityarea.WaitingDialog;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DayHeartRateFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    LineChart chart;
    TextView tv_pre_day;
    TextView tv_next_day;
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
    Observable<DataStruct.DayData> heartRateDataObservable;
    DataStruct.DayData curDayData;
    String curSelectDate;
    String searchDate;
    boolean canClickNextDay = false;

    WaitingDialog pd;

    public DayHeartRateFragment() {

    }

    public DayHeartRateFragment newInstance() {
        DayHeartRateFragment fragment = new DayHeartRateFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        model = HeartRateModel.getInstance(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.heart_rate_day_fragment_ly, container, false);
        initViews(view);
        initDate();
        createHeaartRateDataObservable();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.e("xxxx DayHeartRateFragment onViewCreated.");
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
        tv_pre_day = view.findViewById(R.id.preDay);
        tv_pre_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView_t.setPreDay();
            }
        });
        tv_next_day = view.findViewById(R.id.nextDay);
        tv_next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canClickNextDay) {
                    return;
                }
                calendarView_t.setNextDay();
            }
        });
        tv_date = view.findViewById(R.id.tv_date);
        date_ly = view.findViewById(R.id.date_ly);
        date_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCalendarLocation();
            }
        });
        tv_aver_rate = view.findViewById(R.id.tv_aver_rate);
        tv_high = view.findViewById(R.id.tv_high);
        tv_low = view.findViewById(R.id.tv_low);
        tv_rhr = view.findViewById(R.id.tv_rhr);
        top_ly = view.findViewById(R.id.top_ly);
        tv_summary = view.findViewById(R.id.tv_summary);

        initChart(view);
        initcalendar();
    }

    private void initDate() {
        curSelectDate = TimeUtil.getReversedTimeByTime(TimeUtil.getDay());
        searchDate = curSelectDate;
        final SimpleDateFormat displayformat = new SimpleDateFormat(getResources().getString(R.string.format_time), Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String displayDate = displayformat.format(cal.getTime());
        tv_date.setText(displayDate);
    }

    private void initChart(View view) {
        chart = view.findViewById(R.id.dayChart);
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(this);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        chart.setNoDataText(getString(R.string.heart_rate_no_data_txt));
        chart.setNoDataTextDescription(getString(R.string.heart_rate_no_data_description));
        chart.setScaleEnabled(false);
        chart.setHighlightEnabled(false);
        chart.setPinchZoom(true);
        chart.setVisibleYRange(120f, YAxis.AxisDependency.LEFT);
        chart.setPinchZoom(true);
        chart.getLegend().setEnabled(false);
        chart.animateX(1000);

        XAxis xAxis;
        xAxis = chart.getXAxis();
        chart.getAxisRight().setEnabled(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(359);
        xAxis.setTextSize(8f);

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

        if (getContext() != null)
            chart.setViewPortOffsets(DensityUtil.dip2px(getContext(), 35), DensityUtil.dip2px(getContext(), 10),
                    DensityUtil.dip2px(getContext(), 33), DensityUtil.dip2px(getContext(), 46));
    }

    private void initcalendar() {
        calendarView_t = new calendarView(getContext(), new calendarView.OnItemClickListener() {
            @Override
            public void getItemDate(CustomDate date, int num, int action) {
                Date d1 = new Date(date.getYear() - 1900, date.getMonth() - 1, date.getDay());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String timestr = format.format(d1);
                searchDate = TimeUtil.getReversedTimeByTime(timestr);
                subscribeObservable();

                final SimpleDateFormat displayformat = new SimpleDateFormat(getResources().getString(R.string.format_time), Locale.getDefault());
                String displayDate = displayformat.format(d1);
                tv_date.setText(displayDate);

                Calendar cal = Calendar.getInstance();
                if (TimeUtil.isTheSameDay(d1, cal.getTime())) {
                    //今天 不能点击后一天
                    setNextEnable(false);
                } else {
                    setNextEnable(true);
                }
            }
        });
        calendarView_t.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    private void setCalendarLocation() {
        Rect rect = new Rect();
        top_ly.getGlobalVisibleRect(rect);
        int h = top_ly.getResources().getDisplayMetrics().heightPixels - rect.bottom;
        calendarView_t.setHeight(h);
        calendarView_t.showAsDropDown(top_ly);
        calendarView_t.showCalendar();
    }

    private void setNextEnable(boolean next_enable) {
        canClickNextDay = next_enable;
        if (next_enable) {
            tv_next_day.setTextColor(getResources().getColor(R.color.txt_coupon_color));
        } else {
            tv_next_day.setTextColor(getResources().getColor(R.color.color_content_hint));
        }
    }

    private void setChartData() {
        ArrayList<ArrayList<DataStruct.BaseData>> lineDatas = model.getDayLineData(curDayData.getValues());
        List<LineDataSet> dataSets = new ArrayList<>();
        ArrayList<String> xNames = new ArrayList<>();
        for (int i = 0; i < lineDatas.size(); i++) {
            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<DataStruct.BaseData> map = lineDatas.get(i);
            for (DataStruct.BaseData item : map) {
                int min = item.getMin();
                int rate = item.getRate();
                values.add(new Entry(rate, min + 1));
            }
            LineDataSet set1;
            set1 = new LineDataSet(values, "");
            set1.setDrawValues(false);
            set1.setLineWidth(1f);
            set1.setColor(Color.RED);
            set1.setDrawCircles(false);
//            set1.setDrawFilled(true);
//            set1.setFillColor(Color.RED);
            set1.setDrawCubic(true);
            dataSets.add(set1);
        }

        for (int i = 0; i <= HeartRateModel.ONE_DAY_ALL_MINUTES; i++) {
            DecimalFormat format = new DecimalFormat("00");
            String hour = format.format(i / 60);
            String minutes = format.format(i % 60);
            xNames.add(hour + ":" + minutes);
        }
        LineData lineData = new LineData(xNames, dataSets);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void setViewData() {
        tv_aver_rate.setText(getString(R.string.heart_rate_count, String.valueOf(curDayData.getAverageRate())));
        tv_high.setText(getString(R.string.heart_rate_count, String.valueOf(curDayData.getHighRate())));
        tv_low.setText(getString(R.string.heart_rate_count, String.valueOf(curDayData.getLowRate())));
        tv_rhr.setText(getString(R.string.heart_rate_count, String.valueOf(curDayData.getRestingRate())));
        String curdate = TimeUtil.getReversedTimeByTime(curSelectDate);
        String content = getString(R.string.heart_rate_summary_title, TimeUtil.getSlashFormatTimeFromTimeStr(curdate),
                String.valueOf(curDayData.getAverageRate()));
        ToolUtils.setTVColorText(tv_summary, content, String.valueOf(curDayData.getAverageRate()), getResources().getColor(R.color.heart_rate_summary_average_color));

        Date selectDate = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectDate);
        Calendar calNow = Calendar.getInstance();
        setNextEnable(cal.get(Calendar.DAY_OF_YEAR) != calNow.get(Calendar.DAY_OF_YEAR));
    }

    private void createHeaartRateDataObservable() {
        heartRateDataObservable = Observable.create(new ObservableOnSubscribe<DataStruct.DayData>() {
            @Override
            public void subscribe(ObservableEmitter<DataStruct.DayData> e) throws Exception {
                LogUtil.e("DayHeartRateFragment subscribe");
                DataStruct.DayData dayData = model.getDayDataByDate(searchDate);
                //DataStruct.DayData dayData = model.simulateData(curSelectDate);
                if (dayData != null) {
                    e.onNext(dayData);
                } else {
                    curDayData = null;
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
        Observer<DataStruct.DayData> observer = new Observer<DataStruct.DayData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DataStruct.DayData dayData) {
                curDayData = dayData;
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
        LogUtil.e("DayHeartRateFragment subscribeObservable");
        if (heartRateDataObservable != null)
            heartRateDataObservable.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(observer);
    }

    public void refreshCurDayData() {
        LogUtil.e("DayHeartRateFragment refreshCurDayData");
        //手表E2E消息回执需要刷新数据，则清除今日数据
        if (model == null) {  //折叠屏切换大小屏，Activity重新创建，Fragment也重新创建，但是这里的model却为空指针，深层原因不明
            return;
        }
        if (model.dayDataList != null && model.dayDataList.size() != 0) {
            model.dayDataList.remove(0);
        }
        curSelectDate = TimeUtil.getReversedTimeByTime(TimeUtil.getDay());
        searchDate = curSelectDate;
        subscribeObservable();
    }

    private void emptyDataAndViewInvalid() {
        if(ActivityUtils.getTopActivity().isDestroyed()) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chart.getData() != null) {
                    chart.clearValues();
                    chart.notifyDataSetChanged();
                    chart.invalidate();
                }
                tv_summary.setText("--");
                tv_aver_rate.setText("--");
                tv_high.setText("--");
                tv_low.setText("--");
                tv_rhr.setText("--");

                Date d = TimeUtil.getDateFromOrderTimeStr(curSelectDate);
                Calendar calNow = Calendar.getInstance();
                if (TimeUtil.isTheSameDay(d, calNow.getTime())) {
                    setNextEnable(false);
                } else {
                    setNextEnable(true);
                }

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //切换时需要更新数据
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

    }

    @Override
    public void onNothingSelected() {

    }
}
