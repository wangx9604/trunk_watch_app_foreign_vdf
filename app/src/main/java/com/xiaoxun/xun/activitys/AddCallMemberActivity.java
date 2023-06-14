package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.UserRelationDAO;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.BitmapUtilities;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomFileUtils;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.FileCacheUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.PhotoGetUtil;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.yalantis.ucrop.UCrop;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by liutianxiang on 2016/3/9.
 */
public class AddCallMemberActivity extends NormalActivity implements View.OnClickListener, MsgCallback {
    private ImageButton mBtnBack;
    private RelativeLayout mGroupNickLayout;
    private RelativeLayout mPhonenumLayout;
    private RelativeLayout mPhoneSubnumLayout;
    private RelativeLayout mLayoutAvatar;
    private Button mLayoutNextview;
    private TextView mTvNick;
    private TextView mTvPhonenum;
    private TextView mTvPhoneSubnum;
    private ImageView mIvAvatar;

    private WatchData watch;
    private String watchid;
    private String nickname;
    private String phonenum;
    private String phoneSubnum;
    private String memberEid;
    private String memberGid;
    private int attri = -1;
    private String contactId = null;//联系人条目的id,新增的用时间戳
    private String ring;
    private String avatar;
    private int weight = 0;

    private ImageButton mBtnSave;
    private LoadingDialog loadingdlg;
    private ArrayList<PhoneNumber> mBindWhiteList;
    private JSONArray plA;
    private File mRecordFile = null;
    private String mRingPath = null;
    private String mRingE2cKey = null;
    private boolean isAdd;//
    private String oldPhonenum;
    private String oldPhoneSubNum;
    private String oldContent;
    private String oldNickname;
    private int oldAttri;
    private int contactsType = -1;
    private ArrayList<Integer> attriList = new ArrayList<>();
    private static final int ACTIVITY_RESULT_CODE_PICK_CONTACT_MAIN = 2;
    private static final int ACTIVITY_RESULT_CODE_PICK_CONTACT_SUB = 3;
    private boolean isSavingFalg = false; //正在保存的标识
    private boolean isBinded;

    final ArrayList<String> itemList = new ArrayList<>();
    private File cameraTemp = null;
    private File cropTemp = null;
    private Bitmap oldHeadBitmap;

    private Uri photoUri;
    private File mSaveTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        watchid = getIntent().getExtras().getString(Const.KEY_WATCH_ID);
        //是否是edit
        oldNickname = nickname = getIntent().getExtras().getString("nickname");
        oldPhonenum = phonenum = getIntent().getExtras().getString("phonenum");
        oldPhoneSubNum = phoneSubnum = getIntent().getExtras().getString("sub_number");
        memberEid = getIntent().getExtras().getString("eid");
        memberGid = getIntent().getExtras().getString("gid");
        ring = getIntent().getExtras().getString("ring");
        avatar = getIntent().getExtras().getString("avatar");
        contactsType = getIntent().getExtras().getInt(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, 1);
        weight = getIntent().getExtras().getInt(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, 0);
        contactId = getIntent().getExtras().getString(CloudBridgeUtil.KEY_NAME_CONTACT_ID);
        oldAttri = attri = getIntent().getExtras().getInt(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI, -1);
        if (contactId == null) {
            contactId = TimeUtil.getTimestampCHN();
        }
        if (attri == -1 && nickname != null) {//说明是旧的家庭成员，但是联系人中没有，这里反向匹配一个attri
            attri = getAttriByNameString(nickname);
        }
        isBinded = getIntent().getExtras().getBoolean(Const.SET_CONTACT_ISBIND);
        if (TextUtils.isEmpty(phonenum)) {
            isAdd = true;
            if (isBinded)
                phonenum = myApp.getCurUser().getCellNum();
        }
        oldContent = contactId + nickname + phonenum + phoneSubnum + memberEid + ring + avatar;

        watch = myApp.getCurUser().queryWatchDataByEid(watchid);
        setContentView(R.layout.activity_add_call_member);
        mBindWhiteList = new ArrayList<>();
        getContactListFromLocal();
        plA = new JSONArray();

