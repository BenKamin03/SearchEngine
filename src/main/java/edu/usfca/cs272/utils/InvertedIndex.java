package edu.usfca.cs272.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
    
			/*
			 * TODO The get methods here are breaking encapsulation. It is now time to fix
			 * this problem. The PrefixMap example from the lectures illustrates how to fix
			 * this problem efficiently.
			 */

     /**
      * Returns the indexes
      * 
      * 
      * @return a TreeMap of indexes
      */
     public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndexes() {
          return indexes;
          
					/*
					 * TODO You need this get method that breaks encapsulation to write the data to
					 * a JSON file. Why you break encapsulation is a clue to how to fix the design.
					 * It usually means the class is missing functionality. In this case, think of
					 * writing to JSON more like a get method. It needs to "get" all of the data,
					 * but it just does it to a file instead of in memory. Where do get methods go?
					 * In the data structure! So you need a writeIndex(...) method in here that
					 * calls your JsonWriter method.
					 */
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
    	 // TODO Only put if count > 0
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
				/*
				 * TODO This get method is breaking encapsulation. Do you understand why? The
				 * PrefixMap example illustrates what happens when you have nested mutable data.
				 * 
				 * It is also inefficient. You do not need any copy methods in the index.
				 */

          return Collections.unmodifiableMap(indexes);
     }

     /**
      * Returns an immutable copy of the counts
      * 
      * 
      * @return an immutable copy of the counts
      */
     public Map<String, Integer> getCopyOfCounts() { // TODO Also remove
          return Collections.unmodifiableMap(counts);
     }

     /**
      * Returns a set of all indexes.
      * 
      * 
      * @return a set of all indexes
      */
     public Set<String> getIndexKeys() { // TODO Breaking encapsulation
          return indexes.keySet();
     }

     /**
      * Returns the keys of the counts.
      * 
      * 
      * @return the keys of the counts
      */
     public Set<String> getCountsKeys() { // TODO Breaking encapsulation
          return counts.keySet();
     }

     /**
      * Returns an iterator over the words of all indexes.
      * 
      * 
      * @return an iterator over the words of all indexes
      */
     public Iterator<String> getIndexIterator() { // TODO What are you using as a basis for which methods to include? There is a reason none of the examples I gave have a method like this... it can actually result in modification to your provide data and break encapsulation! 
          return indexes.keySet().iterator();
     }

     /**
      * Returns an iterator over the names of all the counts.
      * 
      * 
      * @return an iterator over the names of all the counts
      */
     public Iterator<String> getCountsIterator() {
          return counts.keySet().iterator(); // TODO Same issue
     }

     /**
      * Returns the indexes of a word
      * 
      * @param word - the word to look up. It must be a String.
      * 
      * @return a TreeSet of the indexes of the word or null if not found in the
      *         index table
      */
     public TreeMap<String, TreeSet<Integer>> getIndexesOfWord(String word) {
          return indexes.get(word); // TODO Also breaks encapsulation
     }

     /**
      * Checks if the indexes contains a word.
      * 
      * @param word - the word to look for
      * 
      * @return true if the word is in the indexes false if not or if the word is not
      *         in the indexes
      */
     public boolean indexesHasWord(String word) {
          return indexes.containsKey(word);
     }

     /**
      * Returns true if the counts map contains a file.
      * 
      * @param file - the file to look for
      * 
      * @return whether or not there is a file in the counts map for the given file
      *         or not
      */
     public boolean countsHasFile(String file) {
          return counts.containsKey(file);
     }
     
     // TODO Group the has methods together. I think you are missing one.

     /**
      * Returns the set of indexes of a word in a location.
      * 
      * @param word     - the word to look up
      * @param location - the location to look up the word in.
      * 
      * @return the set of indexes of the word in the location or null if not found
      *         in the index set ( in which case the set is empty )
      */
     public TreeSet<Integer> getIndexesOfWordInLocation(String word, String location) { // TODO Breaking encapsulation
          return indexes.get(word).get(location);
     }

     /**
      * Returns the set of indexes of a word in a location. If the word is not found
      * in the location the backup set is returned
      * 
      * @param word     - the word to search for
      * @param location - the location to search for the word in ( case sensitive )
      * @param backup   - the set to return if the word is not found
      * 
      * @return the set of indexes of the word in the location or the backup set if
      *         the word is not found
      */
     public TreeSet<Integer> getIndexesOfWordInLocationWithBackup(String word, String location,
               TreeSet<Integer> backup) { // TODO Hmmm, why a backup? How do you anticipate this one being used?
          return (indexesHasWordInLocation(word, location) ? getIndexesOfWordInLocation(word, location) : backup);
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
     public boolean indexesHasWordInLocation(String word, String location) {
          return (indexesHasWord(word) ? indexes.get(word).containsKey(location) : false);
          
					/*
					 * TODO Improve efficiency to avoid re-accessing the same data within the tree
					 * data structure more times than necessary -or- to avoid creating unnecessary
					 * empty instances every time this method is called that eventually need to be
					 * cleaned up by the garbage collector. An example class from lecture that tries
					 * to do this is linked below.
					 */

					// TODO See: https://github.com/usf-cs272-spring2024/cs272-lectures/blob/main/src/main/java/edu/usfca/cs272/lectures/inheritance/word/WordPrefix.java

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
     
			/*
			 * TODO This class is still missing some methods.
			 * 
			 * The first set of methods make sure the data stored in this class is safely
			 * accessible (without any looping required). Those are get/view methods,
			 * has/contains methods, and num/size methods (choose a naming scheme and stick
			 * to it).
			 * 
			 * For example, FileIndex has two "has" methods because there are two pieces of
			 * information stored within that data structure class (the locations and the
			 * words for a location). What does that mean for this class, which is storing
			 * more information? I would expect to see:
			 * 
			 * hasWord(String word) → does the inverted index have this word?
			 * 
			 * hasLocation(String word, String location) → does the inverted index have this
			 * location for this word?
			 * 
			 * hasPosition(String word, String location, Integer position) → does the
			 * inverted index have this position for the given location and word?
			 * 
			 * hasCount(String location) → does the word counts map have a count for this
			 * location?
			 * 
			 * There are usually the same number of get, has, and num methods. Then think
			 * about other methods, like toString, addAll, and write methods, to also
			 * include.
			 */

			/*
			 * TODO I recommend you stop by office hours to discuss some of these comments.
			 * It might be good to have a longer synchronous discussion around get methods
			 * and not breaking encapsulation.
			 */
}
