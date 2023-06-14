package com.xiaoxun.xun.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BitmapUtilities;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.HttpTextView;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RepairTrackDetailActivity extends NormalActivity {

    private TextView tv_subject;
    private TextView tv_status;
    private TextView tv_user_name;
    private TextView tv_staff_name;
    private TextView tv_update_time;
    private TextView tv_repair_id;
    private EditText et_replay;
    private TextView tv_track_count;
    private ListView lv_replay_detail;
    private Button bt_reply_content;
    private TextView tv_repair_photo;
    private TextView tv_repair_photo_desc;
    private ImageView iv_repair_clear_list;

    private ReplyAdapter myAdapter;
    private List<Map<String, Object>> mData;

    private final String getTrackDetailUrl_1 = "https://xxkj.ewei.com/api/v1/tickets/";
    private final String getTrackDetailUrl_2 = ".json";
    private final String getTrackDetailParms = "?include_fields=updatedAt,subject,status,user,engineer";
    private final String getReplyListUrl_1 = "https://xxkj.ewei.com/api/v1/tickets/";
    private final String getReplyListUrl_2 = "/ticket_comments/public.json";
    private final String getReplyListParms = "?_count=100&include_fields=createdAt,content,user,attachments";
    private final String putReplyContentUrl_1 = "https://xxkj.ewei.com/api/v1/tickets/";
    private final String putReplyContentUrl_2 = "/ticket_comments.json";
    private final String getPhotoUrl_1 = "https://xxkj.ewei.com/api/v1/attachments/";
    private final String getPhotoUrl_2 = ".json";
    private final String uploadPhotoUrl = "https://xxkj.ewei.com/api/v1/attachments.json";

    private final String appKey = "MTA5NA==";
    private final String appSectet = "3bddgfd215525f24901dff8f1gfdf925";

    private String trackId;
    private String trackNo;
    private String userId;
    private String trackStatus;
    private String timeStamp;
    private ArrayList<String> photoUploadPath = null;
    private String curDealPicName;
    private LoadingDialog mLoadingDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_track_detail);
        mData = new ArrayList<>();
        Date date = new Date();
        timeStamp = String.valueOf(date.getTime());

        mLoadingDlg = new LoadingDialog(this, R.style.Theme_DataSheet, null);
        photoUploadPath = new ArrayList<>();
        trackId = getIntent().getStringExtra("id");
        trackNo = getIntent().getStringExtra("no");

        initView();
        queryDateByImei();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }

    private void queryDateByImei(){
        if (mLoadingDlg != null && !mLoadingDlg.isShowing()) {
            mLoadingDlg.enableCancel(false);
            mLoadingDlg.changeStatus(1, getString(R.string.repair_get_data));
            mLoadingDlg.show();
        }
        syncHttpGetTrack(appKey,timeStamp);
    }

    private void initView(){
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.repair_track_detail);

        View header = getLayoutInflater().inflate(R.layout.repair_track_detail_head, null);
        initHeadView(header);
        lv_replay_detail = findViewById(R.id.lv_show_reply);

        LayoutInflater inflater = getLayoutInflater();
        myAdapter = new ReplyAdapter(this,inflater,mData);
        lv_replay_detail.setAdapter(myAdapter);
        lv_replay_detail.addHeaderView(header);
    }

    private void initHeadView(View view){
        tv_subject = view.findViewById(R.id.tv_subject);
        tv_status = view.findViewById(R.id.tv_track_status);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_staff_name = view.findViewById(R.id.tv_staff_name);
        tv_update_time = view.findViewById(R.id.tv_update_time);
        tv_repair_id = view.findViewById(R.id.tv_repair_id);
        et_replay = view.findViewById(R.id.repair_reply_content);
        tv_track_count = view.findViewById(R.id.tv_repair_replay_count);
        bt_reply_content = view.findViewById(R.id.bt_reply);
        tv_repair_photo = view.findViewById(R.id.tv_repair_photo);
        tv_repair_photo_desc = view.findViewById(R.id.tv_repair_photo_desc);
        iv_repair_clear_list = view.findViewById(R.id.iv_clear_photo_list);

        tv_repair_id.setText(getString(R.string.repair_id_title,trackNo));
        bt_reply_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_status.getText().equals(getString(R.string.repair_state_solved))){
                    ToastUtil.show(RepairTrackDetailActivity.this,getString(R.string.repair_no_reply));
                    return;
                }
                if(et_replay.getText().toString().equals("") && photoUploadPath.size() == 0){
                    ToastUtil.show(RepairTrackDetailActivity.this,getString(R.string.repair_reply_error));
                    return;
                }
                if (mLoadingDlg != null && !mLoadingDlg.isShowing()) {
                    mLoadingDlg.enableCancel(false);
                    mLoadingDlg.changeStatus(1, getString(R.string.repair_get_data));
                    mLoadingDlg.show();
                }
                syncHttpPutReply(photoUploadPath,appKey, timeStamp);
            }
        });
        tv_repair_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_status.getText().equals(getString(R.string.repair_state_solved))){
                    ToastUtil.show(RepairTrackDetailActivity.this,getString(R.string.repair_no_reply));
                    return;
                }
                getImageFilePath();
            }
        });

        iv_repair_clear_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhotoListView();
            }
        });
    }

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的
    private final int IMAGE_CODE_1 = 1;

    private void getImageFilePath(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, IMAGE_CODE);
    }

    @Override
    protected void onResume() {
        myApp.isCurrentRunningForeground = true;
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE){
            try {
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                Uri originalUri = data.getData(); // 获得图片的uri
                 String[] proj = { MediaStore.Images.Media.DATA };
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                 Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                // 按我个人理解 这个是获得用户选择的图片的索引值
                 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                 cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                File file = new File(path);
                String fileList;
                if(tv_repair_photo_desc.getText() == null || tv_repair_photo_desc.getText().equals("")){
                    fileList= file.getName();
                }else{
                    fileList= tv_repair_photo_desc.getText().toString()+"\n"+file.getName();
                }
                curDealPicName = file.getName();
                tv_repair_photo_desc.setText(fileList);
                iv_repair_clear_list.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == IMAGE_CODE_1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPicToView(data);
                    if(!curDealPicName.equals("")) {
                        if (mLoadingDlg != null && !mLoadingDlg.isShowing()) {
                            mLoadingDlg.enableCancel(false);
                            mLoadingDlg.changeStatus(1, getString(R.string.repair_upload_data));
                            mLoadingDlg.show();
                        }
                        String uploadName = curDealPicName;
                        String filePath = ImibabyApp.getIconCacheDir() + "/" + uploadName + ".png";
                        syncHttpUploadFile(uploadPhotoUrl, filePath, appKey, timeStamp);
                        curDealPicName = "";
                    }

                }
            }, 100);
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {

        Bitmap photo = null;
        try {
            photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(cropTemp));
            if (photo.getWidth() > 720 || photo.getHeight() > 720) {
                photo = BitmapUtilities.getBitmapThumbnail(cropTemp.getPath(), 720, 720);
            }
            FileOutputStream fos = new FileOutputStream(cropTemp);
            photo.compress(Bitmap.CompressFormat.JPEG, 70, fos);
//            if (cropTemp.length() > 45 * 1024)
//                photo.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(cropTemp));
//            if (cropTemp.length() > 45 * 1024)
//                photo.compress(Bitmap.CompressFormat.JPEG, 10, new FileOutputStream(cropTemp));
            //创建文件输出流
            OutputStream os;
            byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);

            File destFile = new File(ImibabyApp.getIconCacheDir(), curDealPicName + ".png");
            cropTemp.renameTo(destFile);
            os = new FileOutputStream(destFile);
            os.write(bitmapArray);
            os.flush();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
            updatePhotoListView();
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
            updatePhotoListView();
        }
    }

    private void updatePhotoListView(){
        tv_repair_photo_desc.setText("");
        iv_repair_clear_list.setVisibility(View.GONE);
        photoUploadPath.clear();
        curDealPicName = "";
    }

    private File cropTemp = null;
    public void startPhotoZoom(Uri uri) {

        File temp = new File(ImibabyApp.getIconCacheDir(), "tempcrop" + ".jpg");
        if (temp.exists()) {
            temp.delete();
        }
        cropTemp = temp;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropTemp));
        intent.putExtra("return-data", false);//设置为不返回数据
        startActivityForResult(intent, IMAGE_CODE_1);
    }

    public class  ReplyAdapter extends BaseAdapter {
        private List<Map<String, Object>> mData;
        private LayoutInflater mInflater = null;
        private Context mContext;

        public ReplyAdapter(Activity mContext, LayoutInflater mInflater,List<Map<String, Object>> mData){
            this.mData = mData;
            this.mInflater = mInflater;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.repair_reply_item, null);

                viewHolder.tv_repair_name = convertView.findViewById(R.id.iv_repair_name);
                viewHolder.tv_update_time = convertView.findViewById(R.id.iv_repair_update);
                viewHolder.tv_repair_subject = convertView.findViewById(R.id.iv_repair_subject);
                viewHolder.line_view = convertView.findViewById(R.id.line_view);
                viewHolder.iv_photo = convertView.findViewById(R.id.iv_repair_photo);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Map<String,Object> map = mData.get(position);
            String isHave = (String)map.get("isPhoto");
            if(isHave.equals("1")){
                viewHolder.iv_photo.setVisibility(View.VISIBLE);
                viewHolder.tv_repair_name.setVisibility(View.GONE);
                viewHolder.tv_update_time.setVisibility(View.GONE);
                viewHolder.tv_repair_subject.setVisibility(View.GONE);
                viewHolder.line_view.setVisibility(View.GONE);

                String photoUrl  = (String)map.get("contentUrl");
                String filePath = ImibabyApp.getIconCacheDir()+"/"+photoUrl+".png";
                File file = new File(filePath);
                if(file.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    viewHolder.iv_photo.setImageBitmap(bitmap);
                }
            }else{
                viewHolder.iv_photo.setVisibility(View.GONE);
                viewHolder.tv_repair_name.setVisibility(View.VISIBLE);
                viewHolder.tv_update_time.setVisibility(View.VISIBLE);
                viewHolder.tv_repair_subject.setVisibility(View.VISIBLE);
                viewHolder.line_view.setVisibility(View.VISIBLE);

                String repair_name = (String)map.get("name");
                String repair_update = (String)map.get("createdAt");
                String repair_subject = (String)map.get("content");
                viewHolder.tv_repair_name.setText(repair_name);
                viewHolder.tv_update_time.setText(repair_update);
                if(repair_subject.equals("")){
                    viewHolder.tv_repair_subject.setVisibility(View.GONE);
                }else {
                    viewHolder.tv_repair_subject.setUrlText(repair_subject);
                }
            }

            return convertView;

        }

        class ViewHolder {
            public TextView tv_repair_name;
            public TextView tv_update_time;
            public HttpTextView tv_repair_subject;
            public View line_view;
            public ImageView iv_photo;

        }
    }

    private String getTrackDetailSign(String sectet,String timestamp){
        String parms = null;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp+"include_fields=updatedAt,subject,status,user,engineer";
        return parms;
    }

    private void syncHttpGetTrack(final String appKey,final String timeStamp){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String getTracksUrl = getTrackDetailUrl_1+trackId+getTrackDetailUrl_2;

                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getTrackDetailSign(encodeSectet,timeStamp);
                    String sign = AESUtil.calcTransAllParmsSign("Get",getTracksUrl,parms);

                    String responseData = ImibabyApp.HttpGetJsonData(getTracksUrl+getTrackDetailParms,appKey,timeStamp,sign);
                    LogUtil.e("repair query Data:"+responseData);
                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get("user");
                    JSONObject jsonObject3 = (JSONObject) jsonObject1.get("engineer");
                    String subject = (String) jsonObject1.get("subject");
                    String name = (String) jsonObject2.get("name");
                    String nickname = (String) jsonObject3.get("nickname");
                    String updatedAt = (String) jsonObject1.get("updatedAt");
                    String status = (String) jsonObject1.get("status");
                    userId = String.valueOf(jsonObject2.get("id"));
                    trackStatus = status;

                    tv_subject.setText(subject);
                    tv_update_time.setText(updatedAt);
                    tv_status.setText(convertYiWeiStatus(status));
                    tv_user_name.setText(name);
                    tv_staff_name.setText(nickname);


                    syncHttpGetReply(appKey,timeStamp);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private String convertYiWeiStatus(String status){
        String convertStatus;
        switch (status){
            case "open":
                convertStatus = getString(R.string.repair_state_open);
                break;
            case "pending":
                convertStatus = getString(R.string.repair_state_pending);
                break;
            case "solved":
                convertStatus = getString(R.string.repair_state_solved);
                break;
            default:
                convertStatus = getString(R.string.repair_state_open);
                break;
        }
        return convertStatus;
    }

    private String getTrackReplySign(String sectet,String timestamp){
        String parms = null;
        parms= "_app_secret="+sectet+"_count=100"+"_timestamp="+timestamp+"include_fields=createdAt,content,user,attachments";
        return parms;
    }

    private void syncHttpGetReply(final String appKey,final String timeStamp){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String getTracksUrl = getReplyListUrl_1+trackId+getReplyListUrl_2;
                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getTrackReplySign(encodeSectet,timeStamp);
                    String sign = AESUtil.calcTransAllParmsSign("Get",getTracksUrl,parms);

                    String responseData = ImibabyApp.HttpGetJsonData(getTracksUrl+getReplyListParms,appKey,timeStamp,sign);
                    LogUtil.e("repair query Data:"+responseData);
                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
                    JSONArray jsonArray = (JSONArray) jsonObject1.get("ticket_comments");
                    mData.clear();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                        JSONObject jsonObject3 = (JSONObject) jsonObject2.get("user");
                        JSONArray jsonArray1 = (JSONArray) jsonObject2.get("attachments");
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", jsonObject3.get("nickname"));
                        map.put("createdAt", jsonObject2.get("createdAt"));
                        map.put("content", jsonObject2.get("content"));
                        if(jsonArray1.size() == 0){
                            map.put("isPhoto","0");
                            mData.add(map);
                        }else{
                            map.put("isPhoto","0");
                            mData.add(map);
                            for(int j = 0;j<jsonArray1.size();j++) {
                                map = new HashMap<>();
                                map.put("isPhoto","1");
                                JSONObject jsonObject4 = (JSONObject) jsonArray1.get(j);
                                String photoUrl = (String) jsonObject4.get("contentUrl");
                                syncHttpGetPhoto(photoUrl, appKey, timeStamp);
                                map.put("contentUrl",photoUrl);
                                mData.add(map);
                            }
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    tv_track_count.setText(getString(R.string.repair_reply_count,jsonArray.size()));
                    if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
                        mLoadingDlg.dismiss();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    private String getNormalSign(String sectet,String timestamp){
        String parms = null;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp;
        return parms;
    }

    private void syncHttpGetPhoto(final String contontUrl,
                                  final String appKey,final String timeStamp){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String getTracksUrl = getPhotoUrl_1+contontUrl+getPhotoUrl_2;
                    String filePath = ImibabyApp.getIconCacheDir()+"/"+contontUrl+".png";
                    File file = new File(filePath);
                    if(file.exists()){
                        return "0";
                    }

                    String fileTmpPath = filePath+".tmp";
                    File tmpFile = new File(fileTmpPath);
                    if(tmpFile.exists()){
                        tmpFile.delete();
                    }

                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getNormalSign(encodeSectet,timeStamp);

                    String sign = AESUtil.calcTransAllParmsSign("Get",getTracksUrl,parms);


                    String isSucess = ImibabyApp.HttpGetFileData(getTracksUrl,tmpFile,appKey,timeStamp,sign);
                    LogUtil.e("repair query Data:"+isSucess);
                    if (file.exists())
                        file.delete();
                    tmpFile.renameTo(file);
                    return isSucess;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if(s.equals("1")){
                        myAdapter.notifyDataSetChanged();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    public String getTrackReplyJsonString(boolean open, String userId,String trackId,
                                          ArrayList<String> photoIdList, String trackStatus, String content) {
        JSONObject user = new JSONObject();
        user.put("id", Integer.valueOf(userId));
        JSONObject track = new JSONObject();
        track.put("id", Integer.valueOf(trackId));
        track.put("status",trackStatus);
        JSONArray attachments = new JSONArray();
        for(int i=0;i<photoIdList.size();i++){
            JSONObject phoneId = new JSONObject();
            phoneId.put("id", Integer.valueOf(photoIdList.get(i)));
            attachments.add(phoneId);
        }
        JSONObject replyJson = new JSONObject();
        replyJson.put("open",open);
        replyJson.put("user",user);
        replyJson.put("ticket",track);
        replyJson.put("content",content);
        replyJson.put("attachments",attachments);
        return replyJson.toJSONString();
    }

    private String getPutReplySign(String sectet,String timestamp,String parmsUrl){
        String parms = null;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp+"requestBody="+parmsUrl;
        return parms;
    }

    private void syncHttpPutReply(final ArrayList<String> photoIdList,final String appKey,final String timeStamp){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String getTracksUrl = putReplyContentUrl_1+trackId+putReplyContentUrl_2;
                    if(userId.equals("") || userId ==null || trackStatus == null || trackStatus.equals("")){
                        return null;
                    }
                    LogUtil.e("reply info:"+userId+":"+trackId+":"+trackStatus+":"+et_replay.getText().toString());
                    String replyContent = getTrackReplyJsonString(true,userId,trackId, photoIdList,trackStatus,et_replay.getText().toString());

                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getPutReplySign(encodeSectet,timeStamp,replyContent);
                    String sign = AESUtil.calcTransAllParmsSign("POST",getTracksUrl,parms);

                    String responseData = ImibabyApp.HttpPostJsonData(replyContent,getTracksUrl,appKey,timeStamp,sign);
                    LogUtil.e("repair query Data:"+responseData);
                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
                        mLoadingDlg.dismiss();
                    }
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    Integer retCode = (Integer) jsonObject.get("status");
                    if(retCode == 0){
                        syncHttpGetReply(appKey,timeStamp);
                    }
                    et_replay.getText().clear();
                    updatePhotoListView();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void syncHttpUploadFile(final String httpUrl_1,final String filePath,final String appKey,final String timeStamp){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = getNormalSign(encodeSectet,timeStamp);
                    String sign = AESUtil.calcTransAllParmsSign("POST",httpUrl_1,parms);

                    String responseData = uploadFile(httpUrl_1,filePath,appKey,timeStamp,sign);
                    LogUtil.e("repair update Data:"+responseData);

                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
                        mLoadingDlg.dismiss();
                    }
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("result");
                    int photoId = (Integer) jsonObject1.get("id");
                    photoUploadPath.add(String.valueOf(photoId));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    /**
     * android上传文件到服务器
     *
     * @param filePath   需要上传的文件的目录
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(String RequestURL, String filePath,final String appKey,final String timeStamp,final String sign) {

        LogUtil.e("file:"+RequestURL+":"+filePath);
        int TIME_OUT = 100000; //超时时间
        String CHARSET = "utf-8"; //编码格式
        String FAILURE = "FAILURE";
        //生成一个文件
        File file = new File(filePath);
        //边界标识 随机生成，这个作为boundary的主体内容
        String BOUNDARY = UUID.randomUUID().toString();

        String PREFIX = "--";
        //回车换行，用于调整协议头的格式
        String LINE_END = "\r\n";
        //格式的内容信息
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign",sign);

            //设置超时时间
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            //允许输入流
            conn.setDoInput(true);
            //允许输出流
            conn.setDoOutput(true);
            //不允许使用缓存
            conn.setUseCaches(false);
            //请求方式
            conn.setRequestMethod("POST");
            //设置编码 utf-8
            conn.setRequestProperty("Charset", CHARSET);
            //设置为长连接
            conn.setRequestProperty("connection", "keep-alive");

            //这里设置请求方式以及boundary的内容，即上面生成的随机字符串
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (file != null) {
                //当文件不为空，把文件包装并且上传
                //这里定义输出流，用于之后向服务器发起请求
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);

                //这里的StringBuffer 用来拼接我们的协议头
                StringBuffer sb = new StringBuffer();

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + LINE_END);

                //这里Content-Type 传给后台一个mime类型的编码字段，用于识别扩展名
                sb.append("Content-Type: image/png" + LINE_END);
                sb.append(LINE_END);

                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                //获取后台返回的数据
                if (res == 200) {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuffer sb_result = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb_result.append(line);
                    }
                    return sb_result.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

}
