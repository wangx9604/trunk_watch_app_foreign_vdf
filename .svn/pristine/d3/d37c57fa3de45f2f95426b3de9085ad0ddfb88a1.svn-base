package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuiyufeng
 * @Description: GuideActivity 引导页切换步骤 第一步完成imageIdArray 数组有几个页面就写几个lout，第二部直接更改图片文字 ok
 * @date 2018/5/7 10:37
 */
public class GuideActivity extends Activity implements View.OnClickListener{
    /**
     *type 1 米兔
     * type 2 小寻
     */
    private int type;
    public static final  int resultCode=2;
    private ViewPager viewPager;
    //图片资源的数组
    private int[] imageIdArray = new int[]{R.layout.view_guide1};
    private List<View> viewList;//图片资源的集合 viewpage 的item
    //实例化原点View
    private ViewGroup vg;//放置圆点
    private ImageView imageView;
    private ImageView[] ivPointArray; //通过viewpage的item获取原点个数add到vg
    private Button ib_start;
    private ImibabyApp myApp;
    private Button btn_jump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        myApp = (ImibabyApp) getApplication();
        ib_start = findViewById(R.id.guide_ib_start);
        btn_jump= findViewById(R.id.btn_jump);
        btn_jump.setOnClickListener(this);
        ib_start.setOnClickListener(this);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            type= bundle.getInt("type");
        }
        //加载底部圆点
        initPoint();
        //加载ViewPager
        initViewPager();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_jump:
            case R.id.guide_ib_start:
                setResult(resultCode);
                finish();
                break;
        }
    }

    /**
     * 加载底部圆点
     */
    private void initPoint() {
        //这里实例化LinearLayout
        vg = findViewById(R.id.guide_ll_point);
        //根据ViewPager的item数量实例化数组
        if(imageIdArray.length ==1){
        }else{
            ivPointArray = new ImageView[imageIdArray.length];
            //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //mParams.setMargins(DensityUtil.px2dip(GuideActivity.this, (float) 12.7), 0, DensityUtil.px2dip(GuideActivity.this, (float) 12.7), 0);//设置小圆点左右之间的间隔
            for (int i = 0; i < imageIdArray.length; i++) {
                ImageView iv_point = new ImageView(this);
                iv_point.setLayoutParams(mParams);
                iv_point.setPadding(DensityUtil.dp2px(GuideActivity.this, 12), 0, DensityUtil.dp2px(GuideActivity.this, 12), 0);
                iv_point.setImageResource(R.drawable.dot_selector);
                if (i == 0) {
                    iv_point.setSelected(true);//默认启动时，选中第一个小圆点
                } else {
                    iv_point.setSelected(false);
                }
                ivPointArray[i] = iv_point;//得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。
                vg.addView(ivPointArray[i]);
            }
        }
    }

    /**
     * 加载图片ViewPager
     */
    private void initViewPager() {
        viewPager = findViewById(R.id.guide_vp);
        viewList = new ArrayList<>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0; i < len; i++) {
            /*imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            Glide.with(GuideActivity.this).load(imageIdArray[i]).into(imageView);*/
            LayoutInflater inflater = LayoutInflater.from(GuideActivity.this);
            View view = inflater.inflate(imageIdArray[i], null);

            TextView title = view.findViewById(R.id.tv_guide1_title);
            TextView introduce = view.findViewById(R.id.tv_guide1_introduce);
            ImageView iv_content = view.findViewById(R.id.iv_guide1_content);

            if(i==0){
                if(type==1){ //米兔
                }else if(type==2){//小寻
                    title.setText(Html.fromHtml(getResources().getString(R.string.guide_videocall_title)));
                    introduce.setText(Html.fromHtml(getResources().getString(R.string.guide_videocall_content)));
                    Glide.with(GuideActivity.this).load(R.drawable.iv_guide2).into(iv_content);
                }
            }else if(i==1){
                if(type == 1){
                }else if(type == 2){
                    title.setText(Html.fromHtml(getResources().getString(R.string.guide_videocall_title)));
                    introduce.setText(Html.fromHtml(getResources().getString(R.string.guide_videocall_content)));
                    Glide.with(GuideActivity.this).load(R.drawable.iv_guide2).into(iv_content);
                }
            }
            viewList.add(view);
        }
        viewPager.setAdapter(new GuidePageAdapter(viewList));

        MyOnPageChangeListener listener= new MyOnPageChangeListener();
        viewPager.addOnPageChangeListener(listener);
        listener.onPageSelected(0);

    }

    public class GuidePageAdapter extends PagerAdapter {
        private List<View> viewList;

        public GuidePageAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            if (viewList != null) {
                return viewList.size();
            }
            return 0;
        }

        /**
         * 判断对象是否生成界面
         *
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        /**
         * 滑动后的监听
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            if(ivPointArray!=null){
                int length = ivPointArray.length;
                for (int i = 0; i < length; i++) {
                    ivPointArray[position].setSelected(true);
                    if (position != i) {
                        ivPointArray[i].setSelected(false);
                    }
                }
            }
            //判断是否是最后一页，若是则显示按钮
           /* if (position == imageIdArray.length - 1) {
                ib_start.setVisibility(View.VISIBLE);
                btn_jump.setVisibility(View.INVISIBLE);
            } else {
                ib_start.setVisibility(View.INVISIBLE);
                btn_jump.setVisibility(View.VISIBLE);
            }*/

        }
    }

}
