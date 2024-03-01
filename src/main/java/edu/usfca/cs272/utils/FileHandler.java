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

     private Path indexesPath, countsPath;
     private InvertedIndex invertedIndex;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      *
      * @param parser - the command line argument
      */
     public FileHandler(Path indexesPath, Path countsPath, InvertedIndex invertedIndex) {
          this.indexesPath = indexesPath;
          this.countsPath = countsPath;
          this.invertedIndex = invertedIndex;
     }

     public void fillInvertedIndex(Path textPath, InvertedIndex invertedIndex) throws IOException {
          fillHash(textPath, true);
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
      *
      * @param hash        - Map to fill with stems
      * @param p           - Path to file or directory to process ( recursive )
      * @param isDirectory - True if Path is a directory false if it's a file
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

     public void handleFile(Path file) throws IOException {
          ArrayList<String> stems = FileStemmer.listStems(file);
          if (stems.size() > 0) {
               if (indexesPath != null) {
                    int i = 1;
                    for (String stem : stems) {
                         invertedIndex.addIndex(stem, file.toString(), i++);
                    }
               }

               if (countsPath != null) {
                    invertedIndex.addCount(file.toString(), stems.size());
               }
          }
     }

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
