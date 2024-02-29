package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import edu.usfca.cs272.utils.ArgumentParser;
import edu.usfca.cs272.utils.FileHandler;
import edu.usfca.cs272.utils.InvertedIndex;
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
		
		/* TODO 
		if (parser.hasFlag("-text")) {
			this stuff here
		}
		
		if -index
			try {
				JsonWriter.writeObjectHash(invertedIndex.getIndexes(), indexesPath);
			}
	
		*/
		
		
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

/*
TODO
Description	Resource	Path	Location	Type
Javadoc: Missing comment for private declaration	Driver.java	/SearchEngine/src/main/java/edu/usfca/cs272	line 46	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 10	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 10	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 10	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 11	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 85	Java Problem
Javadoc: Missing comment for private declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 10	Java Problem
Javadoc: Missing comment for private declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 11	Java Problem
Javadoc: Missing comment for public declaration	ArgumentParser.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 164	Java Problem
Javadoc: Missing comment for public declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 8	Java Problem
Javadoc: Missing comment for public declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 26	Java Problem
Javadoc: Missing comment for public declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 33	Java Problem
Javadoc: Missing comment for public declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 69	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 8	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 13	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 18	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 22	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 29	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 36	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 45	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 42	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 142	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 216	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 297	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 414	Java Problem
Javadoc: Missing tag for declared exception IOException	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 45	Java Problem
Javadoc: Missing tag for parameter countsPath	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 19	Java Problem
Javadoc: Missing tag for parameter hash	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 387	Java Problem
Javadoc: Missing tag for parameter hash	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 438	Java Problem
Javadoc: Missing tag for parameter indexesPath	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 19	Java Problem
Javadoc: Missing tag for parameter invertedIndex	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 19	Java Problem
Javadoc: Missing tag for parameter textPath	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 19	Java Problem
Javadoc: Parameter elements is not declared	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 379	Java Problem
Javadoc: Parameter elements is not declared	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 425	Java Problem
Javadoc: Parameter hash is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 41	Java Problem
Javadoc: Parameter parser is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 17	Java Problem
*/