package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for handling the Queries
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class QueryHandler {

     /**
     * The inverted index
     */
    private final InvertedIndex invertedIndex;
    
     /**
     * The query
     */
    private final TreeMap<String, TreeSet<QueryEntry>> query;

     /**
      * The constructor for a QueryHandler
      * 
     * @param invertedIndex the invertedIndex
     */
    public QueryHandler(InvertedIndex invertedIndex) { // TODO pass partial here instead of the methods below
          this.invertedIndex = invertedIndex;
          query = new TreeMap<>();
     }
    
     /**
      * Handles the queries given a path and whether it's partial search
      * 
     * @param path the input path
     * @param partial whether the search includes partial matches
     * @throws IOException an IO exception
     */
    public void handleQueries(Path path, boolean partial) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               String line = null;

               while ((line = reader.readLine()) != null) {

                    ArrayList<String> stems = new ArrayList<>(FileStemmer.uniqueStems(line));
                    ArrayList<String> searchStems = new ArrayList<>(stems);

                    if (partial) {

                         for (String stem : stems) {
                              invertedIndex.getWords().stream()
                                        .filter(curr -> curr.startsWith(stem) && !curr.equals(stem))
                                        .forEach(searchStems::add);
                         }
                    }
                    if (stems.size() != 0)
                         addQuery(stems, searchStems);
               }

          }
     }
    
    /* TODO 
    public void handleQueries(String line) {
    		TreeSet<String> stems = FileStemmer.uniqueStems(line, stemmer) (create a stemmer to reuse)
    		the other stuff here too
     }
     */

     /**
      * Adds a query given the stems and the searches
      * 
     * @param stems the stems
     * @param search the total stems (include partial)
     */
    public void addQuery(List<String> stems, List<String> search) {
          if (stems.size() == 0)
               return;

          TreeSet<QueryEntry> entries = new TreeSet<>();

          // TODO No forEach just use a for loop for now
          search.stream().forEach(word -> {
               try {
                    invertedIndex.getLocationsOfWord(word).forEach((file) -> {
                         QueryEntry existingEntry = entries.stream()
                                   .filter(entry -> entry.getFile().equals(file))
                                   .findFirst()
                                   .orElse(null);

                         if (existingEntry != null) {
                              entries.remove(existingEntry);
                              addQuery(existingEntry, word, file); // Update the existing entry with new query
                         } else {
                              existingEntry = new QueryEntry(file, invertedIndex.getCountsInLocation(file));
                              addQuery(existingEntry, word, file);
                         }

                         entries.add(existingEntry);
                    });
               } catch (Exception ex) {
              	 // TODO Please no
               }
          });

          query.put(getSearchFromWords(stems), entries);
     }

     /**
      * Adds the query given the search, file and word
      * 
     * @param search the search
     * @param file the file
     * @param word the word
     */
    public void addQuery(String search, String file, String word) {
          TreeSet<QueryEntry> entries = query.computeIfAbsent(search, k -> new TreeSet<>());

          QueryEntry existingEntry = entries.stream()
                    .filter(entry -> entry.getFile().equals(file))
                    .findFirst()
                    .orElse(null);

          if (existingEntry != null) {
               entries.remove(existingEntry);
               addQuery(existingEntry, word, file); // Update the existing entry with new query
          } else {
               existingEntry = new QueryEntry(file, invertedIndex.getCountsInLocation(file));
               addQuery(existingEntry, word, file);
          }

          entries.add(existingEntry); // Re-add the entry to maintain sorting
     }

     /**
      * Adds the query if the size > 0
      * 
     * @param entry the queryEntry
     * @param word the word to add
     * @param file the file
     */
    private void addQuery(QueryEntry entry, String word, String file) {
          int size = invertedIndex.getInstancesOfWordInLocation(word, file).size();
          if (size > 0)
               entry.addQuery(size);
     }

     /**
      * Combines the List into a String
      * 
     * @param words the list of strings
     * @return the string containing the list
     */
    public static String getSearchFromWords(List<String> words) {
          return String.join(" ", words);
     }

     /**
      * Writes the query to a file
      * 
     * @param path the output file
     * @throws IOException an IO Exception
     */
    public void writeQuery(Path path) throws IOException {
          JsonWriter.writeMapCollectionObject(query, path);
     }

}