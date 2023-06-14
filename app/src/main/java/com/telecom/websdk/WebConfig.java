package com.telecom.websdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.DialogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;

public class WebConfig {
    private static final String TAG = WebConfig.class.getSimpleName();

    private static final String ANDROID_IN_JS = "android_callback";
    private static final String JS_FUNCTION = "setAuditImgBase64Val";
    private static final String CAPTURE_RESULT_ACTION = "inline-data";

    private static final int IMAGE_FILE_SELECT_CODE = 101;
    private static final String IMAGE_FILE_TYPE = "image/*";
    private static final String IMAGE_PREFIX = "IMG_";
    private static final String IMAGE_SUFFIX = ".jpg";
    private static final String IMAGE_DIRECTORY = "myImg";
    private static final String BASE64_PREFIX = "data:image/jpg;base64,";
    private static final int IMAGE_COMPRESS_QUALITY = 50;

    private static WeakReference<Activity> sActivity;
    private static WeakReference<Callback> sCallBack;
    private static WeakReference<LoginWebView> sWebView;
    private static Uri sOutputImageUri;
    private static String sSelectImageIndex = "";
    //keyback handler begin
    private static String homeUrl;
    public static boolean homepageFlag =false;
    private static WeakReference<WebView> nWebView;

    public static void setHomeUrl(String url){
        homeUrl = url;
    }
    public static boolean isHomepageGoExit(String url){
        if (homeUrl!=null) {
            homepageFlag = homeUrl.equals(url);
        }
        return homepageFlag;
    }

    public static void configureNormalWebView(Activity activity, NormalWebView webView, LoginWebViewClient client,
                                              WebChromeClient chromeClient, Callback callback,
                                        LoginProgressInterface loginProgressInterface) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        client.setLoginProgressInterface(loginProgressInterface);

