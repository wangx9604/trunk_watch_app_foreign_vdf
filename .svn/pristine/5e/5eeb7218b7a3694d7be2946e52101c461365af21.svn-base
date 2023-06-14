package com.xiaoxun.xun.dialBg;


import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import alex.photojar.photoView.phototCrop.IGetImageBounds;
import alex.photojar.photoView.phototCrop.PhotoCropView;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.xiaoxun.xun.dialBg.DialBgActivity.PERMISSION_RESULT_CAMERA;
import static com.xiaoxun.xun.dialBg.DialBgActivity.RESULT_NO_DATA_BACK;

public class CropPreviewActivity extends NormalActivity {

    public static final int REQUEST_CODE_GET_PIC = 0x1;
    public static final int GET_IMAGE_FROM_ALBUM = 1;
    public static final int GET_IMAGE_FROM_CAMERA = 2;
    public static final int GET_IMAGE_FROM_ZOOM = 3;

    private static final int DEVICE_WIDTH = 240;
    private static final int DEVICE_HEIGHT = 240;
    private final int IMAGE_MAX_SIZE = 1024;

    private WatchData curWatch;

    private File cameraTemp;
    private Uri mImageUri = null;
    private ContentResolver mContentResolver;

    private PhotoCropView iv_photo;
    private CropOverlayView crop_overlay;
    private Button confirm;
    private ImageButton change;
    private ImageButton back;

