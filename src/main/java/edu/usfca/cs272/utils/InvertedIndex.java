package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

     public TreeSet<QueryEntry> exactSearch(Set<String> queries) {
          return search(queries.iterator());
     }

     public TreeSet<QueryEntry> partialSearch(Set<String> queries) {
          ArrayList<String> searchStems = new ArrayList<>();

          for (String stem : queries) {
               getWords().stream()
                         .filter(curr -> curr.startsWith(stem))
                         .forEach(searchStems::add);
          }

          return search(searchStems.iterator());
     }

     public TreeSet<QueryEntry> search(Iterator<String> searchIterator) {
          TreeSet<QueryEntry> entries = new TreeSet<>();

          while (searchIterator.hasNext()) {
               String word = searchIterator.next();

               TreeMap<String, TreeSet<Integer>> wordLocations = indexes.get(word);
               if (wordLocations != null) {
                    var locationIterator = wordLocations.keySet().iterator();
                    while (locationIterator.hasNext()) {
                         String file = locationIterator.next();

                         QueryEntry existingEntry = entries.stream()
                                   .filter(entry -> entry.getFile().equals(file))
                                   .findFirst()
                                   .orElse(null);

                         if (existingEntry != null) {
                              entries.remove(existingEntry);
                         } else {
                              existingEntry = new QueryEntry(file, getCountsInLocation(file));
                         }

                         int size = wordLocations.get(file).size();
                         if (size > 0)
                              existingEntry.addQuery(size);

                         entries.add(existingEntry);
                    }
               }
          }
          return entries;
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
          TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
          return wordInIndex != null ? Collections.unmodifiableSet(wordInIndex.keySet()) : Collections.emptySet();
     }

     /**
      * Returns the all of the instances of a word in a location
      * 
      * @param word     the word
      * @param location the location
      * @return the list of instances
      */
     public Set<Integer> getInstancesOfWordInLocation(String word, String location) {
          TreeMap<String, TreeSet<Integer>> wordMap = indexes.get(word);
          if (wordMap != null) {
               TreeSet<Integer> instances = wordMap.get(location);
               if (instances != null) {
                    return instances;
               }
          }
          return Collections.unmodifiableSet(new TreeSet<>());
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
     public Set<String> getLocations() {
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

     @Override
     public String toString() {
          StringBuilder builder = new StringBuilder();
          builder.append("Indexes:\n");
          builder.append(JsonWriter.writeObjectMap(indexes));
          builder.append("Counts:\n");
          builder.append(JsonWriter.writeObject(counts));
          return builder.toString();
     }
}
