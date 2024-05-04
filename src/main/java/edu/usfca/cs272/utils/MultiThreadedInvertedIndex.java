package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for keeping the data structures for the indexes and counts
 * of the files
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class MultiThreadedInvertedIndex extends InvertedIndex {

     /**
      * the lock for the indexes
      */
     private final MultiReaderLock indexesLock;

     /**
      * the lock for the counts
      */
     private final MultiReaderLock countsLock;

     /**
      * Inverted Index Constructor
      */
     public MultiThreadedInvertedIndex() {
          super();
          indexesLock = new MultiReaderLock();
          countsLock = new MultiReaderLock();
     }

     /**
      * The exact search for a set of queries
      * 
      * @param queries the queries
      * @return the TreeSet of results
      */
     @Override
     public List<QueryEntry> exactSearch(Set<String> queries) {
          indexesLock.readLock().lock();
          countsLock.readLock().lock();
          try {
               return super.exactSearch(queries);
          } finally {
               indexesLock.readLock().unlock();
               countsLock.readLock().unlock();
          }
     }

     /**
      * the partial search given a set of queries
      * 
      * @param queries the queries
      * @return the TreeSet of results
      */
     @Override
     public List<QueryEntry> partialSearch(Set<String> queries) {
          indexesLock.readLock().lock();
          countsLock.readLock().lock();
          try {
               return super.partialSearch(queries);
          } finally {
               indexesLock.readLock().unlock();
               countsLock.readLock().unlock();
          }
     }

     /**
      * Adds an index to the index map. This is used to determine which words are in
      * the index map when looking for a word that has already been added
      * 
      * @param word     - the word to add the index for
      * @param location - the location of the word to add the index for
      * @param index    - the index to add to the index map
      */
     @Override
     public void addIndex(String word, String location, int index) {
          indexesLock.writeLock().lock();
          countsLock.writeLock().lock();
          try {
               super.addIndex(word, location, index);
          } finally {
               indexesLock.writeLock().unlock();
               countsLock.writeLock().unlock();
          }
     }

     @Override
     public void addIndex(String word, String location, Set<Integer> indecies) {
          indexesLock.writeLock().lock();
          countsLock.writeLock().lock();
          try {
               super.addIndex(word, location, indecies);
          } finally {
               indexesLock.writeLock().unlock();
               countsLock.writeLock().unlock();
          }
     }

     @Override
     public void addIndex(InvertedIndex otherIndex) {
          indexesLock.writeLock().lock();
          countsLock.writeLock().lock();
          try {
               super.addIndex(otherIndex);
          } finally {
               indexesLock.writeLock().unlock();
               countsLock.writeLock().unlock();
          }
     }

     /**
      * Returns a list of words in the index
      * 
      * @return the list of words in the index
      */
     @Override
     public Set<String> getWords() {
          indexesLock.readLock().lock();
          try {
               return super.getWords();
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Returns all of the locations that the word appears in (Files)
      * 
      * @param word the word
      * @return the list of locations
      */
     @Override
     public Set<String> getLocationsOfWord(String word) {
          indexesLock.readLock().lock();
          try {
               return super.getLocationsOfWord(word);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Returns the all of the instances of a word in a location
      * 
      * @param word     the word
      * @param location the location
      * @return the list of instances
      */
     @Override
     public Set<Integer> getInstancesOfWordInLocation(String word, String location) {
          indexesLock.readLock().lock();
          try {
               return super.getInstancesOfWordInLocation(word, location);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Checks if the indexes contains a word.
      * 
      * @param word - the word to look for
      * 
      * @return true if the word is in the indexes false if not or if the word is not
      *         in the indexes
      */
     @Override
     public boolean hasWord(String word) {
          indexesLock.readLock().lock();
          try {
               return super.hasWord(word);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Checks if there is a list of indexes in a location for a specified word
      * 
      * @param word     - the word to search for
      * @param location - the location to search for the word in.
      * 
      * @return true if found false otherwise. Note that false is returned if the
      *         word is not found in the index
      */
     @Override
     public boolean hasLocation(String word, String location) {
          indexesLock.readLock().lock();
          try {
               return super.hasLocation(word, location);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Gets whether the position exists for the word in the location
      * 
      * @param word     the word
      * @param location the location
      * @param position the position
      * @return whether the position exists in the instances of a word in a location
      */
     @Override
     public boolean hasPosition(String word, String location, int position) {
          indexesLock.readLock().lock();
          try {
               return super.hasPosition(word, location, position);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Writes the index to a file
      * 
      * @param path the output that
      * @throws IOException io exception
      */
     @Override
     public void writeIndex(Path path) throws IOException {
          indexesLock.readLock().lock();
          try {
               super.writeIndex(path);
          } finally {
               indexesLock.readLock().unlock();
          }
     }

     /**
      * Returns a TreeMap of counts keyed by category.
      * 
      * 
      * @return A TreeMap of counts keyed by category.
      */
     @Override
     public Map<String, Integer> getCounts() {
          countsLock.readLock().lock();
          try {
               return super.getCounts();
          } finally {
               countsLock.readLock().unlock();
          }
     }

     /**
      * gets the word count in a location
      * 
      * @param location the file location
      * @return the word count
      */
     @Override
     public int getCountsInLocation(String location) {
          countsLock.readLock().lock();
          try {
               return getCountsInLocation(location);
          } finally {
               countsLock.readLock().unlock();
          }
     }

     /**
      * Returns true if the counts map contains a file.
      * 
      * @param file - the file to look for
      * 
      * @return whether or not there is a file in the counts map for the given file
      *         or not
      */
     @Override
     public boolean containsFile(String file) {
          countsLock.readLock().lock();
          try {
               return super.containsFile(file);
          } finally {
               countsLock.readLock().unlock();
          }
     }

     /**
      * Returns the keys of the counts.
      * 
      * 
      * @return the keys of the counts
      */
     @Override
     public Set<String> getLocations() {
          countsLock.readLock().lock();
          try {
               return super.getLocations();
          } finally {
               countsLock.readLock().unlock();
          }
     }

     /**
      * writes the counts to a file
      * 
      * @param path the output path
      * @throws IOException io exception
      */
     @Override
     public void writeCounts(Path path) throws IOException {
          countsLock.readLock().lock();
          try {
               super.writeCounts(path);
          } finally {
               countsLock.readLock().unlock();
          }
     }

     @Override
     public String toString() {
          StringBuilder builder = new StringBuilder();
          builder.append("Indexes:\n");

          indexesLock.readLock().lock();
          try {
               builder.append(super.writeIndex());
          } finally {
               indexesLock.readLock().unlock();
          }

          builder.append("Counts:\n");

          countsLock.readLock().lock();
          try {
               builder.append(super.writeCounts());
          } finally {
               countsLock.readLock().unlock();
          }
          return builder.toString();
     }
}