    private static final String TEST_FILE_PATH = "temp.jpg";
    private static final String TEST_FILE_OUT_PATH = "out.jpg";
    private File testFile;
    private Uri mSaveUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_preview);
        mContentResolver = getContentResolver();
        curWatch = getMyApp().getCurUser().getFocusWatch();
        if (curWatch == null) {
            finish();
            return;
        }
        initViews();

        if (savedInstanceState == null || !savedInstanceState.getBoolean("restoreState")) {
            String action_str = getIntent().getStringExtra("action");
            if (null != action_str) {
                int action = Integer.valueOf(action_str);
                switch (action) {
                    case GET_IMAGE_FROM_CAMERA:
                        getIntent().removeExtra("action");
                        takePic();
                        return;
                    case GET_IMAGE_FROM_ALBUM:
                        getIntent().removeExtra("action");
                        startPick();
                        return;
                }
            }
        }

        //testIniFile();
        //init();
    }

    private void initViews() {
        iv_photo = findViewById(R.id.iv_photo);
        crop_overlay = findViewById(R.id.crop_overlay);
        iv_photo.setImageBoundsListener(new IGetImageBounds() {
            @Override
            public Rect getImageBounds() {
                return crop_overlay.getImageBounds();
            }
        });
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveCropImg()) {
                    Toast.makeText(getApplicationContext(), "OK.", Toast.LENGTH_LONG).show();
                    showEditNameDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "FAILED.", Toast.LENGTH_LONG).show();
                }
            }
        });
        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAvatarEditDialog();
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_NO_DATA_BACK);
                finish();
            }
        });
    }

    private void createTempFile() {
        String eid = curWatch.getEid();
        String eidAes = AESUtil.getInstance().encryptDataStr(eid);
        String mPath = getExternalFilesDir(Const.MY_BASE_DIR + "/DIAL_LOCAL_BG").getAbsolutePath();
        File fp = new File(mPath + "/" + eidAes);
        if (!fp.exists()) {
            fp.mkdirs();
        }
        cameraTemp = new File(fp, "/temp.jpg");
        if (!cameraTemp.exists()) {
            try {
                cameraTemp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private void init() {
        Bitmap bitmap = getBitmap(mImageUri);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        float minScale = iv_photo.setMinimumScaleToFit(drawable);
        iv_photo.setMaximumScale(minScale * 3);
        iv_photo.setMediumScale(minScale * 2);
        //mImageView.setScale(minScale * 2);
        iv_photo.setImageDrawable(drawable);
    }

    private boolean saveCropImg() {
        Bitmap croppedImage = iv_photo.getCroppedImage();
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LogUtil.e("not defined image url");
            return false;
        }
        croppedImage.recycle();
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        createTempFile();
        switch (requestCode) {
            case GET_IMAGE_FROM_ALBUM:  // 相册获取
                if (resultCode == RESULT_CANCELED) {
                    setResult(RESULT_NO_DATA_BACK);
                    finish();
                } else if (resultCode == RESULT_OK) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData()); // Got the bitmap .. Copy it to the temp file for cropping
                        FileOutputStream fileOutputStream = new FileOutputStream(cameraTemp);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();
                        mImageUri = getImageUri(cameraTemp.getPath());
                        mSaveUri = getImageUri(cameraTemp.getPath());
                        init();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setResult(RESULT_NO_DATA_BACK);
                    finish();
                }
                break;

            case GET_IMAGE_FROM_CAMERA: // 相机拍照
                if (requestCode == RESULT_CANCELED) {
                    setResult(RESULT_NO_DATA_BACK);
                    finish();
                } else if (resultCode == RESULT_OK) {
                    mImageUri = Uri.fromFile(cameraTemp);
                    mSaveUri = Uri.fromFile(cameraTemp);
                    init();
                } else {
                    setResult(RESULT_NO_DATA_BACK);
                    finish();
                }
                break;

            default:
                break;
        }

    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap getBitmap(Uri uri) {
        InputStream in = null;
        Bitmap returnedBitmap = null;
        try {
            in = mContentResolver.openInputStream(uri);
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            //First check
            ExifInterface ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    returnedBitmap = rotateImage(bitmap, 90);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    returnedBitmap = rotateImage(bitmap, 180);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    returnedBitmap = rotateImage(bitmap, 270);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                default:
                    returnedBitmap = bitmap;
            }
            return returnedBitmap;
        } catch (FileNotFoundException e) {
            LogUtil.e(e.toString());
        } catch (IOException e) {
            LogUtil.e(e.toString());
        }
        return null;
    }

    private void showEditNameDialog() {
        Dialog dlg = CustomSelectDialogUtil.CustomInputDialog(CropPreviewActivity.this,
                getText(R.string.edit_name_alert).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                        // TODO Auto-generated method stub
                    }
                },
                getText(R.string.cancel).toString(),
                new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                        // TODO Auto-generated method stub
                        String time = TimeUtil.getOrderTime(TimeUtil.getTimeStampLocal());
                        String img_name = getApplication().getResources().getString(R.string.dial_bg_txt);
                        if (text != null && !("").equals(text)) {
                            img_name = text;
                        }

                        File fp = new File(cameraTemp.getParentFile(), time + ".jpg");
                        boolean ret = cameraTemp.renameTo(fp);
                        if (!ret) {
                            LogUtil.e("rename failed.");
                            return;
                        }

                        Intent it = new Intent(CropPreviewActivity.this, DialBgActivity.class);
                        it.putExtra("name", img_name);
                        it.putExtra("time", time);
                        it.putExtra("img_path", fp.getAbsolutePath());
                        setResult(RESULT_OK, it);
                        finish();
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    public Bitmap compressPicture(Bitmap srcPath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        srcPath.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options op = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = DEVICE_WIDTH;//
        float ww = DEVICE_HEIGHT;//
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = w / ww;
        } else if (w < h && h > hh) {
            be = h / hh;
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, op);
        return bitmap;
    }

    private void takePic() {
        createTempFile();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //指定调用相机拍照后的照片存储的路径
            if (cameraTemp.exists()) {
                cameraTemp.delete();
            }
            Uri contentUri = FileProvider.getUriForFile(getMyApp(), getMyApp().getPackageName() + ".xun.fileprovider", cameraTemp);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, GET_IMAGE_FROM_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //指定调用相机拍照后的照片存储的路径
            if (cameraTemp.exists()) {
                cameraTemp.delete();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraTemp));
            startActivityForResult(intent, GET_IMAGE_FROM_CAMERA);
        }
    }

    private void startPick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            startActivityForResult(intent, GET_IMAGE_FROM_ALBUM);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No image source available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAvatarEditDialog() {
        ArrayList<String> itemList = new ArrayList<String>();
        itemList.add(getResources().getText(R.string.head_edit_camera).toString());
        itemList.add(getResources().getText(R.string.head_edit_pics).toString());
        Dialog dlg = CustomSelectDialogUtil.CustomItemSelectDialogWithTitle(this,
                getResources().getText(R.string.dial_bg_dialog_title).toString(), itemList,
                new CustomSelectDialogUtil.AdapterItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        if (position == 1) {
                            if (ActivityCompat.checkSelfPermission(CropPreviewActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                                takePic();
                            } else {
                                ActivityCompat.requestPermissions(CropPreviewActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_RESULT_CAMERA);
                            }
                        } else {
                            //gallery ,then go to CropActivity
                            startPick();
                        }
                    }
                },
                -1, new CustomSelectDialogUtil.CustomDialogListener() {
                    @Override
                    public void onClick(View v, String text) {
                    }
                }, getResources().getText(R.string.cancel).toString());
        dlg.show();
    }
}
