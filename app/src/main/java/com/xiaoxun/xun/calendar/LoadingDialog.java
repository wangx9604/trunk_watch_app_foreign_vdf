package com.xiaoxun.xun.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.R;

public class LoadingDialog extends Dialog implements View.OnClickListener{

	private Context mContext;
	private LinearLayout waitingView;
	private LinearLayout reloadView;
	private ProgressBar loadingBar;
	private ImageView exclamation;
	private TextView waitingText;
	private Button cancelBtn;
	private Button confirmBtn;
	private TextView contentText;
	private Boolean enableKeyBack = true;
	
	private OnConfirmClickListener mConfirmListen = null;
	
	private Handler mHandler = new Handler();
	
	private Runnable mRun = new Runnable() {
		
		@Override
		public void run() {
			
			changeStatus(0,"当天无轨迹数据");
		}
	};

	public interface OnConfirmClickListener {
		void confirmClick();
	}
		
	public LoadingDialog(Context context, int theme , OnConfirmClickListener listen) {
		super(context, theme);
		mContext = context;
		mConfirmListen = listen;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.history_trace_bottom_dialog, null);
		waitingView = layout.findViewById(R.id.waitingView);
		reloadView = layout.findViewById(R.id.reloadView);
		loadingBar = layout.findViewById(R.id.loadingBar);
		exclamation = layout.findViewById(R.id.exclamation);
		waitingText = layout.findViewById(R.id.waitingText);
		cancelBtn = layout.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(this);
		confirmBtn = layout.findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);
		contentText = layout.findViewById(R.id.contentText);
		
		Window w = this.getWindow();
		w.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		onWindowAttributesChanged(lp);
		enableCancel(false);
		setContentView(layout);
	}

	public void enableCancel(boolean enable){
		//setCancelable(enable);
		setCanceledOnTouchOutside(enable);		
	}

	/*
	* modify by fushiqing
	* 添加back键的使能接口
	* ture：可以返回
	* false：不可以返回
	* */
	public void enableKeyBack(boolean enable){
		enableKeyBack = enable;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		return  enableKeyBack;
	}

	public void hideReloadView(){
	    reloadView.setVisibility(View.GONE);
	}
	public void changeImgHandler(){
		mHandler.postDelayed(mRun, 2000);
	}
	
	public void changeStatus(int status , String cause){
		if(status == 1){
			waitingView.setVisibility(View.VISIBLE);
			reloadView.setVisibility(View.GONE);			
			loadingBar.setVisibility(View.VISIBLE);
			exclamation.setVisibility(View.GONE);
			waitingText.setText(cause);
		} else if(status == 2){
			waitingView.setVisibility(View.GONE);
			reloadView.setVisibility(View.VISIBLE);
			loadingBar.setVisibility(View.GONE);
			exclamation.setVisibility(View.VISIBLE);
			exclamation.setImageDrawable(mContext.getResources().getDrawable(R.drawable.exclamation_mark_1));
			contentText.setText(cause);
		}else if(status == 3){
			waitingView.setVisibility(View.VISIBLE);
			reloadView.setVisibility(View.GONE);
			loadingBar.setVisibility(View.GONE);
			exclamation.setVisibility(View.VISIBLE);
			exclamation.setImageDrawable(mContext.getResources().getDrawable(R.drawable.success));
			waitingText.setText(cause);
		}else if(status == 4){
			waitingView.setVisibility(View.VISIBLE);
			reloadView.setVisibility(View.GONE);
			loadingBar.setVisibility(View.GONE);
			exclamation.setVisibility(View.VISIBLE);
			exclamation.setImageDrawable(mContext.getResources().getDrawable(R.drawable.failed));
			waitingText.setText(cause);
		} else{
			loadingBar.setVisibility(View.GONE);
			exclamation.setVisibility(View.VISIBLE);
			waitingText.setText(cause);
		}	
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.cancelBtn:
			dismiss();
			break;
		case R.id.confirmBtn:
			mConfirmListen.confirmClick();
			enableCancel(false);
			changeStatus(1,getContext().getResources().getString(R.string.trace_getting_data));
			break;
		}		
	}
}
