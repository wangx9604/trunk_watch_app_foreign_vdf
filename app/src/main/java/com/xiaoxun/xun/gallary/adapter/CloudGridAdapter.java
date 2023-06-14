package com.xiaoxun.xun.gallary.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.OVERSEAURL;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.gallary.ImageVedioFiles;
import com.xiaoxun.xun.gallary.PreviewActivity;
import com.xiaoxun.xun.gallary.VideoPlayActivity;
import com.xiaoxun.xun.gallary.dataBase.DataBaseHelper;
import com.xiaoxun.xun.gallary.dataStruct.GalleryData;
import com.xiaoxun.xun.gallary.dataStruct.Section;
import com.xiaoxun.xun.gallary.downloadUtils.DownloadListener;
import com.xiaoxun.xun.gallary.downloadUtils.ListDownLoader;
import com.xiaoxun.xun.gallary.interfaces.DataChangeListener;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xilvkang on 2017/6/5.
 */

public class CloudGridAdapter extends BaseGridAdapter {

    public static final int PULLUP_NORMAL = 0;
    public static final int PULLUP_LOADING = 1;
    public static final int PULLDOWN_UPDATING = 2;

    private DataBaseHelper db;

    public CloudGridAdapter(Context context, ArrayList<GalleryData> src, ImibabyApp mapp, DataBaseHelper db, DataChangeListener listener) {
        super(context,src,mapp,listener);
        this.db = db;
    }

