package com.xiaoxun.xun.securityarea.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.securityarea.bean.EfencesAreaBean;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CommonUtil;
import com.xiaoxun.xun.utils.CustomFileUtils;

import java.io.FileInputStream;
import java.util.List;

//图片
public class AreaMapAdapter extends BaseQuickAdapter<EfencesAreaBean, BaseViewHolder> {
    String eid;
    Context context;
    ImibabyApp myApp;

    public AreaMapAdapter(List<EfencesAreaBean> data, Context context, String eid, ImibabyApp myApp) {
        super(R.layout.adapter_item_map_pic_area, data);
        this.eid = eid;
        this.context = context;
        this.myApp = myApp;
    }

    @Override
    protected void convert(BaseViewHolder helper, EfencesAreaBean item) {
        ImageView ivMap = helper.getView(R.id.iv_map);
        //获取图片
        //从本地获取
        String fileName = ImibabyApp.getIconCacheDir() + "/" + eid + item.getEfid() + ".jpg";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            fileInputStream.close();
            byte[] tmp = AESUtil.getInstance().decrypt(buffer);
            Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            ivMap.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //从网络获取
        String key = "EP/" + eid + "/EFENCE/" + item.getEfid() + ".jpg";
        @SuppressLint("StaticFieldLeak")
        AsyncTask<byte[], byte[], byte[]> task = new AsyncTask<byte[], byte[], byte[]>() {
            @Override
            protected byte[] doInBackground(byte[]... strings) {
                try {
                    return CustomFileUtils.getInstance(myApp).downloadUrl(key);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                if (!CommonUtil.isDestroy((Activity) context)) {
//                    Glide.with(context).load(bytes).into(ivMap);
                    Bitmap bitmap;
                    if (bytes == null) {
                        ivMap.setImageResource(R.drawable.area_error_default);
                    } else {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivMap.setImageBitmap(bitmap);
                    }
                }
            }
        };
        task.execute();
    }

}
