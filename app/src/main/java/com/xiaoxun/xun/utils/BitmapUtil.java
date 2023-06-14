package com.xiaoxun.xun.utils;

import static com.yalantis.ucrop.util.BitmapLoadUtils.transformBitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaoxun.xun.beans.Image;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by guxiaolong on 2016/7/21.
 */

public class BitmapUtil {
    private static final String TAG = "BitmapLoadUtils";
    /**
     * 根据资源id获取到图片，并进行压缩
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, opts);
        int inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inSampleSize = inSampleSize;
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
        return bitmap;
    }

    public static JSONArray CompressImageFormSizeAndWH(Context mContext, ArrayList<Image> mImages,
                                                       int mMaxWH, int mMaxSize) {
        JSONArray picArray = new JSONArray();

        for (Image mImage : mImages) {

            Uri mUri = mImage.getUri();
            final ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(mUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return picArray;
            }

            final FileDescriptor fileDescriptor;
            if (parcelFileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            } else {
                LogUtil.e("ParcelFileDescriptor was null for given Uri");
                return picArray;
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            if (options.outWidth == -1 || options.outHeight == -1) {
                LogUtil.e("Bounds for bitmap could not be retrieved from Uri");
                return picArray;
            }

            options.inSampleSize = calculateInSampleSize(options, mMaxWH, mMaxWH);
            options.inJustDecodeBounds = false;

            Bitmap decodeSampledBitmap = null;

            boolean decodeAttemptSuccess = false;
            while (!decodeAttemptSuccess) {
                try {
                    decodeSampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                    decodeAttemptSuccess = true;
                } catch (OutOfMemoryError error) {
                    Log.e("BitmapUtils decode", "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                    options.inSampleSize++;
                }
            }

            if (decodeSampledBitmap == null) {
                LogUtil.e("Bitmap could not be decoded from Uri");
                return picArray;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                close(parcelFileDescriptor);
            }

            int exifOrientation = getExifOrientation(mContext, mUri);
            int exifDegrees = exifToDegrees(exifOrientation);
            int exifTranslation = exifToTranslation(exifOrientation);

            Matrix matrix = new Matrix();
            if (exifDegrees != 0) {
                matrix.preRotate(exifDegrees);
            }
            if (exifTranslation != 1) {
                matrix.postScale(exifTranslation, 1);
            }
            if (!matrix.isIdentity()) {
                decodeSampledBitmap = transformBitmap(decodeSampledBitmap, matrix);
            }

            ByteArrayOutputStream baos = null;
            String result = null;
            try {
                if (decodeSampledBitmap != null) {
                    baos = new ByteArrayOutputStream();
                    decodeSampledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    int quality = 100;
                    while(baos.toByteArray().length > mMaxSize){
                        baos.reset();
                        decodeSampledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                        quality-=10;
                        LogUtil.e("compress:"+baos.toByteArray().length+":"+mMaxSize);
                    }

                    baos.flush();
                    baos.close();

                    byte[] bitmapBytes = baos.toByteArray();
                    result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.flush();
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file", "data:image/png;base64,"+ result);
            jsonObject.put("size","34");
            picArray.add(jsonObject);

        }

        return picArray;
    }

    public static int exifToDegrees(int exifOrientation) {
        int rotation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    public static int exifToTranslation(int exifOrientation) {
        int translation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                translation = -1;
                break;
            default:
                translation = 1;
        }
        return translation;
    }

    public static int getExifOrientation(@NonNull Context context, @NonNull Uri imageUri) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        try {
            InputStream stream = context.getContentResolver().openInputStream(imageUri);
            if (stream == null) {
                return orientation;
            }
            orientation = new ImageHeaderParser(stream).getOrientation();
            close(stream);
        } catch (IOException e) {
            Log.e(TAG, "getExifOrientation: " + imageUri.toString(), e);
        }
        return orientation;
    }

    @SuppressWarnings("ConstantConditions")
    public static void close(@Nullable Closeable c) {
        if (c != null && c instanceof Closeable) { // java.lang.IncompatibleClassChangeError: interface not implemented
            try {
                c.close();
            } catch (IOException e) {
                // silence
            }
        }
    }

    /**
     * 从byte数组中获取图片并压缩
     *
     * @param data
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                opts);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {
        if (opts == null) {
            return 1;
        }
        int inSampleSize = 1;
        int realWidth = opts.outWidth;
        int realHeight = opts.outHeight;
        if (realHeight > reqHeight || realWidth > reqWidth) {
            final int halfHeight = realHeight / 2;
            final int halfWidth = realWidth / 2;
            while ((halfHeight / inSampleSize) >=  realHeight && (halfWidth / inSampleSize) >= realWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /*
      * 压缩图片
      * */

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 400) {  //循环判断如果压缩后图片是否大于400kb,大于继续压缩（这里可以设置大些）
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }



    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            System.out.println("image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * bitmao de byte
     * @param bmp
     * @param needRecycle
     * @return bitmap 转 byte
     */
    public static byte[] bmpToByteArray( Bitmap bmp,boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if(bmp!=null){
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, output);
            if (needRecycle) {
                bmp.recycle();
            }
        }
        byte[] result = output.toByteArray();
        double mid = result.length/1024;
        Log.i("cui","mid=="+mid+"K");
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "" + e);
        }
        return result;
    }

    public static Bitmap imageZoom(Bitmap bitMap) {
        //图片允许最大空间   单位：KB
        double maxSize =32.00;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length/1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }


    /***
     * 图片的缩放方法
     * @param bgimage ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,(int) height, matrix, true);
        return bitmap;
    }
}
