package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

/**
 * Class responsible for keeping the data structures for the indexes and counts
 * of the files
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class InvertedIndex {

     /**
      * private final indexes
      */
     private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexes;

     /**
      * private final counts
      */
     private final TreeMap<String, Integer> counts;

     /**
      * Inverted Index Constructor
      */
     public InvertedIndex() {
          indexes = new TreeMap<>();
          counts = new TreeMap<>();
     }

     /**
      * Adds an index to the index map. This is used to determine which words are in
      * the index map when looking for a word that has already been added
      * 
      * @param word     - the word to add the index for
      * @param location - the location of the word to add the index for
      * @param index    - the index to add to the index map
      */
     public void addIndex(String word, String location, int index) {
          indexes.computeIfAbsent(word, k -> new TreeMap<>()).computeIfAbsent(location, k -> new TreeSet<>())
                    .add(index);
     }

     /**
      * Returns a list of words in the index
      * 
      * @return the list of words in the index
      */
     public Set<String> getWords() {
          return Collections.unmodifiableSet(indexes.keySet());
     }

     /**
      * Returns all of the locations that the word appears in (Files)
      * 
      * @param word the word
      * @return the list of locations
      */
     public Set<String> getLocationsOfWord(String word) {
				/*
				 * TODO It is actually quite inefficient to use getOrDefault here. The new
				 * TreeMap instance is being created *every time* this method is called,
				 * regardless of whether one is needed. That ends up creating a lot of
				 * unnecessary empty instances in memory that the Java garbage collector must
				 * eventually clean up.
				 * 
				 * You have a better approach in your has methods that rely on calling get and
				 * checking for null values. Use that here (and everywhere else you rely on
				 * getOrDefault for this problem) instead.
				 */
          /*-
          TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
          return wordInIndex != null ? Collections.unmodifiableSet(wordInIndex.keySet()) : Collections.emptySet();
          */

          return Collections.unmodifiableSet(indexes.getOrDefault(word, new TreeMap<>()).keySet());
     }

     /**
      * Returns the all of the instances of a word in a location
      * 
      * @param word the word
      * @param location the location
      * @return the list of instances
      */
     public Set<Integer> getInstancesOfWordInLocation(String word, String location) {
          return Collections.unmodifiableSet(
                    indexes.getOrDefault(word, new TreeMap<>()).getOrDefault(location, new TreeSet<>()));
     }

     /**
      * Checks if the indexes contains a word.
      * 
      * @param word - the word to look for
      * 
      * @return true if the word is in the indexes false if not or if the word is not
      *         in the indexes
      */
     public boolean hasWord(String word) {
          return indexes.containsKey(word);
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
     public boolean hasLocation(String word, String location) {
          TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
          return wordInIndex != null && wordInIndex.containsKey(location);
     }

     /**
      * Gets whether the position exists for the word in the location
      * 
      * @param word     the word
      * @param location the location
      * @param position the position
      * @return whether the position exists in the instances of a word in a location
      */
     public boolean hasPosition(String word, String location, int position) {
          TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
          if (wordInIndex != null) {
               TreeSet<Integer> locationInWord = wordInIndex.get(location);
               return locationInWord != null && locationInWord.contains(position);
          }
          return false;
     }

     /**
      * Writes the index to a file
      * 
      * @param path the output that
      * @throws IOException io exception
      */
     public void writeIndex(Path path) throws IOException {
          JsonWriter.writeObjectMap(indexes, path);
     }

     /**
      * Returns a TreeMap of counts keyed by category.
      * 
      * 
      * @return A TreeMap of counts keyed by category.
      */
     public Map<String, Integer> getCounts() {
          return Collections.unmodifiableMap(counts);
     }

     /**
      * gets the word count in a location
      * 
      * @param location the file location
      * @return the word count
      */
     public int getCountsInLocation(String location) {
          return counts.getOrDefault(location, 0); // TODO This getOrDefault is okay, because it is not creating a new instance
     }

     /**
      * Adds the count with the specified file
      * 
      * @param file  - The file to count the number of times
      * @param count - The number of times the item is
      */
     public void addCount(String file, int count) {
          if (count > 0)
               counts.put(file, count);
     }

     /**
      * Returns true if the counts map contains a file.
      * 
      * @param file - the file to look for
      * 
      * @return whether or not there is a file in the counts map for the given file
      *         or not
      */
     public boolean hasCounts(String file) {
          return counts.containsKey(file);
     }

     /**
      * Clears the counts.
      */
     public void clearCounts() {
          counts.clear();
     }

     /**
      * Returns the keys of the counts.
      * 
      * 
      * @return the keys of the counts
      */
     public Set<String> getCountsKeys() { // TODO Call getLocations
          return getCounts().keySet();
     }

     /**
      * writes the counts to a file
      * 
      * @param path the output path
      * @throws IOException io exception
      */
     public void writeCounts(Path path) throws IOException {
          JsonWriter.writeObject(counts, path);
     }
     
     // TODO Missing toString
}
