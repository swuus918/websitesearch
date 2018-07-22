package com.wework.assignment;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.wework.assignment.concurrent.ConcurrentFileWriter;
import com.wework.assignment.concurrent.ThreadPool;

/**
 * The Class WebSearchManager.
 */
public class WebSearchManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebSearchManager.class.getName());

	/** The blocking queue. */
	private BlockingQueue<String> blockingQueue;

	/** The message blocking queue. */
	private BlockingQueue<String> messageBlockingQueue;

	/** The properties. */
	private Properties properties;

	/** The factory. */
	private WebSearchProducerFactory factory;

	/**
	 * Sets the factory.
	 *
	 * @param factory the new factory
	 */
	public void setFactory(WebSearchProducerFactory factory) {
		this.factory = factory;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the new properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Execute.
	 *
	 * @param searchTerm     the search term
	 * @param consumerCount  the consumer count
	 * @param outputFilename the output filename
	 */
	public void execute(String searchTerm, Integer consumerCount, String outputFilename) {
		final Integer httpTimeout = Integer.parseInt(properties.getProperty("HTTP_TIMEOUT"));

		// Get the queue of web sites for threads to do look up
		blockingQueue = factory.getWebsiteBlockingQueue();

		// Initialize one concurrent queue to take all the output strings for
		// concurrentFileWriter to write to one file
		messageBlockingQueue = new LinkedBlockingQueue<>();
		ConcurrentFileWriter concurrentFileWriter = new ConcurrentFileWriter(messageBlockingQueue, outputFilename);

		// Start the worker thread to take string from blocking queue then write into
		// one output file
		concurrentFileWriter.execute();

		final Pattern p = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
		final Predicate<String> isMatchPredicate = html -> p.matcher(html).find();

		LOGGER.info("The searchTerm is [" + searchTerm + "]");
		LOGGER.info("The Http timeout is [" + httpTimeout + "] milliseconds");

		// create the thread pool
		ThreadPool websearchThreadPool = new ThreadPool(consumerCount);

		LOGGER.info("Initial size of URLs: " + blockingQueue.size() + ". Initializing " + consumerCount
				+ " concurrent threads...");

		for (int i = 0; i < consumerCount; i++) {
			// put consumers into thread pool
			websearchThreadPool
					.execute(new WebSearchExecutor(blockingQueue, messageBlockingQueue, isMatchPredicate, httpTimeout));
		}

		// close the pool and wait for all tasks to finish.
		websearchThreadPool.join();
		concurrentFileWriter.join();

		LOGGER.info("final size of URLs: " + blockingQueue.size());
	}
}