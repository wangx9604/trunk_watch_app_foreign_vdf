package com.xiaoxun.xun.utils;
/**
 * @Description:用于回调
 */
public class CallBack {
	public interface ReturnCallback<T>{
		void back(T obj);
		//public void error(Throwable t);
	}
}
