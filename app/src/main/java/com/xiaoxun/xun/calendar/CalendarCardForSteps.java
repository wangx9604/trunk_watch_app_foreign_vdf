package com.xiaoxun.xun.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;

/**
 * 自定义日历卡
 *
 * @author xilvkang
 */
public class CalendarCardForSteps extends View {

    private static final int TOTAL_COL = 7; // 7列
    private static final int TOTAL_ROW = 6; // 6行

    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private Paint mPointPaint;// 绘制点的画笔
    private Paint mCurSelectPaint;// 当前选择天的画笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距
    private Row[] rows = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop; //
    private boolean callBackCellSpace;

    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    private Context mContext;
    //private ArrayList<Date> calenderCounterList = new ArrayList<Date>();
    //private ArrayList<Integer> curCalendarPointCounter = new ArrayList<Integer>();
    private ImibabyApp myApp;
    private WatchData curWatch;
    //private boolean needSetPoint = false;

    /**
     * 单元格点击的回调接口
     *
     * @author xilvkang
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date, int PointNum); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCardForSteps(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCardForSteps(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCardForSteps(Context context) {
        super(context);
        init(context);
    }

    public CalendarCardForSteps(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {

        mContext = context;
        myApp = (ImibabyApp) ((Activity) (mContext))
                .getApplication();
        curWatch = myApp.getCurUser().getFocusWatch();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurSelectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(Color.RED);
        mPointPaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStyle(Paint.Style.FILL);
        //modify by fushiqing
//		mCirclePaint.setColor(Color.parseColor("#00b0b0")); // 红色圆形
        mCirclePaint.setColor(Color.parseColor("#f66d3e")); //橙色
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        fillDate();//
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month - 1); // 上个月的天数
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek
                        && position < firstDayWeek + currentMonthDays) {
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(
                            mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    // 今天
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifiDayForObject(
                                mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i, j);
                    }

                    // 过去一个月
                } else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
                            mShowDate.month - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
                            mShowDate.month + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }

                if (DatePointForSteps.stepsDataReady == 1) {
                    if (DatePointForSteps.curPageIndex == 1) {
                        if (DatePointForSteps.stepsPointList_cur.get(
                                j * TOTAL_COL + i) != null) {
                            rows[j].cells[i]
                                    .setPointNum(DatePointForSteps.stepsPointList_cur.get(
                                            j * TOTAL_COL + i).getPointNum());
                        }
                    } else if (DatePointForSteps.curPageIndex == 0 && DatePointForSteps.stepsPointList_pre1.size() >= 0) {
                        if (DatePointForSteps.stepsPointList_pre1.get(
                                j * TOTAL_COL + i) != null) {
                            rows[j].cells[i]
                                    .setPointNum(DatePointForSteps.stepsPointList_pre1.get(
                                            j * TOTAL_COL + i).getPointNum());
                        }
                    }
                }

            }
        }

        mCellClickListener.changeDate(mShowDate);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        //mTextPaint.setTextSize(mCellSpace / 3);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        float ratioWidth = (float) screenWidth / 1080;
        float ratioHeight = (float) screenHeight / 1920;
        float RATIO = Math.min(ratioWidth, ratioHeight);


        mTextPaint.setTextSize(Math.round(45 * RATIO));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            //该月还未到的天点击不响应
//            if (rows[row].cells[col].state == State.UNREACH_DAY) {
//                return;
//            }
//            if (DatePoint.curPageIndex == 2 && rows[row].cells[col].state == State.NEXT_MONTH_DAY) {
//                return;
//            }
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            int pointNum = rows[row].cells[col].getPointNum();
            mCellClickListener.clickDate(date, pointNum);

            // 刷新界面
            update();
        }
    }

    /**
     * 组元素
     *
     * @author xilvkang
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author xilvkang
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public int pointNum;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
            this.pointNum = 0;
        }

        public void drawSelf(Canvas canvas) {
            boolean isSelect = false;
            if (date.getDay() == DatePointForSteps.curSelectItem.getDay()
                    && date.getMonth() == DatePointForSteps.curSelectItem.getMonth()
                    && date.getYear() == DatePointForSteps.curSelectItem.getYear()) {
                isSelect = true;
                canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
                        (float) ((j + 0.5) * mCellSpace), mCellSpace / 3,
                        mCirclePaint);
            }
            switch (state) {
                case TODAY: // 今天
//				Bitmap selectBitmap = null;
//				if(!isSelect) {
//					selectBitmap = BitmapFactory.decodeResource(
//							getResources(), R.drawable.select_point_0);
//					canvas.drawBitmap(selectBitmap,
//							(float)((i+0.5)*mCellSpace) - selectBitmap.getWidth() / 2,
//							(float)((j+0.7)*mCellSpace) - selectBitmap.getHeight() / 2,
//							mCurSelectPaint);
//				}else{
//					selectBitmap = BitmapFactory.decodeResource(
//							getResources(), R.drawable.select_point_1);
//				}
                    mTextPaint.setColor(Color.parseColor("#f66d3e"));
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.parseColor("#666666"));
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.parseColor("#ffb5b5b4"));
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(Color.parseColor("#666666"));
                    break;
                default:
                    break;
            }
            if (pointNum > 0) {
                //modify by fushiqing
//				mTextPaint.setColor(Color.parseColor("#3bb4a2"));
//				mTextPaint.setColor(Color.parseColor("#f66d3e"));
                Bitmap selectBitmap = null;
                selectBitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.select_point_0);
                canvas.drawBitmap(selectBitmap,
                        (float) ((i + 0.5) * mCellSpace) - selectBitmap.getWidth() / 2,
                        (float) ((j + 0.2) * mCellSpace) - selectBitmap.getHeight() / 2,
                        mCurSelectPaint);
            } else {
                // mTextPaint.setColor(Color.parseColor("#666666"));
            }
            if (isSelect) {
                mTextPaint.setColor(Color.parseColor("#fffffe"));
            }
            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content,
                    (float) ((i + 0.5) * mCellSpace - mTextPaint
                            .measureText(content) / 2), (float) ((j + 0.7)
                            * mCellSpace - mTextPaint
                            .measureText(content, 0, 1) / 2), mTextPaint);


        }

        public void setPointNum(int num) {
            pointNum = num;
        }

        public int getPointNum() {
            return pointNum;
        }
    }

    /**
     * @author xilvkang 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.month == 1) {
            mShowDate.month = 12;
            mShowDate.year -= 1;
        } else {
            mShowDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();

        invalidate();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

}
