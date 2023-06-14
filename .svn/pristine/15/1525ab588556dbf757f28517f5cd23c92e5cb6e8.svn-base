package com.xiaoxun.xun.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoxun.xun.R;

/**
 * Created by guxiaolong on 2016/7/25.
 */

public class GifView extends View {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private int mMovieResourceId;
    private Movie mMovie;
    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;
    private float mLeft;
    private float mTop;
    private float mScale;
    private int mMeasuredMovieWidth;
    private int mMeasuredMovieHeight;
    private boolean mVisible = true;
    private volatile boolean mPaused = false;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setViewAttribbutes(context, attrs, defStyle);

    }

    @SuppressLint("NewApi")
    private void setViewAttribbutes(Context context, AttributeSet attrs, int defStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView, defStyle, R.style.Gif);
        mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1);
        mPaused = array.getBoolean(R.styleable.GifView_paused, false);
        array.recycle();
        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }

    public void setMovieResource(int movieId) {
        this.mMovieResourceId = movieId;
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        requestLayout();
    }

    public void setMovie(Movie movie) {
        this.mMovie = movie;
        requestLayout();
    }

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovieTime(int time) {
        mCurrentAnimationTime = time;
        invalidate();
    }

    public void setPaused(boolean paused) {
        this.mPaused = paused;
        if (!paused) {
            mMovieStart = SystemClock.uptimeMillis() - mCurrentAnimationTime;
        }
        invalidate();
    }

    public boolean isPaused() {
        return mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHight = mMovie.height();
            int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
            float scaleWidth = (float) movieWidth / (float) maxWidth;
            mScale = 1f / scaleWidth;
            mMeasuredMovieWidth = maxWidth;
            mMeasuredMovieHeight = (int) (movieHight * mScale);
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!isPaused()) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
        super.onDraw(canvas);
    }


//    @Override
//    protected void onDraw(Canvas canvas) {
//        long curTime = android.os.SystemClock.uptimeMillis();
////第一次播放
//        if (mMovieStart == 0) {
//            mMovieStart = curTime;
//        }
//        if (mMovie != null) {
//            int duraction = mMovie.duration();
//            int relTime = (int) ((curTime - mMovieStart) % duraction);
//            mMovie.setTime(relTime);
//            mMovie.draw(canvas, 0, 0);
////强制重绘
//            invalidate();
//        }
//        super.onDraw(canvas);
//    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (mVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }

    private void updateAnimationTime() {
        long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawMovieFrame(Canvas canvas) {
        mMovie.setTime(mCurrentAnimationTime);
        canvas.save();
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
        canvas.restore();
    }

    @SuppressLint("NewApi")
    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        invalidateView();
    }
}
