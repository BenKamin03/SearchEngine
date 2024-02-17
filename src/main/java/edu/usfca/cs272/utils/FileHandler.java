package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileHandler {

     private Path indexesPath, countsPath, textPath;
     private InvertedIndex invertedIndex;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      *
      * @param parser - the command line argument
      */
     public FileHandler(Path textPath, Path indexesPath, Path countsPath, InvertedIndex invertedIndex) {
          this.textPath = textPath;
          this.indexesPath = indexesPath;
          this.countsPath = countsPath;
          this.invertedIndex = invertedIndex;
     }

     public void write() throws IOException {
          if (indexesPath != null)
               JsonWriter.writeObjectHash(invertedIndex.getIndexes(), indexesPath);
          if (countsPath != null)
               JsonWriter.writeObject(invertedIndex.getCounts(), countsPath);
     }
     
     public void fillInvertedIndex() throws IOException {
          fillHash(textPath, false);
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
      *
      * @param hash        - Map to fill with stems
      * @param p           - Path to file or directory to process ( recursive )
      * @param isDirectory - True if Path is a directory false if it's a file
      */
     public void fillHash(Path p, boolean isDirectory) throws IOException {
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
               for (Path path : Files.newDirectoryStream(p)) {
                    fillHash(path, true);
               }
          } else {
               // Path is a File --> Base Case
               if (fileExtensionFilter(p, new String[] { ".txt", ".text" }) || !isDirectory) {
                    handleFile(p);
               }
               return;
          }
     }

     public void handleFile(Path p) throws IOException {
          ArrayList<String> stems = FileStemmer.listStems(p);
          if (stems.size() > 0) {
               if (indexesPath != null) {
                    int i = 1;
                    for (String stem : stems) {
                         invertedIndex.addIndex(stem, p, i++);
                    }
               }

               if (countsPath != null) {
                    invertedIndex.addCount(p.toString(), stems.size());
               }
          }
     }

     private static boolean fileExtensionFilter(Path p, String[] extensions) {
          for (String ext : extensions) {
               if (p.getFileName().toString().toLowerCase().endsWith(ext)) {
                    return true;
               }
          }
          return false;
     }
}
