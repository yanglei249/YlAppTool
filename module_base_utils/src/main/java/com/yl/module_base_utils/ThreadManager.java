package com.yl.module_base_utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//杨磊新增

/**
 * 管理线程池
 * 
 * @author itcast
 * 
 */
public class ThreadManager {
	private ThreadManager() {

	}
	//参数初始化
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	//核心线程数量大小
	private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
	//线程池最大容纳线程数
	private static final int maximumPoolSize = CPU_COUNT * 2 + 1;

	private static ThreadManager instance = new ThreadManager();
	private ThreadPoolProxy longPool;
	private ThreadPoolProxy shortPool;

	public static ThreadManager getInstance() {
		return instance;
	}

	// 联网比较耗时
	// cpu的核数*2+1
	public synchronized ThreadPoolProxy createLongPool() {
		if (longPool == null) {
			longPool = new ThreadPoolProxy(9, 9, 5000L);
		}
		return longPool;
	}
	// 操作本地文件
	public synchronized ThreadPoolProxy createShortPool() {
		if(shortPool==null){
			shortPool = new ThreadPoolProxy(3, 3, 5000L);
		}
		return shortPool;
	}

	public class ThreadPoolProxy {
		private ThreadPoolExecutor pool;
		private int corePoolSize;
		private int maximumPoolSize;
		private long time;

		public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.time = time;

		}
		/**
		 * 执行任务
		 * @param runnable
		 */
		public void execute(Runnable runnable) {
			if (pool == null) {
				// 创建线程池
				/*
				 * 1. 线程池里面管理多少个线程
				 * 2. 如果排队满了, 额外的开的线程数
				 * 3. 如果线程池没有要执行的任务 存活多久
				 * 4.时间的单位
				 *  5 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到LinkedBlockingQueue对象中 排队
				 */
				pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
						time, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>(10));
			}
			pool.execute(runnable); // 调用线程池 执行异步任务
		}
		/**
		 * 取消任务
		 * @param runnable
		 */
		public void cancel(Runnable runnable) {
			if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
				pool.remove(runnable); // 取消异步任务
			}
		}
	}
}
