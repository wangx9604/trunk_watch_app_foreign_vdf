package com.xiaoxun.xun.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * @author  Liutianxiang
 * @date 2013-10-21 上午10:43:43
 * @version V1.0
 * @Description: TODO(添加描述)
 */
public class ImageUtil {

    public static Bitmap getMaskBitmap(Bitmap mask, Bitmap original) {

        original = Bitmap.createScaledBitmap(original, mask.getWidth(), mask.getHeight(), true);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    public static boolean saveImageToGallery(Context context, File file, String fileName) {
        // 其次把文件插入到系统图库
        try {

            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // 最后通知图库更新
        // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" +
        // path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

        return true;
    }

    public static Bitmap getGroupBitmap(Bitmap mask, Bitmap[] originals) {
        if (originals.length == 2) {
            return getTwoMemBitmap(mask, originals);
        } else if (originals.length == 3) {
            return getThreeMemBitmap(mask, originals);
        } else {
            return getFourMemBitmap(mask, originals);
        }
    }

    public static Bitmap getLocationBitmap(Bitmap head, Bitmap bg) {

        //通过比例大小来计算头像的长宽与边距
        head = Bitmap.createScaledBitmap(head, bg.getWidth() * 45 / 75, bg.getWidth() * 45 / 75, true);
        Bitmap result = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(bg, 0, 0, null);
        mCanvas.drawBitmap(head, bg.getWidth() * 15f / 75, bg.getWidth() * 7f / 75, paint);
        return result;
    }

    private static Bitmap getTwoMemBitmap(Bitmap mask, Bitmap[] bitmaps) {
        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();
        int memWidth = (int)(maskWidth * 0.35);
        int memHeight = (int)(maskHeight * 0.35);
        bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], memWidth, memHeight, true);
        bitmaps[1] = Bitmap.createScaledBitmap(bitmaps[1], memWidth, memHeight, true);
        Bitmap result = Bitmap.createBitmap(maskWidth, maskHeight, Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        mCanvas.drawBitmap(bitmaps[0], (int)(maskWidth * 0.1), (int)(maskHeight * 0.325), null);
        mCanvas.drawBitmap(bitmaps[1], (int)(maskWidth * 0.55), (int)(maskHeight * 0.325), null);
        paint.setXfermode(null);
        return result;
    }

    private static Bitmap getThreeMemBitmap(Bitmap mask, Bitmap[] bitmaps) {
        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();
        int memWidth = (int)(maskWidth * 0.35);
        int memHeight = (int)(maskHeight * 0.35);
        bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], memWidth, memHeight, true);
        bitmaps[1] = Bitmap.createScaledBitmap(bitmaps[1], memWidth, memHeight, true);
        bitmaps[2] = Bitmap.createScaledBitmap(bitmaps[2], memWidth, memHeight, true);
        Bitmap result = Bitmap.createBitmap(maskWidth, maskHeight, Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        mCanvas.drawBitmap(bitmaps[0], (int)(maskWidth * 0.325), (int)(maskHeight * 0.075), null);
        mCanvas.drawBitmap(bitmaps[1], (int)(maskWidth * (0.325 - Math.sqrt(3) * 0.125)), (int)(maskHeight * 0.45), null);
        mCanvas.drawBitmap(bitmaps[2], (int)(maskWidth * (0.325 + Math.sqrt(3) * 0.125)), (int)(maskHeight * 0.45), null);
        paint.setXfermode(null);
        return result;
    }

