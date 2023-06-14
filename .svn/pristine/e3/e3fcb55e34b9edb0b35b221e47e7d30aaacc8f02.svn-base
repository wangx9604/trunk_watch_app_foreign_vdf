package com.xiaoxun.xun.utils;

import android.os.Handler;

public abstract class AbstractAsyncTask<T> implements Runnable{
	//���÷��ᴴ��holderHandler,�첽Task�����첽֪ͨ�����ִ�н��
	private Handler mHolderHandler = new Handler();
	private boolean mCanceled = false;
	//��������ִ�н��
	private T 		mResultObject = null;
	private Thread 	mThread = null;
	protected Object 	mContext = null;
	
	public AbstractAsyncTask(Object context)
	{
		mContext = context;
	}
	
	//ȡ������ִ��
	public final synchronized void cancel(boolean bCanceled)
	{
		if (mCanceled != bCanceled)
		{
			mCanceled = bCanceled;
			Thread curThread = mThread;
			if (mCanceled == true && curThread != null)
			{
				curThread.interrupt();
			}
		}
	}
	
	public final synchronized boolean isCanceled()
	{
		return mCanceled;
	}
	
	//postִ�н���holder
	protected abstract void onPostExecute(Object context,T objResult);
	
	//execute����ִ�н��
	protected abstract T execute();
	
	//run����������ͨ��override��ʽ��������
	//��Ϊrun()���Ѿ������첽���õ��߼�
	@Override
	public final void run() {
		synchronized(this)
		{
			mThread = Thread.currentThread();
		}
		// TODO Auto-generated method stub
		if(!mThread.isInterrupted())
		{
			//��������û��ȡ����ִ��execute()����
			if (!isCanceled())
			{
				mResultObject = execute();
				mHolderHandler.post(new Runnable(){
                    @Override
                    public void run() { 
                    	if (!isCanceled())
                    	onPostExecute(mContext,mResultObject);
                    }
                }); 
			}
		}
		synchronized(this)
		{
			mThread = null;
		}
	}
	
}
