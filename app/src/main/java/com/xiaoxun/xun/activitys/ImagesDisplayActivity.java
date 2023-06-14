package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.ChatImage;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import alex.photojar.photoView.PhotoView;
import alex.photojar.photoView.PhotoViewAttacher;

/**
 * Uesr：yaoyonghui on 2020/4/16 14:14
 * Email：yaoyonghui@loogcheer.com
 * Project: trunk_watch_app
 */
public class ImagesDisplayActivity extends Activity {

    private int index;
    private ViewPager viewPager;
    private ArrayList<ChatImage> fileList = new ArrayList<>();
    private Map<Integer,File> files;
    private ImagePagerAdaper imagePagerAdaper;
    private ChatPopupWindow menuWindow;
    private ImibabyApp mApp;
    public static final int VIEW_TYPE_INTERVAL = 100;
    private Hashtable<Integer, Boolean> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_images_display);
        mApp = (ImibabyApp) getApplication();
        index = getIntent().getIntExtra("index", 0);
        initViews();
        initDatas();
        initViewPager();
    }

    private void initViews() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void initViewPager() {
        imagePagerAdaper = new ImagePagerAdaper(this);
        viewPager.setAdapter(imagePagerAdaper);
        viewPager.setCurrentItem(index);

    }

    private void initDatas() {
        mMap = new Hashtable<>();
        String fileListStr = getIntent().getStringExtra("file_list");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ChatImage>>() {
        }.getType();
        fileList = gson.fromJson(fileListStr, type);
        files = new HashMap<>();
    }

    class ImagePagerAdaper extends PagerAdapter {

        private Activity context;

        public ImagePagerAdaper(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            ChatImage chat = fileList.get(position);
            final View view = LayoutInflater.from(context).inflate(R.layout.activity_image_display, null);
            PhotoView mImageView = (PhotoView) view.findViewById(R.id.photoview);
            Uri imageUri = null;

            File imageFile = null;
            if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE) {
                imageFile = new File(chat.getmAudioPath());
            } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO) {
                imageFile = new File(mApp.getChatCacheDir().getPath(), chat.getmAudioPath().substring(chat.getmAudioPath().lastIndexOf("/")));
            } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO + VIEW_TYPE_INTERVAL) {
                imageFile = new File(chat.getmAudioPath());
            }
            if (imageFile != null && imageFile.exists()) {
                files.put(position,imageFile);
                imageUri = ImageUtil.getUriFromFile(context, imageFile);
                Glide.with(context).load(imageFile).into(mImageView);

                mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float v, float v1) {
                        context.finish();
                    }

                    @Override
                    public void onOutsidePhotoTap() {
                        context.finish();
                    }
                });
                mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        menuWindow = new ChatPopupWindow("", ImagesDisplayActivity.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        /**
                                         * 分享
                                         */
                                        menuWindow.dismiss();
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("image/*");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra(Intent.EXTRA_STREAM, ImageUtil.getUriFromFile(context, files.get(position)));
                                        ImagesDisplayActivity.this.startActivity(intent);
                                    }
                                }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * 保存到相册
                                 */
                                menuWindow.dismiss();
                                if (mMap.get(position) == null || !mMap.get(position)){
                                    realSaveImage(files.get(position), position);
                                }else {
                                    Toast.makeText(context,context.getString(R.string.image_saved),Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        menuWindow.showAtLocation(view.findViewById(R.id.layout_image_display), Gravity.CENTER, 0, 0);
                        return false;
                    }
                });
            } else {
                ToastUtil.showMyToast(context, context.getString(R.string.image_delete), Toast.LENGTH_SHORT);
            }



            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        private void realSaveImage(File mediaFile , int postion) {
            boolean flag = ImageUtil.saveImageToGallery(context, mediaFile,
                    mediaFile.getName());
            if (flag) {
                mMap.put(postion, true);
                Toast.makeText(context, context.getString(R.string.image_saved),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_zoom_exit);
    }
}
