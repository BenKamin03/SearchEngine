package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
    private InvertedIndex invertedIndex; // TODO Add either final -or- static... which one makes sense here?

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
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
     public void fillInvertedIndex(Path textPath, InvertedIndex invertedIndex) throws IOException {
          if (textPath != null)
               fillHash(textPath, true);
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
     * @param input the input path
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
               return; // TODO Remove, not needed
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
				/*
				 * TODO Fantastic implementation! But, it is now time to make this more
				 * efficient.
				 * 
				 * You should always start with reusing as much code as possible to get the
				 * initial functionality working. This implementation is perfect for that!
				 * 
				 * When refactoring, we then consider whether it makes sense to reduce code
				 * reuse to improve efficiency. The answer depends on the class. A class like
				 * FileStemmer is very general, and so it might be more important there to use
				 * the most general approach (more code reuse) over the most efficient approach
				 * (less code reuse) everywhere. However, this class solves a more specific
				 * problem. In the more specific classes, we tend to choose efficiency over
				 * generalization (and code reuse).
				 * 
				 * Here, the use of a *temporary* list to hold the stemmed words causes more
				 * looping through the words than necessary. You loop once to copy words from
				 * the file into a list, then loop through that list again to move those words
				 * into the index. The list ends up being temporary storage, which can often be
				 * eliminated.
				 * 
				 * To fix this, copy/paste logic from the stemmer class and customize to add
				 * directly to the inverted index instead of to a list first. Use a buffered
				 * line-by-line approach. The parse method will still be helpful here, and you
				 * will need the FileStemmer class for future projects as well so do not get rid
				 * of it.
				 */

          ArrayList<String> stems = FileStemmer.listStems(file);
          if (stems.size() > 0) { // TODO Fix formatting, indentation is inconsistent below
               // Add the index to the inverted index.
               
                    int i = 1;
                    for (String stem : stems) {
                         invertedIndex.addIndex(stem, file.toString(), i++);
                    }
               

               // Add the count to the inverted index.
               
                    invertedIndex.addCount(file.toString(), stems.size());
               
          }
     }

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