        int size = mBindWhiteList.size();
        for (int i = 0; i < size; i++) {
            PhoneNumber phoneNumber = mBindWhiteList.get(i);
            if (phoneNumber.contactType != 2) {
                attriList.add(phoneNumber.attri);
            }
        }
        initViews();
        loadingdlg = new LoadingDialog(this, R.style.Theme_DataSheet, new LoadingDialog.OnConfirmClickListener() {
            @Override
            public void confirmClick() {

            }
        });
        loadingdlg.hideReloadView();
    }

    private void getContactListFromLocal() {
        String jsonStr = myApp.getStringValue(watch.getEid() + Const.SHARE_PREF_DEVICE_CONTACT_KEY, null);
        mBindWhiteList = CloudBridgeUtil.parseContactListFromJsonStr(jsonStr);
    }

    private void initViews() {
        findViewById(R.id.iv_title_cancel).setVisibility(View.GONE);
        mBtnBack = findViewById(R.id.iv_title_back);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnBack.setOnClickListener(this);
        mBtnBack.setBackground(this.getResources().getDrawable(R.drawable.btn_cancel_selector));
        mBtnSave = findViewById(R.id.iv_title_menu);
        mBtnSave.setVisibility(View.VISIBLE);
        mBtnSave.setOnClickListener(this);
        mBtnSave.setBackground(this.getResources().getDrawable(R.drawable.btn_confirm_selector));

        mGroupNickLayout = findViewById(R.id.layout_group_nickname);
        mGroupNickLayout.setOnClickListener(this);
        mPhonenumLayout = findViewById(R.id.layout_phone_number);
        mPhonenumLayout.setOnClickListener(this);
        mPhoneSubnumLayout = findViewById(R.id.layout_phone_subnumber);
        mPhoneSubnumLayout.setOnClickListener(this);
        mLayoutAvatar = findViewById(R.id.layout_member_avatar);
        mLayoutAvatar.setOnClickListener(this);
        if (watch.isDevice102())
            mLayoutAvatar.setVisibility(View.GONE);
        mLayoutNextview = findViewById(R.id.btn_next_step);
        mLayoutNextview.setOnClickListener(this);

        mTvNick = findViewById(R.id.tv_group_nickname);
        mTvNick.setText(nickname);
        mTvPhonenum = findViewById(R.id.tv_phone_number);
        mTvPhonenum.setText(phonenum);
        mTvPhoneSubnum = findViewById(R.id.tv_phone_subnumber);
        mTvPhoneSubnum.setText(phoneSubnum);

        if (!isBinded) {
            if (nickname != null) {
                ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.edit_group_member));
            } else {
                ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.add_group_member));
            }
            mLayoutNextview.setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.perfect_group_member));
            mLayoutNextview.setVisibility(View.VISIBLE);
            mBtnBack.setVisibility(View.GONE);
            mBtnSave.setVisibility(View.GONE);
        }

        mIvAvatar = findViewById(R.id.iv_member_avatar);
        mIvAvatar.setOnClickListener(this);
        if (avatar != null) {
            oldHeadBitmap = new ImageDownloadHelper(AddCallMemberActivity.this).downloadImage(avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                @Override
                public void onImageDownload(String url, Bitmap bitmap) {
                    oldHeadBitmap = bitmap;
                    Drawable headDrawable = new BitmapDrawable(getResources(), bitmap);
                    ImageUtil.setMaskImage(mIvAvatar, R.drawable.head_2, headDrawable);
                }
            });
            if (oldHeadBitmap != null) {
                Drawable headDrawable = new BitmapDrawable(getResources(), oldHeadBitmap);
                ImageUtil.setMaskImage(mIvAvatar, R.drawable.head_2, headDrawable);
            }
        } else {
            ImageUtil.setMaskImage(mIvAvatar, R.drawable.mask, ((ImibabyApp) AddCallMemberActivity.this.getApplicationContext()).getHeadDrawableByFile(AddCallMemberActivity.this.getResources(), Integer.toString(attri), memberEid, R.drawable.relation_custom));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkQuit() {
        String curContent = contactId + nickname + phonenum + phoneSubnum + memberEid + ring + avatar;
        if (!curContent.equals(oldContent)) {
            Dialog dlg = DialogUtil.CustomNormalDialog(AddCallMemberActivity.this,
                    getString(R.string.prompt),
                    getString(R.string.prompt_not_saved),
                    new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    },
                    getText(R.string.quit_without_save).toString(),
                    new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            doSaveEdit();
                        }
                    },
                    getText(R.string.save_edit).toString());
            dlg.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        checkQuit();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnBack) {
            checkQuit();
        } else if (v == mGroupNickLayout) {
            //选择关系
            Intent modifyIntent = new Intent(AddCallMemberActivity.this, WhitePhoneListRelationshipSet.class);
            modifyIntent.putExtra("attri", attri);
            modifyIntent.putExtra("nickname", nickname);
            startActivityForResult(modifyIntent, 1);
        } else if (v == mPhonenumLayout) {
            Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithSelect(AddCallMemberActivity.this,
                    18,
                    InputType.TYPE_CLASS_PHONE,
                    getText(R.string.phone_num).toString(),
                    null,
                    phonenum, getString(R.string.input_valid_num), new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            phonenum = oldPhonenum;
                            mTvPhonenum.setText(phonenum);
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text0) {
                            String text;
                            text = text0;
                            if (!StrUtil.isMobileNumber(text, 2)) {
                                ToastUtil.showMyToast(AddCallMemberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
                                phonenum = "";
                            } else {
                                phonenum = StrUtil.formatPhoneNumber(text);
                            }
                            mTvPhonenum.setText(phonenum);
                        }
                    },
                    getText(R.string.confirm).toString(), getResources().getDrawable(R.drawable.img_pick_contact_selector), new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            checkReadContactsPermission(ACTIVITY_RESULT_CODE_PICK_CONTACT_MAIN);
                        }
                    });
            dlg.show();
        } else if (v == mPhoneSubnumLayout) {
            Dialog dlg = CustomSelectDialogUtil.CustomInputDialogWithSelect(AddCallMemberActivity.this,
                    18,
                    InputType.TYPE_CLASS_PHONE,
                    getText(R.string.phone_subnum).toString(),
                    null,
                    phoneSubnum, getString(R.string.input_valid_num), new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            phoneSubnum = oldPhoneSubNum;
                            mTvPhoneSubnum.setText(phoneSubnum);
                        }
                    },
                    getText(R.string.cancel).toString(),
                    new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text0) {
                            String text;
                            text = text0;
                            if (text != null && text.length() > 0 && !StrUtil.isMobileNumber(text, 2)) {
                                ToastUtil.showMyToast(AddCallMemberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
                                phoneSubnum = "";
                            } else {
                                phoneSubnum = StrUtil.formatPhoneNumber(text);
                            }
                            mTvPhoneSubnum.setText(phoneSubnum);

                        }
                    },
                    getText(R.string.confirm).toString(), getResources().getDrawable(R.drawable.img_pick_contact_selector), new CustomSelectDialogUtil.CustomDialogListener() {
                        @Override
                        public void onClick(View v, String text) {
                            checkReadContactsPermission(ACTIVITY_RESULT_CODE_PICK_CONTACT_SUB);
                        }
                    });
            dlg.show();
        } else if (v == mLayoutAvatar || v == mIvAvatar) {
            openAvatarEditDialog();
        } else if (v == mBtnSave || v == mLayoutNextview) {
            String curContent = contactId + nickname + phonenum + phoneSubnum + memberEid + ring + avatar;
            if (!isBinded && curContent.equals(oldContent)) {
                AddCallMemberActivity.this.finish();
            } else {
                doSaveEdit();
            }
        }
    }

    private void doSaveEdit() {
        if (nickname == null || nickname.length() < 1) {
            ToastUtil.showMyToast(AddCallMemberActivity.this, getText(R.string.wrong_nickname).toString(), Toast.LENGTH_SHORT);
        } else if (phonenum == null
                || !StrUtil.isMobileNumber(phonenum, 2)) {
            ToastUtil.showMyToast(AddCallMemberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
        } else if (phoneSubnum != null && phoneSubnum.length() > 0 && !StrUtil.isMobileNumber(phoneSubnum, 2)) {
            ToastUtil.showMyToast(AddCallMemberActivity.this, getText(R.string.format_error).toString(), Toast.LENGTH_SHORT);
        } else if (isSavingFalg) {
            if (!loadingdlg.isShowing()) {
                loadingdlg.changeStatus(1, getString(R.string.saving));
                loadingdlg.show();
            }
        } else {
            if (!loadingdlg.isShowing()) {
                loadingdlg.changeStatus(1, getString(R.string.saving));
                loadingdlg.show();
            }
            //check 是否需要tts，条件是attri >1000, nickname变化
            if (watch.isNeedTTS() && attri >= 1000
                    && ((oldNickname != null && !oldNickname.equals(nickname))//修改
                    || (oldNickname == null)//新增
                    || (oldAttri < 24)//原来是固定关系，现在是自定义，昵称没有变,也要合成
                    || (oldPhonenum == null))) //原来群组联系人没有号码
            {
                attri = getNextCustomAttri();
                sendTTS(watchid, phonenum, nickname);
            } else {
                //直接保存
                if (attri < 24) {
                    ring = null;
                }
                doSendEditCallMemberReq();
            }
            isSavingFalg = true;
        }
    }

    private int getNextCustomAttri() {
        int attri = 1000;
        while (true) {
            if (!attriList.contains(attri)) {
                break;
            } else {
                attri++;
            }
        }
        return attri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_RESULT_READCONTACT:
                if (grantResults != null && grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, reqCode);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                        ToastUtil.showMyToast(this, getString(R.string.contact_has_no_phonenumber), Toast.LENGTH_SHORT);
                }
                break;
            case PERMISSION_RESULT_CAMERA:
                if (grantResults != null && grantResults.length > 0) {
                    if (grantResults[0] == PERMISSION_GRANTED) {
                        mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                        photoUri = PhotoGetUtil.startCameraCapture(myApp, this, mSaveTemp);
                    } else {
                        Toast.makeText(AddCallMemberActivity.this, getString(R.string.camera_premission_tips), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void openAvatarEditDialog() {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getText(R.string.head_edit_camera).toString());
        itemList.add(getText(R.string.head_edit_pics).toString());
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(AddCallMemberActivity.this, getText(R.string.edit_head).toString(), itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        if (position == 1) {
                            if (ActivityCompat.checkSelfPermission(AddCallMemberActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                                photoUri = PhotoGetUtil.startCameraCapture(myApp, AddCallMemberActivity.this, mSaveTemp);
                            } else {
                                ActivityCompat.requestPermissions(AddCallMemberActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_RESULT_CAMERA);
                            }
                        } else {
                            PhotoGetUtil.startPickHead(AddCallMemberActivity.this);
                        }
                    }
                },
                -1, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                }, getText(R.string.cancel).toString());
        dlg.show();
    }

    private void setPicToView(Intent mData) {
        Uri resultUri = UCrop.getOutput(mData);
        Bitmap photo = null;
        File mCropTemp = mSaveTemp;
        try {
            photo =  BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
            if (photo.getWidth() > 320 || photo.getHeight() > 320) {
                photo = BitmapUtilities.getBitmapThumbnail(mSaveTemp.getPath(), 320, 320);
            }
            FileOutputStream fos = new FileOutputStream(mCropTemp);
            photo.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            if (mCropTemp.length() > 45 * 1024)
                photo.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(mCropTemp));
            if (mCropTemp.length() > 45 * 1024)
                photo.compress(Bitmap.CompressFormat.JPEG, 10, new FileOutputStream(mCropTemp));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
        } catch (Exception e1) {
            e1.printStackTrace();
            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
        }

        Drawable drawable = new BitmapDrawable(getApplicationContext().getResources(), photo);
        final Bitmap tempPhoto = photo;
        try {
            final byte[] bitmapArray = StrUtil.getBytesFromFile(mCropTemp);
            ImageUtil.setMaskImage(mIvAvatar, R.drawable.head_2, drawable);
            if (loadingdlg != null) {
                loadingdlg.changeStatus(1, getString(R.string.saving));
                loadingdlg.show();
            }
            CustomFileUtils.getInstance(myApp).uploadData(bitmapArray, watch.getEid(), phonenum, new CustomFileUtils.UploadListener() {
                @Override
                public void uploadSuccess(String result) {
                    avatar = result;
                    new FileCacheUtil(AddCallMemberActivity.this).addBitmapToFile(result, tempPhoto);
                    if (loadingdlg != null && loadingdlg.isShowing())
                        loadingdlg.dismiss();
                }

                @Override
                public void uploadFail(String error) {
                    ToastUtil.show(AddCallMemberActivity.this, AddCallMemberActivity.this.getString(R.string.set_error));
                    if (oldHeadBitmap != null) {
                        ImageUtil.setMaskImage(mIvAvatar, R.drawable.head_2, new BitmapDrawable(oldHeadBitmap));
                    } else {
                        ImageUtil.setMaskImage(mIvAvatar, R.drawable.mask, ((ImibabyApp) AddCallMemberActivity.this.getApplicationContext()).getHeadDrawableByFile(AddCallMemberActivity.this.getResources(), Integer.toString(attri), memberEid, R.drawable.relation_custom));
                    }
                    if (loadingdlg != null && loadingdlg.isShowing())
                        loadingdlg.dismiss();
                }
            });
        } catch (Exception e) {

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {//选择关系结果
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                attri = data.getIntExtra("attri", -1);
                if (data.getStringExtra("nickname") != null) {
                    nickname = data.getStringExtra("nickname");
                } else {
                    nickname = getNicknameByAttri(attri);
                }
                mTvNick.setText(nickname);
                // 如果没有自定义头像，设置关系后根据关系显示头像
                if (avatar == null) {
                    ImageUtil.setMaskImage(mIvAvatar, R.drawable.mask, ((ImibabyApp) AddCallMemberActivity.this.getApplicationContext()).getHeadDrawableByFile(AddCallMemberActivity.this.getResources(), Integer.toString(attri), memberEid, R.drawable.relation_custom));
                }
            }

        } else if (requestCode == ACTIVITY_RESULT_CODE_PICK_CONTACT_MAIN) {
            if (resultCode == RESULT_OK) {
                itemList.clear();
                if (data != null) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        int idColumn = c.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactId = c.getString(idColumn);
                        Cursor cursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (number != null && number.length() > 0) {
                                    number = StrUtil.formatPhoneNumber(number);
                                    if (!itemList.contains(number)) {
                                        itemList.add(number);
                                    }
                                }
                                cursor.moveToNext();
                            }
                        }
                        if (cursor != null) cursor.close();
                    }
                }
                int count = itemList.size();
                if (count == 0) {
                    ToastUtil.show(AddCallMemberActivity.this, getText(R.string.contact_has_no_phonenumber).toString());
                    mPhonenumLayout.callOnClick();
                } else if (count == 1) {
                    phonenum = itemList.get(0);
                    mPhonenumLayout.callOnClick();
                } else if (count > 1) {
                    Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(AddCallMemberActivity.this, itemList,
                            new CustomSelectDialogUtil.AdapterItemClickListener() {
                                @Override
                                public void onClick(View v, int position) {
                                    phonenum = itemList.get(position - 1);
                                    mPhonenumLayout.callOnClick();
                                }
                            });
                    dlg.show();
                }
            } else {
                mPhoneSubnumLayout.callOnClick();
            }
        } else if (requestCode == ACTIVITY_RESULT_CODE_PICK_CONTACT_SUB) {
            if (resultCode == RESULT_OK) {
                itemList.clear();
                if (data != null) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        int idColumn = c.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactId = c.getString(idColumn);
                        Cursor cursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (number != null && number.length() > 0) {
                                    number = StrUtil.formatPhoneNumber(number);
                                    if (!itemList.contains(number)) {
                                        itemList.add(number);
                                    }
                                }
                                cursor.moveToNext();
                            }
                        }
                        if (cursor != null) cursor.close();
                    }
                }
                int count = itemList.size();
                if (count == 0) {
                    ToastUtil.show(AddCallMemberActivity.this, getText(R.string.contact_has_no_phonenumber).toString());
                    mPhoneSubnumLayout.callOnClick();
                } else if (count == 1) {
                    phoneSubnum = itemList.get(0);
                    mPhoneSubnumLayout.callOnClick();
                } else if (count > 1) {
                    Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithNoIndicator(AddCallMemberActivity.this, itemList,
                            new CustomSelectDialogUtil.AdapterItemClickListener() {
                                @Override
                                public void onClick(View v, int position) {
                                    phoneSubnum = itemList.get(position - 1);
                                    mPhoneSubnumLayout.callOnClick();
                                }
                            });
                    dlg.show();
                }
            } else {
                mPhoneSubnumLayout.callOnClick();
            }
        } else if (requestCode == PhotoGetUtil.GET_IMAGE_FROM_ALBUM) {
            try {
                if (data != null) {
                    mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                    PhotoGetUtil.startPhotoZoom(myApp, data.getData(), this, mSaveTemp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PhotoGetUtil.GET_IMAGE_FROM_CAMERA) {
            try {
                mSaveTemp = PhotoGetUtil.getDestinationFile(myApp);
                PhotoGetUtil.startPhotoZoom(myApp, photoUri, this, mSaveTemp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            setPicToView(data);
        }
    }

    void updateEditWhiteList(String updateTS) {
        boolean isNewWhite = true;
        if (mBindWhiteList.size() > 0) {
            for (PhoneNumber tmpphone : mBindWhiteList) {

                if (tmpphone.id != null && tmpphone.id.equals(contactId)) {
                    tmpphone.nickname = nickname;
                    if (memberEid != null && memberEid.length() > 0) {
                        tmpphone.userEid = memberEid;
                    }
                    if (memberGid != null && memberGid.length() > 0) {
                        tmpphone.userGid = memberGid;
                    }
                    tmpphone.timeStampId = updateTS;
                    tmpphone.nickname = nickname;
                    tmpphone.number = phonenum;
                    tmpphone.subNumber = phoneSubnum;
                    tmpphone.attri = attri;
                    tmpphone.avatar = avatar;
                    if (memberEid != null && memberEid.length() > 0) {
                        tmpphone.userEid = memberEid;
                    }
                    tmpphone.ring = ring;
                    isNewWhite = false;
                    break;
                }
            }
        }
        if (isNewWhite) {
            PhoneNumber newphone = new PhoneNumber();
            newphone.id = contactId;
            newphone.nickname = nickname;
            if (memberEid != null && memberEid.length() > 0) {
                newphone.userEid = memberEid;
            }
            if (memberGid != null && memberGid.length() > 0) {
                newphone.userGid = memberGid;
            }
            if (ring != null && ring.length() > 0) {
                newphone.ring = ring;
            }
            newphone.number = phonenum;
            newphone.subNumber = phoneSubnum;
            newphone.attri = attri;
            newphone.avatar = avatar;
            newphone.timeStampId = updateTS;
            mBindWhiteList.add(newphone);
        }
    }

    void sendSetphonenumReq() {//userset phonenum
        {
            MyMsgData relationMsg = new MyMsgData();
            relationMsg.setCallback(AddCallMemberActivity.this);
            //set msg body
            JSONObject pl = new JSONObject();
            pl.put(CloudBridgeUtil.KEY_NAME_CELLPHONE, getMyApp().getCurUser().getCellNum());

//        pl.put(CloudBridgeUtil.KEY_NAME_ALIAS, Integer.valueOf(selRelation).toString());
            relationMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));

            myApp.getNetService().sendNetMsg(relationMsg);
            //本地先刷新再说
            for (FamilyData fmy : myApp.getCurUser().getFamilyList()) {
                for (MemberUserData member : fmy.getMemberList()) {
                    if (member.getEid().equals(myApp.getCurUser().getEid())) {
                        member.setCellNum(myApp.getCurUser().getCellNum());
                        UserRelationDAO.getInstance(getApplicationContext()).addUserRelation(member, myApp.getCurUser().getFocusWatch().getFamilyId(), myApp.getCurUser().getFocusWatch().getWatchId(), myApp.getCurUser().getFocusWatch().getNickname(), member.getCustomData().toJsonStr());
                    }
                }
            }
        }

    }


    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {

        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
        switch (cid) {
            case CloudBridgeUtil.CID_TTS_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    mRingE2cKey = (String) pl.get(CloudBridgeUtil.KEY_NAME_KEY);

                    JSONObject data = (JSONObject) pl.get(CloudBridgeUtil.NORMAL_DATA);
                    String format = (String) data.get(CloudBridgeUtil.E2C_PL_KEY_FORMAT);
                    mRingPath = genRingPathByE2cKey(mRingE2cKey) + "." + format;
                    byte[] bitmapArray;
                    try {
                        bitmapArray = Base64.decode((String) data.get(CloudBridgeUtil.E2C_PL_KEY_CONTENT), Base64.NO_WRAP);
                        File headfile = new File(mRingPath);
                        FileOutputStream out = new FileOutputStream(headfile);
                        out.write(bitmapArray);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ring = mRingE2cKey;//更新ring的值
                    //编辑新增联系人
                    doSendEditCallMemberReq();
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_MESSAGE_ILLEGAL) {//保存失败
                    loadingdlg.dismiss();
                    ToastUtil.showMyToast(this, getString(R.string.tts_failed), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.RC_NETERROR
                        || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    loadingdlg.dismiss();
                } else {
                    ToastUtil.showMyToast(this, getString(R.string.set_custom_nickname_failed), Toast.LENGTH_SHORT);
                    loadingdlg.dismiss();
                }
                break;
            case CloudBridgeUtil.CID_E2C_DOWN:
                if (rc == 1) {
                    JSONArray key = (JSONArray) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    String temp = key.get(0).toString().replace("/", "_");
                    String oldpath = mRecordFile.getAbsolutePath();
                    mRecordFile.renameTo(new File(ImibabyApp.getAlarmRecordDir(), temp));
                    mRingE2cKey = key.get(0).toString();
                    mRingPath = genRingPathByE2cKey(mRingE2cKey);
                    //编辑新增联系人
                    doSendEditCallMemberReq();
                    //
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                }
                break;
            case CloudBridgeUtil.CID_OPT_CONTACT_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    loadingdlg.dismiss();
                    myApp.setValue(watchid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));//保存
                    if (!isBinded) {
                        finish();
                    } else {
                        if (myApp.getAdminBindFlag()) {
                            if (watch.isBindSetMode()) {
                                Intent intent = new Intent(AddCallMemberActivity.this, OperationMode.class);
                                intent.putExtra("bindstate", true);
                                startActivity(intent);
                            } else {
                                //打开安全区域设置
                                Intent intent = new Intent(AddCallMemberActivity.this, SecurityZoneActivity.class);
                                intent.putExtra("enter", "first_set");
                                intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, watch.getEid());
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(AddCallMemberActivity.this, NewMainActivity.class);
                            startActivity(intent);
                        }
                    }
                    //发送添加成员成功的广播
                    sendBroadcast(new Intent(Const.ACTION_ADD_CALLMEMBER_OK));
                } else {
                    loadingdlg.dismiss();
                    if (isAdd) {
                        contactId = null;
                    }
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    } else {
                        if (rc == -121) {
                            ToastUtil.showMyToast(this, getText(R.string.max_contact_prompt_msg).toString(), Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                        }
                    }
                }
                isSavingFalg = false;
                break;
            case CloudBridgeUtil.CID_MAPSET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS) {
                    loadingdlg.dismiss();
                    myApp.setValue(watchid + Const.SHARE_PREF_DEVICE_CONTACT_KEY, CloudBridgeUtil.genContactListJsonStr(mBindWhiteList));//保存
                    if (!isBinded) {
                        finish();
                    } else {
                        if (myApp.getAdminBindFlag()) {
                            Intent intent = new Intent(AddCallMemberActivity.this, SecurityZoneActivity.class);
                            intent.putExtra("enter", "first_set");
                            intent.putExtra(CloudBridgeUtil.KEY_NAME_EID, watch.getEid());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(AddCallMemberActivity.this, NewMainActivity.class);
                            startActivity(intent);
                        }
                    }
                    //发送添加成员成功的广播
                    sendBroadcast(new Intent(Const.ACTION_ADD_CALLMEMBER_OK));
                } else {
                    if (rc == CloudBridgeUtil.RC_NETERROR
                            || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                        ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtil.showMyToast(this, getString(R.string.modify_failed), Toast.LENGTH_SHORT);
                    }
                    //解除界面锁定
                    loadingdlg.dismiss();
                }
                isSavingFalg = false;
                break;
            case CloudBridgeUtil.CID_USER_SET_RESP:
                if (rc == CloudBridgeUtil.RC_SUCCESS
                        || rc == CloudBridgeUtil.ERROR_CODE_SECURITY_USER_NOT_EXIST) {//服务器错误，临时修改大家可以使用
                    //save relation save nickname local
                    if (nickname != null && nickname.length() > 0) {
                        sendUserSetChangeE2G(myApp.getCurUser().getEid(), watch.getFamilyId());
                    }
                }
                break;
            default:
                break;

        }
    }

    private String genRingPathByE2cKey(String key) {
        String path = null;
        String temp = key.replace("/", "_");
        File file = new File(ImibabyApp.getAlarmRecordDir(), temp);

        return file.getAbsolutePath();
    }

    private int sendTTS(String watcheid, String phonenum, String nickname) {
        MyMsgData e2c = new MyMsgData();
        int sn;
        e2c.setCallback(AddCallMemberActivity.this);
        JSONObject pl = new JSONObject();
        StringBuilder key = new StringBuilder(CloudBridgeUtil.PREFIX_EP_E2C_MESSAGE);
        key.append(watcheid);
        key.append(CloudBridgeUtil.E2C_SPLIT_PHONELIST);
        key.append(phonenum);
        key.append(CloudBridgeUtil.E2C_SPLIT_RING);
        key.append(CloudBridgeUtil.E2C_SERVER_SET_TIME);

        pl.put(CloudBridgeUtil.KEY_NAME_KEY, key.toString());
        pl.put(CloudBridgeUtil.KEY_NAME_VOICE, nickname);
        pl.put(CloudBridgeUtil.E2C_PL_KEY_THEME, "1");

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        e2c.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_TTS_REQ,
                sn, myApp.getToken(), pl));
        getMyApp().getNetService().sendNetMsg(e2c);
        return sn;
    }

    private void doSendEditCallMemberReq() {
        updateEditWhiteList(TimeUtil.getTimestampCHN() + "-00000000");
        if (watch.isDevice102()) {//102，采用mapset，刷新整个列表上传
            if (isAdd) {//新增
                mapSetDataValueMsg(1);
            } else {//替换
                mapSetDataValueMsg(2);
            }
        } else {
            sendEditCallMemberReq(watchid, watch.getFamilyId());
        }
        //设置关系还要待定，是否只能设置自己，不可以设置其他人
        if (getMyApp().getCurUser() != null && memberEid != null && memberEid.equals(getMyApp().getCurUser().getEid())) {
            getMyApp().getCurUser().setRelation(watchid, nickname);
            getMyApp().getCurUser().getCustomData().reloadRelation(getMyApp().getCurUser().getWatchList());
            sendRelationSetMsg();
        }
        // 如果是绑定阶段，再userset下号码
        if (isBinded) {
            myApp.getCurUser().setCellNum(phonenum);
            sendSetphonenumReq();
        }
    }

    private void mapSetDataValueMsg(int type) {
        MyMsgData queryGroupsMsg = new MyMsgData();
        queryGroupsMsg.setCallback(AddCallMemberActivity.this);
        JSONObject pl = new JSONObject();
        preparePLA();
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        pl.put(CloudBridgeUtil.PHONE_WHITE_LIST, plA.toString());
        pl.put(CloudBridgeUtil.KEY_NAME_TEID, watch.getEid());
        pl.put(CloudBridgeUtil.KEY_SET_TYPE, "true");
        pl.put(CloudBridgeUtil.KEY_NAME_TGID, myApp.getCurUser().getFocusWatch().getFamilyId());
        queryGroupsMsg.setReqMsg(CloudBridgeUtil.CloudMapSetContent(CloudBridgeUtil.CID_MAPSET, sn, myApp.getToken(), pl));
        if (myApp.getNetService() != null) {
            myApp.getNetService().sendNetMsg(queryGroupsMsg);
        }
    }

    private void preparePLA() {
        plA.clear();
        PhoneNumber phoneNumber;
        for (int i = 0; i < mBindWhiteList.size(); i++) {
            phoneNumber = mBindWhiteList.get(i);
            JSONObject plO = new JSONObject();
            plO.put("number", phoneNumber.number);
            if (phoneNumber.subNumber != null && phoneNumber.subNumber.length() > 0) {
                plO.put("sub_number", phoneNumber.subNumber);
            }
            if (phoneNumber.ring != null) {
                plO.put("ring", phoneNumber.ring);
            }
            if (phoneNumber.nickname != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, phoneNumber.nickname);
            }
            if (phoneNumber.userEid != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, phoneNumber.userEid);
            }
            if (phoneNumber.userGid != null) {
                plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, phoneNumber.userGid);
            }
            plO.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, phoneNumber.weight);
            plO.put("attri", phoneNumber.attri);
            plO.put("timeStampId", phoneNumber.timeStampId);
            plA.add(plO);
        }
    }

    private int sendEditCallMemberReq(String watcheid, String watchGid) {
        MyMsgData req = new MyMsgData();
        int sn;
        req.setCallback(AddCallMemberActivity.this);
        JSONObject pl = new JSONObject();

        if (contactId == null) {
            contactId = TimeUtil.getTimestampCHN();//
        }
        pl.put(CloudBridgeUtil.KEY_NAME_GID, watchGid);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, watcheid);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ID, contactId);
        if (memberEid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_EID, memberEid);
        }
        if (memberGid != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, memberGid);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NUMBER, phonenum);
        if (phoneSubnum != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_SUB_NUMBER, phoneSubnum);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_NAME, nickname);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_ATTRI, attri);
        if (ring != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_RING, ring);
        }
        if (avatar != null) {
            pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_AVATAR, avatar);
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_WEIGHT, weight);
        int opt = 0;
        if (isAdd) {
            opt = 0;
        } else {
            opt = 1;
        }
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_TYPE, contactsType);
        pl.put(CloudBridgeUtil.KEY_NAME_CONTACT_OPT_TYPE, opt);
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_CONTACT_CHANGE_NOTICE);

        sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        req.setReqMsg(CloudBridgeUtil.obtainCloudMsgContent(CloudBridgeUtil.CID_OPT_CONTACT_REQ,
                sn, myApp.getToken(), pl));
        getMyApp().getNetService().sendNetMsg(req);
        return sn;
    }

    /**
     * 检测String是否全是中文
     */
    public boolean checkNameChinese(String name) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]*");
        Matcher m = p.matcher(name);
        if (p.matcher(name).matches()) {
            temp = true;
        }
        return temp;
    }

    private void sendRelationSetMsg() {
        MyMsgData relationMsg = new MyMsgData();
        relationMsg.setCallback(AddCallMemberActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_CUSTOM, getMyApp().getCurUser().getCustomData().toJsonStr());
        relationMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_USER_SET, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(relationMsg);
        //本地先刷新再说
        refreshRelationLocalData();
    }

    private void sendUserSetChangeE2G(String eid, String gid) {

        MyMsgData e2e = new MyMsgData();
        e2e.setCallback(AddCallMemberActivity.this);
        JSONObject pl = new JSONObject();
        pl.put(CloudBridgeUtil.KEY_NAME_SUB_ACTION, CloudBridgeUtil.SUB_ACTION_VALUE_NAME_USER_CHANGE_NOTICE);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);

        e2e.setReqMsg(CloudBridgeUtil.CloudE2gMsgContent(CloudBridgeUtil.CID_E2G_UP,
                Long.valueOf(TimeUtil.getTimeStampGMT()).intValue(), myApp.getToken(), null, gid, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(e2e);
    }

    private void refreshRelationLocalData() {
        for (FamilyData fmy : myApp.getCurUser().getFamilyList()) {
            for (MemberUserData member : fmy.getMemberList()) {
                if (member.getEid().equals(myApp.getCurUser().getEid())) {
                    member.getCustomData().setFromJsonStr(myApp.getCurUser().getCustomData().toJsonStr());
                    UserRelationDAO.getInstance(getApplicationContext()).addUserRelation(member, myApp.getCurUser().getFocusWatch().getFamilyId(), myApp.getCurUser().getFocusWatch().getWatchId(), myApp.getCurUser().getFocusWatch().getNickname(), member.getCustomData().toJsonStr());
                }
            }
        }
    }

    private String getNicknameByAttri(int attr) {
        String hint = "";
        if (attr == 0) {
            hint = getText(R.string.relation_0).toString();
        } else if (attr == 1) {
            hint = getText(R.string.relation_1).toString();
        } else if (attr == 2) {
            hint = getText(R.string.relation_2).toString();
        } else if (attr == 3) {
            hint = getText(R.string.relation_3).toString();
        } else if (attr == 4) {
            hint = getText(R.string.relation_4).toString();
        } else if (attr == 5) {
            hint = getText(R.string.relation_5).toString();
        } else if (attr == 6) {
            hint = getText(R.string.relation_6).toString();
        } else if (attr == 7) {
            hint = getText(R.string.relation_7).toString();
        } else if (attr == 8) {
            hint = getText(R.string.relation_8).toString();
        } else if (attr == 9) {
            hint = getText(R.string.relation_9).toString();
        } else if (attr == 10) {
            hint = getText(R.string.relation_10).toString();
        } else if (attr == 11) {
            hint = getText(R.string.relation_11).toString();
        } else if (attr == 12) {
            hint = getText(R.string.relation_12).toString();
        } else if (attr == 13) {
            hint = getText(R.string.relation_13).toString();
        } else if (attr == 14) {
            hint = getText(R.string.relation_14).toString();
        } else if (attr == 15) {
            hint = getText(R.string.relation_15).toString();
        } else if (attr == 16) {
            hint = getText(R.string.relation_16).toString();
        } else if (attr == 17) {
            hint = getText(R.string.relation_17).toString();
        } else if (attr == 18) {
            hint = getText(R.string.relation_18).toString();
        } else if (attr == 19) {
            hint = getText(R.string.relation_19).toString();
        } else if (attr == 20) {
            hint = getText(R.string.relation_20).toString();
        } else if (attr == 21) {
            hint = getText(R.string.relation_21).toString();
        } else if (attr == 22) {
            hint = getText(R.string.relation_22).toString();
        } else if (attr == 23) {
            hint = getText(R.string.relation_23).toString();
        }
        return hint;
    }

    private int getAttriByNameString(String nickname) {
        int attri = -1;
        if (getText(R.string.relation_0).toString().equals(nickname)) {
            attri = 0;
        } else if (getText(R.string.relation_1).toString().equals(nickname)) {
            attri = 1;
        } else if (getText(R.string.relation_2).toString().equals(nickname)) {
            attri = 2;
        } else if (getText(R.string.relation_3).toString().equals(nickname)) {
            attri = 3;
        } else if (getText(R.string.relation_4).toString().equals(nickname)) {
            attri = 4;
        } else if (getText(R.string.relation_5).toString().equals(nickname)) {
            attri = 5;
        } else if (getText(R.string.relation_6).toString().equals(nickname)) {
            attri = 6;
        } else if (getText(R.string.relation_7).toString().equals(nickname)) {
            attri = 7;
        } else if (getText(R.string.relation_8).toString().equals(nickname)) {
            attri = 8;
        } else if (getText(R.string.relation_9).toString().equals(nickname)) {
            attri = 9;
        } else if (getText(R.string.relation_10).toString().equals(nickname)) {
            attri = 10;
        } else if (getText(R.string.relation_11).toString().equals(nickname)) {
            attri = 11;
        } else if (getText(R.string.relation_12).toString().equals(nickname)) {
            attri = 12;
        } else if (getText(R.string.relation_13).toString().equals(nickname)) {
            attri = 13;
        } else if (getText(R.string.relation_14).toString().equals(nickname)) {
            attri = 14;
        } else if (getText(R.string.relation_15).toString().equals(nickname)) {
            attri = 15;
        } else if (getText(R.string.relation_16).toString().equals(nickname)) {
            attri = 16;
        } else if (getText(R.string.relation_17).toString().equals(nickname)) {
            attri = 17;
        } else if (getText(R.string.relation_18).toString().equals(nickname)) {
            attri = 18;
        } else if (getText(R.string.relation_19).toString().equals(nickname)) {
            attri = 19;
        } else if (getText(R.string.relation_20).toString().equals(nickname)) {
            attri = 20;
        } else if (getText(R.string.relation_21).toString().equals(nickname)) {
            attri = 21;
        } else if (getText(R.string.relation_22).toString().equals(nickname)) {
            attri = 22;
        } else if (getText(R.string.relation_23).toString().equals(nickname)) {
            attri = 23;
        } else {
            attri = 1000;
        }
        return attri;
    }

    static final String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS
    };
    private static final int PERMISSION_RESULT_READCONTACT = 0xee;
    private static final int PERMISSION_RESULT_CAMERA = 0xff;
    private int reqCode;

    private void checkReadContactsPermission(int reqCode) {
        if (PermissionUtils.hasPermissions(AddCallMemberActivity.this, permissions)) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, reqCode);
        } else {
            this.reqCode = reqCode;
            ActivityCompat.requestPermissions(AddCallMemberActivity.this, permissions, PERMISSION_RESULT_READCONTACT);
        }
    }

}