    void initData(ArrayList<GalleryData> datas) {
        data = new ArrayList<>();
        HashMap<String, ArrayList<GalleryData>> linkdata = new LinkedHashMap<>();
        for (int i = 0; i < datas.size(); i++) {
            boolean isNew = true;
            Calendar cal_time = Calendar.getInstance();
            cal_time.setTime(TimeUtil.getTimeStampGMTFromFmt(datas.get(i).getTime(),0));
            Date date = cal_time.getTime();
            for (Map.Entry<String, ArrayList<GalleryData>> entry : linkdata.entrySet()) {
                String key = entry.getKey();
                cal_time.setTime(TimeUtil.getTimeStampGMTFromFmt(Long.valueOf(key),0));
                Date dateT = cal_time.getTime();
                if (TimeUtil.isTheSameDay(date, dateT)) {
                    ArrayList<GalleryData> value = entry.getValue();
                    value.add(0,datas.get(i));
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(TimeUtil.getTimeStampGMTFromFmt(datas.get(i).getTime(),0));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.MILLISECOND, 59);
                String key = TimeUtil.getTimeStringFromDate(cal.getTime());//String.valueOf(cal.getTimeInMillis());
                ArrayList<GalleryData> value = new ArrayList<>();
                value.add(datas.get(i));
                linkdata.put(key, value);
            }
        }
        for (Map.Entry<String, ArrayList<GalleryData>> entry : linkdata.entrySet()) {
            String key = entry.getKey();
            Section item = new Section(0, key, null);
            data.add(item);
            ArrayList<GalleryData> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                Section img = new Section(1, String.valueOf(value.get(i).getTime()), value.get(i));
                data.add(img);
            }
        }
        Section item = new Section(0, ctxt.getString(R.string.gallery_grid_title), null);
        data.add(0,item);
    }

    public void deleteselectedItem() {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected) {
                if (data.get(i).type == 1) {
                    for (int j = 0; j < ImageVedioFiles.ImagevideoFiles.size(); j++) {
                        if (ImageVedioFiles.ImagevideoFiles.get(j).getName().equals(data.get(i).item.getName())) {
                            ImageVedioFiles.ImagevideoFiles.remove(j);
                            break;
                        }
                    }
                    File fp_pre = new File(data.get(i).item.getLocal_pre_path());
                    if (fp_pre.exists()) {
                        fp_pre.delete();
                    }
                    if (data.get(i).item.getLocal_src_path() != null) {
                        File fp_src = new File(data.get(i).item.getLocal_src_path());
                        if (fp_src.exists()) {
                            fp_src.delete();
                        }
                    }
                    db.deleteItem(data.get(i).item);
                }
                data.remove(i);
                i--;
            }
        }
        AllChooseItemCancel();
        notifyDataSetChanged();
        lis.DataChanged(DataChangeListener.RESULT_OK);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PhotoHolder holder;
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_title_ly, null, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_item_ly, null, false);
        }
        holder = new PhotoHolder(itemView, viewType, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (((PhotoHolder) holder).type == 0) {
            if(position == 0){
                ((PhotoHolder) holder).titleTV.setText(data.get(position).title);
                ((PhotoHolder) holder).leftLine.setVisibility(View.GONE);
                ((PhotoHolder) holder).rightLine.setVisibility(View.GONE);
            }else {
                String ret = TimeUtil.getOrderTimeFromTimeStamp(data.get(position).title);
                long mill = TimeUtil.getDataFromTimeStamp(ret).getTime();
                String time = TimeUtil.timeTextFromString(ctxt, mill);
                ((PhotoHolder) holder).titleTV.setText(time);
            }
        } else {
            final GalleryData item = data.get(position).item;
            if (item.getType() == ImageVedioFiles.FILE_TYPE_IMAGE) {
                ((PhotoHolder) holder).videoSign.setVisibility(View.INVISIBLE);
            } else if (item.getType() == ImageVedioFiles.FILE_TYPE_VEDIO) {
                ((PhotoHolder) holder).videoSign.setVisibility(View.VISIBLE);
            }
            if (item.getPreview_url() != null && !item.getPreview_url().equals("")) {
                File fp = ctxt.getExternalFilesDir(ImageVedioFiles.PREVIEW_PATH + item.getEid() + "/" + item.getName());
                if (fp.exists()) {
                    Picasso.with(ctxt).load(fp).
                            resizeDimen(R.dimen.imageview_width, R.dimen.imageview_height).
                            centerCrop().
                            placeholder(R.drawable.mc_album_default).
                            into(((PhotoHolder) holder).imgView);
                    item.setLocal_pre_path(fp.getAbsolutePath());
                    db.upgradeItem(item);
                } else {
                    ((PhotoHolder) holder).imgView.setTag(item.getName());

//                    Log.e("xxxx", "PicassoLoadedUrl");
//                    Picasso.with(ctxt).load(item.getPreview_url()).
//                            resizeDimen(R.dimen.imageview_width, R.dimen.imageview_height).
//                            centerCrop().
//                            placeholder(R.drawable.mc_album_default).
//                            into(((PhotoHolder) holder));
                    Glide.with(ctxt).load(item.getPreview_url()).downloadOnly(((PhotoHolder) holder));

                }
            }
            if (ImageVedioFiles.GALLERY_STATUS == 1) {
                ((PhotoHolder) holder).checkBox.setVisibility(View.VISIBLE);
                ((PhotoHolder) holder).checkBox.setChecked(data.get(position).isSelected);
            } else {
                ((PhotoHolder) holder).checkBox.setVisibility(View.INVISIBLE);
            }
            ((PhotoHolder) holder).startAnim(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    public void requestDeleteFiles() {
        final ProgressDialog pd = new ProgressDialog(ctxt);

        String eid = mApp.getCurUser().getFocusWatch().getEid();
        String src_path = "EP/" + eid + "/ALBUM/SOURCE/";
        String pre_path = "EP/" + eid + "/ALBUM/PREVIEW/";
        if(data.size() <= 0){
            LogUtil.e("nothing has beem selected.");
            lis.DataChanged(DataChangeListener.RESULT_DELETE_ERROR);
            return;
        }
        JSONArray array = new JSONArray();
        for (Section t : data) {
            if (t.type == 1 && t.isSelected) {
                if(!t.item.getName().contains("xxx")) {
                    array.add(src_path + t.item.getName());
                }
                array.add(pre_path + t.item.getName());
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("keys", array);
        obj.put("sid", mApp.getToken());
        ListDownLoader downLoader = new ListDownLoader(new DownloadListener() {
            @Override
            public void onStartDownload() {
                pd.dismiss();
            }

            @Override
            public void onFinished(String result) {
                if (result == null || result.equals("")) {
                    lis.DataChanged(DataChangeListener.RESULT_EMPTY);
                    AllChooseItemCancel();
                    notifyDataSetChanged();
                    pd.dismiss();
                    return;
                }
                String key = mApp.getNetService().getAESKEY();
                String decRes = ToolUtils.decryptUrl(result,key);
                if(!ToolUtils.isJsonData(decRes)){
                    LogUtil.e("xxxx not Jason.");
                    lis.DataChanged(DataChangeListener.RESULT_EMPTY);
                    AllChooseItemCancel();
                    notifyDataSetChanged();
                }else {
                    JSONObject obj = (JSONObject) JSONValue.parse(decRes);
                    int code = (Integer) obj.get("code");
                    if (code == 0) {
                        deleteselectedItem();
                    } else {
                        Log.e("xxxx", "delete error.reason:" + result);
                        ImageVedioFiles.GALLERY_STATUS = 0;
                        AllChooseItemCancel();
                        notifyDataSetChanged();
                        lis.DataChanged(DataChangeListener.RESULT_DELETE_ERROR);
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onError(String cause) {
                lis.DataChanged(DataChangeListener.RESULT_DELETE_ERROR);
                pd.dismiss();
            }
        });
        String key = mApp.getNetService().getAESKEY();
        String ret = BASE64Encoder.encode(AESUtil.encryptAESCBC(obj.toJSONString(),key,key)) + mApp.getToken();
        downLoader.HttpsDownloadList(XunKidsDomain.getInstance(ctxt).getXunKidsFilesDomain(OVERSEAURL.FDSFILE_DELETE_URL), ret);
        ProgressDialog.show(ctxt,ctxt.getString(R.string.please_waiting),ctxt.getString(R.string.removing));
    }

    class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, Target<File> {
        int type;
        TextView titleTV;
        View leftLine;
        View rightLine;
        ImageView imgView;
        ImageView videoSign;
        CheckBox checkBox;
        private ViewPropertyAnimator mAnimator;
        CloudGridAdapter adapter;

        public PhotoHolder(View itemView) {
            super(itemView);
        }

        public PhotoHolder(View itemView, int type, CloudGridAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.type = type;
            if (type == 0) {
                titleTV = itemView.findViewById(R.id.title);
                leftLine = itemView.findViewById(R.id.leftLine);
                rightLine = itemView.findViewById(R.id.rightLine);
            } else {
                imgView = itemView.findViewById(R.id.img_item);
                checkBox = itemView.findViewById(R.id.checkbox);
                checkBox.setClickable(false);
                videoSign = itemView.findViewById(R.id.video_sign);
            }
            this.adapter = adapter;
        }

        @Override
        public void onClick(View v) {
            if (ImageVedioFiles.GALLERY_STATUS == 1 && type == 1) {
                checkBox.setChecked(!checkBox.isChecked());
                changeDatasSelect(getAdapterPosition(), checkBox.isChecked());
                notifyItemChanged(getAdapterPosition());
            } else if (ImageVedioFiles.GALLERY_STATUS == 0 && type == 1) {
                GalleryData item = data.get(getAdapterPosition()).item;
                if (item.getLocal_pre_path() == null || item.getLocal_pre_path().equals("")) {
                    Log.e("xxxx", "preview path null.");
                    return;
                }
                if (item.getType() == ImageVedioFiles.FILE_TYPE_IMAGE) {
                    //preview activity
                    Intent it = new Intent(ctxt, PreviewActivity.class);
                    it.putExtra("name", item.getName());
                    ctxt.startActivity(it);
                } else {
                    //vedio play activity
                    Intent it = new Intent(ctxt, VideoPlayActivity.class);
                    it.putExtra("name", item.getName());
                    ctxt.startActivity(it);
                }
            }
        }

        /**
         * 选中动画
         *
         * @param photoItem
         */
        public void startAnim(Section photoItem) {
            if (mAnimator == null) {
                mAnimator = ViewPropertyAnimator.animate(imgView);
            }
            if (photoItem.isSelected) {
                mAnimator.scaleX(0.8f).scaleY(0.8f).setDuration(200);
            } else {
                mAnimator.scaleX(1.0f).scaleY(1.0f).setDuration(200);
            }
            mAnimator.start();
        }

        @Override
        public boolean onLongClick(View view) {
            if (itemListener != null) {
                Log.e("xxxx", "onLongClick.");
                return itemListener.onLongClick(view, getAdapterPosition());
            }
            return false;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {

        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {

        }


        @Override
        public void onResourceReady(File resource, Transition<? super File> glideAnimation) {
            if(resource.exists() && getAdapterPosition() >= 0){
                String key = mApp.getCurUser().getFocusWatch().getEid().substring(0,16);
                Bitmap bitmap = ToolUtils.decryptImgFile(resource,key);
                GalleryData item = data.get(getAdapterPosition()).item;
                File fp = ctxt.getExternalFilesDir(ImageVedioFiles.PREVIEW_PATH + data.get(getAdapterPosition()).item.getEid());
                String path = ImageVedioFiles.saveBitmapToLocal(bitmap,
                        fp.getAbsolutePath(),
                        item.getName());
                item.setLocal_pre_path(path);
                if (db != null) {
                    db.upgradeItem(item);
                }
                imgView.setImageBitmap(bitmap);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(getAdapterPosition());
                    }
                },200);
            }
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {

        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        @Override
        public void removeCallback(@NonNull SizeReadyCallback cb) {

        }

        @Override
        public void setRequest(Request request) {

        }

        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }
    }


}
