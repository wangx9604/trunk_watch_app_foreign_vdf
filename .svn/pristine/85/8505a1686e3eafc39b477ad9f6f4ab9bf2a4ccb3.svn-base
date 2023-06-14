package com.xiaoxun.xun.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.google.android.material.shadow.ShadowDrawableWrapper;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class ShadowImageView extends androidx.appcompat.widget.AppCompatImageView {

    public ShadowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * background属性：解析background → setBackground → setBackgroundDrawable
     * setBackgroundResource接口：setBackgroundResource → setBackground → setBackgroundDrawable
     * 所以，选择重写setBackground
     */
    @Override
    public void setBackground(Drawable background) {

        Context context = getContext();
        // 仿照FAB实现机制，在原来drawable上绘制阴影
//        context, background, 10, 6, 6
        MaterialShapeDrawable wrapper = new MaterialShapeDrawable(new ShapeAppearanceModel().withCornerSize(10));
        setBackgroundDrawable(wrapper);
    }
}


