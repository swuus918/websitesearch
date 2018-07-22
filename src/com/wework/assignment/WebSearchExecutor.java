package com.wework.assignment;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * The Class WebSearchExecutor.
 */
public class WebSearchExecutor implements Runnable {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebSearchExecutor.class.getName());

	/** The blocking queue. */
	private BlockingQueue<String> blockingQueue;

	/** The message blocking queue. */
	private BlockingQueue<String> messageBlockingQueue;

	/** The is match predicate. */
	private Predicate<String> isMatchPredicate;

	/** The http timeout. */
	private Integer httpTimeout;

	/**
	 * Instantiates a new web search executor.
	 *
	 * @param blockingQueue        the blocking queue
	 * @param messageBlockingQueue the message blocking queue
	 * @param isMatchPredicate     the is match predicate
	 * @param httpTimeout          the http timeout
	 */
	public WebSearchExecutor(BlockingQueue<String> blockingQueue, BlockingQueue<String> messageBlockingQueue,
			Predicate<String> isMatchPredicate, Integer httpTimeout) {
		this.blockingQueue = blockingQueue;
		this.messageBlockingQueue = messageBlockingQueue;
		this.isMatchPredicate = isMatchPredicate;
		this.httpTimeout = httpTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while (!blockingQueue.isEmpty()) {
			// Serving the customer with the token
			String website = blockingQueue.poll();
			if (website == null) {
				return;
			}

			websearch(website);
		}
	}

	/**
	 * Websearch.
	 *
	 * @param website the website
	 */
	private void websearch(String website) {

		String html = null;
		String line;

		try {
			html = Jsoup.connect("http://" + website).timeout(httpTimeout).get().html();
		} catch (IOException e) {
			line = website + " cannot be processed properly";
			LOGGER.info(line + " - " + e.getMessage());
			messageBlockingQueue.add(line);
			return;
		}

		if (isMatchPredicate.test(html)) {
			line = website + " has the word!";
		} else {
			line = website + " has No such word!";
		}

		LOGGER.info(line);
		messageBlockingQueue.add(line);
	}
}