package edu.usfca.cs272.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
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
      * The inverted index
      */
     private final MultiThreadedInvertedIndex invertedIndex;

     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      *
      * @param invertedIndex the invertedIndex
      * @param workQueue     the work queue
      */
     public MultiThreadedFileHandler(MultiThreadedInvertedIndex invertedIndex, WorkQueue workQueue) {
          super(invertedIndex);
          this.workQueue = workQueue;
          this.invertedIndex = invertedIndex;
     }

     /**
      * Fills the inverted index with the contents of the file.
      *
      * @param textPath - Path to the text file to be hashed
      * @throws IOException the IO exception
      */
     @Override
     public void fillInvertedIndex(Path textPath) throws IOException {
          super.fillInvertedIndex(textPath);
          workQueue.finish();
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
          workQueue.execute(new FileTask(file));
     }

     /**
      * The task for parsing a file
      */
     public class FileTask implements Runnable {
          /**
           * The path
           */
          private Path input;

          /**
           * The constructor
           * 
           * @param input the input path
           */
          public FileTask(Path input) {
               this.input = input;
          }

          @Override
          public void run() {
               try {
                    InvertedIndex local = new InvertedIndex();
                    FileHandler.handleFile(input, local);
                    invertedIndex.addIndex(local);
               } catch (IOException e) {
                    throw new UncheckedIOException(e);
               }
          }
     }
}
