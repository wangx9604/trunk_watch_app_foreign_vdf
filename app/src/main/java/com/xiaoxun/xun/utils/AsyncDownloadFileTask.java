package com.xiaoxun.xun.utils;

import java.io.File;

public class AsyncDownloadFileTask extends AbstractAsyncTask<Object>{
	private String 					mUrl = null;
	private String					mFilePath = null;
	private IAsyncDownLoadFileHandler mDownFileHandler = null;
	
	/**
	 * 
	 * @param context: ������
	 * @param url��	      �ļ����ص�ַ
	 * @param filePath: �ļ�����·��
	 * @param callback: ����֪ͨ
	 */
	
	
	public AsyncDownloadFileTask(Object context,String url,String filePath,IAsyncDownLoadFileHandler handler)
	{
		super(context);
		mUrl = url;
		mFilePath = filePath;
		mDownFileHandler = handler;
	}

	@Override
	protected final Object execute() {
		File file = new File(mFilePath);
		if (file.exists()) {
			LogUtil.i("already downloaded!");
		} 
		else 
		{
			File tmpFile = new File(file.getPath() + ".tmp");

			LogUtil.i(mUrl + " " + tmpFile.getPath());
			//download(mUrl, tmpFile, false);
			if (DownloadHelper.downloadFile(mUrl, tmpFile, false, true, null))
			{
				tmpFile.renameTo(file);
			}
			else
			{
				return null;
			}
			LogUtil.i(mUrl + " " + file.getPath() + " OK");
		}
		if (mDownFileHandler != null)
			return mDownFileHandler.decode(file);
		return file;
	}
	
	@Override
	protected void onPostExecute(Object context, Object objResult) {
		if (mDownFileHandler != null)
		{
			mDownFileHandler.onFileDownloaded(context, objResult);
		}
	}

	public interface IAsyncDownLoadFileHandler{
		Object decode(File fileObject);
		void onFileDownloaded(Object context,Object result);
	}

}
