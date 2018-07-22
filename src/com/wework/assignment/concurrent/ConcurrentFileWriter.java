package com.wework.assignment.concurrent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

/**
 * The Class ConcurrentFileWriter.
 */
public class ConcurrentFileWriter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ConcurrentFileWriter.class.getName());

	/** The queue. */
	private final BlockingQueue<String> queue;

	/** The is active. */
	private boolean isActive;

	/** The worker. */
	private Worker worker;

	/** The filename. */
	private String filename;

	/**
	 * Instantiates a new concurrent file writer.
	 *
	 * @param queue    the queue
	 * @param filename the filename
	 */
	public ConcurrentFileWriter(BlockingQueue<String> queue, String filename) {
		this.queue = queue;
		this.filename = filename;
		this.isActive = true;
		this.worker = new Worker();
	}

	/**
	 * Put message.
	 *
	 * @param message the message
	 * @throws InterruptedException the interrupted exception
	 */
	public void putMessage(String message) throws InterruptedException {
		queue.put(message);
	}

	/**
	 * Execute.
	 */
	public void execute() {
		worker.start();
	}

	/**
	 * Join.
	 */
	public void join() {
		synchronized (queue) {
			isActive = false;
		}
	}

	/**
	 * The Class Worker.
	 */
	private class Worker extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				File file = new File(filename);

				LOGGER.info("Creating new output file [" + filename + "]");
				file.createNewFile();

				FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
				BufferedWriter bw = new BufferedWriter(fw);

				String line = "";

				while (true) {
					synchronized (queue) {
						if (!queue.isEmpty()) {
							line = queue.take();
							bw.write(line + "\n"); // write to file
							bw.flush();

						}
						if (!isActive) {
							bw.close();
							fw.close();
							return;
						}
					}
				}
			} catch (IOException | InterruptedException e) {
			}
		}
	}
}
