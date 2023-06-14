package com.xiaoxun.xun.dialBg;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import com.xiaoxun.xun.R;

public class RoundImageView extends AppCompatImageView {

    private static final int COLORDRAWABLE_DIMENSION = 2;

    private Bitmap mBitmap;

    /** 图片的类型，圆形or圆角  */
    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    /** 圆角大小的默认值  */
    private static final int BODER_RADIUS_DEFAULT = 10;
    /**  * 圆角的大小 */
    private int mBorderRadius;

    /**  * 绘图的Paint */
    private Paint mBitmapPaint;
    /** * 圆角的半径 */
    private int mRadius;
    /** * 3x3 矩阵，主要用于缩小放大 */
    private Matrix mMatrix;
    /**  * 渲染图像，使用图像为绘制图形着色 */
    private BitmapShader mBitmapShader;
    /** * view的宽度  */
    private int mWidth;
    private RectF mRoundRect;

    public RoundImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageView);
        mBorderRadius = a.getDimensionPixelSize(
                R.styleable.RoundImageView_radius, (int) TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                BODER_RADIUS_DEFAULT, getResources()
                                        .getDisplayMetrics()));// 默认为10dp
        type = a.getInt(R.styleable.RoundImageView_type, TYPE_ROUND);// 默认为Circle
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (type == TYPE_CIRCLE)
        {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if(drawable == null){
            Log.e("xxxx","setImageDrawable null.");
        }
        mBitmap = getBitmapFromDrawable(drawable);
        setUpShader();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setUpShader();
    }

    private void setUpShader()
    {
        if (mBitmap == null) {
            return;
        }
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE)
        {
            int bSize = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (type == TYPE_ROUND)
        {
            scale = Math.max(getWidth() * 1.0f / mBitmap.getWidth(), getHeight()
                    * 1.0f / mBitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mBitmap == null) {
            Log.e("xxxx","getDrawable() null.");
            return;
        }
        setUpShader();
        if (type == TYPE_ROUND)
        {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius,
                    mBitmapPaint);
        } else
        {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (type == TYPE_ROUND)
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
    }

    public Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
