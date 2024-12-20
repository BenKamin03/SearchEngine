package edu.usfca.cs272;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.FileHandler;
import edu.usfca.cs272.utils.InvertedIndex;
import edu.usfca.cs272.utils.MultiThreadedFileHandler;
import edu.usfca.cs272.utils.MultiThreadedInvertedIndex;
import edu.usfca.cs272.utils.MultiThreadedQueryHandler;
import edu.usfca.cs272.utils.QueryHandler;
import edu.usfca.cs272.utils.QueryHandlerInterface;
import edu.usfca.cs272.utils.WebCrawler;
import edu.usfca.cs272.utils.WebServer;
import edu.usfca.cs272.utils.WorkQueue;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {

		// store initial start time
		Instant start = Instant.now();

		System.out.println("Working Directory: " + Path.of(".").toAbsolutePath().normalize().getFileName());
		System.out.println("Arguments: " + Arrays.toString(args));
		long elapsed = Duration.between(start, Instant.now()).toMillis();

		run(args);

		// calculate time elapsed and output
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	/**
	 * Runs the InvertedIndex program. This method is called from the command line.
	 * The arguments are parsed and passed to the class as arguments
	 * 
	 * @param args - The command line arguments
	 */
	private static void run(String[] args) {
		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex invertedIndex;
		QueryHandlerInterface queryHandler;
		FileHandler fileHandler;
		WorkQueue workQueue;

		if (parser.hasFlag("-threads") || parser.hasFlag("-html")) {
			int threads = parser.getInteger("-threads", 5);
			if (threads < 1) {
				threads = 5;
			}

			workQueue = new WorkQueue(threads);

			invertedIndex = new MultiThreadedInvertedIndex();
			queryHandler = new MultiThreadedQueryHandler(invertedIndex, parser.hasFlag("-partial"), workQueue);
			fileHandler = new MultiThreadedFileHandler((MultiThreadedInvertedIndex) invertedIndex, workQueue);
		} else {
			invertedIndex = new InvertedIndex();
			queryHandler = new QueryHandler(invertedIndex, parser.hasFlag("-partial"));
			fileHandler = new FileHandler(invertedIndex);
			workQueue = null;
		}

		if (parser.hasFlag("-text")) {
			Path text = parser.getPath("-text");

			try {
				if (text != null)
					fileHandler.fillInvertedIndex(text);
				else
					System.out.println("The path given by -text is null");
			} catch (FileNotFoundException fnf) {
				System.out.println("The -text flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error with -text file ");
			}
		}

		if (parser.hasFlag("-html")) {
			if (parser.hasValue("-html")) {
				if (workQueue == null) {
					workQueue = new WorkQueue();
				}

				WebCrawler webCrawler = new WebCrawler((MultiThreadedInvertedIndex) invertedIndex, workQueue);
				try {
					webCrawler.crawl(new URI(parser.getString("-html")), parser.getInteger("-crawl", 1));
				} catch (URISyntaxException e) {
					System.out.println("Error with URI syntax in '-html' tag");
				}
			} else {
				System.out.println("Missing value for '-html' tag");
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");

			try {
				if (queryPath != null)
					queryHandler.handleQueries(queryPath);
				else
					System.out.println("The path given by -query is null");
			} catch (FileNotFoundException fnf) {
				System.out.println("The -query flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error with -query flag");
			}
		}

		if (workQueue != null) {
			workQueue.shutdown();
		}

		if (parser.hasFlag("-server")) {

			int port = parser.getInteger("-server", 3000);

			WebServer webServer = new WebServer(port, queryHandler);
			try {
				webServer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));

			try {
				invertedIndex.writeCounts(countsPath);
			} catch (FileNotFoundException fnf) {
				System.out.println("The -counts flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error with -counts file");
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexesPath = parser.getPath("-index", Path.of("index.json"));

			try {
				invertedIndex.writeIndex(indexesPath);
			} catch (FileNotFoundException fnf) {
				System.out.println("The -index flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error with -index flag");
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));

			try {
				queryHandler.writeQuery(resultsPath);
			} catch (FileNotFoundException fnf) {
				System.out.println("The -results flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error with -results flag");
			}
		}
	}
}