package com.xiaoxun.xun.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class BitmapUtilities {

	public BitmapUtilities() {
		// TODO Auto-generated constructor stub
	}
	
	public static Bitmap getBitmapThumbnail(String path,int width,int height){
		Bitmap bitmap = null;
		//这里可以按比例缩小图片：
		/*BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 4;//宽和高都是原来的1/4
		bitmap = BitmapFactory.decodeFile(path, opts); */
		
		/*进一步的，
	            如何设置恰当的inSampleSize是解决该问题的关键之一。BitmapFactory.Options提供了另一个成员inJustDecodeBounds。
	           设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。
	           有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。*/
		BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, opts); 
	    opts.inSampleSize = computeSampleSize(opts, -1, height * width);
	    opts.inJustDecodeBounds = false;
	    bitmap = BitmapFactory.decodeFile(path, opts);
		return bitmap;
	}
	

	
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 80 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	/**
     * 得到压缩图片
     * @param resources
     * @param reqWidth
     * @param reqHeight
     * @param resourceId
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(Resources resources,  
            int reqWidth, int reqHeight,int resourceId) {  
    	final  BitmapFactory.Options options = new BitmapFactory.Options();  
        // First decode with inJustDecodeBounds=true to check dimensions  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeResource(resources, resourceId, options);
  
        // Calculate inSampleSize  
        options.inSampleSize = computeSampleSize(options, -1, reqHeight * reqWidth);
  
        // Decode bitmap with inSampleSize set  
        options.inJustDecodeBounds = false;  
        return BitmapFactory.decodeResource(resources, resourceId, options);
    } 
    
    public static Bitmap convertViewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap; 
    }
    
    public enum ScalingLogic { CROP, FIT, SCALE_CROP }
    
    public static Bitmap createScaledBitmap(ImageView imgView, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
    	BitmapDrawable bd = null; 
    	Bitmap unscaledBitmap = null;
    	try {
    		bd = (BitmapDrawable) imgView.getDrawable();
	    	if (bd != null)
	    		unscaledBitmap = bd.getBitmap(); 
	    	if (unscaledBitmap == null) {
	    		bd = (BitmapDrawable) imgView.getBackground();
	    		unscaledBitmap = bd.getBitmap(); 
	    	}
    	} catch (ClassCastException e) {
			// TODO: handle exception
    		unscaledBitmap = drawableToBitamp(imgView.getDrawable());
		}
//    	Bitmap unscaledBitmap = decodeFile(path, dstWidth, dstHeight, scalingLogic);
    	Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
    	Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
    	Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
    	if (scaledBitmap != null) {
	    	Canvas canvas = new Canvas(scaledBitmap);
	    	canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
    	}
    	return scaledBitmap;
    }
    
    public static Bitmap createScaledBitmap(String path, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
    	Bitmap unscaledBitmap = decodeFile(path, dstWidth, dstHeight, scalingLogic);
    	if (unscaledBitmap != null) {
	    	Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
	    	Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
	    	Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
	    	Canvas canvas = new Canvas(scaledBitmap);
	    	canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
	    	return scaledBitmap;
    	}
    	return null;
    }
    
    public static Bitmap drawableToBitamp(Drawable drawable)
    {
    	Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = 
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);   
        drawable.setBounds(0, 0, w, h);   
        drawable.draw(canvas);
        return bitmap;
    }
    
    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
		if (scalingLogic == ScalingLogic.CROP) {
		  final float srcAspect = (float)srcWidth / (float)srcHeight;
		  final float dstAspect = (float)dstWidth / (float)dstHeight;
		  if (srcAspect > dstAspect) {
		    final int srcRectWidth = (int)(srcHeight * dstAspect);
		    final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
		    return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
		  } else {
		    final int srcRectHeight = (int)(srcWidth / dstAspect);
		    final int scrRectTop = (srcHeight - srcRectHeight) / 2;
		    return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
		  }
		} else {
		  return new Rect(0, 0, srcWidth, srcHeight);
		}
	}
    
    private static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
	  if (scalingLogic == ScalingLogic.FIT) {
	    final float srcAspect = (float)srcWidth / (float)srcHeight;
	    final float dstAspect = (float)dstWidth / (float)dstHeight;
	    if (srcAspect > dstAspect) {
	      return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
	    } else {
	      return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
	    }
	  } else {
	    return new Rect(0, 0, dstWidth, dstHeight);
	  }
	}
    
    private static Bitmap decodeFile(String pathName, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
    	  Options options = new Options();
    	  options.inJustDecodeBounds = true;
    	  BitmapFactory.decodeFile(pathName, options);
    	  options.inJustDecodeBounds = false;
    	  options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
    	  Bitmap unscaledBitmap = BitmapFactory.decodeFile(pathName, options);
    	  return unscaledBitmap;
	}
    
    private static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
	  if (scalingLogic == ScalingLogic.FIT) {
	    final float srcAspect = (float)srcWidth / (float)srcHeight;
	    final float dstAspect = (float)dstWidth / (float)dstHeight;
	    if (srcAspect > dstAspect) {
	      return srcWidth / dstWidth;
	    } else {
	      return srcHeight / dstHeight;
	    }
	  } else {
	    final float srcAspect = (float)srcWidth / (float)srcHeight;
	    final float dstAspect = (float)dstWidth / (float)dstHeight;
	    if (srcAspect > dstAspect) {
	      return srcHeight / dstHeight;
	    } else {
	      return srcWidth / dstWidth;
	    }
	  }
	}
	
	public static Bitmap toRoundBitmap(Bitmap bitmap) { 
        int width = bitmap.getWidth(); 
        int height = bitmap.getHeight();
        float offest = width / 9;
        float roundPx; 
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom; 
        if (width <= height) { 
            roundPx = width / 2 - offest; 
            top = 0; 
            bottom = width; 
            left = 0; 
            right = width; 
            height = width; 
            dst_left = 0 + offest; 
            dst_top = 0 + offest; 
            dst_right = width - offest; 
            dst_bottom = width - offest; 
        } else { 
            roundPx = height / 2; 
            float clip = (width - height) / 2; 
            left = clip; 
            right = width - clip; 
            top = 0; 
            bottom = height; 
            width = height; 
            dst_left = 0; 
            dst_top = 0; 
            dst_right = height; 
            dst_bottom = height; 
        } 
                  
        Bitmap output = Bitmap.createBitmap(width, 
                height, Bitmap.Config.ARGB_8888); 
        Canvas canvas = new Canvas(output); 

        final int color = 0xff424242; 
        final Paint paint = new Paint(); 
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom); 
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom); 
        final RectF rectF = new RectF(dst); 
        paint.setAntiAlias(true); 

        canvas.drawARGB(0, 0, 0, 0); 
        paint.setColor(color); 
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); 
        canvas.drawBitmap(bitmap, src, dst, paint); 
        return output; 
    }
	
     public static byte[] Bitmap2Bytes(Bitmap bm) {
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             return baos.toByteArray();
    }
}
