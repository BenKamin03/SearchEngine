package edu.usfca.cs272.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class responsible for filling the InvertedIndex
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class MultiThreadedFileHandler extends FileHandler {

     /**
      * the work queue
      */
     private final WorkQueue workQueue;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      *
      * @param invertedIndex the invertedIndex
      * @param threads       the number of threads to use in the work queue
      */
     public MultiThreadedFileHandler(InvertedIndex invertedIndex, WorkQueue workQueue) {
          super(invertedIndex);
          this.workQueue = workQueue;
     }

     /**
      * Fills the inverted index with the contents of the file.
      *
      * @param textPath - Path to the text file to be hashed
      * @throws IOException the IO exception
      */
     public void fillInvertedIndex(Path textPath) throws IOException {
          fillHash(textPath, true);
          workQueue.finish();
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
      *
      * @param input       the input path
      * @param requireText whether the hash should include text files
      * @throws IOException an IO exception
      */
     @Override
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
                    workQueue.execute(new FileTask(input));
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
     @Override
     public void handleFile(Path file) throws IOException {
          InvertedIndex local = new InvertedIndex();
          super.handleFile(file, local);
          invertedIndex.addIndex(local, file.toString());
     }

     public class FileTask implements Runnable {
          private Path input;

          public FileTask(Path input) {
               this.input = input;
          }

          @Override
          public void run() {
               try {
                    handleFile(input);
               } catch (IOException e) {
                    throw new UncheckedIOException(e);
               }
          }
     }
}