    private static Bitmap getFourMemBitmap(Bitmap mask, Bitmap[] bitmaps) {
        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();
        int memWidth = (int)(maskWidth * 0.35);
        int memHeight = (int)(maskHeight * 0.35);
        bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], memWidth, memHeight, true);
        bitmaps[1] = Bitmap.createScaledBitmap(bitmaps[1], memWidth, memHeight, true);
        bitmaps[2] = Bitmap.createScaledBitmap(bitmaps[2], memWidth, memHeight, true);
        bitmaps[3] = Bitmap.createScaledBitmap(bitmaps[3], memWidth, memHeight, true);
        Bitmap result = Bitmap.createBitmap(maskWidth, maskHeight, Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        mCanvas.drawBitmap(bitmaps[0], (int)(maskWidth * 0.135), (int)(maskHeight * 0.135), null);
        mCanvas.drawBitmap(bitmaps[1], (int)(maskWidth * 0.515), (int)(maskHeight * 0.135), null);
        mCanvas.drawBitmap(bitmaps[2], (int)(maskWidth * 0.135), (int)(maskHeight * 0.515), null);
        mCanvas.drawBitmap(bitmaps[3], (int)(maskWidth * 0.515), (int)(maskHeight * 0.515), null);
        paint.setXfermode(null);
        return result;
    }

    public static Bitmap getMaskBitmap(Bitmap mask, Drawable drawable) {

        BitmapDrawable bb = (BitmapDrawable) drawable;
        Bitmap original = bb.getBitmap();
        Bitmap result = getMaskBitmap(mask, original);
        return result;
    }

    public static void setMaskImage(ImageView view, int maskResId, Drawable drawable) {

        try {
            Bitmap mask = BitmapFactory.decodeResource(view.getResources(), maskResId);
            view.setImageBitmap(ImageUtil.getMaskBitmap(mask, drawable));
            view.setScaleType(ScaleType.CENTER_CROP);
        } catch (Exception e) {
        }
    }

    public static void setGroupMaskImage(ImageView view, int maskResId, Bitmap[] bitmaps) {

        try {
            Bitmap mask = BitmapFactory.decodeResource(view.getResources(), maskResId);
            view.setImageBitmap(ImageUtil.getGroupBitmap(mask, bitmaps));
            view.setScaleType(ScaleType.CENTER_CROP);
        } catch (Exception e) {
        }
    }

    //thanks to  http://www.cnblogs.com/mythou/
    static final int QR_WIDTH = 400;
    static final int QR_HEIGHT = 400;
    public static Bitmap createQRImage(String url) {

        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createQRCodeWithLogo(String text, Bitmap mBitmap) {

        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

            //将logo图片按martix设置的信息缩放
            mBitmap = Bitmap.createScaledBitmap(mBitmap, QR_WIDTH, QR_HEIGHT, false);
            int width = bitMatrix.getWidth();//矩阵高度
            int height = bitMatrix.getHeight();//矩阵宽度
            int halfW = width / 2;
            int halfH = height / 2;

            Matrix m = new Matrix();
            int logoHalfWidth = QR_WIDTH / 12;
            int logoHalfHeight = QR_HEIGHT / 12;
            float sx = (float) 2 * logoHalfWidth / mBitmap.getWidth();
            float sy = (float) 2 * logoHalfHeight / mBitmap.getHeight();
            m.setScale(sx, sy);
            //将logo图片按martix设置的信息缩放
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);

            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (x > halfW - logoHalfWidth && x < halfW + logoHalfWidth
                            && y > halfH - logoHalfHeight && y < halfH + logoHalfHeight) {
                        //中心1/6区域用于存放图片信息，记录图片每个像素信息
                        pixels[y * QR_WIDTH + x] = mBitmap.getPixel(x - halfW + logoHalfWidth, y - halfH + logoHalfHeight);
                    } else {
                        //中心1/6区域之外存放text信息
                        if (bitMatrix.get(x, y)) {
                            pixels[y * QR_WIDTH + x] = 0xff000000;
//                            if (x < 92 && (y < 92 || y >= QR_HEIGHT - 92) || (y < 92 && x >= QR_WIDTH - 92)) {
//                                pixels[y * QR_WIDTH + x] = 0xfff92736;
//                            }
                        } else {
                            pixels[y * QR_WIDTH + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap scale(Bitmap bitmap, float size, Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        float fitWidth = (float) width * size / 1080;
        Matrix matrix = new Matrix();
        matrix.postScale(fitWidth,fitWidth); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getImageContentUri(context,file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void saveBitmap(Bitmap bitmap, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File compressImage(File imageFile, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            // write the compressed bitmap at the destination specified by destinationPath.
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationPath);
    }

    public static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        return scaledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
