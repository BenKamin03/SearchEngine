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
     * Private Path indexesPath
     */
    private Path indexesPath;
    
    /**
     * Private Path countsPath
     */
    private Path countsPath;
    
    /**
     * Private InvertedIndex invertedIndex
     */
     private InvertedIndex invertedIndex;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
     * @param indexesPath 
     * @param countsPath 
     * @param invertedIndex 
      */
     public FileHandler(Path indexesPath, Path countsPath, InvertedIndex invertedIndex) {
          this.indexesPath = indexesPath;
          this.countsPath = countsPath;
          this.invertedIndex = invertedIndex;
     }

     
     /**
     * Fills the inverted index with the contents of the file. 
     * 
     * @param textPath - Path to the text file to be hashed
     * @param invertedIndex - Inverted index to be
     * @throws IOException 
     */
     public void fillInvertedIndex(Path textPath, InvertedIndex invertedIndex) throws IOException {
          fillHash(textPath, true);
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
     * @param input 
     * @param requireText 
      *
     * @throws IOException 
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
               return;
          }
     }

     /**
     * Adds a file to the index. This is called by the IndexWriter when it detects a stem file that is to be added to the index
     * 
     * @param file - the path to the
     * @throws IOException 
     */
     public void handleFile(Path file) throws IOException {
          ArrayList<String> stems = FileStemmer.listStems(file);
          if (stems.size() > 0) {
               // Add the index to the inverted index.
               if (indexesPath != null) {
                    int i = 1;
                    for (String stem : stems) {
                         invertedIndex.addIndex(stem, file.toString(), i++);
                    }
               }

               // Add the count to the inverted index.
               if (countsPath != null) {
                    invertedIndex.addCount(file.toString(), stems.size());
               }
          }
     }

     /**
     * Filters a path to see if it ends with one of the given extensions. This is used to avoid file names that are inappropriate for the user's file system.
     * 
     * @param p - The path to check. Must not be null.
     * @param extensions - An array of extensions. May be null.
     * 
     * @return true if the path ends with one of the given extensions false otherwise. Note that it is possible for this method to return false even if the path doesn't have a file
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
