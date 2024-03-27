package edu.usfca.cs272;

import java.io.FileNotFoundException;
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

	/**
	 * Runs the InvertedIndex program. This method is called from the command line.
	 * The arguments are parsed and passed to the class as arguments
	 * 
	 * @param args - The command line arguments
	 */
	private static void run(String[] args) {
		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex invertedIndex = new InvertedIndex();
		; // TODO Remove?

		if (parser.hasFlag("-text")) {
			Path text = parser.getPath("-text");
			FileHandler fileHandler = new FileHandler(invertedIndex);
			try {
				fileHandler.fillInvertedIndex(text, invertedIndex);
			} catch (FileNotFoundException fnf) {
				System.out.println("File Not Found"); // TODO Better output! If File Not Found was going to be output every time, we didn't need different try/catch blocks. Try:
				// TODO System.out.println("The -text flag is missing a necessary path value.");
			} catch (IOException io) {
				System.out.println("IO Error"); // TODO Fix all the exception output!
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));

			try {
				invertedIndex.writeCounts(countsPath);
			} catch (FileNotFoundException fnf) {
				System.out.println("File Not Found");
			} catch (IOException io) {
				System.out.println("IO Error");
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexesPath = parser.getPath("-index", Path.of("index.json"));

			try {
				invertedIndex.writeIndex(indexesPath);
			} catch (FileNotFoundException fnf) {
				System.out.println("File Not Found");
			} catch (IOException io) {
				System.out.println("IO Error");
			}
		}
	}
}