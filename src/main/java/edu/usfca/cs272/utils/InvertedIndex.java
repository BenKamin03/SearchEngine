package edu.usfca.cs272.utils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
      * The exact search for a set of queries
      * 
      * @param queries the queries
      * @return the TreeSet of results
      */
     public List<QueryEntry> exactSearch(Set<String> queries) {
          Iterator<String> searchIterator = queries.iterator();

          List<QueryEntry> entries = new ArrayList<>();
          Map<String, QueryEntry> lookup = new HashMap<>();

          while (searchIterator.hasNext()) {
               queryWord(indexes.get(searchIterator.next()), entries, lookup);
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
     public List<QueryEntry> partialSearch(Set<String> queries) {
          List<QueryEntry> entries = new ArrayList<>();
          Map<String, QueryEntry> lookup = new HashMap<>();

          for (String stem : queries) {

               Iterator<Entry<String, TreeMap<String, TreeSet<Integer>>>> entrySet = indexes.tailMap(stem).entrySet()
                         .iterator();
               Entry<String, TreeMap<String, TreeSet<Integer>>> curr = null;

               while (entrySet.hasNext() && (curr = entrySet.next()).getKey().startsWith(stem)) {
                    queryWord(indexes.get(curr.getKey()), entries, lookup);
               }
          }

          Collections.sort(entries);
          return entries;
     }

     private void queryWord(TreeMap<String, TreeSet<Integer>> wordLocations, List<QueryEntry> entries, Map<String, QueryEntry> lookup) {
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
     public void addIndex(String word, String location, int index) {
          if (!hasPosition(word, location, index)) {
               counts.compute(location, (key, val) -> {
                    return (counts.containsKey(key) ? val : 0) + 1;
               });
               // counts.computeIfPresent(location, (key, val) -> {
               // return val + 1;
               // });
               // counts.computeIfAbsent(location, (key) -> {
               // return 1;
               // });

               indexes.computeIfAbsent(word, k -> new TreeMap<>()).computeIfAbsent(location, k -> new TreeSet<>())
                         .add(index);
          }
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
                    return Collections.unmodifiableSet(instances);
               }
          }
          return Collections.emptySet();
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

     /**
      * A simple query entry class
      * 
      * @author Ben Kamin
      */
     public class QueryEntry implements Comparable<QueryEntry> {
          /**
           * The total words in the file
           */
          private final int totalWords;

          /**
           * The total applied words in the file. Stored because counts.get() is O(log(n))
           */
          private int appliedWords;

          /**
           * the current score
           */
          private double score;

          /**
           * The file
           */
          private final String file;

          /**
           * The constructor for a QueryEntry Object
           * 
           * @param file the query File
           */
          public QueryEntry(String file) {
               this.file = file;
               this.totalWords = counts.get(file);
               appliedWords = 0;
               score = 0;
          }

          /**
           * A getter for the file
           * 
           * @return the file
           */
          public String getFile() {
               return file;
          }

          /**
           * Adds a file to the query
           * 
           * @param addAppliedWords the amount of applied words in the file
           */
          private void addQuery(int addAppliedWords) {
               appliedWords += addAppliedWords;
               score = ((double) appliedWords / totalWords);
          }

          /**
           * A simple calculation for determining the score of the query in the file
           * 
           * @return the score
           */
          public double getScore() {
               return score;
          }

          @Override
          public String toString() {
               return "\"count\": " + appliedWords + ",\n"
                         + "\"score\": " + String.format("%.8f", getScore()) + ",\n"
                         + "\"where\": \"" + file + "\"";
          }

          /**
           * writes the query entry into the writer in JSON format
           * 
           * @param writer the writer
           * @param level  the level
           * @throws IOException an IO Exception
           */
          public void toJSON(Writer writer, int level) throws IOException {
               JsonWriter.writeIndent(writer, level);
               writer.write("\"count\": " + appliedWords + ",\n");
               JsonWriter.writeIndent(writer, level);
               writer.write("\"score\": " + String.format("%.8f", getScore()) + ",\n");
               JsonWriter.writeIndent(writer, level);
               writer.write("\"where\": \"" + file + "\"");
          }

          /**
           * A simple getter for the total words in the file
           * 
           * @return the total words
           */
          public int getTotalWords() {
               return totalWords;
          }

          @Override
          public int compareTo(QueryEntry o) {
               return Comparator.comparing(QueryEntry::getScore).thenComparingInt(QueryEntry::getTotalWords)
                         .thenComparing(QueryEntry::getFile, Comparator.reverseOrder()).compare(o, this);
          }

     }
}
