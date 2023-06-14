package com.xiaoxun.xun.gallary;

import android.app.Dialog;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.gallary.control.FragmentController;
import com.xiaoxun.xun.gallary.interfaces.TitleButtonChangeListener;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

public class GalleryActivity extends FragmentActivity {
    private ImibabyApp mApp;

    private FragmentController controller;

    private ImageButton iv_title_back;
    private ImageButton title_del_confirm;
    private ImageButton title_del;
    private TextView capacity;

    private String capacity_txt;
    private int curFragmentPos = 0;
    public TitleButtonChangeListener titleButtonChangeListener = new TitleButtonChangeListener() {
        @Override
        public void OnStatuChanged(int state) {
            if (state != 3) {
                modeSwitch();
            } else {
                capacity_txt = mApp.getStringValue("gallery_capacity" + mApp.getCurUser().getFocusWatch().getEid(),
                        getString(R.string.gallery_capacity_default));
                capacity.setText(capacity_txt);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mApp = (ImibabyApp) getApplication();
        ImageVedioFiles.initFiles(this);
        initviews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controller != null) {
            controller.FragmentClear();
            controller = null;
        }
        ImageVedioFiles.switchDevClearData();
    }

    private void initviews() {
        controller = FragmentController.getInstance(getSupportFragmentManager(), R.id.container, titleButtonChangeListener);
        iv_title_back = findViewById(R.id.iv_title_back);
        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageVedioFiles.GALLERY_STATUS == 0) {
                    finish();
                } else {
                    modeSwitch();
                }
            }
        });
        title_del_confirm = findViewById(R.id.title_del_confirm);
        title_del_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageVedioFiles.GALLERY_STATUS == 0) {
                    modeSwitch();
                } else {
                    if (controller.getFragment(curFragmentPos).getAdapterGrid().selectListSize() <= 0) {
                        ToastUtil.showMyToast(GalleryActivity.this,"请选择要删除的照片",Toast.LENGTH_LONG);
                    } else {
                        Dialog dlg = DialogUtil.CustomNormalDialog(GalleryActivity.this, getString(R.string.prompt),
                                getString(R.string.files_delete_content),
                                new DialogUtil.OnCustomDialogListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, getString(R.string.cancel),
                                new DialogUtil.OnCustomDialogListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (curFragmentPos == 0) {
                                            controller.getFragment(curFragmentPos).getAdapterGrid().requestDeleteFiles();
                                        }
                                    }
                                }, getString(R.string.confirm));
                        dlg.show();
                    }
                }
            }
        });

        title_del = findViewById(R.id.title_del);
        title_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getFragment(curFragmentPos).getAdapterGrid().AllChooseItemCancel();
                modeSwitch();
            }
        });
        capacity = findViewById(R.id.capacity);
        capacity_txt = mApp.getStringValue("gallery_capacity" + mApp.getCurUser().getFocusWatch().getEid(), getString(R.string.gallery_capacity_default));
        capacity.setText(capacity_txt);
    }

    private void modeSwitch() {
        if (ImageVedioFiles.GALLERY_STATUS == 1) {
            iv_title_back.setVisibility(View.VISIBLE);
            title_del.setVisibility(View.GONE);
            title_del_confirm.setVisibility(View.GONE);
            ImageVedioFiles.GALLERY_STATUS = 0;
        } else {
            iv_title_back.setVisibility(View.GONE);
            title_del.setVisibility(View.VISIBLE);
            title_del_confirm.setVisibility(View.VISIBLE);
            ImageVedioFiles.GALLERY_STATUS = 1;
        }
        controller.getFragment(curFragmentPos).getAdapterGrid().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ImageVedioFiles.GALLERY_STATUS == 1) {
            controller.FragmentsOnBackPress();
        }
    }
}