        webView.getSettings().setUserAgentString("XiaoMi/MiuiBrowser/4.3");
        configureNormalWebView(activity, webView, client, chromeClient, callback);
    }
    public static void configureNormalWebView(Activity activity, NormalWebView webView,
                                        WebViewClient client, WebChromeClient chromeClient, Callback callback) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        //}
        webView.getSettings().setAllowContentAccess(true);

        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        //}

        webView.addJavascriptInterface(new CallbackProxy(callback), ANDROID_IN_JS);

        sActivity = new WeakReference<Activity>(activity);
        sCallBack = new WeakReference<Callback>(callback);
        nWebView = new WeakReference<WebView>(webView);
    }
    /**
     * 类名称：WebConfig
     * 创建人：zhangjun5
     * 创建时间：2016/7/12 9:47
     * 方法描述：区别于充值和认证，适用于广告的webview的configure
     */
    public static void configureNormalWebViewByAD(Activity activity, NormalWebView webView, LoginWebViewClient client,
                                                  WebChromeClient chromeClient, Callback callback,
                                                  LoginProgressInterface loginProgressInterface) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        client.setLoginProgressInterface(loginProgressInterface);

        configureNormalWebViewByAD(activity, webView, client, chromeClient, callback);
    }
    public static void configureNormalWebViewByAD(Activity activity, NormalWebView webView,
                                                  WebViewClient client, WebChromeClient chromeClient, Callback callback) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        //}
        webView.getSettings().setAllowContentAccess(true);

        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        //}
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        if (VERSION.SDK_INT < 19) {
            webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
        }
        webView.addJavascriptInterface(new CallbackProxy(callback), ANDROID_IN_JS);

        sActivity = new WeakReference<Activity>(activity);
        sCallBack = new WeakReference<Callback>(callback);
        nWebView = new WeakReference<WebView>(webView);
    }
    //keyback handler end

    /**
     * 在小米手机上调用这个，设置webview js回调
     *
     * @param activity @NotNull
     * @param webView                @NotNull
     * @param client                 @NotNull
     * @param chromeClient @NotNull
     * @param loginProgressInterface @Nullable
     * @param callback               @Nullable
     */
    public static void configureWebView(Activity activity, LoginWebView webView, LoginWebViewClient client,
                                        WebChromeClient chromeClient, Callback callback,
                                        LoginProgressInterface loginProgressInterface) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        client.setLoginProgressInterface(loginProgressInterface);

        webView.getSettings().setUserAgentString("XiaoMi/MiuiBrowser/4.3");
        configureWebView(activity, webView, client, chromeClient, callback);
    }

    /**
     * 在非小米手机上调用
     */
    public static void configureWebView(Activity activity, LoginWebView webView,
                                        WebViewClient client, WebChromeClient chromeClient, Callback callback) {
        if (webView == null) {
            throw new RuntimeException("configureWebView the param webView is null");
        }

        if (client == null) {
            throw new RuntimeException("configureWebView the param webViewClient is null");
        }

        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        //}
        webView.getSettings().setAllowContentAccess(true);

        //if (VERSION.SDK_INT >= 16) {
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        //}

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        if (VERSION.SDK_INT < 19) {
            webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
        }
        webView.addJavascriptInterface(new CallbackProxy(callback), ANDROID_IN_JS);

        sActivity = new WeakReference<Activity>(activity);
        sCallBack = new WeakReference<Callback>(callback);
        sWebView = new WeakReference<LoginWebView>(webView);
    }

    static void startActivityForResult(String target) {
        Log.d(TAG, "startActivityForResult");
        Activity context = null;
        if (sActivity != null) {
            context = sActivity.get();
        }

        if (context == null) {
            Log.d(TAG, "context is null");
            return;
        }

        final File root = context.getExternalFilesDir(IMAGE_DIRECTORY);
        if (!root.exists()) {
            root.mkdirs();
        }
        final String fName = generateUniqueName();
        final File sdImageName = new File(root, fName);
        sOutputImageUri = Uri.fromFile(sdImageName);
        sSelectImageIndex = target;

        Intent pickIntent = new Intent();
        pickIntent.setType(IMAGE_FILE_TYPE);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, sOutputImageUri);

        Intent chooserIntent = Intent.createChooser(pickIntent, null);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        context.startActivityForResult(chooserIntent, IMAGE_FILE_SELECT_CODE);
    }

    /**
     * 在activity或者fragment的onActivityResult中回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);

        if (requestCode == IMAGE_FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri result = null;
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = CAPTURE_RESULT_ACTION.equals(action);
            }

            if (isCamera) {
                result = sOutputImageUri;
            } else {
                result = data == null ? null : data.getData();
            }
            transBitmapToJs(result);
        }
    }
//    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
//
//        if (requestCode == IMAGE_FILE_SELECT_CODE) {
//            LoginWebChromeClient client = sWebChromeClient == null ? null : sWebChromeClient.get();
//            if (client == null) {
//                return;
//            }
//
//            ValueCallback<Uri> callback = client.getUploadCallBack();
//            ValueCallback<Uri[]> callbackForL = client.getUploadCallBackForL();
//
//            if (callback == null && callbackForL == null) {
//                return;
//            }
//
//            if (resultCode != Activity.RESULT_OK) {
//                if (callback != null) {
//                    callback.onReceiveValue(null);
//                }
//
//                if (callbackForL != null) {
//                    callbackForL.onReceiveValue(null);
//                }
//
//                client.clearUploadCallBack();
//                return;
//            }
//
//            final boolean isCamera;
//            if (data == null) {
//                isCamera = true;
//            } else {
//                final String action = data.getAction();
//                if (action == null) {
//                    isCamera = false;
//                } else {
//                    isCamera = action.equals(CAPTURE_RESULT_ACTION);
//                }
//            }
//
//            if (isCamera) {
//                if (callback != null) {
//                    callback.onReceiveValue(sOutputImageUri);
//                } else if (callbackForL != null) {
//                    callbackForL.onReceiveValue(new Uri[]{sOutputImageUri});
//                }
//                client.clearUploadCallBack();
//            } else {
//                Uri result = (data == null) ? null : data.getData();
//                if (callback != null) {
//                    callback.onReceiveValue(result);
//                    client.clearUploadCallBack();
//                } else if (callbackForL != null) {
//                    Uri[] res = onActivityResultForL(client, callbackForL, data);
//                }
//            }
//        }
//    }

//    @TargetApi(21)
//    private static Uri[] onActivityResultForL(LoginWebChromeClient client, ValueCallback<Uri[]> callbackForL, Intent data) {
//        if (client == null || callbackForL == null) {
//            return null;
//        }
//        Uri[] results = null;
//        if (data != null) {
//            String dataString = data.getDataString();
//            ClipData clipData = data.getClipData();
//            if (clipData != null) {
//                results = new Uri[clipData.getItemCount()];
//                for (int i = 0; i < clipData.getItemCount(); i++) {
//                    results[i] = clipData.getItemAt(i).getUri();
//                }
//            }
//
//            if (dataString != null) {
//                results = new Uri[]{Uri.parse(dataString)};
//            }
//        }
//
//        callbackForL.onReceiveValue(results);
//        client.clearUploadCallBack();
//        return results;
//    }

    /**
     * 判断是否为小米手机
     *
     * @param context
     * @return
     */
    public static boolean isMiui(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo pkgInfo = packageManager.getPackageInfo("com.miui.cloudservice", 0);
            return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    static String generateUniqueName() {
        return IMAGE_PREFIX + System.currentTimeMillis() + IMAGE_SUFFIX;
    }

    static void goBack() {
        Callback callback = sCallBack == null ? null : sCallBack.get();
        if (callback != null) {
            callback.backClose();
        }
    }

    /**
     * 将bitmap数据传给js端，为解决4.4.2版本无法打开选择框的问题
     * @param uri
     */
    static void transBitmapToJs(Uri uri) {
        Log.d(TAG, "transBitmapToJs");
        String encodeString = encodeBitmapToBase64(uri);
        if (TextUtils.isEmpty(encodeString)) {
            return;
        }
        WebView webView = sWebView == null ? null : sWebView.get();
        if (webView != null) {
            Log.d(TAG, "load url transBitmapToJs");
            webView.loadUrl("javascript:" + JS_FUNCTION + "('" + encodeString + "', '" + sSelectImageIndex + "')");
        }
    }

    static final String DOCUMENT_SPLIT_CHARACTER = ":";
    static String encodeBitmapToBase64(Uri uri) {
        if (uri == null) {
            Log.d(TAG, "encodeBitmap uri is null");
            return null;
        }

        Context context = sActivity == null ? null : sActivity.get();
        if (context == null) {
            return null;
        }
        String path = null;

        if (VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(DOCUMENT_SPLIT_CHARACTER);
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    path =  context.getExternalFilesDir(Const.MY_BASE_DIR) + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                path =  getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(DOCUMENT_SPLIT_CHARACTER);
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                path =  getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (uri.getScheme().equals("content")) {
            if (context != null) {
                path = getDataColumn(context, uri, null, null);
            }
        } else if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        }
        if(path == null){
            Dialog dlg = DialogUtil.CustomNormalDialog(context, context.getString(R.string.on_available_path),
                    context.getString(R.string.get_path_help), new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    }, "我知道了");
            dlg.show();
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            Log.d(TAG, "encodeBitmap bitmap is null " + uri);
            return null;
        }
//超大的图片缩小尺寸
//        if (bitmap.getWidth()>1080||bitmap.getHeight()>1080) {
//            bitmap = BitmapUtilities.getBitmapThumbnail(path, 1080, 1080);
//        }
        String encodeString = encodeToBase64(bitmap);
        Log.i(TAG, "encodeBitmapToBase64: " + uri + " " + encodeString);
        return encodeString;
    }

    static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, IMAGE_COMPRESS_QUALITY, outputStream);
        byte[] outputBytes = outputStream.toByteArray();
        return BASE64_PREFIX + Base64.encodeToString(outputBytes, Base64.NO_WRAP);
    }

    private static final String[] PROJECTIONS = new String[]{Media.DATA};
    static String getDataColumn(Context context, Uri contentUri, String selections, String[] selectionArgs) {
        Log.d(TAG, "getDataColumn: " + contentUri);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentUri, PROJECTIONS, selections, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
