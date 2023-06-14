package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.camera.RGBLuminanceSource;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.region.BindFailDialog;
import com.xiaoxun.xun.region.RegionSelectActivity;
import com.xiaoxun.xun.region.RegionWatchActivity;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.MidDetailDidlog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Vector;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends NormalActivity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private BroadcastReceiver mReceiver;
    private Button btnSelQr;
    private String photo_path;
    private Bitmap scanBitmap;
    private String goBind;

    public static final int Request_Code_Bind = 2001;
    public static final int Result_Code_Bind = 2002;
    private ImageButton mBackbtn, iv_title_menu;
    public static String SCAN_TYPE = "type";
    String scanType; //bind addFriend

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CameraManager.init(getApplication());
        goBind = getIntent().getStringExtra(NewLoginActivity.GOBIND);
        if (getIntent() != null) {
            scanType = getIntent().getStringExtra(SCAN_TYPE);
        }
        viewfinderView = findViewById(R.id.viewfinder_view);
        mBackbtn = findViewById(R.id.iv_title_back);
        mBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_title_menu = findViewById(R.id.iv_title_menu);

        if (!TextUtils.isEmpty(scanType) && scanType.equals("bind")) {
            ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.guide_bind_title));
            iv_title_menu.setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(getText(R.string.add_watch_friend));
            iv_title_menu.setVisibility(View.GONE);
        }

        iv_title_menu.setBackgroundResource(R.drawable.btn_bindcode_selector);
        iv_title_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MipcaActivityCapture.this, BindInputImsiActivity.class);
                startActivity(intent);
            }
        });

        btnSelQr = findViewById(R.id.btn_sel_doc);
        btnSelQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
                if (Build.VERSION.SDK_INT < 19) {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                }
                innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                innerIntent.setType("image/*");

                Intent wrapperIntent = Intent.createChooser(innerIntent, getString(R.string.choose_qrcode_image));
                startActivityForResult(wrapperIntent, 100);
            }
        });
