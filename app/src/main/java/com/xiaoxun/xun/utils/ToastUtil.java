/**
 * 
 */
package com.xiaoxun.xun.utils;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.Params;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchData;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	public static Toast toastStr;
	public synchronized static void show(final Context context, final String info) {
		try {
			if (toastStr == null) {
				toastStr = Toast.makeText(context.getApplicationContext(), info, Toast.LENGTH_LONG);
			} else {
				toastStr.setText(info);
			}
			toastStr.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static Toast toastInt;
	public synchronized static void show(Context context, int info) {
		try {
			if (toastInt == null) {
				toastInt = Toast.makeText(context.getApplicationContext(), info, Toast.LENGTH_LONG);
			} else {
				toastInt.setText(info);
			}
			toastInt.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	static Toast toast = null;

	public static void showInThread(Context context, String text) {
		try {
			if (toast != null) {
				toast.setText(text);
			} else {
				toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			}
			toast.show();
		} catch (Exception e) {
			//解决在子线程中调用Toast的异常情况处理
			Looper.prepare();
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			Looper.loop();
		}
	}

	public static Toast myToast;
	public static void showMyToast(Context context, String info,int duration ){
	    //separate info str
	    StringBuilder buff = new StringBuilder();
	    buff.append(info);
		try {
			if (myToast == null) {
				myToast = Toast.makeText(context.getApplicationContext(), buff.toString(), duration);
				myToast.getView().setBackgroundResource(R.drawable.toast_bg_0);

				int margin = 200;
				int padding = DensityUtil.dip2px(context, (float) 13.3);
				int width = Params.getInstance(context).getScreenWidthInt();

				if (width > 1080) {
					margin = 200;
				} else if (width > 720) {
					margin = 150;
				} else {
					margin = 100;
				}
				myToast.getView().setPadding(padding, padding, padding, padding);
				myToast.setGravity(Gravity.BOTTOM, 0, margin);
			} else {
				myToast.setText(buff.toString());
			}
			myToast.show();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 最大版本，大于等于所支持的版本，提示升级固件，否则提示待上线
	 */
	public static void showNewVerToast(Context context, WatchData watch, String supportVersion) {
		boolean isLowerSupportVersion=((ImibabyApp)context.getApplicationContext()).isLowerSupportVersion(watch,supportVersion);
		if(isLowerSupportVersion){
			ToastUtil.show(context, context.getString(R.string.please_wait_support_version));
		}else {
			ToastUtil.show(context, context.getString(R.string.please_update_new_version));
		}
	}
}
