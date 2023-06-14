package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.adapter.ChatRecyclerViewAdaper;
import com.xiaoxun.xun.adapter.EmojiAdapter;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.SilenceTime;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.camerax.VideoRecordNewActivity;
import com.xiaoxun.xun.db.ChatHisDao;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomFileUtils;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.EmojiUtil;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.MyMediaPlayerUtil;
import com.xiaoxun.xun.utils.MyRecorder;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.UriUtil;
import com.xiaoxun.xun.views.TimeoutButton;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by guxiaolong on 2017/1/11.
 */

public class PrivateMessageActivity extends NormalActivity implements MsgCallback, View.OnClickListener, SensorEventListener {
    private final static int RECORD_INTERVAL_TIME = 200; //MS
    private ImibabyApp mApp;
    private TimeoutButton mRecord;

    private RecyclerView mChatList;
    private RelativeLayout mChatSending;
    private ImageView mSendAnim;
    private ChatRecyclerViewAdaper mChatAdater;
    private RelativeLayout mNoChatLy;

    private HashMap<Integer, ChatMsgEntity> mSending;
    private long mStartRecordT;
    private long mRecordInterval = 0;
    private long mEndRecordT;
    private int mRecordT;
    private boolean mFailToastShow = false;
    private File mRecordFile;
    private MioAsyncTask<String, Integer, String> mRecordTask = null;
    private ChatReceiver mChatReceiver;
    private boolean isRecording = false;
    private ArrayList<ChatMsgEntity> mChatMsgList = new ArrayList<>();
    private ArrayList<ChatMsgEntity> mDeleteChatMsgList = new ArrayList<>();

    private String mUserChatGid = null;

    private String mWatchEid;
    private WatchData mCurWatch;
    private ImageButton mBackBtn;
    private ImageView mWathcHead;
    private TextView mWatchName;
    private ImageView mInsertMode;
    private ImageButton mChangeInputTypeBtn;
    private ImageButton mSendTxtBtn;
    private ImageButton mSendPhoto;
    private ImageButton mSendVideo;
    private LinearLayout mSendPhotoLayout;
    private LinearLayout mSendVideoLayout;
    private EditText mSendTextEdit;
    private RelativeLayout mTextInputLayout;
    private TextView mHasInputNumber;
    private RelativeLayout mLayoutRecord;
    private int mMaxInput;
    private RelativeLayout mLayoutEmojiBtn;
    private ImageButton mEmojiBtn;
    private RelativeLayout mLayoutMore;
    private ImageButton mMoreBtn;

    private File mImageFile;
    private SensorManager mSensorManager = null; // 传感器管理器
    private Sensor mProximiny = null; // 传感器实例

    private TextView watchStateTips;

    private AsyncTask<Void, Void, ArrayList<ChatMsgEntity>> mLoadPrivateMsgTask;

    private OnItemClickListener emojiItemListener;
    private RecyclerView emojiRecyclerView;
    private EmojiAdapter emojiAdapter;
    private int[] emojiIds = {R.drawable.cry, R.drawable.nose, R.drawable.basketball,
            R.drawable.snot, R.drawable.naughty, R.drawable.smile,
            R.drawable.flower, R.drawable.laugh, R.drawable.moon};

    private int[] emojiIds960 = {R.drawable.rabbit_anger, R.drawable.rabbit_awkward, R.drawable.rabbit_bad_laugh,
            R.drawable.rabbit_cry, R.drawable.rabbit_cute, R.drawable.rabbit_despise,
            R.drawable.rabbit_happy, R.drawable.rabbit_love, R.drawable.rabbit_shy,
            R.drawable.rabbit_smile, R.drawable.rabbit_surprised, R.drawable.rabbit_zibi};

    private static final int PERMISSION_RESULT_RECORD_AUDIO = 2;
    private static final int PERMISSION_RESULT_CAMERA = 4;
    private static final int PERMISSION_RESULT_CALL_PHONE = 6;
    private static final int PERMISSON_RESULT_CAMERA_VIDEO = 8;

    private static final int GET_IMAGE_FROM_CAMERA = 3;
    private static final int GET_IMAGE_FROM_ALBUM = 4;
    private static final int GET_VIDEO_FROM_CAMERA = 5;
    private static final int GET_VIDEO_FROM_RECORD = 6;

    public enum State {
        NEAR, FAR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message);
        mApp = (ImibabyApp) this.getApplication();
        if (mSending == null) {
            mSending = new HashMap<Integer, ChatMsgEntity>();
        }
        mWatchEid = getIntent().getStringExtra(Const.KEY_WATCH_ID);
        if (mWatchEid == null) {
            finish();
            return;
        }
        mUserChatGid = getIntent().getStringExtra(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID);
        if (mUserChatGid == null || mUserChatGid.length() == 0) {
            mUserChatGid = mApp.getWatchPrivateGid(mWatchEid);
        }

        if ((mUserChatGid == null || mUserChatGid.length() == 0) && mApp.getNetService() != null) {
            mApp.getNetService().sendGetContactReq(mWatchEid);
        }

        mCurWatch = mApp.getCurUser().queryWatchDataByEid(mWatchEid);

        initViews();
        initDatas();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximiny = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWatchEid = intent.getStringExtra(Const.KEY_WATCH_ID);
        if (mWatchEid == null) {
            finish();
            return;
        }
        mUserChatGid = getIntent().getStringExtra(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID);
        if (mUserChatGid == null || mUserChatGid.length() == 0) {
            mUserChatGid = mApp.getWatchPrivateGid(mWatchEid);
        }

