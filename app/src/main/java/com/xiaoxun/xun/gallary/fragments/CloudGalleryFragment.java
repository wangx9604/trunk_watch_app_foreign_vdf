package com.xiaoxun.xun.gallary.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.OVERSEAURL;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.WatchManagerActivity;
import com.xiaoxun.xun.gallary.ImageVedioFiles;
import com.xiaoxun.xun.gallary.adapter.CloudGridAdapter;
import com.xiaoxun.xun.gallary.dataBase.DataBaseHelper;
import com.xiaoxun.xun.gallary.dataStruct.GalleryData;
import com.xiaoxun.xun.gallary.downloadUtils.DownloadListener;
import com.xiaoxun.xun.gallary.downloadUtils.ListDownLoader;
import com.xiaoxun.xun.gallary.dragSelect.DragSelectTouchListener;
import com.xiaoxun.xun.gallary.dragSelect.DragSelectionProcessor;
import com.xiaoxun.xun.gallary.interfaces.DataChangeListener;
import com.xiaoxun.xun.gallary.interfaces.itemOnClickListeners;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.ToolUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import static com.xiaoxun.xun.gallary.ImageVedioFiles.ImagevideoFiles;
import static com.xiaoxun.xun.utils.ToolUtils.getNameFromKey;
import static com.xiaoxun.xun.utils.ToolUtils.getTimeFromName;
import static com.xiaoxun.xun.utils.ToolUtils.getTimeFromNextStr;
import static com.xiaoxun.xun.utils.ToolUtils.imgOrVideo;
import static com.xiaoxun.xun.utils.ToolUtils.isNetworkAvailable;
import static com.xiaoxun.xun.utils.ToolUtils.isPreviewOrSrc;

/**
 * Created by xilvkang on 2017/9/2.
 */

public class CloudGalleryFragment extends BaseGalleryFragment implements DataChangeListener {
    private RecyclerView listView;
    private ProgressBar progressbar;
    private RelativeLayout nodata;
    private TextView no_data_txt;
    private DragSelectTouchListener mDragSelectTouchListener;
    private DragSelectionProcessor mDragSelectionProcessor;

    private DataBaseHelper db;
    private String nextkey = "";

    public static final int DO_REFRESHING = 0;
    public static final int DO_LOADING = 1;
    public boolean load_finish = false;

