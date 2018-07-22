package com.wework.assignment.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Class ThreadPool.
 */
public class ThreadPool {

	/** The threads. */
	private final List<PoolWorker> threads;

	/** The queue. */
	private final LinkedBlockingQueue<Runnable> queue;

	/** The is alive. */
	private volatile boolean isAlive = true;

	/**
	 * Instantiates a new thread pool.
	 *
	 * @param threadCount the thread count
	 */
	public ThreadPool(int threadCount) {

		queue = new LinkedBlockingQueue<>();
		threads = new ArrayList<>(threadCount);
		isAlive = true;

		for (int i = 0; i < threadCount; i++) {
			PoolWorker thread = new PoolWorker();
			threads.add(thread);
			thread.start();
		}
	}

	/**
	 * Execute.
	 *
	 * @param task the task
	 */
	public void execute(Runnable task) {
		synchronized (queue) {
			queue.add(task);
			queue.notify();
		}
	}

	/**
	 * Join.
	 */
	public void join() {
		synchronized (queue) {
			isAlive = false;
		}

		// wait for all threads to finish
		for (int i = 0; i < threads.size(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * The Class PoolWorker.
	 */
	private class PoolWorker extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			Runnable webSearchExecutor;
			while (true) {
				synchronized (queue) {
					if (queue.isEmpty()) {
						if (!isAlive) {
							return;
						}

						try {
							queue.wait();
						} catch (InterruptedException e) {
						}
					}
					webSearchExecutor = queue.poll();
				}

				try {
					webSearchExecutor.run();
				} catch (RuntimeException e) {

				}
			}
		}
	}
}