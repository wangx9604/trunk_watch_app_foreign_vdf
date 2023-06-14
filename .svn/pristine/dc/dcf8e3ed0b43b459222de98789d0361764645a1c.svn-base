package com.xiaoxun.xun.utils;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;


public class TransactionExecutor implements Runnable{
	public static final String FILE_URL_PREFIX = "apkfile://";
	
	private static TransactionExecutor instance = null;
	private static final int FILE_DOWNLOAD_OK 		= 1;
	private static final int FILE_DOWNLOAD_FAILURE 	= 0;
	
	private int 							mMaxThreads;
	private TransactionHandler				mHandler;
	private File							mFileCacheDir;
	private BlockingQueue<TransactionObject> 	mBlockingDeque;
    private TransactionRunnable[] mRunnable;
    private Thread[] mThreads;
	private Object							mLockObject;
	private volatile boolean				bLockExecutor;
	
	private TransactionExecutor(int maxThreads,File cacheDir)
	{
		mMaxThreads 	= maxThreads;
		mHandler 		= new TransactionHandler();
		mLockObject     = new Object();

		mFileCacheDir 	= cacheDir;
		
		//创建阻塞队列. 阻塞队列必须先于线程的创建
		mBlockingDeque = new LinkedBlockingQueue<TransactionObject>();
		
		//创建线程池
		mThreads  = new Thread[mMaxThreads];
		mRunnable = new TransactionRunnable[mMaxThreads];
		for (int i = 0; i < mMaxThreads; i ++)
		{
			mRunnable[i] = new TransactionRunnable();
			mThreads[i] = new Thread(mRunnable[i]);
			mThreads[i].start();
		}
	}
	
	public boolean isExecuting()
	{
		for (int i = 0; i < mMaxThreads; i ++)
		{
			if (mRunnable[i].isExecuting())
				return true;
		}
		return false;
	}
	
	/**
	 * 单例模式
	 * @return
	 */
	public static TransactionExecutor getInstance(File dir)
	{
		if (instance == null)
		{
			instance = new TransactionExecutor(3,dir);
		}
		
		return instance;
	}
	
	public void lock()
	{
		if (bLockExecutor)
			return;
		bLockExecutor = true;
	}
	
	public boolean isLocked()
	{
		return bLockExecutor;
	}
	
	public void unlock()
	{
		bLockExecutor = false;
	}
	
	public boolean submit(Object context,String transactionTag,Object transactionURL,TransactionCallback executorCallback,Object paramObject)
	{
		if (transactionURL == null)
			return false;
		
		TransactionObject transactionObj = new TransactionObject(context,transactionURL,executorCallback);
		transactionObj.setParamObject(paramObject);
		transactionObj.setTransactonTag(transactionTag);
		
		LogUtil.i("submit() downloadURL:"+transactionURL);
		
		if (mBlockingDeque.contains(transactionObj))
		{
			LogUtil.i(transactionURL+" already exists in mBlockingDeque");
			return false;
		}
		
		return mBlockingDeque.add(transactionObj);
	}
	
	public int cancelByTag(String transactionTag)
	{
		if (transactionTag == null)
			return 0;
		
		int size = 0;
		synchronized(mLockObject)
		{
            Object[] objs = mBlockingDeque.toArray();
			if (objs != null)
			{
				for (Object obj:objs)
				{
					TransactionObject transactionObj = (TransactionObject)obj;
					if (transactionObj != null)
					{
						if (transactionTag.equals(transactionObj.getTransactonTag()))
						{
							transactionObj.setCancelled(true);
							mBlockingDeque.remove(transactionObj);
							size ++;
						}
					}
				}
			}
			for (int i = 0; i < mMaxThreads; i ++)
			{
				TransactionObject lastTransactionObj = mRunnable[i].getLastTransactionObject();
				if (lastTransactionObj != null &&
						transactionTag.equals(lastTransactionObj.getTransactonTag()))
				{
					lastTransactionObj.setCancelled(true);
					size ++;
					if (mRunnable[i].isExecuting())
					{
						mThreads[i].interrupt();
					}
				}
			}
		}
		LogUtil.i("cancelByTag() type="+transactionTag+" size="+size);
		
		return size;
	}
	
	public int cancelAll()
	{
		int size = 0;
		
		synchronized (mLockObject) {
			size = mBlockingDeque.size();
			mBlockingDeque.clear();
		}
		
		for (int i = 0; i < mMaxThreads; i ++)
		{
			if (mRunnable[i].isExecuting())
			{
				size ++;
				mThreads[i].interrupt();
			}
		}
		
		return size;
	}
	
	private class TransactionRunnable implements Runnable{
		private volatile boolean mExecuting = false;
		private TransactionObject	lastTransactionObject;
		
		private TransactionObject getLastTransactionObject()
		{
			return lastTransactionObject;
		}
		
		public boolean isExecuting() {
			return mExecuting;
		}

