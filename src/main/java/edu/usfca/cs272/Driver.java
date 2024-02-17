package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.FileHandler;
import edu.usfca.cs272.utils.InvertedIndex;

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

	private static void run(String[] args) {
		ArgumentParser parser = new ArgumentParser();
		
		parser.parse(args);

		Path text = parser.getPath("-text");

		Path countsPath = null, indexesPath = null;
		if (parser.hasFlag("-counts"))
			countsPath = parser.getPath("-counts", Path.of("counts.json"));
		if (parser.hasFlag("-index"))
			indexesPath = parser.getPath("-index", Path.of("index.json"));

		InvertedIndex invertedIndex = new InvertedIndex();

		if (indexesPath != null || countsPath != null) {
			try {
				FileHandler fileHandler = new FileHandler(text, indexesPath, countsPath, invertedIndex);
				if (text != null) {
					fileHandler.fillInvertedIndex();
				}
				fileHandler.write();
			} catch (IOException ex) {
				System.out.println("Missing input file.");
			}
		} else {
			System.out.println("Missing flag: -index or -counts");
		}
	}
}
