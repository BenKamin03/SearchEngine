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

	/**
	* Runs the InvertedIndex program. This method is called from the command line. The arguments are parsed and passed to the class as arguments
	* 
	* @param args - The command line arguments
	*/
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

		try {
			FileHandler fileHandler = new FileHandler(indexesPath, countsPath, invertedIndex);
			if (text != null) {
				fileHandler.fillInvertedIndex(text, invertedIndex);
			}
			if (indexesPath != null) {
				JsonWriter.writeObjectHash(invertedIndex.getIndexes(), indexesPath);
			}
			if (countsPath != null) {
				JsonWriter.writeObject(invertedIndex.getCounts(), countsPath);
			}
		} catch (IOException ex) {
			System.out.println("Missing input file.");
		}
	}
}

/*
 * TODO Fix the Javadoc warnings in the code.
 * 
 * Other developers will *not* use poorly unprofessionally documented code
 * regardless of whether the code itself is well designed! It is a tedious but
 * critical step to the final steps of refactoring. The "Configuring Eclipse"
 * guide on the course website shows how to setup Eclipse to see the Javadoc
 * warnings. (Open the "View Screenshot" section.) Here is a direct link:
 * 
 * https://usf-cs272-spring2024.notion.site/Configuring-Eclipse-4f735d746e004dbdbc34af6ad2d988cd#1a1a870909bb45f2a92ef5fc51038635
 * 
 * When conducting asynchronous reviews, I will no longer review code with major
 * formatting issues or warnings in it. Please do a complete pass of your code
 * for these issues before requesting code review.
 * 
 * The warnings found are below for reference:
Description	Resource	Path	Location	Type
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 17	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 17	Java Problem
Javadoc: Missing comment for private declaration	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 18	Java Problem
Javadoc: Missing comment for private declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 18	Java Problem
Javadoc: Missing comment for private declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 19	Java Problem
Javadoc: Missing comment for public declaration	ArgumentParser.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 164	Java Problem
Javadoc: Missing comment for public declaration	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 21	Java Problem
Javadoc: Missing comment for public declaration	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 151	Java Problem
Javadoc: Missing tag for declared exception IOException	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 39	Java Problem
Javadoc: Missing tag for declared exception IOException	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 51	Java Problem
Javadoc: Missing tag for declared exception IOException	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 81	Java Problem
Javadoc: Missing tag for declared exception IOException	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 239	Java Problem
Javadoc: Missing tag for declared exception IOException	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 331	Java Problem
Javadoc: Missing tag for declared exception IOException	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 460	Java Problem
Javadoc: Missing tag for parameter countsPath	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 26	Java Problem
Javadoc: Missing tag for parameter element	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 51	Java Problem
Javadoc: Missing tag for parameter hash	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 420	Java Problem
Javadoc: Missing tag for parameter hash	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 484	Java Problem
Javadoc: Missing tag for parameter indexesPath	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 26	Java Problem
Javadoc: Missing tag for parameter input	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 51	Java Problem
Javadoc: Missing tag for parameter invertedIndex	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 26	Java Problem
Javadoc: Missing tag for parameter requireText	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 51	Java Problem
Javadoc: Parameter elements is not declared	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 412	Java Problem
Javadoc: Parameter elements is not declared	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 471	Java Problem
Javadoc: Parameter hash is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 47	Java Problem
Javadoc: Parameter isDirectory is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 49	Java Problem
Javadoc: Parameter parser is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 24	Java Problem
Javadoc: Parameter p is not declared	FileHandler.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 48	Java Problem
Javadoc: The method writeObject(Map<String,? extends Number>) in the type JsonWriter is not applicable for the arguments (Collection)	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 328	Java Problem
Javadoc: The method writeObjectArrays(Map<String,? extends Collection<? extends Number>>) in the type JsonWriter is not applicable for the arguments (Collection)	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 456	Java Problem
Javadoc: The method writeQuote(String, Writer, int) in the type JsonWriter is not applicable for the arguments (Map, Writer, int)	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 234	Java Problem
Javadoc: The method writeQuote(String, Writer, int) in the type JsonWriter is not applicable for the arguments (Map, Writer, int)	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 327	Java Problem
Javadoc: The method writeQuote(String, Writer, int) in the type JsonWriter is not applicable for the arguments (Map, Writer, int)	JsonWriter.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 455	Java Problem

 */