		@Override
		public void run()
		{
			while(true)
			{
				TransactionObject transactionObj = null;
				try
				{				
					Thread.sleep(10);
					synchronized(mLockObject){}
					//从下载队列中摘取下载任务
					transactionObj = mBlockingDeque.take();
					while (bLockExecutor)
					{
						Thread.sleep(100);
					}
					lastTransactionObject = transactionObj;
					if (transactionObj != null)
					{
						LogUtil.i("take transactionObj. transactionURL="+transactionObj.getTransactionURL());
					}
				}
				catch(Exception e)
				{
					LogUtil.e("TransactionRunnable exception:"+e.getMessage());
				}
				if (transactionObj != null)
				{
					mExecuting = true;
					Object resultObject = null;

					Message msg = new Message();
					TransactionCallback callback = transactionObj.getExecutorCallback();
					if (callback != null)
					{
						resultObject = callback.doTransactionInThread(transactionObj);
					}
					if (resultObject == null)
						msg.what = FILE_DOWNLOAD_FAILURE;
					else
						msg.what = FILE_DOWNLOAD_OK;

					//设置任务执行结果
					transactionObj.setResultObject(resultObject);
					msg.obj  = transactionObj;
					if (!Thread.currentThread().isInterrupted())
						mHandler.sendMessage(msg);
					mExecuting = false;
				}
				if (Thread.currentThread().isInterrupted())
				{
					Thread.interrupted(); //清除中断标志,以便能够继续下一个task下载
				}
			}
		}
	}
	
	public static class TransactionObject{
		private String				mTransactonTag = "unkown";
		private Object 				mContext;
		private Object				mTransactionURL;
		private TransactionCallback	mExecutorCallback;
		private Object				mResultObject;
		private Object				mParamObject;
		private volatile boolean	mCancelled;
		
		public TransactionObject(Object mContext, Object mTransactionURL,TransactionCallback mExecutorCallback) {
			super();
			this.mContext = mContext;
			this.mTransactionURL = mTransactionURL;
			this.mExecutorCallback = mExecutorCallback;
			this.mCancelled = false;
		}
		
		public String getTransactonTag() {
			return mTransactonTag;
		}

		public void setTransactonTag(String mTransactonTag) {
			this.mTransactonTag = mTransactonTag;
		}

		public Object getContext() {
			return mContext;
		}
		public Object getTransactionURL() {
			return mTransactionURL;
		}
		public TransactionCallback getExecutorCallback() {
			return mExecutorCallback;
		}
		
		public void setCancelled(boolean mCancelled) {
			this.mCancelled = mCancelled;
		}
		
		
		
		public boolean isCancelled() {
			return mCancelled;
		}


		public void setResultObject(Object mResultObject) {
			this.mResultObject = mResultObject;
		}
		

		public Object getResultObject() {
			return mResultObject;
		}


		public Object getParamObject() {
			return mParamObject;
		}

		public void setParamObject(Object mParamObject) {
			this.mParamObject = mParamObject;
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			TransactionObject transactionObj = (TransactionObject)o;
			Object url = transactionObj.getTransactionURL();
			if (!url.equals(mTransactionURL))
				return false;
			
			Object contextObj = transactionObj.getContext();
			if (contextObj != mContext)
				return false;
			
			TransactionCallback callback = transactionObj.getExecutorCallback();
            return callback == mExecutorCallback;
        }
		
	}
	
	private static class TransactionHandler extends Handler{
		public TransactionHandler() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;
			switch(what)
			{
			case FILE_DOWNLOAD_FAILURE:
			case FILE_DOWNLOAD_OK:
				TransactionObject transactionObj = (TransactionObject)msg.obj;
				if (transactionObj != null)
				{
					TransactionCallback callback = transactionObj.getExecutorCallback();
					if (callback != null)
					{
						if (!transactionObj.isCancelled())
						{
							callback.onTransactionFinished(transactionObj.getContext(), 
									transactionObj.getTransactionURL(), 
									transactionObj.getResultObject(),
									transactionObj.getParamObject());
						}
						else
						{
							callback.onTransactionCancelled(transactionObj.getContext(), transactionObj.getTransactionURL());
						}
					}
				}
				break;
			}
		}
	}
	
	public interface TransactionCallback{
		/**
		 * 文件解码函数.该函数在线程中被调用，并将返回值作为onFileDownloaded的result参数
		 * @param fileObject
		 * @return
		 */
		Object doTransactionInThread(TransactionObject transactionObject);
		/**
		 * 文件下载完成后会执行该函数
		 * @param context: 上下文对象
		 * @param downloadURL:文件的下载URL
		 * @param result:结果对象。具体取决于decodeFileInThread函数
		 */
		void onTransactionFinished(Object context,Object transactionURL,Object result,Object paramObject);
		/**
		 * 任务被取消
		 */
		void onTransactionCancelled(Object context,Object transactionURL);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
