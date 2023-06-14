package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.UriUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author cuiyufeng
 * @Description: ShareUtil
 * @date 2018/7/26 11:45
 */
public class ShareUtil {
    private Context context;
    private IWXAPI mWXapi = null;
    private Tencent mTencent;

    public ShareUtil(Context context) {
        this.context = context;
        init();
    }

    public static String getLocalShareFile(ViewGroup mLayoutAllView) {
        String fileName = null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(mLayoutAllView.getWidth(), mLayoutAllView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mLayoutAllView.draw(canvas);
            fileName = ShareUtil.SaveImageFile(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        return  UriUtils.file2Uri(imageFile);
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Images.Media._ID },
//                MediaStore.Images.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor
//                    .getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                return context.getContentResolver().insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
    }

    private void init() {
        //微信sdk注册应用
        mWXapi = WXAPIFactory.createWXAPI(context, Const.WECHAT_APP_ID, true);
        mWXapi.registerApp(Const.WECHAT_APP_ID);
        mTencent = Tencent.createInstance(Const.QQ_APP_ID, context.getApplicationContext());
    }

    public boolean isWXAppInstalled() {
        if (mWXapi.isWXAppInstalled()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkapp() {
        if (checkApkExist(context, "com.tencent.mobileqq")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 类名称：ShareStepsActivity
     * 创建人：zhangjun5
     * 创建时间：2016/5/9 15:58
     * 方法描述：检查是否存在需要的包名
     */
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void shareToQzone(String title, String description, String actionUrl, String imageUrl) {//, IUiListener uiListener
        if (checkApkExist(context, "com.tencent.mobileqq")) {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);//选填
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, actionUrl);//必填
            ArrayList<String> imageUrls = new ArrayList<String>();
            imageUrls.add(imageUrl);  //SHARE_TO_QQ_IMAGE_LOCAL_URL
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            doShareToQzone((Activity) context, params);
        } else {
            ToastUtil.show(context, context.getString(R.string.steps_share_no_app));
        }
    }

    public void shareToQzone(Activity context, String shareTitle, String shareUrl, String fileName, StepsUtil.BaseUiListener backListener) {
        //分享类型
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "");
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitle);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, fileName);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(context, params, backListener);
    }

    /**
     * 用异步方式启动分享
     *
     * @param params
     */
    private void doShareToQzone(Activity activity, final Bundle params) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQzone((Activity) context, params, qZoneShareListener);
                }
            }
        });
    }


    IUiListener qZoneShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            Toast.makeText(context, R.string.share_cancel, Toast.LENGTH_SHORT).show();
            Log.i("cui", "--onCancel");
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            Toast.makeText(context, R.string.steps_share_send_fail, Toast.LENGTH_SHORT).show();
            Log.i("cui", "--onError" + e.errorMessage);
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(context, R.string.share_success, Toast.LENGTH_SHORT).show();
            Log.i("cui", "--onComplete" + response.toString());
        }
    };

    /**
     * -----------------------------------------------------------------------------
     * 网页连接
     * 微信分享
     * isfriends 是不是朋友圈
     */
    public void sharewx(boolean isfriends, String title, String description, String actionUrl, byte[] bytes, final String imgurl) {
        if (mWXapi.isWXAppInstalled()) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = actionUrl;
            final WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            if (!TextUtils.isEmpty(description)) {
                msg.description = description;
            }
            if (bytes != null) {
                msg.thumbData = bytes;
            } else {
                Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
            }
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = isfriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
            mWXapi.sendReq(req);
        } else {
            ToastUtil.show(context, context.getString(R.string.steps_share_no_app));
        }
    }

    public void sharewx(boolean isfriends, String title, String description, String localFile) {
        if (mWXapi.isWXAppInstalled()) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(localFile);
                WXImageObject imgObj = new WXImageObject(bmp);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 60, 100, true);
                bmp.recycle();
                msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);

                msg.title = title;
                if (!TextUtils.isEmpty(description)) {
                    msg.description = description;
                }

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = isfriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                mWXapi.sendReq(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.show(context, context.getString(R.string.steps_share_no_app));
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showShareMore(Context context, final String title, final String url, Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//		intent.putExtra(Intent.EXTRA_STREAM, imageUri);
//		intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    public static String SaveImageFile(Bitmap bitmap) {
        File dir = ImibabyApp.getIconCacheDir();
        if (dir.isDirectory()) {
            File[] fileArray = dir.listFiles();
            if (fileArray != null && fileArray.length > 0) {
                for (File f : fileArray) {
                    String fileName = f.getName();
                    if (fileName.contains("share")) {
                        f.delete();
                    }
                }
            }
        }

        String fileName = ImibabyApp.getIconCacheDir() + "/"
                + TimeUtil.getTimeStampLocal() + "share.jpg";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

}
