package com.weexbox.core.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * 线程，异步任务，异步线程池管理
 *
 * Created by masanbing on 15-11-17.
 */
public class TaskManager {

	/** 工作线程，用于处理时间比较长的异步任务,该线程相对普通线程会创建一个looper */
	private static HandlerThread sWorkerThread = new HandlerThread("work_thread");
	static {
		sWorkerThread.start();
	}
	/** 在sWorkerThread创建一个handler */
	private static Handler sWorkHandler = new Handler(sWorkerThread.getLooper());

	/** 在ui主线程上创建一个handler */
	private static Handler sUIHandler = new Handler(Looper.getMainLooper());

	/**
	 * <br>提交一个Runable到work_thread线程上
	 * <br>注：所有数据库操作都必须在该数据库线程上执行
	 * <br>需要串行，避免互斥访问的任务也可提交到该工作线程上运行
	 * 
	 * @param r
	 */
	public static void execWorkTask(Runnable r) {
		if (r == null) {
			return;
		}
		sWorkHandler.post(r);
	}

	/**
	 * post 一个delay 的runnable 到work_thread线程
	 * <br>注：所有数据库操作都必须在该数据库线程上执行
	 * <br>需要串行，避免互斥访问的任务也可提交到该工作线程上运行
	 * @param r
	 * @param delay
	 */
	public static void execWorkTaskDelay(Runnable r, long delay) {
		if (r == null || sWorkHandler == null) {
			return;
		}

		if (delay > 0) {
			sWorkHandler.postDelayed(r, delay);
		} else {
			execWorkTask(r);
		}
	}

	/**
	 * 移除work_thread一个runnable任务
	 * @param r
	 */
	public static void removeWorkTask(Runnable r) {
		if (r == null || sWorkHandler == null) {
			return;
		}
		sWorkHandler.removeCallbacks(r);
	}

	/** <br>功能简述:提交一个异步任务到异步线程池
	 * <br>功能详细描述:在异步任务线程池中选一个空闲的线程执行该异步任务
	 * <br>注意:若第一次执行异步任务，将会创建一个异步任务线程池
	 * <br>一些独立的，耗时的，非数据库操作的后台任务可以提交到异步线程池执行
	 * @param r
	 */
	@SuppressLint("NewApi")
	public static void execAsynTask(Runnable r) {
		if (r == null) {
			return;
		}

		AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
	}

	/** <br>功能简述:执行一个异步任务
	 * <br>功能详细描述:在异步任务线程池中选一个空闲的线程执行该异步任务
	 * <br>注意:由于AsynTask没有延时功能,到达延时时间后由work thread线程去发起该异步任务
	 * <br>一些独立的，耗时的，非数据库操作的后台任务可以提交到异步线程池执行
	 * @param r
	 * @param delay
	 */
	@SuppressLint("NewApi")
	public static void execAsynTaskDelay(final Runnable r, long delay) {
		if (r == null || sWorkHandler == null) {
			return;
		}

		if (delay > 0) {
			sWorkHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
				}
			}, delay);
		} else {
			AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
		}
	}

	/** <br>功能简述:在ui线程上执行一个任务
	 * <br>功能详细描述:该任务并非在一个新的线程上执行，而是提交一个异步任务到ui线程任务队列
	 * <br>注意:因为ui线程耗时过长会报anr异常，所以提交的任务避免耗时的后台操作
	 * @param r
	 */
	public static void execTaskOnUIThread(Runnable r) {
		sUIHandler.post(r);
	}

	/** <br>功能简述:延迟一段时间在ui线程上执行任务
	 * <br>功能详细描述::该任务并非在一个新的线程上执行，而是提交一个异步任务到ui线程任务队列
	 * <br>注意:因为ui线程耗时过长会报anr异常，所以提交的任务避免耗时的后台操作
	 * @param r ui任务
	 * @param delay 任务延时时间
	 */
	public static void execTaskOnUIThreadDelay(Runnable r, long delay) {
		sUIHandler.postDelayed(r, delay);
	}
	
	public static void removeUITask(Runnable r) {
		sUIHandler.removeCallbacks(r);
	}
	
	/** <br>功能简述:确保运行在主线程断言
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public static void assertMainThread() {
		if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
			throw new IllegalStateException("Must be called on the main thread.");
		}
	} 
	
	/** <br>功能简述:确保运行在工作线程断言
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public static void assertWorkThread() {
		if (sWorkerThread != Thread.currentThread()) {
			throw new IllegalStateException("Must be called on the work thread.");
		}
	}
}
