package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xiaoxun.xun.R;

import alex.photojar.photoView.DefaultOnDoubleTapListener;
import alex.photojar.photoView.PhotoViewAttacher;

/**
 * Created by guxiaolong on 2018/3/30.
 */

public class ImageDisplayActivity extends Activity {

    PhotoViewAttacher mAttacher;
    ImageView mImageView;
    private ChatPopupWindow menuWindow;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_display);

        imageUri = getIntent().getData();
        mImageView = findViewById(R.id.photoview);
        mImageView.setImageURI(imageUri);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

        mAttacher.setOnDoubleTapListener(new OnDoubleTopLisnter(mAttacher));
        mAttacher.update();

        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
        mAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("onLongClick");
                menuWindow = new ChatPopupWindow(ImageDisplayActivity.this, getString(R.string.share_title), new View.OnClickListener() {
                    public void onClick(View v) {
                        menuWindow.dismiss();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        ImageDisplayActivity.this.startActivity(intent);
                    }
                });
                menuWindow.showAtLocation(ImageDisplayActivity.this.findViewById(R.id.layout_image_display), Gravity.CENTER, 0, 0);
                return false;
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_zoom_exit);
    }

    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            ImageDisplayActivity.this.finish();
        }

        @Override
        public void onOutsidePhotoTap() {
            ImageDisplayActivity.this.finish();
        }
    }

    private class MatrixChangeListener implements PhotoViewAttacher.OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            System.out.println("onMatrixChanged");
        }
    }

    private class OnDoubleTopLisnter extends DefaultOnDoubleTapListener {
        private PhotoViewAttacher photoViewAttacher;
        public OnDoubleTopLisnter (PhotoViewAttacher photoViewAttacher) {
            super(photoViewAttacher);
            this.photoViewAttacher = photoViewAttacher;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            if(this.photoViewAttacher == null) {
                return false;
            } else {
                try {
                    float scale = this.photoViewAttacher.getScale();
                    float x = ev.getX();
                    float y = ev.getY();
                    if(scale < this.photoViewAttacher.getMediumScale()) {
                        this.photoViewAttacher.setScale(this.photoViewAttacher.getMediumScale(), x, y, true);
                    } else {
                        this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException var5) {
                }

                return true;
            }
        }
    }

}
