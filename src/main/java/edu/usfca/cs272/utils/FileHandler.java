package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

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
      * the work queue
      */
     private final WorkQueue workQueue;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      *
      * @param invertedIndex the invertedIndex
      * @param threads the number of threads to use in the work queue
      */
     public FileHandler(InvertedIndex invertedIndex, int threads) {
          this.invertedIndex = invertedIndex;
          workQueue = new WorkQueue(threads);
     }

     /**
      * Fills the inverted index with the contents of the file.
      *
      * @param textPath - Path to the text file to be hashed
      * @throws IOException the IO exception
      */
     public void fillInvertedIndex(Path textPath) throws IOException {
          fillHash(textPath, true);
          workQueue.join();
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
                    workQueue.execute(() -> {
                         try {
                              handleFile(input);
                         } catch (IOException e) {
                              System.out.println("IO Exception in File: " + input.toString());
                         }
                    });
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
          TreeMap<String, TreeSet<Integer>> fileInfo = new TreeMap<>(); //Word -> List of Occurance
          try (BufferedReader reader = Files.newBufferedReader(file, UTF_8)) {
               String line = null;
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);
               int i = 1;
               while ((line = reader.readLine()) != null) {
                    String[] parsedLine = FileStemmer.parse(line);
                    for (String word : parsedLine) {
                         // invertedIndex.addIndex(stemmer.stem(word).toString(), fileString, i++);
                         String stem = stemmer.stem(word).toString();
                         fileInfo.computeIfAbsent(stem, (key) -> new TreeSet<>());
                         fileInfo.get(stem).add(i++);
                    }
               }
          }
          invertedIndex.addIndex(fileInfo, file.toString());
     }

     /**
      * Filters a path to see if it ends with one of the given extensions. This is
      * used to avoid file names that are inappropriate for the user's file system.
      *
      * @param p          - The path to check. Must not be null.
      * @param extensions - An array of extensions. May be null.
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