//		TextView qrcode = findViewById(R.id.qr_tips);
//		String content = getString(R.string.qr_tips_new);
//		SpannableString builder = new SpannableString(content);
//		ClickableSpan clickableSpan1 = new ClickableSpan() {
//			@Override
//			public void onClick(View widget) {
//				Intent it = new Intent(MipcaActivityCapture.this, RegionWatchActivity.class);
//				it.putExtra("entry_type", 0);
//				MipcaActivityCapture.this.startActivity(it);
//			}
//
//			@Override
//			public void updateDrawState(TextPaint ds) {
//				super.updateDrawState(ds);
//				ds.setColor(getResources().getColor(R.color.btn_agree_continue_color));
//			}
//		};
//		String protectionPolicy = getString(R.string.device_name);
//		int start = content.indexOf(protectionPolicy);
//		if (start > 0)
//			builder.setSpan(clickableSpan1, start, start + protectionPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		qrcode.setText(builder);
//		qrcode.setHighlightColor(getResources().getColor(R.color.transparent));
//		qrcode.setMovementMethod(LinkMovementMethod.getInstance());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        initReceivers();

        if (ActivityCompat.checkSelfPermission(MipcaActivityCapture.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            Log.i("cui", "权限已经开启");
        } else {
            ActivityCompat.requestPermissions(MipcaActivityCapture.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1 == requestCode && grantResults.length == 1) {
            if (grantResults[0] == PERMISSION_GRANTED) {
				/*Intent intent = new Intent();
				intent.setClass(BindNewActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				startActivity(intent);*/
            } else {
                Toast.makeText(MipcaActivityCapture.this, getString(R.string.camera_premission_tips), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case 100:

                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        cursor.close();
                    }
                    if (photo_path == null) {
                        photo_path = com.xiaoxun.xun.utils.DocUtils.getPath(getApplicationContext(),
                                data.getData());
                        LogUtil.i("123path  Utils" + photo_path);
                    }
                    LogUtil.i("123path" + photo_path);


                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Looper.prepare();
                                ToastUtil.show(getApplicationContext(), getString(R.string.image_format_wrong));
                                Looper.loop();
                            } else {
                                Log.i("123result", result.toString());
                                // Log.i("123result", result.getText());
                                // 数据返回
                                String recode = recode(result.toString());
                                int watchRegion ;
                                if (result.toString().contains("sgp-app")) {
                                    watchRegion = 2;
                                } else if (result.toString().contains("ru-app")) {
                                    watchRegion = 1;
                                } else {
                                    watchRegion = 0;
                                }
                                if (!TextUtils.isEmpty(scanType) && scanType.equals("bind")) {
                                    if (recode != null) {
                                        if (myApp.getIntValue(Constants.KEY_NAME_COUNTRY_SELECTED, 0) != watchRegion) {
                                            BindFailDialog dlg = new BindFailDialog(MipcaActivityCapture.this, () -> {
                                            }, () -> {
                                                Intent intent = new Intent(MipcaActivityCapture.this, RegionSelectActivity.class);
                                                intent.putExtra("watchRegion", watchRegion);
                                                intent.putExtra("entry_type", 1);
                                                MipcaActivityCapture.this.startActivity(intent);
                                                finish();
                                            });
                                            dlg.show();
                                        } else {
                                            if (getMyApp().getCurUser().isWatchSNBinded(recode)) {
                                                Intent intent = new Intent();
                                                intent.setClass(MipcaActivityCapture.this, BindResultActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra(Const.KEY_RESULT_CODE, 0);
                                                intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_binded));
                                                startActivity(intent);
                                            } else {
                                                sendBindRequest(recode);
//											showAgreementAndPrivacy(recode);
                                            }
                                        }
                                    } else {
                                        Intent intent = new Intent();
                                        intent.setClass(MipcaActivityCapture.this, BindResultActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        intent.putExtra(Const.KEY_RESULT_CODE, 0);
                                        intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_wrong));
                                        startActivity(intent);
                                    }
                                    finish();
                                } else {
                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("result", recode);
                                    resultIntent.putExtras(bundle);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                }
                            }
                        }
                    }).start();
                    break;

            }

        }

    }

    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes(StandardCharsets.ISO_8859_1), "GB2312");
                Log.i("1234      ISO8859-1", formart);
            } else {
                formart = str;
                Log.i("1234      stringExtra", str);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {

            return null;

        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 400);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        if (scanBitmap == null) {
            return null;
        }
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {

            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {

            e.printStackTrace();

        } catch (ChecksumException e) {

            e.printStackTrace();

        } catch (FormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

        }

        return null;

    }

    private void initReceivers() {
        // TODO Auto-generated method stub
        mReceiver = new BroadcastReceiver() {
            //wifi状态广播接收，连接变化，扫描结果等
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_BIND_RESULT_END)) {
                    finish();
                } else {
                }
            }

        };
        IntentFilter baseFilter = new IntentFilter();
        baseFilter.addAction(Const.ACTION_BIND_RESULT_END);
        registerReceiver(mReceiver, baseFilter);
    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        clearReceivers();
        super.onDestroy();
    }

    /**
     * ����ɨ����
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            ToastUtil.showMyToast(MipcaActivityCapture.this,
                    "Scan failed!", Toast.LENGTH_SHORT);
        } else {
//			Intent resultIntent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putString("result", resultString);
////			bundle.putParcelable("bitmap", barcode);
//			resultIntent.putExtras(bundle);
//			this.setResult(RESULT_OK, resultIntent);
            Log.i("123result", result.toString());
            int watchRegion ;
            if (result.toString().contains("sgp-app")) {
                watchRegion = 2;
            } else if (result.toString().contains("ru-app")) {
                watchRegion = 1;
            } else {
                watchRegion = 0;
            }
            if (!TextUtils.isEmpty(scanType) && scanType.equals("bind")) {
                if (resultString != null) {
                    if (myApp.getIntValue(Constants.KEY_NAME_COUNTRY_SELECTED, 0) != watchRegion) {
                        BindFailDialog dlg = new BindFailDialog(MipcaActivityCapture.this, () -> {
                        }, () -> {
                            Intent intent = new Intent(MipcaActivityCapture.this, RegionSelectActivity.class);
                            intent.putExtra("watchRegion", watchRegion);
                            intent.putExtra("entry_type", 1);
                            MipcaActivityCapture.this.startActivity(intent);
                            finish();
                        });
                        dlg.show();
                    } else {
                        if (getMyApp().getCurUser().isWatchSNBinded(resultString)) {
                            Intent intent = new Intent();
                            intent.setClass(MipcaActivityCapture.this, BindResultActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(Const.KEY_RESULT_CODE, 0);
                            intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_binded));
                            startActivity(intent);
                            finish();
                        } else {
                            sendBindRequest(resultString);
                            finish();
//						showAgreementAndPrivacy(resultString);
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MipcaActivityCapture.this, BindResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Const.KEY_RESULT_CODE, 0);
                    intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_wrong));
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
//        MipcaActivityCapture.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            ToastUtil.showMyToast(MipcaActivityCapture.this, getString(R.string.camera_wrong_tips), Toast.LENGTH_LONG);
            return;
        } catch (RuntimeException e) {
            ToastUtil.showMyToast(MipcaActivityCapture.this, getString(R.string.check_camera_permission), Toast.LENGTH_LONG);
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.isEmpty(goBind)) {
                //Intent intent = new Intent(BindNewActivity.this, NewMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent intent = new Intent(MipcaActivityCapture.this, NewMainActivity.class);
                startActivity(intent);
            } else {
                setResult(Result_Code_Bind);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showAgreementAndPrivacy(String qrCodeString) {
        Intent intent = new Intent(MipcaActivityCapture.this, AgreementAndPrivacyActivity.class);
        intent.putExtra("qrcode", qrCodeString);
        startActivity(intent);
    }

    private void sendBindRequest(String qrCodeString) {
        Intent intent = new Intent();
        intent.setClass(MipcaActivityCapture.this, BindResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Const.KEY_RESULT_CODE, 1);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_SERINALNO, qrCodeString);
        intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_req_send));
        startActivity(intent);
        finish();
    }
}
