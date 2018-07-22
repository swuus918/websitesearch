package com.wework.assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * A factory for creating WebSearchProducer objects.
 */
public class WebSearchProducerFactory {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebSearchProducerFactory.class.getName());

	/** The blocking queue. */
	private BlockingQueue<String> blockingQueue;

	/** The Constant SOURCE_WEBSITES_URL. */
	private static final String SOURCE_WEBSITES_URL = "https://s3.amazonaws.com/fieldlens-public/urls.txt";

	/**
	 * Gets the website blocking queue.
	 *
	 * @return the website blocking queue
	 */
	public BlockingQueue<String> getWebsiteBlockingQueue() {
		blockingQueue = new LinkedBlockingQueue<>();

		URL sourceWebSites;
		try {
			sourceWebSites = new URL(SOURCE_WEBSITES_URL);
			BufferedReader in = new BufferedReader(new InputStreamReader(sourceWebSites.openStream()));

			String line;
			while ((line = in.readLine()) != null) {
				String website = line.split(",")[1].trim().replace("/", "").replace("\"", "");
				if (website.equals("URL"))
					continue;
				blockingQueue.add(website);
			}
			in.close();

		} catch (IOException e) {
			LOGGER.error("Having issue when getting the list of websites from [" + SOURCE_WEBSITES_URL + "] - "
					+ e.getMessage());
		}

		return blockingQueue;
	}
}