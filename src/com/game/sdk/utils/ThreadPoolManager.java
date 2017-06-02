package com.game.sdk.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author janecer 2014年4月10日上午11:46:31 线程池管理
 */
public class ThreadPoolManager {

	private static ThreadPoolManager tpm;
	private ExecutorService service;

	private ThreadPoolManager() {
		// 返回java虚拟机可用处理器的数目
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num * 2);
	}

	public static ThreadPoolManager getInstance() {
		if (null == tpm) {
			tpm = new ThreadPoolManager();
		}
		return tpm;
	}

	public void addTask(Runnable task) {
		service.submit(task);
	}
}
