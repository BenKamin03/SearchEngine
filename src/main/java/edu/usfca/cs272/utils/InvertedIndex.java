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
     
     /*
      * TODO Remove all of the get methods and replace with EFFICIENT views that
      * do not break encapsulation or require looping.
      */

     public Set<Integer> viewIndexOfWordInLocation(String word, String location) {
          Map<String, TreeSet<Integer>> wordIndex = indexes.getOrDefault(word, new TreeMap<>());
          return Collections.unmodifiableSet(wordIndex.getOrDefault(location, new TreeSet<>()));
      }

     /**
      * Returns the indexes
      * 
      * 
      * @return a TreeMap of indexes
      */
     public Map<String, Map<String, Set<Integer>>> getIndexes() {
          Map<String, Map<String, Set<Integer>>> unmodifiableIndexes = new TreeMap<>();
          for (String word : indexes.keySet()) {
               unmodifiableIndexes.put(word, Collections.unmodifiableMap(getIndexOfWord(word)));
          }
          return Collections.unmodifiableMap(unmodifiableIndexes);
     }

     /**
      * Finds the index of a word in the index
      * 
      * @param word the word to find
      * @return an unmodifiable map of the word's index
      */
     public Map<String, Set<Integer>> getIndexOfWord(String word) {
          TreeMap<String, TreeSet<Integer>> innerMap = indexes.get(word);
          Map<String, Set<Integer>> unmodifiableInnerMap = new TreeMap<>();
          for (String location : innerMap.keySet()) {
               Set<Integer> unmodifiableSet = getIndexOfWordInLocation(word, location);
               unmodifiableInnerMap.put(location, unmodifiableSet);
          }
          return Collections.unmodifiableMap(unmodifiableInnerMap);
     }

     /**
      * Finds the list of instances of a word within a file in the index
      * 
      * @param word     the word
      * @param location the location
      * @return the unmodifiable set of instances
      */
     public Set<Integer> getIndexOfWordInLocation(String word, String location) {
          return Collections.unmodifiableSet(indexes.get(word).get(location)); // TODO Throws a null pointer if get(word) is null
     }

     /**
      * Clears all indexes.
      */
     public void clearIndexes() {
          indexes.clear();
     }

     /**
      * Returns a set of all the words in the index.
      * 
      * 
      * @return a set of all words
      */
     public Set<String> getWords() {
          return Collections.unmodifiableSet(getIndexes().keySet());
     }

     public Set<String> getPathsOfWord(String word) {
          return Collections.unmodifiableSet(indexes.get(word).keySet());
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
          return Collections.unmodifiableMap(counts); // TODO Keep this one
     }

     /**
      * gets the word count in a location
      * 
      * @param location the file location
      * @return the word count
      */
     public int getCountsInLocation(String location) {
          return counts.getOrDefault(location, 0);
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
     public Set<String> getCountsKeys() { // TODO Could keep this one too
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
     
     /*
      * TODO Okay so the get methods are an issue because of the nested data
      * and the looping, but the other methods look great.
      * 
      * Create a view method per has method that is looking at the same type of
      * data instead. That is closer to the FileIndex, PrefixMap, and
      * WordPrefix examples. Like:
      * 
      * hasWord --> getWords() returns a view of the indexes keyset
      * hasLocation --> getLocations(String word) returns a view of the indexes inner map keyset for the word
      * hasPosition --> ... view of inner most set
      * 
      * Does that make sense? You can use the newer WordPrefix example as a basis:
      * 
      * https://github.com/usf-cs272-spring2024/cs272-lectures/blob/b58d2cfc1f26c8916ddcb9261bc1143e29923e6d/src/main/java/edu/usfca/cs272/lectures/inheritance/word/WordPrefix.java#L94-L109
      */
}
