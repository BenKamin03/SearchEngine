package edu.usfca.cs272;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.CountsHandler;
import edu.usfca.cs272.utils.IndexHandler;

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
		ArgumentParser parser = new ArgumentParser();

		// store initial start time
		Instant start = Instant.now();

		System.out.println("Working Directory: " + Path.of(".").toAbsolutePath().normalize().getFileName());
		System.out.println("Arguments: " + Arrays.toString(args));
		long elapsed = Duration.between(start, Instant.now()).toMillis();


		/* TODO
		 *
		InvertedIndex index = ...

		if (parser.hasFlag("-text")) {
      Path in = parser.getPath("-text");

      try {
      		1-2 lines of code here
      }
      catch ( ) {
      		warn the user
      }
		}

		if (parser.hasFlag("-index")) {
      Path in = parser.getPath("-index");

		}

		if (parser.hasFlag("-counts")) {
      Path in = parser.getPath("-counts");

		}
		 */

		parser.parse(args);

		CountsHandler.run(parser);
		IndexHandler.run(parser);

		// calculate time elapsed and output
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	/*
	 * Method to check if a variable is null and set it to a safe value if it is
	 */
	public static <T> T checkSafeValue(T variable, T safeValue) {
          if (variable == null) {
               return safeValue;
          }
          return variable;
     }

	/*
	 * Generally, "Driver" classes are responsible for setting up and calling other
	 * classes, usually from a main() method that parses command-line parameters.
	 * Generalized reusable code are usually placed outside of the Driver class.
	 * They are sometimes called "Main" classes too, since they usually include the
	 * main() method.
	 *
	 * If the driver were only responsible for a single class, we use that class
	 * name. For example, "TaxiDriver" is what we would name a driver class that
	 * just sets up and calls the "Taxi" class.
	 *
	 * The starter code (calculating elapsed time) is not necessary. It can be
	 * removed from the main method.
	 */
}
