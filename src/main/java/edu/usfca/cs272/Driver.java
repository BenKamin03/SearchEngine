package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.FileHandler;
import edu.usfca.cs272.utils.FileStemmer;
import edu.usfca.cs272.utils.InvertedIndex;
import edu.usfca.cs272.utils.JsonWriter;
import java.nio.charset.StandardCharsets;

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
		ArgumentParser parser = new ArgumentParser();

		parser.parse(args);

		Path text = parser.getPath("-text");

		Path countsPath = null, indexesPath = null, queryPath = null, resultsPath = null;
		if (parser.hasFlag("-counts"))
			countsPath = parser.getPath("-counts", Path.of("counts.json"));
		if (parser.hasFlag("-index"))
			indexesPath = parser.getPath("-index", Path.of("index.json"));
		if (parser.hasFlag("-query"))
			queryPath = parser.getPath("-query");
		if (parser.hasFlag("-results"))
			resultsPath = parser.getPath("-results", Path.of("results.json"));

		InvertedIndex invertedIndex = new InvertedIndex();

		try {
			FileHandler fileHandler = new FileHandler(invertedIndex);
			fileHandler.fillInvertedIndex(text, invertedIndex);

			if (indexesPath != null) {
				JsonWriter.writeObjectHash(invertedIndex.getIndexes(), indexesPath);
			}
			if (countsPath != null) {
				JsonWriter.writeObject(invertedIndex.getCounts(), countsPath);
			}

			if (queryPath != null) {

				try (BufferedReader reader = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
					String line = null;

					while ((line = reader.readLine()) != null) {
						TreeSet<String> searchStems = FileStemmer.uniqueStems(line);

						for (String stem : searchStems) {
							TreeMap<String, TreeSet<Integer>> indexesOfWord = invertedIndex.getIndexes()
									.get(stem);
							if (indexesOfWord != null) {
								var iterator = indexesOfWord.keySet().iterator();
								while (iterator.hasNext()) {
									String file = iterator.next();
									invertedIndex.addQuery(searchStems, file);
								}
							} else {
								invertedIndex.addQuery(searchStems);
							}
						}
					}

				}
			}
			if (resultsPath != null) {
				invertedIndex.sortQuery();
				JsonWriter.writeMapCollectionObject(invertedIndex.getQuery(), resultsPath);
			}
		} catch (IOException ex) {
			System.out.println("Missing input file.");
		}
	}
}

