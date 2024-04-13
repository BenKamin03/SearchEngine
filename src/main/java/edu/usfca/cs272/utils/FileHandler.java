package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/**
 * Class responsible for filling the InvertedIndex
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class FileHandler {

     /**
      * private InvertedIndex
      */
     private final InvertedIndex invertedIndex;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      * 
      * @param invertedIndex the invertedIndex
      */
     public FileHandler(InvertedIndex invertedIndex) {
          this.invertedIndex = invertedIndex;
     }

     /**
      * Fills the inverted index with the contents of the file.
      * 
      * @param textPath      - Path to the text file to be hashed
      * @param invertedIndex - Inverted index to be
      * @throws IOException the IO exception
      */
     public void fillInvertedIndex(Path textPath, InvertedIndex invertedIndex) throws IOException { // TODO Remove InvertedIndex parameter
          fillHash(textPath, true);
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
      * 
      * @param input       the input path
      * @param requireText whether the hash should include text files
      * @throws IOException an IO exception
      */
     public void fillHash(Path input, boolean requireText) throws IOException {
          /*
           * ---------------------------------------------
           *
           * Credit: Sophie Engle
           * https://github.com/usf-cs272-spring2024/cs272-lectures/blob/main/src/main/
           * java/edu/usfca/cs272/lectures/basics/io/DirectoryStreamDemo.java
           *
           * ---------------------------------------------
           */
          // Recursively fill hash of files and files.
          if (Files.isDirectory(input)) {
               // Path is a Directory --> Recurse Through Each Sub Path
               for (Path path : Files.newDirectoryStream(input)) {
                    fillHash(path, false);
               }
          } else {
               // Path is a File --> Base Case
               if (fileExtensionFilter(input, new String[] { ".txt", ".text" }) || requireText) {
                    handleFile(input);
               }
          }
     }

     /**
      * Adds a file to the index. This is called by the IndexWriter when it detects a
      * stem file that is to be added to the index
      * 
      * @param file - the path to the
      * @throws IOException an IO exception
      */
     public void handleFile(Path file) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(file, UTF_8)) {
               String line = null;
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);
               int i = 1;
               String fileString = file.toString();

               while ((line = reader.readLine()) != null) {
                    String[] parsedLine = FileStemmer.parse(line);

                    for (String word : parsedLine) {
                         invertedIndex.addIndex(stemmer.stem(word).toString(), fileString, i++);
                    }
               }

								/*
								 * TODO At this point, we need to remove this operation from here.
								 * 
								 * It is efficient (happening only once per file), but not encapsulated (the
								 * word count for a file can be set to an arbitrary value). Since we use that
								 * word count for the search score and ranking, it needs to match exactly what
								 * is stored in the index at all times.
								 * 
								 * That means instead of once per file, you need to update the count once per
								 * word. It also makes your code friendlier for multithreading when build and
								 * search operations are happening concurrently. See the index for details.
								 * 
								 * Note: We might make a different design decision in a different setting. For
								 * example, ElasticSearch (used by GitHub to enable search of your repositories)
								 * uses inverted indices and may prioritize efficiency over encapsulation in
								 * that more controlled setting. See: https://www.elastic.co/customers/github
								 */

               invertedIndex.addCount(file.toString(), i - 1);
          }
     }
     
     /*
      * TODO It can help to have a static version of handleFile for future projects.
      * To do this with the least amount of code change, do this:
      * 
      * 1. Change the current handleFile to:
      * public static void handleFile(Path file, InvertedIndex invertedIndex) throws IOException
      * 
      * 2. Create a NEW version of the method like this:
      * 
      * public void handleFile(Path file) throws IOException {
      *     handleFile(file, this.invertedIndex);
      * }
      */

     /**
      * Filters a path to see if it ends with one of the given extensions. This is
      * used to avoid file names that are inappropriate for the user's file system.
      * 
      * @param p          - The path to check. Must not be null.
      * @param extensions - An array of extensions. May be null.
      * 
      * @return true if the path ends with one of the given extensions false
      *         otherwise. Note that it is possible for this method to return false
      *         even if the path doesn't have a file
      */
     public static boolean fileExtensionFilter(Path p, String[] extensions) {
          String lower = p.getFileName().toString().toLowerCase();
          for (String ext : extensions) {
               if (lower.endsWith(ext)) {
                    return true;
               }
          }
          return false;
     }
}
