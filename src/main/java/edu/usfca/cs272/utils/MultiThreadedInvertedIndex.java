package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
          Iterator<String> searchIterator = queries.iterator();

          List<QueryEntry> entries = new ArrayList<>();
          Map<String, QueryEntry> lookup = new HashMap<>();

          while (searchIterator.hasNext()) {
               indexesLock.readLock().lock();
               try {
                    queryWord(indexes.get(searchIterator.next()), entries, lookup);
               } finally {
                    indexesLock.readLock().unlock();
               }
          }

          Collections.sort(entries);
          return entries;
     }

     /**
      * the partial search given a set of queries
      * 
      * @param queries the queries
      * @return the TreeSet of results
      */
     @Override
     public List<QueryEntry> partialSearch(Set<String> queries) {
          List<QueryEntry> entries = new ArrayList<>();
          Map<String, QueryEntry> lookup = new HashMap<>();

          for (String stem : queries) {
               Iterator<Entry<String, TreeMap<String, TreeSet<Integer>>>> entrySet;
               Entry<String, TreeMap<String, TreeSet<Integer>>> curr = null;

               indexesLock.readLock().lock();
               try {
                    entrySet = indexes.tailMap(stem)
                              .entrySet()
                              .iterator();
               } finally {
                    indexesLock.readLock().unlock();
               }

               while (entrySet.hasNext() && (curr = entrySet.next()).getKey().startsWith(stem)) {
                    queryWord(indexes.get(curr.getKey()), entries, lookup);
               }
          }

          Collections.sort(entries);
          return entries;
     }

     /**
      * queries the word in the locations
      * 
      * @param wordLocations the locations of the word
      * @param entries       the entries of QueryEntries
      * @param lookup        the lookup table
      */
     private void queryWord(TreeMap<String, TreeSet<Integer>> wordLocations, List<QueryEntry> entries,
               Map<String, QueryEntry> lookup) {
          if (wordLocations != null) {
               var locationIterator = wordLocations.entrySet().iterator();
               while (locationIterator.hasNext()) {
                    Entry<String, TreeSet<Integer>> location = locationIterator.next();

                    lookup.computeIfAbsent(location.getKey(), (String f) -> {
                         QueryEntry newEntry = new QueryEntry(location.getKey());
                         entries.add(newEntry);
                         return newEntry;
                    });

                    int size = location.getValue().size();
                    lookup.get(location.getKey()).addQuery(size);
               }
          }
     }

     /**
      * searches from the queries and whether it's partial
      * 
      * @param queries the queries
      * @param partial search type
      * @return the list of query entries
      */
     @Override
     public List<QueryEntry> search(Set<String> queries, boolean partial) {
          if (partial) {
               return partialSearch(queries);
          } else {
               return exactSearch(queries);
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
          if (!hasPosition(word, location, index)) {
               countsLock.writeLock().lock();
               try {
                    counts.merge(location, 1, Integer::sum);
               } finally {
                    countsLock.writeLock().unlock();
               }

               indexesLock.writeLock().lock();
               try {
                    indexes.computeIfAbsent(word, k -> new TreeMap<>())
                              .computeIfAbsent(location, k -> new TreeSet<>()).add(index);
               } finally {
                    indexesLock.writeLock().unlock();
               }
          }
     }

     @Override
     public void addIndex(String word, String location, Set<Integer> indecies) {
          indexesLock.writeLock().lock();
          int newSize, originalSize;
          try {
               TreeSet<Integer> instances = indexes.computeIfAbsent(word, k -> new TreeMap<>())
                         .computeIfAbsent(location, k -> new TreeSet<>());
               originalSize = instances.size();
               instances.addAll(indecies);
               newSize = instances.size();
          } finally {
               indexesLock.writeLock().unlock();
          }

          countsLock.writeLock().lock();
          try {
               counts.merge(location, newSize - originalSize, Integer::sum);
          } finally {
               countsLock.writeLock().unlock();
          }
     }

     /**
      * Adds the indices of another InvertedIndex to this one
      * 
      * @param invertedIndex the inverted index
      * @param location      the location
      */
     @Override
     public void addIndex(InvertedIndex invertedIndex, String location) {
          var words = invertedIndex.getWords().iterator();
          while (words.hasNext()) {
               String word = words.next();
               addIndex(word, location, invertedIndex.getInstancesOfWordInLocation(word, location));
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
               return Collections.unmodifiableSet(indexes.keySet());
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
               TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
               return wordInIndex != null ? Collections.unmodifiableSet(wordInIndex.keySet()) : Collections.emptySet();
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
               TreeMap<String, TreeSet<Integer>> wordMap = indexes.get(word);
               if (wordMap != null) {
                    TreeSet<Integer> instances = wordMap.get(location);
                    if (instances != null) {
                         return Collections.unmodifiableSet(instances);
                    }
               }
               return Collections.emptySet();
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
               return indexes.containsKey(word);
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
               TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
               return wordInIndex != null && wordInIndex.containsKey(location);
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
               TreeMap<String, TreeSet<Integer>> wordInIndex = indexes.get(word);
               if (wordInIndex != null) {
                    TreeSet<Integer> locationInWord = wordInIndex.get(location);
                    return locationInWord != null && locationInWord.contains(position);
               }
               return false;
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
               JsonWriter.writeObjectMap(indexes, path);
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
               return Collections.unmodifiableMap(counts);
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
               return counts.getOrDefault(location, 0);
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
     public boolean hasCounts(String file) {
          countsLock.readLock().lock();
          try {
               return counts.containsKey(file);
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
               return getCounts().keySet();
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
               JsonWriter.writeObject(counts, path);
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
               builder.append(JsonWriter.writeObjectMap(indexes));
          } finally {
               indexesLock.readLock().unlock();
          }

          builder.append("Counts:\n");

          countsLock.readLock().lock();
          try {
               builder.append(JsonWriter.writeObject(counts));
          } finally {
               countsLock.readLock().unlock();
          }
          return builder.toString();
     }
}
