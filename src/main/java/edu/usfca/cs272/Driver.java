package edu.usfca.cs272;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.FileStemmer;
import edu.usfca.cs272.utils.JsonWriter;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class Driver {

	private static ArgumentParser parser = new ArgumentParser();

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

		parser.parse(args);
		version1_0();

		// calculate time elapsed and output
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	public static void version1_0() {
		if (parser.hasFlag("-text") && parser.getString("-text") != null && parser.getString("-text") != "") {
			String path = parser.getString("-text");
			String output_path = parser.getString("-counts", "counts.json");

			SortedMap<String, Integer> hash = new TreeMap<>();
			fillHash(hash, FileSystems.getDefault().getPath(path), false);

			try {
				JsonWriter.writeObject(hash, FileSystems.getDefault().getPath(output_path));
			} catch (IOException e) {
			}
		}
	}

	public static void fillHash(Map<String, Integer> hash, Path p, boolean isDirectory) {
		/*
		 * ---------------------------------------------
		 * 
		 * Credit: Sophie Engle
		 * https://github.com/usf-cs272-spring2024/cs272-lectures/blob/main/src/main/
		 * java/edu/usfca/cs272/lectures/basics/io/DirectoryStreamDemo.java
		 * 
		 * ---------------------------------------------
		 */
		if (Files.isDirectory(p)) {
			// Path is a Directory --> Recurse Through Each Sub Path
			try {
				for (Path path : Files.newDirectoryStream(p)) {
					fillHash(hash, path, true);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			// Path is a File --> Base Case
			if (p.getFileName().toString().toLowerCase().endsWith(".txt")
					|| p.getFileName().toString().toLowerCase().endsWith(".text") || !isDirectory) {
				try {
					int size = FileStemmer.listStems(p).size();
					if (size > 0)
						hash.put(p.toString(), (Integer) size);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return;
		}
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
