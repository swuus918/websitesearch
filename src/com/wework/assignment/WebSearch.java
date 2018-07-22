package com.wework.assignment;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringOptionHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class WebSearch.
 */
public class WebSearch {

	/** The search term. */
	@Option(name = "-searchTerm", required = true, usage = "search term in regex", handler = StringOptionHandler.class)
	private String searchTerm;

	/** The consumer count. */
	@Option(name = "-consumerCount", required = true, usage = "number of concurrent http requests", handler = IntOptionHandler.class)
	private Integer consumerCount;

	/** The output filename. */
	@Option(name = "-outputFilename", required = false, usage = "output filename", handler = StringOptionHandler.class)
	private String outputFilename = "Results.txt";

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws CmdLineException the cmd line exception
	 */
	public static void main(String[] args) throws CmdLineException {
		new WebSearch().processArgs(args);
	}

	/**
	 * Process args.
	 *
	 * @param args the args
	 * @throws CmdLineException the cmd line exception
	 */
	private void processArgs(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.parseArgument(args);

		if (consumerCount <= 0)
			return;

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"com/wework/assignment/config/app-context.xml");

		WebSearchManager manager = (WebSearchManager) context.getBean("webSearchManager");

		manager.execute(searchTerm, consumerCount, outputFilename);
		context.close();
	}
}