        if ((mUserChatGid == null || mUserChatGid.length() == 0) && mApp.getNetService() != null) {
            mApp.getNetService().sendGetContactReq(mWatchEid);
        }
        mCurWatch = mApp.getCurUser().queryWatchDataByEid(mWatchEid);
        initViews();
        initDatas();
    }

    private void initViews() {
        mBackBtn = (ImageButton) findViewById(R.id.iv_title_back);
        mBackBtn.setOnClickListener(this);
        mWathcHead = (ImageView) findViewById(R.id.iv_watch_head);
        mWatchName = (TextView) findViewById(R.id.tv_watch_name);
        mInsertMode = (ImageView) findViewById(R.id.iv_insert_mode);

        mChatSending = (RelativeLayout) findViewById(R.id.luying);
        mSendAnim = (ImageView) findViewById(R.id.view2);
        mNoChatLy = (RelativeLayout) findViewById(R.id.no_chat_ly);
        mRecord = (TimeoutButton) findViewById(R.id.record_button);
        mChatList = (RecyclerView) findViewById(R.id.chat_list);
        mChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mApp.hideKeyboard(mSendTextEdit);

                if (mLayoutMore.getVisibility() == View.VISIBLE) {
                    mLayoutMore.setVisibility(View.GONE);
                }
                if (emojiRecyclerView.getVisibility() == View.VISIBLE) {
                    emojiRecyclerView.setVisibility(View.GONE);
                }
                return false;
            }
        });
        mLayoutMore = (RelativeLayout) findViewById(R.id.layout_more);
        mMoreBtn = (ImageButton) findViewById(R.id.btn_more);
        mMoreBtn.setOnClickListener(this);
        showMoreButton();
        emojiRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_emoji);
        mLayoutEmojiBtn = (RelativeLayout) findViewById(R.id.layout_emoji_btn);
        mEmojiBtn = (ImageButton) findViewById(R.id.btn_emoji);
        mEmojiBtn.setOnClickListener(this);


        mChangeInputTypeBtn = (ImageButton) findViewById(R.id.btn_chane_input_type);
        mChangeInputTypeBtn.setOnClickListener(this);

        mLayoutRecord = (RelativeLayout) findViewById(R.id.chat);

        mSendTxtBtn = (ImageButton) findViewById(R.id.btn_send_txt);
        mSendTxtBtn.setOnClickListener(this);

        mSendPhotoLayout = (LinearLayout) findViewById(R.id.layout_send_photo);
        mSendVideoLayout = (LinearLayout) findViewById(R.id.layout_send_video);
        mSendPhoto = (ImageButton) findViewById(R.id.send_photo);
        mSendPhoto.setOnClickListener(this);
        mSendVideo = (ImageButton) findViewById(R.id.send_video);
        mSendVideo.setOnClickListener(this);

        mLayoutEmojiBtn.setVisibility(View.GONE);

        mSendPhotoLayout.setVisibility(View.GONE);
        mSendVideoLayout.setVisibility(View.GONE);
        if (mCurWatch.isDevice900_A03() || mCurWatch.isDevice708() || mCurWatch.isDevice708_A06() || mCurWatch.isDevice708_A07()) {
            mMoreBtn.setVisibility(View.VISIBLE);
            mSendPhotoLayout.setVisibility(View.VISIBLE);
            mLayoutEmojiBtn.setVisibility(View.VISIBLE);
            mSendVideoLayout.setVisibility(View.VISIBLE);
            if (mCurWatch.isDevice707_A05()) {
                mMoreBtn.setVisibility(View.GONE);
                mSendPhotoLayout.setVisibility(View.GONE);
                mSendVideoLayout.setVisibility(View.GONE);
            }
        }

        if (mCurWatch.isDevice709() || mCurWatch.isDevice709_A03() || mCurWatch.isDevice709_A05()||mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
            mMoreBtn.setVisibility(View.GONE);
            mLayoutEmojiBtn.setVisibility(View.VISIBLE);
            mSendPhotoLayout.setVisibility(View.GONE);
            mSendVideoLayout.setVisibility(View.GONE);
        }

        mTextInputLayout = (RelativeLayout) findViewById(R.id.layout_text_input);
        mHasInputNumber = (TextView) findViewById(R.id.tv_input_number);
        mSendTextEdit = (EditText) findViewById(R.id.edit_send_text);
        if (mCurWatch.isDevice707_A05()) {
            mMaxInput = 70;
        } else {
            mMaxInput = 1000;
        }
        mSendTextEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxInput)});
        if (mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
            mSendTextEdit.setHint(getString(R.string.input_number_available, String.valueOf(mMaxInput)));
        }
        mSendTextEdit.setOnClickListener(this);
        mSendTextEdit.addTextChangedListener(textWatcher);
        mSendTextEdit.setOnEditorActionListener(onEditorActionListener);
        watchStateTips = (TextView) findViewById(R.id.tv_watch_state);
    }

    private void showMoreButton() {
        mMoreBtn.setVisibility(View.GONE);
        if(mCurWatch!=null){
            return;
        }
        if (mCurWatch.isDevice708() || mCurWatch.isDevice708_A06() || mCurWatch.isDevice708_A07()) {
            mMoreBtn.setVisibility(View.VISIBLE);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("")) {
                showMoreButton();
                mSendTxtBtn.setVisibility(View.GONE);
            } else {
                mMoreBtn.setVisibility(View.INVISIBLE);
                mSendTxtBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                mHasInputNumber.setVisibility(View.VISIBLE);
                mHasInputNumber.setText(editable.length() + "/" + mMaxInput);
            } else {
                mHasInputNumber.setVisibility(View.GONE);
            }
        }
    };

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                myApp.hideKeyboard(mSendTextEdit);
                return true;
            } else {
                return false;
            }
        }
    };

    private void initDatas() {
        updateWatchInfo();
        updateHeadImage();

        mChatAdater = new ChatRecyclerViewAdaper(this, mChatMsgList, 1, mCurWatch);
        mChatList.setAdapter(mChatAdater);
        mChatList.setLayoutManager(new LinearLayoutManager(this));
        if (mChatMsgList != null && mChatMsgList.size() > 0) {
            mChatList.scrollToPosition(mChatMsgList.size() - 1);
        }
        getChatMsgFromDB();

        mChatReceiver = new ChatReceiver();
        mChatReceiver.registerReceiver(this);

        mRecord.setOnTouchListener(recordTouchListener);

        if (mApp.getNetService() != null) {
            mApp.getNetService().sendUserOrDeviceSetChangeE2G(mWatchEid, mUserChatGid, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_WATCH_CHANGE_NOTICE);
        }

        if (mCurWatch.isDevice708() || mCurWatch.isDevice708_A06() || mCurWatch.isDevice708_A07()) {
            emojiAdapter = new EmojiAdapter(this, emojiIds);
        } else {
            emojiAdapter = new EmojiAdapter(this, emojiIds960);
        }
        emojiRecyclerView.setLayoutManager(new GridLayoutManager(this, 9));
        emojiRecyclerView.setAdapter(emojiAdapter);
        initEmojiItemListener();
    }

    private void initEmojiItemListener() {
        emojiItemListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id;
                if (mCurWatch.isDevice708() || mCurWatch.isDevice708_A06() || mCurWatch.isDevice708_A07()) {
                    id = emojiIds[position];
                } else {
                    id = emojiIds960[position];
                }
                String emojiText = EmojiUtil.getEmojiText(id);
                sendTextMessage(emojiText, ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        };
        emojiAdapter.setOnItemClickListener(emojiItemListener);
    }

    private View.OnTouchListener recordTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, final MotionEvent event) {
            if (ActivityCompat.checkSelfPermission(PrivateMessageActivity.this, Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrivateMessageActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RESULT_RECORD_AUDIO);
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                LogUtil.d("huangqilin MotionEvent.ACTION_DOWN");

                if (mUserChatGid == null || mUserChatGid.length() == 0) {
                    ToastUtil.show(PrivateMessageActivity.this, R.string.no_user_gid_tips);
                    return false;
                }

                isRecording = false;

                if (mRecordInterval != 0 && (SystemClock.uptimeMillis() - mRecordInterval < RECORD_INTERVAL_TIME)) {
                    mRecordInterval = SystemClock.uptimeMillis();
                    return false;
                }
                startRecord();

            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {//松开手势时执行录制完成
                LogUtil.d("huangqilin MotionEvent.ACTION_UP");
                mChatSending.setVisibility(View.GONE);

                if (!isRecording || SystemClock.uptimeMillis() - mRecordInterval < RECORD_INTERVAL_TIME) {
                    if (mRecordTask != null) {
                        mRecordTask.cancel(true);
                    }
                    if (!mFailToastShow) {
                        mFailToastShow = true;
                    }
                    return false;
                }
                stopRecord();
            }
            return false;
        }
    };

    private void startRecord() {
        mRecordInterval = SystemClock.uptimeMillis();
        MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
        mChatAdater.stopPlayAnimation();
        MyMediaPlayerUtil.getInstance().requestAudioFocus(mApp);
        mStartRecordT = SystemClock.uptimeMillis();

        if (mRecordTask != null) {
            mRecordTask.cancel(true);
        }
        mRecordTask = new MioAsyncTask<String, Integer, String>() {
            protected String doInBackground(String... params) {
                try {
                    MyRecorder.getInstance().cancelRecorder();
                    MyRecorder.getInstance().startRecorder();
                    isRecording = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        mRecordTask.execute();

        mChatSending.setVisibility(View.VISIBLE);
        AnimationDrawable animDra = (AnimationDrawable) mSendAnim.getBackground();
        animDra.start();
    }

    private void stopRecord() {
        MyMediaPlayerUtil.getInstance().abandonAudioFocus(mApp);
        if (mRecord.getmCancelFlag() == 2) {
            mRecord.setmCancelFlag(3);
            MyRecorder.getInstance().cancelRecorder();
            ToastUtil.showMyToast(mApp, getString(R.string.send_cancel), Toast.LENGTH_SHORT);
        } else if (mRecord.getmCancelFlag() == 1 || mRecord.getmCancelFlag() == -1) {
            if (mRecord.getmTimeFlag() == true) {
                mRecord.setmTimeFlag(false);
                mRecord.setmRepeatAction(true);
                mRecordT = 15;
                sendRecord();
            } else if (mRecord.getmRepeatAction() == false) {
                mEndRecordT = SystemClock.uptimeMillis();
                mRecordT = (int) (mEndRecordT - mStartRecordT) / 1000;
                if (mRecordT < 1) {
                    MyRecorder.getInstance().cancelRecorder();
                    if (mRecordTask != null) {
                        mRecordTask.cancel(true);
                    }
                    ToastUtil.showMyToast(mApp, getString(R.string.record_too_short), Toast.LENGTH_LONG);
                } else {
                    sendRecord();
                }
            }
        }
        isRecording = false;
        if (!mFailToastShow) {
            mFailToastShow = true;
        }
    }

    private void sendRecord() {
        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        mRecordFile = MyRecorder.getInstance().endRecorder();
        if (mRecordFile != null) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setmWatchId(mCurWatch.getWatchId());
            entity.setmAudioPath(mRecordFile.getPath());
            entity.setmDuration(mRecordT);
            entity.setmSrcId(mApp.getCurUser().getEid());
            entity.setmDate(TimeUtil.getTimeStampLocal());
            entity.setmIsFrom(false);
            entity.setmFamilyId(mUserChatGid);
            entity.setmSended(ChatMsgEntity.CHAT_SEND_STATE_SENDING);
            entity.setmTryTime(1);
            entity.setmType(ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE);
            mChatMsgList.add(entity);
            mChatAdater.notifyItemInserted(mChatMsgList.indexOf(entity));
            mChatList.scrollToPosition(mChatMsgList.size() - 1);
            ChatHisDao.getInstance(mApp).addChatMsg(mUserChatGid, entity);
            AESUtil.getInstance().encryptFile(mRecordFile);
            e2c4Chat(entity);
            sendPrivateMessageNotify();
        } else if (mFailToastShow) {
            ToastUtil.showMyToast(mApp, getString(R.string.record_permission_tips), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_RESULT_RECORD_AUDIO == requestCode && grantResults.length == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {

            } else {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.record_permission_tips), Toast.LENGTH_SHORT).show();
            }
        } else if (PERMISSION_RESULT_CAMERA == requestCode && grantResults.length == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                startCameraCapture();
            } else {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.camera_premission_tips), Toast.LENGTH_SHORT).show();
            }
        } else if (PERMISSON_RESULT_CAMERA_VIDEO == requestCode && grantResults.length == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                startVideoCapture();
            } else {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.camera_premission_tips), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUserChatGid != null) {
            mApp.setHasNewPrivateMsg(mUserChatGid, false);
            sendBroadcast(new Intent(Constants.ACTION_UPDATE_NEW_MESSAGE_NOTICE));
        }
        mApp.setPrivateMsgOpenEid(mWatchEid);
        if (mApp.getmUseCall()) {
            mInsertMode.setVisibility(View.VISIBLE);
        } else {
            mInsertMode.setVisibility(View.GONE);
        }


        NotificationManager notifyMng = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notifyMng.cancel(mUserChatGid, Const.TITLE_BAR_NEW_PRIVATE_MESSAGE);


        //注册距离传感器，用于判断听筒模式自动切换
        mSensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);

        mChangeInputTypeBtn.setVisibility(View.VISIBLE);

        updateWatchState();
        if (mApp.getNetService() != null) {
            mApp.getNetService().getDeviceOfflineState(mWatchEid);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mApp.setPrivateMsgOpenEid(null);
        mChatAdater.stopPlayRecord();
        mSensorManager.unregisterListener(this);
        if (mRecordTask != null) {
            mRecordTask.cancel(true);
        }
        MyRecorder.getInstance().cancelRecorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatReceiver.unregisterReceiver(this);
        if (mLoadPrivateMsgTask != null) {
            mLoadPrivateMsgTask.cancel(true);
            mLoadPrivateMsgTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_IMAGE_FROM_CAMERA:
                handleCameraImageResult();
                break;
            case GET_IMAGE_FROM_ALBUM:
                handleAlbumImageResult(data);
                break;
            case GET_VIDEO_FROM_CAMERA:
                handleCameraVideoResult(data);
                break;
            case GET_VIDEO_FROM_RECORD:
                if (resultCode == Activity.RESULT_OK) {
                    handleRecordVideoResult(data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = CloudBridgeUtil.getCloudMsgCID(respMsg);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        int sn = (Integer) reqMsg.get(CloudBridgeUtil.KEY_NAME_SN);
        JSONObject pl;
        JSONArray key;
        switch (cid) {
            case CloudBridgeUtil.CID_E2C4_DEVICE_LONGTIME_MSG_RESP:
                ChatMsgEntity chat = mSending.get(sn);
                if (chat != null) {
                    int position = mChatMsgList.indexOf(chat);
                    if (CloudBridgeUtil.RC_SUCCESS == rc) {
                        if (mApp.getNetService() != null) {
                            mApp.getNetService().timeoutZero();
                        }
                        JSONObject key1 = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String temp = (String) key1.get(CloudBridgeUtil.KEY_NAME_KEY);
                        sendMessageSuccess(chat, temp);
                        sendMiStateRecordChat(sn, true);
                    } else if (CloudBridgeUtil.RC_NETERROR == rc) {
                        sendMessageFail(chat);
                    } else if (CloudBridgeUtil.RC_NOT_LOGIN == rc || CloudBridgeUtil.RC_SOCKET_NOTREADY == rc) {
                        sendMiStateRecordChat(sn, false);
                        mApp.sdcardLog("huangqilin 333 send num:" + chat.getmTryTime());
                        sendMessageFail(chat);
                    } else if (CloudBridgeUtil.RC_TIMEOUT == rc) {
                        if (mApp.getNetService() != null) {
                            mApp.getNetService().timeoutInc();
                        }
                        if (mApp.getNetService() != null && mApp.getNetService().isCloudBridgeClientOk()) {
                            mApp.getNetService().manualPing();
                        }
                        sendMessageTimeout(chat, sn);
                    } else {
                        sendMessageTimeout(chat, sn);
                    }
                    mSending.remove(sn);
                    mChatAdater.notifyItemChanged(position);
                }
                break;
            case CloudBridgeUtil.CID_UPLOAD_NOTICE_RESP:
                ChatMsgEntity imageEntity = mSending.get(sn);
                if (imageEntity != null) {
                    int position = mChatMsgList.indexOf(imageEntity);
                    if (CloudBridgeUtil.RC_SUCCESS == rc) {
                        if (mApp.getNetService() != null) {
                            mApp.getNetService().timeoutZero();
                        }
                        pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                        String msgKey = (String) pl.get(CloudBridgeUtil.KEY_NAME_KEY);
                        sendMessageSuccess(imageEntity, msgKey);
                    } else if (CloudBridgeUtil.RC_NETERROR == rc) {
                        sendMessageFail(imageEntity);
                    } else if (CloudBridgeUtil.RC_NOT_LOGIN == rc || CloudBridgeUtil.RC_SOCKET_NOTREADY == rc) {
                        sendMessageFail(imageEntity);
                    } else if (CloudBridgeUtil.RC_TIMEOUT == rc) {
                        if (mApp.getNetService() != null) {
                            mApp.getNetService().timeoutInc();
                        }
                        if (mApp.getNetService() != null && mApp.getNetService().isCloudBridgeClientOk()) {
                            mApp.getNetService().manualPing();
                        }
                        sendMessageTimeout(imageEntity, sn);
                    } else {
                        sendMessageTimeout(imageEntity, sn);
                    }
                    mSending.remove(sn);
                    mChatAdater.notifyItemChanged(position);
                }
                break;
            case CloudBridgeUtil.CID_E2G_DOWN:
                pl = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                //测试服务器上,发送70091，会先收到30032，再收到70092，收到30032时候req根本不是30031，会错误
                if (CloudBridgeUtil.getCloudMsgCID(respMsg) != CloudBridgeUtil.CID_E2G_UP)
                    break;
                if (CloudBridgeUtil.SUB_ACTION_VALUE_NAME_FORCE_RECORD == (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION)) {
                    String[] teid = (String[]) reqMsg.get(CloudBridgeUtil.KEY_NAME_TEID);
                    String eid = teid[0];
                    mApp.setForceRecordState(eid, false);
                }
                if (rc == 0) {
                    pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (pl != null && (Integer) pl.get(CloudBridgeUtil.KEY_NAME_SUB_ACTION) == CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE) {
                        Intent it = new Intent(Const.ACTION_RECEIVE_CHAT_MSG);
                        it.putExtra(Const.KEY_JSON_MSG, respMsg.toJSONString());
                        mApp.sendBroadcast(it);
                    }
                }

                break;
            default:
                break;
        }

    }

    private void sendMessageSuccess(ChatMsgEntity chat, String key) {
        chat.setmSended(ChatMsgEntity.CHAT_SEND_STATE_SUCCESS);
        String findkey = chat.getmDate();
        chat.setmDate(TimeUtil.getOrderTime(key.substring(key.indexOf("/MSG/") + 5)));
        ChatHisDao.getInstance(mApp).updateChatMsg(mUserChatGid, chat, findkey);
        mApp.sdcardLog("huangqilin send num:" + chat.getmTryTime());
    }

    private void sendMessageFail(ChatMsgEntity chat) {
        chat.setmTryTime(0);
        chat.setmSended(ChatMsgEntity.CHAT_SEND_STATE_FAIL);
        ChatHisDao.getInstance(mApp).updateChatMsg(mUserChatGid, chat, chat.getmDate());
    }

    private void sendMessageTimeout(ChatMsgEntity chat, int sn) {
        if (chat.getmTryTime() > ChatMsgEntity.MAX_TRY_SEND_TIME) {
            sendMessageFail(chat);
        } else {
            if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO || chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO) {
                sendNoticeRetry(chat);
            } else {
                sendRecordRetry(chat);
            }
        }
    }

    private void sendNoticeRetry(ChatMsgEntity chat) {
        chat.setmTryTime(chat.getmTryTime() + 1);
        if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO) {
            sendNotice(chat, CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO);
        } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO) {
            sendNotice(chat, CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO);
        }
    }

    private void sendRecordRetry(ChatMsgEntity chat) {
        chat.setmTryTime(chat.getmTryTime() + 1);
        e2c4Chat(chat);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_chane_input_type:
                clickChangeInput();
                break;
            case R.id.edit_send_text:
                clickEditText();
                break;
            case R.id.btn_send_txt:
                clickSendText();
                break;
            case R.id.btn_emoji:
                clickEmoji();
                break;
            case R.id.send_photo:
                clickSendPhoto();
                break;
            case R.id.send_video:
                clickSendVideo();
                break;
            case R.id.btn_more:
                clickMore();
                break;
            default:
                break;
        }
    }

    private void clickChangeInput() {
        mLayoutMore.setVisibility(View.GONE);
        emojiRecyclerView.setVisibility(View.GONE);
        if (mTextInputLayout.getVisibility() == View.VISIBLE) {
            mLayoutRecord.setVisibility(View.VISIBLE);
            mChangeInputTypeBtn.setBackgroundResource(R.drawable.btn_message_mode_keyboard_selector);
            mTextInputLayout.setVisibility(View.GONE);
            mSendTxtBtn.setVisibility(View.GONE);
            showMoreButton();
            mApp.hideKeyboard(mSendTextEdit);
        } else {
            mLayoutRecord.setVisibility(View.GONE);
            mChangeInputTypeBtn.setBackgroundResource(R.drawable.btn_message_mode_voice_selector);
            mTextInputLayout.setVisibility(View.VISIBLE);
            mSendTextEdit.requestFocus();
            if (mSendTextEdit.getText().toString().length() > 0) {
                mSendTxtBtn.setVisibility(View.VISIBLE);
                mMoreBtn.setVisibility(View.INVISIBLE);
            }
            mApp.showKeyboard(mSendTextEdit);

        }
        mChatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatList.scrollToPosition(mChatMsgList.size() - 1);
            }
        }, 250);
    }

    private void clickMore() {
        mApp.hideKeyboard(mSendTextEdit);
        emojiRecyclerView.setVisibility(View.GONE);
        if (mLayoutMore.getVisibility() == View.GONE) {
            mLayoutMore.setVisibility(View.VISIBLE);
        } else {
            mLayoutMore.setVisibility(View.GONE);
        }
        mChatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatList.scrollToPosition(mChatMsgList.size() - 1);
            }
        }, 250);
    }

    private void clickEditText() {
        mLayoutMore.setVisibility(View.GONE);
        emojiRecyclerView.setVisibility(View.GONE);
        mSendTextEdit.requestFocus();
        mChatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatList.scrollToPosition(mChatMsgList.size() - 1);
            }
        }, 250);
    }

    private void clickSendText() {
        if (mUserChatGid == null || mUserChatGid.length() == 0) {
            ToastUtil.show(PrivateMessageActivity.this, R.string.no_user_gid_tips);
            return;
        }
        if (mSendTextEdit.getText().toString().length() == 0) {
            ToastUtil.show(PrivateMessageActivity.this, getString(R.string.no_text_warning));
            return;
        }

        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        String content = mSendTextEdit.getText().toString();
        mSendTextEdit.setText("");
        sendTextMessage(content, ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT);
    }

    private void clickEmoji() {
        mLayoutMore.setVisibility(View.GONE);
        mApp.hideKeyboard(emojiRecyclerView);
        if (emojiRecyclerView.getVisibility() == View.VISIBLE) {
            emojiRecyclerView.setVisibility(View.GONE);
        } else {
            emojiRecyclerView.setVisibility(View.VISIBLE);
        }
        mChatList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatList.scrollToPosition(mChatMsgList.size() - 1);
            }
        }, 250);
    }

    private void clickSendPhoto() {
//        openPhotoSelectDialog();
        startAlbumCapture();
    }

    private void clickSendVideo() {
        if (ActivityCompat.checkSelfPermission(PrivateMessageActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            startVideoCapture();
        } else {
            ActivityCompat.requestPermissions(PrivateMessageActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSON_RESULT_CAMERA_VIDEO);
        }
    }

    private void sendTextMsgE2C(ChatMsgEntity entity) {
        MyMsgData e2c = new MyMsgData();
        e2c.setTimeout(60 * 1000);
        e2c.setFinalTimeout(60 * 1000);
        e2c.setNeedNetTimeout(true);
        e2c.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, entity.getmFamilyId());
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(entity.getmFamilyId());
        key.append(CloudBridgeUtil.E2C_SPLIT_MEG);
        key.append(CloudBridgeUtil.E2C_SERVER_SET_TIME);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key.toString());
        JSONObject chat = new JSONObject();
        if (entity.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI) {
            chat.put(CloudBridgeUtil.E2C_PL_KEY_TYPE, CloudBridgeUtil.OFFLINE_MSG_TYPE_EMOJI);
        } else {
            chat.put(CloudBridgeUtil.E2C_PL_KEY_TYPE, CloudBridgeUtil.E2C_PL_KEY_TYPE_TEXT);
        }
        chat.put(CloudBridgeUtil.E2C_PL_KEY_EID, entity.getmSrcId());
        chat.put(CloudBridgeUtil.E2C_PL_KEY_DURATION, entity.getmDuration());
        chat.put(CloudBridgeUtil.E2C_PL_KEY_CONTENT, entity.getmAudioPath());

        pl.put(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY, chat);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();

        entity.setmSendStartTime(System.currentTimeMillis());
        mSending.put(sn, entity);
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C4_DEVICE_LONGTIME_MSG_REQ, sn, mApp.getToken(), pl));
        e2c.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, mCurWatch.getDeviceProtocolVersion());
        mApp.getNetService().sendNetMsg(e2c);
    }

    private State mLastState = State.FAR;

    private State getStateFromValve(float valve) {
        if (valve >= 5.0f || valve >= mProximiny.getMaximumRange()) {
            return State.FAR;
        } else {
            return State.NEAR;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values == null) return;
        if (event.values.length == 0) return;
        if (mApp.getmUseCall() == false) {
            State state = getStateFromValve(event.values[0]);
            if (state == mLastState) return;
            mLastState = state;

            if (state == State.FAR) {
                if (mChatAdater != null) {
                    mChatAdater.setNearEar(false);
                }
                MyMediaPlayerUtil.getInstance().StarMediaPlayer(mApp.mAudioPath, mApp, false);
            } else {
                if (mChatAdater != null) {
                    mChatAdater.setNearEar(true);
                }
                MyMediaPlayerUtil.getInstance().StarMediaPlayer(mApp.mAudioPath, mApp, true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class chatStat {
        public int all;
        public int success;
        public int timeout;
        public int time_5;
        public int time_avg_5;
        public int time_10;
        public int time_avg_10;
        public int time_20;
        public int time_avg_20;
        public int time_40;
        public int time_avg_40;
        public int time_60;
        public int time_avg_60;
    }

    private void sendMiStateRecordChat(int chatSn, boolean isSuccess) {
        ChatMsgEntity chat;
        JSONArray statArray;
        chatStat mChatStat = new chatStat();
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHH");
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        chat = mSending.get(chatSn);
        String category = "Chat_to_server_1";
        String key = "ok_5";
        String value = "5";
        if (mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()) != null) {
            String eid = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0).getEid();
            Boolean statFlag = false;

            long deltaTime = System.currentTimeMillis() - chat.getmSendStartTime();

            String timestr = TimeUtil.getTimeStampGMT(chat.getmSendStartTime());
            category = "Chat_to_server_" + timestr.substring(8, 10);
            mChatStat.all = 1;
            if (isSuccess == false) {
                key = "fail";
                mChatStat.timeout = 1;
            } else if (deltaTime < 5 * 1000) {
                value = "5";
                mChatStat.success = 1;
                mChatStat.time_5 = 1;
                mChatStat.time_avg_5 = (int) (deltaTime / 1000);
            } else if (deltaTime < 10 * 1000) {
                value = "10";
                mChatStat.success = 1;
                mChatStat.time_10 = 1;
                mChatStat.time_avg_10 = (int) (deltaTime / 1000);
            } else if (deltaTime < 20 * 1000) {
                value = "20";
                mChatStat.success = 1;
                mChatStat.time_20 = 1;
                mChatStat.time_avg_20 = (int) (deltaTime / 1000);
            } else if (deltaTime < 30 * 1000) {
                value = "30";
                mChatStat.success = 1;
                mChatStat.time_40 = 1;
                mChatStat.time_avg_40 = (int) (deltaTime / 1000);
            } else if (deltaTime < 40 * 1000) {
                value = "40";
                mChatStat.success = 1;
                mChatStat.time_40 = 1;
                mChatStat.time_avg_40 = (int) (deltaTime / 1000);
            } else if (deltaTime < 60 * 1000) {
                value = "60";
                mChatStat.success = 1;
                mChatStat.time_60 = 1;
                mChatStat.time_avg_60 = (int) (deltaTime / 1000);
            } else {
                value = "fail";
                mChatStat.timeout = 1;
            }
            key = "ok_" + value;
            String tempkey = mApp.getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + mApp.getCurUser().getEid(), null);
            if (tempkey == null) {
                statArray = new JSONArray();
            } else {
                statArray = (JSONArray) JSONValue.parse(tempkey);
                JSONObject checkobj = (JSONObject) statArray.get(0);
                String checkTime = (String) checkobj.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                checkTime = checkTime.substring(0, 8);
                if (!checkTime.equals(format1.format(today).toString())) {
                    String privKey = mApp.getStringValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), Const.DEFAULT_NEXT_KEY);
                    if (privKey.equals(Const.DEFAULT_NEXT_KEY)) {
                        mApp.setValue(Const.SHARE_PREF_CLOUDBRIDGE_YESTODAY_STATE + mApp.getCurUser().getEid(), tempkey);
                    }
                    statArray.clear();
                }
            }
            for (Object object : statArray) {
                JSONObject json = (JSONObject) object;
                String tempeid = (String) json.get(CloudBridgeUtil.KEY_NAME_EID);
                if (tempeid.equals(eid)) {
                    String tempTime = (String) json.get(CloudBridgeUtil.KEY_NAME_TIMESTAMP);
                    if (tempTime.equals(format.format(today).toString())) {
                        String statLocation = (String) json.get("voice_send");
                        mChatStat.all += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.success += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.timeout += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_5 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_avg_5 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mChatStat.time_avg_5;
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_10 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_avg_10 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mChatStat.time_avg_10;
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_20 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_avg_20 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mChatStat.time_avg_20;
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_40 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_avg_40 = Integer.valueOf(statLocation.substring(0, statLocation.indexOf(","))) + mChatStat.time_avg_40;
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_60 += Integer.valueOf(statLocation.substring(0, statLocation.indexOf(",")));
                        statLocation = statLocation.substring(statLocation.indexOf(",") + 1);
                        mChatStat.time_avg_60 = Integer.valueOf(statLocation) + mChatStat.time_avg_60;
                        json.put("voice_send", mChatStat.all + "," + mChatStat.success + ","
                                + mChatStat.timeout + "," + mChatStat.time_5 + "," + mChatStat.time_avg_5 + "," + mChatStat.time_10 + ","
                                + mChatStat.time_avg_10 + "," + mChatStat.time_20 + "," + mChatStat.time_avg_20 + "," + mChatStat.time_40
                                + "," + mChatStat.time_avg_40 + "," + mChatStat.time_60 + "," + mChatStat.time_avg_60);
                        statFlag = true;
                    }
                }
            }
            if (!statFlag) {
                JSONObject locationObject = new JSONObject();
                locationObject.put(CloudBridgeUtil.KEY_NAME_EID, eid);
                locationObject.put(CloudBridgeUtil.KEY_NAME_TIMESTAMP, format.format(today).toString());
                locationObject.put("voice_recv", "0,0");
                locationObject.put("location", "0,0,0,0,0,0,0,0,0,0,0,0,0");
                locationObject.put("voice_send", mChatStat.all + "," + mChatStat.success + ","
                        + mChatStat.timeout + "," + mChatStat.time_5 + "," + mChatStat.time_avg_5 + "," + mChatStat.time_10 + ","
                        + mChatStat.time_avg_10 + "," + mChatStat.time_20 + "," + mChatStat.time_avg_20 + "," + mChatStat.time_40
                        + "," + mChatStat.time_avg_40 + "," + mChatStat.time_60 + "," + mChatStat.time_avg_60);
                statArray.add(locationObject);
            }
            mApp.setValue(Const.SHARE_PREF_CLOUDBRIDGE_STATE + mApp.getCurUser().getEid(), statArray.toString());
        }
    }

    private void e2c4Chat(ChatMsgEntity entity) {
        MyMsgData e2c = new MyMsgData();
        e2c.setTimeout(60 * 1000);
        e2c.setFinalTimeout(60 * 1000);
        e2c.setNeedNetTimeout(true);
        e2c.setCallback(this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, entity.getmFamilyId());
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE);
        key.append(entity.getmFamilyId());
        key.append(CloudBridgeUtil.E2C_SPLIT_MEG);
        key.append(CloudBridgeUtil.E2C_SERVER_SET_TIME);
        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key.toString());
        JSONObject chat = new JSONObject();
        chat.put(CloudBridgeUtil.E2C_PL_KEY_TYPE, CloudBridgeUtil.E2C_PL_KEY_TYPE_VOICE);
        chat.put(CloudBridgeUtil.E2C_PL_KEY_EID, entity.getmSrcId());
        chat.put(CloudBridgeUtil.E2C_PL_KEY_DURATION, entity.getmDuration());
        try {
            chat.put(CloudBridgeUtil.E2C_PL_KEY_CONTENT, StrUtil.encodeBase64File(entity.getmAudioPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        pl.put(CloudBridgeUtil.KEY_NAME_MAP_GET_KEY, chat);
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        String familyid = entity.getmFamilyId();

        StringBuffer sms = new StringBuffer();
        sms.append("<" + sn + "," + familyid + "," + "G" + CloudBridgeUtil.SUB_ACTION_VALUE_NAME_SEND_VOICE
                + ",1@" + CloudBridgeUtil.PREFIX_GP_E2C_MESSAGE
                + familyid + CloudBridgeUtil.E2C_SPLIT_MEG
                + CloudBridgeUtil.E2C_SERVER_SET_TIME + ">");
        pl.put(CloudBridgeUtil.KEY_NAME_SMS, sms.toString());
        LogUtil.d("Sms = " + sms.toString());

        entity.setmSendStartTime(System.currentTimeMillis());
        mSending.put(sn, entity);
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_E2C4_DEVICE_LONGTIME_MSG_REQ, sn, mApp.getToken(), pl));
        e2c.getReqMsg().put(CloudBridgeUtil.KEY_NAME_VERSION, mCurWatch.getDeviceProtocolVersion());
        mApp.getNetService().sendNetMsg(e2c);
    }

    private class ChatReceiver extends BroadcastReceiver {

        public void registerReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            //filter.addAction(Const.ACTION_PROCESSED_NOTIFY_OK);
            filter.addAction(Const.ACTION_RESEND_CHAT);
            filter.addAction(Const.ACTION_ADAPTER_DATA_CHANGE);
            filter.addAction(Const.ACTION_PLAY_RECORD_COMPLETION);
            filter.addAction(Const.ACTION_STOP_VOICE_ANIMATION);
            filter.addAction(Const.ACTION_GET_CONTACT_SUCCESS);
            filter.addAction(Const.ACTION_CLEAR_MESSAGE);
            filter.addAction(Const.ACTION_RECEIVE_GET_DEVICE_INFO);
            filter.addAction(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY);
            filter.addAction(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE);
            filter.addAction(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE);
            filter.addAction(Const.ACTION_REFRESH_WATCH_TITLE);
            filter.addAction(Const.ACTION_RECEIVE_SILENCETIME_UPDATE);
            context.registerReceiver(this, filter);
        }

        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_PROCESSED_NOTIFY_OK) || action.equals(Constants.ACTION_RECEIVE_PRIVATE_MESSAGE_NOTIFY)) {
                receiveMessage();
            } else if (action.equals(Const.ACTION_RESEND_CHAT)) {
                int position = intent.getIntExtra("position", -1);
                resendMessage(position);
            } else if (action.equals(Const.ACTION_ADAPTER_DATA_CHANGE)) {
                getChatMsgFromDB();
            } else if (action.equals(Const.ACTION_PLAY_RECORD_COMPLETION)) {
                int position = intent.getIntExtra("position", -1);
                playRecordComplete(position);
            } else if (action.equals(Const.ACTION_STOP_VOICE_ANIMATION)) {
                stopVoiceAnimation();
            } else if (action.equals(Const.ACTION_GET_CONTACT_SUCCESS)) {
                getContactSuccess();
            } else if (action.equals(Const.ACTION_CLEAR_MESSAGE)) {
                clearMessage();
            } else if (action.equals(Const.ACTION_RECEIVE_GET_DEVICE_INFO)) {
                updateWatchInfo();
            } else if (action.equals(Const.ACTION_DOWNLOAD_HEADIMG_OK)) {
                updateHeadImage();
            } else if (action.equals(Const.ACTION_DEVICE_OFFLINE_STATE_UPDATE) ||
                    action.equals(Const.ACTION_RECEIVE_WATCH_STATE_CHANGE) ||
                    action.equals(Const.ACTION_REFRESH_WATCH_TITLE) ||
                    action.equals(Const.ACTION_RECEIVE_SILENCETIME_UPDATE)) {
                updateWatchState();
            }
        }
    }

    private void receiveMessage() {
        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        mApp.setHasNewPrivateMsg(mUserChatGid, false);
        getChatMsgFromDB();
    }

    private void resendMessage(int position) {
        if (position != -1) {
            ChatMsgEntity entity = mChatMsgList.get(position);
            String key = entity.getmDate();
            mChatMsgList.remove(position);
            mChatAdater.notifyItemRemoved(position);
            entity.setmDate(TimeUtil.getTimeStampLocal());
            entity.setmSended(ChatMsgEntity.CHAT_SEND_STATE_RETRYING);
            mChatMsgList.add(entity);
            mChatAdater.notifyItemInserted(mChatMsgList.indexOf(entity));
            mChatList.scrollToPosition(mChatMsgList.size() - 1);
            ChatHisDao.getInstance(mApp).updateChatMsg(mUserChatGid, entity, entity.getmDate());
            if (entity.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT) {
                sendTextMsgE2C(entity);
            } else if (entity.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI) {
                sendTextMsgE2C(entity);
            } else if (entity.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO) {
                uploadPhoto(entity);
            } else if (entity.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO) {
                uploadVideo(entity);
            } else {
                e2c4Chat(entity);
            }
        }
    }

    private void playRecordComplete(int position) {
        if (position != -1 && mChatMsgList.size() > position) {
            ChatMsgEntity entity = mChatMsgList.get(position);
            if (entity != null && entity.getmPlayAnimation() != null) {
                mChatAdater.handleRecordClick(entity);
            }
        }
    }

    private void stopVoiceAnimation() {
        mChatAdater.stopPlayAnimation();
    }

    private void changeAudioMode() {
        if (mApp.getmUseCall()) {
            mInsertMode.setVisibility(View.VISIBLE);
        } else {
            mInsertMode.setVisibility(View.INVISIBLE);
        }
    }

    private void getContactSuccess() {
        mUserChatGid = mApp.getWatchPrivateGid(mWatchEid);
        getChatMsgFromDB();
    }

    private void clearMessage() {
        if (mChatMsgList.size() == 0) {
            mNoChatLy.setVisibility(View.VISIBLE);
        } else {
            mNoChatLy.setVisibility(View.GONE);
        }
    }


    private void getChatMsgFromDB() {
        if (mLoadPrivateMsgTask != null) {
            mLoadPrivateMsgTask.cancel(true);
            mLoadPrivateMsgTask = null;
        }
        mLoadPrivateMsgTask = new AsyncTask<Void, Void, ArrayList<ChatMsgEntity>>() {

            @Override
            protected ArrayList<ChatMsgEntity> doInBackground(Void... params) {
                mDeleteChatMsgList.clear();
                ArrayList<ChatMsgEntity> chatList = new ArrayList<ChatMsgEntity>();
                ChatHisDao.getInstance(mApp.getApplicationContext()).readAllChatFromFamily(mUserChatGid, chatList, mDeleteChatMsgList);
                return chatList;
            }

            @Override
            protected void onPostExecute(ArrayList<ChatMsgEntity> chatList) {
                super.onPostExecute(chatList);
                mChatMsgList.clear();
                mChatMsgList.addAll(chatList);
                mChatAdater.notifyDataSetChanged();
                mChatList.scrollToPosition(mChatMsgList.size() - 1);
                if (mChatMsgList.size() == 0) {
                    mNoChatLy.setVisibility(View.VISIBLE);
                } else {
                    mNoChatLy.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    private void showSilenceTimeToast(int resId) {
        SilenceTime tmp = mApp.getEffectingAdvanceSilenceTime();
        String time = tmp.starthour + ":" + tmp.startmin + "-" + tmp.endhour + ":" + tmp.endmin;
        String info = getString(resId, time);
        ToastUtil.show(PrivateMessageActivity.this, info);
    }

    private void sendTextMessage(String text, int type) {
        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        ChatMsgEntity entity = getSendEntity(text, type);
        mChatMsgList.add(entity);
        mChatAdater.notifyItemInserted(mChatMsgList.indexOf(entity));
        mChatList.scrollToPosition(mChatMsgList.size() - 1);
        ChatHisDao.getInstance(mApp).addChatMsg(mUserChatGid, entity);
        sendTextMsgE2C(entity);
        sendPrivateMessageNotify();
    }

    private ChatMsgEntity getSendEntity(String text, int type) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setmWatchId(mCurWatch.getWatchId());
        entity.setmDuration(mRecordT);
        entity.setmAudioPath(text);
        entity.setmSrcId(mApp.getCurUser().getEid());
        entity.setmDate(TimeUtil.getTimeStampLocal());
        entity.setmIsFrom(false);
        entity.setmFamilyId(mUserChatGid);
        entity.setmSended(ChatMsgEntity.CHAT_SEND_STATE_SENDING);
        entity.setmTryTime(1);
        entity.setmType(type);
        entity.setmContent(text);
        return entity;
    }

    private void updateWatchInfo() {
        mWatchName.setText(mCurWatch.getNickname());
    }

    private void updateHeadImage() {
        ImageUtil.setMaskImage(mWathcHead, R.drawable.head_1, mApp.getHeadDrawableByFile(mApp.getResources(),
                mCurWatch.getHeadPath(), mCurWatch.getEid(), R.drawable.small_default_head));
    }

    private void openPhotoSelectDialog() {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getText(R.string.head_edit_camera).toString());
        itemList.add(getText(R.string.head_edit_pics).toString());
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(PrivateMessageActivity.this, itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        if (position == 1) {
                            if (ActivityCompat.checkSelfPermission(PrivateMessageActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                                startCameraCapture();
                            } else {
                                ActivityCompat.requestPermissions(PrivateMessageActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_RESULT_CAMERA);
                            }
                        } else {
                            startAlbumCapture();
                        }
                    }
                });
        dlg.show();
    }

    private void startCameraCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageFile = new File(myApp.getIconCacheDir(), System.currentTimeMillis() + ".jpg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Uri contentUri = FileProvider.getUriForFile(myApp, myApp.getPackageName() + ".xun.fileprovider", mImageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));
        }
        startActivityForResult(intent, GET_IMAGE_FROM_CAMERA);
    }

    private void startAlbumCapture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_IMAGE_FROM_ALBUM);
    }

    private void startVideoCapture() {
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
//        startActivityForResult(intent, GET_VIDEO_FROM_CAMERA);

        Intent it = new Intent(PrivateMessageActivity.this, VideoRecordNewActivity.class);
        startActivityForResult(it, GET_VIDEO_FROM_RECORD);
    }

    private void handleCameraImageResult() {
        if (mImageFile != null && mImageFile.exists()) {
            sendPhoto(mImageFile.getAbsolutePath());
        }
    }

    private void handleAlbumImageResult(Intent data) {
        try {
            if (data != null) {
                String imagePath = UriUtil.getPath(PrivateMessageActivity.this, data.getData());
                sendPhoto(imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCameraVideoResult(Intent data) {
        Uri uri = data.getData();
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
            File previewFile = new File(filePath + ChatRecyclerViewAdaper.VIDEO_PREVIEW_SUFFIX);
            ImageUtil.saveBitmap(bitmap, previewFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(previewFile)));
            cursor.close();
            sendVideo(filePath);
        }
    }

    private void sendPhoto(String filePath) {
        File file = new File(filePath);
        if (file != null) {
            mApp.sdcardLog("PrivateMessageActivity" + "source file size: " + file.length());
            try {
                File destFile = ImageUtil.compressImage(file, 480, 480, Bitmap.CompressFormat.JPEG, 75,
                        mApp.getIconCacheDir().getAbsolutePath() + "/" + file.getName() + "_compress.jpg");
                mApp.sdcardLog("PrivateMessageActivity" + "destFile size: " + destFile.length());
                filePath = destFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        ChatMsgEntity entity = getSendEntity(filePath, ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO);
        mChatMsgList.add(entity);
        mChatAdater.notifyItemInserted(mChatMsgList.indexOf(entity));
        mChatList.scrollToPosition(mChatMsgList.size() - 1);
        ChatHisDao.getInstance(mApp).addChatMsg(mUserChatGid, entity);
        uploadPhoto(entity);
        sendPrivateMessageNotify();
    }

    private void uploadPhoto(final ChatMsgEntity entity) {
        CustomFileUtils.getInstance(mApp).uploadFile(mWatchEid, mUserChatGid, null, entity.getmAudioPath(), new CustomFileUtils.UploadListener() {
            @Override
            public void uploadSuccess(String result) {
                entity.setmContent(result);
                sendNotice(entity, CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO);
            }

            @Override
            public void uploadFail(String error) {
                sendMessageFail(entity);
            }
        });
    }

    private void sendNotice(ChatMsgEntity entity, String messageType) {
        int sn = Long.valueOf(TimeUtil.getTimeStampLocal()).intValue();
        mSending.put(sn, entity);
        if (mApp.getNetService() != null) {
            mApp.getNetService().uploadNotice(sn, mApp.getCurUser().getEid(), mUserChatGid, messageType, entity.getmContent(), PrivateMessageActivity.this);
        }
    }

    private void handleRecordVideoResult(Intent data) {
        String path = data.getStringExtra("path");
        String type = data.getStringExtra("type");
        if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_PHOTO)) {
            sendPhoto(path);
        } else if (type.equals(CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO)) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
            File previewFile = new File(path + ChatRecyclerViewAdaper.VIDEO_PREVIEW_SUFFIX);
            ImageUtil.saveBitmap(bitmap, previewFile);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(previewFile)));
            sendVideo(path);
        }

    }

    private void sendVideo(String filePath) {
        mNoChatLy.setVisibility(View.GONE);
        mChatList.setVisibility(View.VISIBLE);
        ChatMsgEntity entity = getSendEntity(filePath, ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO);
        mChatMsgList.add(entity);
        mChatAdater.notifyItemInserted(mChatMsgList.indexOf(entity));
        mChatList.scrollToPosition(mChatMsgList.size() - 1);
        ChatHisDao.getInstance(mApp).addChatMsg(mUserChatGid, entity);
        uploadVideo(entity);
        sendPrivateMessageNotify();
    }

    private void uploadVideo(final ChatMsgEntity entity) {
        CustomFileUtils.getInstance(mApp).uploadFile(mWatchEid, mUserChatGid, entity.getmAudioPath(),
                entity.getmAudioPath() + ChatRecyclerViewAdaper.VIDEO_PREVIEW_SUFFIX, new CustomFileUtils.UploadListener() {
                    @Override
                    public void uploadSuccess(String result) {
                        entity.setmContent(result);
                        sendNotice(entity, CloudBridgeUtil.OFFLINE_MSG_TYPE_VIDEO);
                    }

                    @Override
                    public void uploadFail(String error) {
                        sendMessageFail(entity);
                    }
                });
    }

    private void sendPrivateMessageNotify() {
        Intent intent = new Intent(Constants.ACTION_PRIVATE_SEND_MESSAGE_NOTIFY);
        sendBroadcast(intent);
    }

    private void updateWatchState() {
        String tips = null;
        Integer offlineState = mApp.getmWatchOfflineState().get(mWatchEid);
        if (null != offlineState && 1 == offlineState) {
            tips = getString(R.string.watch_offline_state);
        } else if (mApp.isInSilenceTime(mWatchEid) > 0) {
            tips = getString(R.string.watch_state_silence);
        }

        int superPowerSaving = mApp.getIntValue(mCurWatch.getEid() + Constants.SHARE_PREF_SUPER_POWER_SAVING,0);
        if(1 == superPowerSaving){
            tips = getString(R.string.super_power_saving_on_tips1);
        }

        if (!TextUtils.isEmpty(tips)) {
            watchStateTips.setVisibility(View.VISIBLE);
            watchStateTips.setText(tips);
        } else {
            watchStateTips.setVisibility(View.GONE);
        }
    }
}

