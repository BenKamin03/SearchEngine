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
	 * Runs the InvertedIndex program. This method is called from the command line.
	 * The arguments are parsed and passed to the class as arguments
	 * 
	 * @param args - The command line arguments
	 */
	private static void run(String[] args) {
		ArgumentParser parser = new ArgumentParser();

		parser.parse(args);
		
		/*
		 * TODO Just do:
		 * 
		 * ArgumentParser parser = new ArgumentParser(args);
		 */

		/*
		 * TODO Do not create these variables here. Declare and define them in the scope
		 * they are needed. There will be too many flag/value pairs to keep this approach
		 * of declaring all the variables you want to use at the start of code. 
		 * 
		 * What happened to: https://github.com/usf-cs272-spring2024/project-BenKamin03/blob/64a28313df7134830b3c159e5a2a01f519567065/src/main/java/edu/usfca/cs272/Driver.java#L62-L72
		 */
		Path text = parser.getPath("-text");

		Path countsPath = null, indexesPath = null;
		if (parser.hasFlag("-counts"))
			countsPath = parser.getPath("-counts", Path.of("counts.json"));
		if (parser.hasFlag("-index"))
			indexesPath = parser.getPath("-index", Path.of("index.json"));

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
		} catch (IOException ex) {
			/*
			 * TODO This exception can also happen when writing to file, so this is not
			 * a good way to handle exceptions here.
			 */
		
			System.out.println("Missing input file.");
		}
	}
	
	/*
	TODO Try this for Driver.main:
	
	Create the argument parser
	Create the empty inverted index 
	
	Separate out the read/write operations into disconnected if blocks...
	Do not test for combinations of flags!
	
	For example:
	
	if (-text flag exists) {
	  get -text flag value
	
	  try { 
	    1-2 lines of code to build the inverted index
	  }
	  catch (...) {
	    User-friendly warning so user knows the operation (build)
	    and parameter (value of the -text flag) that had issues
	  }
	}
	
	Use a similar pattern for the other flag/value pairs
*/

	
	/*
	 * TODO Fix the Javadoc warnings in the code.
	 * 
	 * Other developers will *not* use poorly unprofessionally documented code
	 * regardless of whether the code itself is well designed! It is a tedious but
	 * critical step to the final steps of refactoring. The "Configuring Eclipse"
	 * guide on the course website shows how to setup Eclipse to see the Javadoc
	 * warnings. (Open the "View Screenshot" section.)
	 * 
	 * As announced in class, when conducting asynchronous reviews, I will no longer
	 * review code with warnings or major formatting issues in it. That is a sign
	 * you still need to do a cleanup pass of your code. Please do a complete pass
	 * of your code for these issues before requesting code review. See the
	 * "Project Review" guide for details.
	 * 
	 * For reference, direct links to the guides and the warnings found are included
	 * below.
	 */

	// Configuring Eclipse: https://usf-cs272-spring2024.notion.site/Configuring-Eclipse-4f735d746e004dbdbc34af6ad2d988cd#1a1a870909bb45f2a92ef5fc51038635
	// Project Review: https://usf-cs272-spring2024.notion.site/Project-Review-c04d5128395a4eb499e30f6fbd0c0352
	
	/*-
Description	Resource	Path	Location	Type
The import java.util.ArrayList is never used	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 3	Java Problem
The import java.util.List is never used	InvertedIndex.java	/SearchEngine/src/main/java/edu/usfca/cs272/utils	line 6	Java Problem
	 */
	
	/*
	 * TODO I will still review this since they are easy to fix warnings and the last
	 * review was delayed due to the merge conflicts, but it will be the last time I'll
	 * move forward with a review when there are warnings in the code!
	 */
	
	// TODO I've warned you about this before: https://github.com/usf-cs272-spring2024/project-BenKamin03/pull/8#issuecomment-1984709989
}