    private Comparator<GalleryData> comparator = new Comparator<GalleryData>() {
        public int compare(GalleryData s1, GalleryData s2) {
            if (s1.getTime() < s2.getTime()) {
                return -1;
            } else if (s1.getTime() > s2.getTime()) {
                return 1;
            } else {
                return 0;
            }
        }
    };
    private BroadcastReceiver mreceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_GET_OFFLINE_CHAT_MSG)) {
                if (ImagevideoFiles.size() == 0) {
                    downloadImageList("", DO_LOADING);
                } else {
                    downloadImageList("", DO_REFRESHING);
                }
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    void initdata() {
        if (!isNetworkAvailable(getActivity())) {
            //loadLocalData();
        } else {
            if (mApp.getToken() != null) {
                mapGet();
                downloadImageList("", DO_REFRESHING);
            } else {
                Calendar cal = Calendar.getInstance();
                long end = cal.getTimeInMillis();
                ArrayList<GalleryData> list = getDataFromLocalSql("",
                        String.valueOf(end));
                if (list != null) {
                    ImagevideoFiles.addAll(list);
                }
            }
        }
    }

    @Override
    void initviews(View v) {
        nodata = v.findViewById(R.id.no_data);
        no_data_txt = v.findViewById(R.id.no_data_string);
        if(ImagevideoFiles.size() == 0 && mApp.getCurUser().isMeAdminByWatch(mApp.getCurUser().getFocusWatch())
                && !mApp.getCloudPhotosOnoff(mApp.getCurUser().getFocusWatch().getEid())){
            setTextSpan();
        }
        if (ImageVedioFiles.ImagevideoFiles.size() > 0) {
            nodata.setVisibility(View.GONE);
        }
        progressbar = v.findViewById(R.id.progressbar);
        listView = v.findViewById(R.id.list);
        adapterGrid = new CloudGridAdapter(getActivity(), ImagevideoFiles, mApp, db, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapterGrid.getItemViewType(position) == 0) {
                    return 2;
                }
                return 1;
            }
        });
        listView.setLayoutManager(gridLayoutManager);
        listView.setAdapter(adapterGrid);
        adapterGrid.setitemOnClickListeners(new itemOnClickListeners() {
            @Override
            public boolean onLongClick(View v, int position) {
                if (ImageVedioFiles.GALLERY_STATUS == 0) {
                    if (mApp.getCurUser().isMeAdminByWatch(mApp.getCurUser().getFocusWatch()))
                    {
                        statuChange.OnStatuChanged(ImageVedioFiles.GALLERY_STATUS);
                    }
                } else {
                    mDragSelectTouchListener.startDragSelection(position);
                }
                return true;
            }
        });
        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public Set<Integer> getSelection() {
                return null;
            }

            @Override
            public boolean isSelected(int index) {
                return adapterGrid.isSelected(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                adapterGrid.selectRange(start, end, isSelected);
            }
        }).withMode(DragSelectionProcessor.Mode.Simple);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        loadLocalData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mreceive);
    }

    @Override
    void initConfig() {
        db = new DataBaseHelper(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_GET_OFFLINE_CHAT_MSG);
        Log.e("xxxx", "registerReceiver com.robot.logfinish");
        getActivity().registerReceiver(mreceive, filter);
        ImageVedioFiles.initEidDir(getActivity(), mApp.getCurUser().getFocusWatch().getEid());
    }

    @Override
    void swipLayoutRefresh() {
        if (ImageVedioFiles.GALLERY_REFRESH_STATUS != 1) {
            load_finish = false;
            downloadImageList("", DO_REFRESHING);
        } else {
            swipeRefreshLayout.finishRefresh();
        }
    }

    @Override
    void swipLayoutLoading() {
        if (!load_finish && ImageVedioFiles.GALLERY_REFRESH_STATUS != 1) {
            if (nextkey.equals("")) {
                if (ImagevideoFiles.size() > 0) {
                    String end = String.valueOf(ImagevideoFiles.get(ImagevideoFiles.size() - 1).getTime() - 1);
                    downloadImageList(end, DO_LOADING);
                } else {
                    downloadImageList("", DO_LOADING);
                }
            } else {
                String time = ToolUtils.getNameFromKey(nextkey);
                downloadImageList(time, DO_LOADING);
            }
        } else {
            swipeRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void downloadImageList(final String time, final int type) {

        ImageVedioFiles.GALLERY_REFRESH_STATUS = 1;
        String post = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddmmss");
        String nowTime = format.format(cal.getTime());
        JSONObject obj = new JSONObject();
        post = "EP/" + mApp.getCurUser().getFocusWatch().getEid() + "/ALBUM/PREVIEW/" + time;
        obj.put("prefix", post);
        obj.put("sid", mApp.getToken());
        ListDownLoader downLoader = new ListDownLoader(new DownloadListener() {
            @Override
            public void onStartDownload() {

            }

            @Override
            public void onFinished(String result) {
                if (result == null || result.equals("") || result.length() < 4) {
                    Log.e("xxxx", "no result.");
                } else {
                    if (db != null) {
                        //result = result.replaceAll("\\s*", "");
                        String key = mApp.getNetService().getAESKEY();
                        String decRes = ToolUtils.decryptUrl(result, key);
                        prepareImageVideoList(decRes, time, type);
                        if (ImagevideoFiles.size() > 0) {
                            nodata.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("xxxx", "db closed.");
                    }
                }
                if (type == DO_REFRESHING) {
                    swipeRefreshLayout.finishRefresh();
                } else {
                    swipeRefreshLayout.finishLoadmore();
                }
                ImageVedioFiles.GALLERY_REFRESH_STATUS = 0;
            }

            @Override
            public void onError(String cause) {
                if (type == DO_REFRESHING) {
                    swipeRefreshLayout.finishRefresh();
                } else {
                    swipeRefreshLayout.finishLoadmore();
                }
            }
        });
        String key = mApp.getNetService().getAESKEY();
        String ret = BASE64Encoder.encode(AESUtil.encryptAESCBC(obj.toJSONString(), key, key)) + mApp.getToken();
        downLoader.HttpsDownloadList(XunKidsDomain.getInstance(getActivity()).getXunKidsFilesDomain(OVERSEAURL.FDSFILE_FILE_LIST_URL), ret);

    }

    @Override
    public void DataChanged(int result) {
        if (result == DataChangeListener.RESULT_OK) {
            statuChange.OnStatuChanged(DataChangeListener.RESULT_OK);
            adapterGrid.notifyDataSetChanged();
        } else {
            Log.e("xxxx", String.valueOf(result));
        }
        progressbar.setVisibility(View.INVISIBLE);
    }

    private void mapGet() {
        String teid = mApp.getCurUser().getFocusWatch().getEid();
        if (mApp.getNetService() != null) {
            mApp.getNetService().sendMapGetMsg(teid, CloudBridgeUtil.ONOFF, new MsgCallback() {
                @Override
                public void doCallBack(JSONObject requestMsg, JSONObject responseMsg) {
                    LogUtil.e("xxxx" + responseMsg.toJSONString());
                }
            });
        }
    }

    private ArrayList<GalleryData> getDataFromLocalSql(String begin, String end) {
        ArrayList<GalleryData> array;
        if (begin.equals("")) {
            array = db.getImageVideoBySize(mApp.getCurUser().getFocusWatch().getEid(), end, 50);
        } else {
            array = db.getImageVideo(mApp.getCurUser().getFocusWatch().getEid(), begin, end);
        }
        return array;
    }

    public void prepareImageVideoList(String result, String end, int type) {
        Log.e("xxxx", result);
        mApp.sdcardLog("xxx : " + result + " xxx");
        JSONObject obj = (JSONObject) JSONValue.parse(result);
        int code = (Integer) obj.get("code");
        if (code < 0) {
            ToastUtil.show(getActivity(), getResources().getString(R.string.gallery_load_failed));
            return;
        }
        JSONArray array = (JSONArray) obj.get("files");
        if (array.size() == 0) {
            ToastUtil.show(getActivity(), getResources().getString(R.string.gallery_no_more_data));
            if (type == DO_LOADING) {
                load_finish = true;
            }
            return;
        }

        getCapacityString(obj);

        ArrayList<GalleryData> netData = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = (JSONObject) array.get(i);
            String key = (String) item.get("key");
            String name = getNameFromKey(key);
            if (name.contains(".log")) {
                continue;
            }
            String time = getTimeFromName(name);

            GalleryData one = new GalleryData();
            one.setEid(mApp.getCurUser().getFocusWatch().getEid());
            one.setType(imgOrVideo(name));
            one.setName(name);
            one.setTime(Long.valueOf(time));
            if (isPreviewOrSrc(key) == 0) {
                one.setPreview_url((String) item.get("url"));
            } else {
                one.setSrc_url((String) item.get("url"));
            }
            netData.add(one);
        }
        String begin;
        if (obj.containsKey("NextKey")) {
            String next = (String) obj.get("NextKey");
            begin = getTimeFromNextStr(next);
            nextkey = next;
        } else {
            load_finish = true;
            if (netData.size() == 0) {
                Log.e("xxxx", "no img video files.");
                return;
            }
            begin = String.valueOf(netData.get(netData.size() - 1).getTime());
        }
        if (end.equals("")) {
            Calendar cal = Calendar.getInstance();
            long endtime = getMilesecondFromTime(cal.getTime());
            //end = TimeUtil.getReversedOrderTime(cal.getTimeInMillis());
            end = String.valueOf(endtime);
        }
        ArrayList<GalleryData> localData = db.getImageVideo(mApp.getCurUser().getFocusWatch().getEid(), begin, end);
        Collections.sort(localData, comparator);


        int ret = updateLocalData(netData, localData);
        if (type == DO_LOADING) {
            ImagevideoFiles.addAll(netData);//
            adapterGrid.addMoreItems(netData);
        } else {
            ImagevideoFiles.clear();
            ImagevideoFiles.addAll(db.getImageVideoBySize(mApp.getCurUser().getFocusWatch().getEid(), end, 50));
            adapterGrid.refreshItems(ImagevideoFiles);

        }
    }

    private int updateLocalData(ArrayList<GalleryData> netData, ArrayList<GalleryData> localData) {
        int ret = 0;
        if (localData == null || localData.size() == 0) {
            for (GalleryData item : netData) {
                db.addItem(item);
            }
            ret = 1;
            return ret;
        }
        for (int i = 0; i < netData.size(); i++) {
            boolean isNew = true;
            for (int j = 0; j < localData.size(); j++) {
                Date time = TimeUtil.getTimeStampGMTFromFmt(netData.get(i).getTime(),0);
                Date localDate = TimeUtil.getTimeStampGMTFromFmt(localData.get(j).getTime(),0);
                if (time.equals(localDate)) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                db.addItem(netData.get(i));
                netData.remove(i);
                i--;
            }
        }
        for (GalleryData item : localData) {
            boolean existInNet = false;
            for (int j = 0; j < netData.size(); j++) {
                if (item.getName().equals(netData.get(j).getName())) {
                    existInNet = true;
                    break;
                }
            }
            if (!existInNet) {
                File fp = new File(getActivity().getExternalFilesDir(null), ImageVedioFiles.PREVIEW_PATH + item.getEid() + "/" + item.getName());
                if (fp != null && fp.exists()) {
                    fp.delete();
                }
                File fp_src = new File(getActivity().getExternalFilesDir(null), ImageVedioFiles.SRC_PATH + item.getEid() + "/" + item.getName());
                if (fp_src != null && fp.exists()) {
                    fp_src.delete();
                }
                db.deleteItem(item);
                ret = -1;
            }
        }

        return ret;
    }

    private void getCapacityString(JSONObject result) {
        int asizestr = (Integer) result.get("asize");
        int usizestr = (Integer) result.get("usize");
        long asize = Long.valueOf(asizestr);
        long usize = Long.valueOf(usizestr);
        String asizef = ToolUtils.asizeConvert(asize);
        String usizef = ToolUtils.usizeConvert(usize);

        mApp.setValue("gallery_capacity" + mApp.getCurUser().getFocusWatch().getEid(), usizef + "/" + asizef);
        statuChange.OnStatuChanged(3);
    }

    private void loadLocalData() {
        if (ImagevideoFiles.size() == 0) {
            Calendar cal = Calendar.getInstance();
            long end = cal.getTimeInMillis();
            ArrayList<GalleryData> list = getDataFromLocalSql("",
                    String.valueOf(end));
            if (list != null) {
                ImagevideoFiles.clear();
                ImagevideoFiles.addAll(list);
                adapterGrid.refreshItems(ImagevideoFiles);
                nodata.setVisibility(View.GONE);
            }
        }
    }

    private long getMilesecondFromTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
        String str = format.format(date);
        long fd = Long.valueOf(str);
        return (999999999999999L - fd);
    }

    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    private void setTextSpan(){
        SpannableString spannableString = new SpannableString(getString(R.string.gallery_goto_setting));
        spannableString.setSpan(new Clickable(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(getContext(), WatchManagerActivity.class));
            }

        }), 13, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        no_data_txt.setText(spannableString);
    }
}
