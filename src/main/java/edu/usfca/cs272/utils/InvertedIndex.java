package edu.usfca.cs272.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
/**
 * Class responsible for keeping the data structures for the indexes and counts of the files
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
     * InvertedIndex Constructor
     */
    public InvertedIndex() {
          indexes = new TreeMap<>();
          counts = new TreeMap<>();
     }

     /**
      * Returns the indexes
      * 
      * 
      * @return a TreeMap of indexes
      */
     public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndexes() {
          return indexes;
     }

     /**
      * Returns a TreeMap of counts keyed by category.
      * 
      * 
      * @return A TreeMap of counts keyed by category.
      */
     public TreeMap<String, Integer> getCounts() {
          return counts;
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
      * Adds the count with the specified file
      * 
      * @param file  - The file to count the number of times
      * @param count - The number of times the item is
      */
     public void addCount(String file, int count) {
          counts.put(file, count);
     }

     /**
     * Clears all indexes.
     */
     public void clearIndexes() {
          indexes.clear();
     }

     /**
     * Clears the counts.
     */
     public void clearCounts() {
          counts.clear();
     }

     /**
     * Returns an immutable map of indexes. 
     * 
     * 
     * @return an immutable map of indexes
     */
     public Map<String, TreeMap<String, TreeSet<Integer>>> getCopyOfIndexes() {
          return Collections.unmodifiableMap(indexes);
     }

     /**
     * Returns an immutable copy of the counts
     * 
     * 
     * @return an immutable copy of the counts
     */
     public Map<String, Integer> getCopyOfCounts() {
          return Collections.unmodifiableMap(counts);
     }

     /**
     * Returns a set of all indexes. 
     * 
     * 
     * @return a set of all indexes
     */
     public Set<String> getIndexKeys() {
          return indexes.keySet();
     }

     /**
     * Returns the keys of the counts.
     * 
     * 
     * @return the keys of the counts
     */
     public Set<String> getCountsKeys() {
          return counts.keySet();
     }

     /**
     * Returns an iterator over the words of all indexes.
     * 
     * 
     * @return an iterator over the words of all indexes
     */
     public Iterator<String> getIndexIterator() {
          return indexes.keySet().iterator();
     }

     /**
     * Returns an iterator over the names of all the counts. 
     * 
     * 
     * @return an iterator over the names of all the counts
     */
     public Iterator<String> getCountsIterator() {
          return counts.keySet().iterator();
     }

     /**
     * Returns the indexes of a word
     * 
     * @param word - the word to look up. It must be a String.
     * 
     * @return a TreeSet of the indexes of the word or null if not found in the index table
     */
     public TreeMap<String, TreeSet<Integer>> getIndexesOfWord(String word) {
          return indexes.get(word);
     }

     /**
     * Checks if the indexes contains a word. 
     * 
     * @param word - the word to look for
     * 
     * @return true if the word is in the indexes false if not or if the word is not in the indexes
     */
     public boolean indexesHasWord(String word) {
          return indexes.containsKey(word);
     }

     /**
     * Returns true if the counts map contains a file.
     * 
     * @param file - the file to look for
     * 
     * @return whether or not there is a file in the counts map for the given file or not
     */
     public boolean countsHasFile(String file) {
          return counts.containsKey(file);
     }

     /**
     * Returns the set of indexes of a word in a location.
     * 
     * @param word - the word to look up
     * @param location - the location to look up the word in.
     * 
     * @return the set of indexes of the word in the location or null if not found in the index set ( in which case the set is empty )
     */
     public TreeSet<Integer> getIndexesOfWordInLocation(String word, String location) {
          return indexes.get(word).get(location);
     }

     /**
     * Returns the set of indexes of a word in a location. If the word is not found in the location the backup set is returned
     * 
     * @param word - the word to search for
     * @param location - the location to search for the word in ( case sensitive )
     * @param backup - the set to return if the word is not found
     * 
     * @return the set of indexes of the word in the location or the backup set if the word is not found
     */
     public TreeSet<Integer> getIndexesOfWordInLocationWithBackup(String word, String location,
               TreeSet<Integer> backup) {
          return (indexesHasWordInLocation(word, location) ? getIndexesOfWordInLocation(word, location) : backup);
     }

     /**
     * Checks if there is a list of indexes in a location for a specified word
     * 
     * @param word - the word to search for
     * @param location - the location to search for the word in.
     * 
     * @return true if found false otherwise. Note that false is returned if the word is not found in the index
     */
     public boolean indexesHasWordInLocation(String word, String location) {
          return (indexesHasWord(word) ? indexes.get(word).containsKey(location) : false);
     }

     /**
     * Returns the number of occurrences of a file.
     * 
     * @param file - The name of the file to look up.
     * 
     * @return The number of occurrences of the file or null if not found
     */
     public Integer getCountsInFile(String file) {
          return counts.get(file);
     }
}
