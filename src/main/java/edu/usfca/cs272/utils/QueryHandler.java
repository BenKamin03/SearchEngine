package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import edu.usfca.cs272.utils.InvertedIndex.QueryEntry;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/**
 * Class responsible for handling the Queries
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class QueryHandler implements QueryHandlerInterface {

     /**
      * The query
      */
     private final TreeMap<String, List<QueryEntry>> query;

     /**
      * the search function
      */
     private final Function<Set<String>, List<QueryEntry>> searchFunction;

     /**
      * The constructor for a QueryHandler
      * 
      * @param invertedIndex the invertedIndex
      * @param partial       whether the search should include partial matches
      */
     public QueryHandler(InvertedIndex invertedIndex, boolean partial) {
          query = new TreeMap<>();
          searchFunction = partial ? invertedIndex::partialSearch : invertedIndex::exactSearch;
     }

     /**
      * Handles the queries given a path and whether it's partial search
      * 
      * @param path the input path
      * @throws IOException an IO exception
      */
     public void handleQueries(Path path) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               String line = null;
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH); // TODO Make a member

               while ((line = reader.readLine()) != null) {
                    handleQueries(line, stemmer);
               }

          }
     }

     /**
      * handles the queries given a line of search
      * 
      * @param line the line
      */
     public void handleQueries(String line) {
          handleQueries(line, new SnowballStemmer(ENGLISH));
     }

     /**
      * handles the queries given a line of search and a stemmer
      * 
      * @param line    the line
      * @param stemmer the stemmer
      */
     public void handleQueries(String line, SnowballStemmer stemmer) {
          Set<String> queries = FileStemmer.uniqueStems(line, stemmer);
          if (queries.size() > 0) {
               String key = getSearchFromWords(queries);
               query.put(key, getQueryResults(queries, key));
          }
     }

     /**
      * Combines the List into a String
      * 
      * @param words the list of strings
      * @return the string containing the list
      */
     public static String getSearchFromWords(Set<String> words) {
          return String.join(" ", words);
     }

     /**
      * Writes the query to a file
      * 
      * @param path the output file
      * @throws IOException an IO Exception
      */
     public void writeQuery(Path path) throws IOException {
          JsonWriter.writeQuery(query, path);
     }

     @Override
     public String toString() {
          return JsonWriter.writeQuery(query);
     }

     /**
      * returns the search function
      * 
      * @return the search function
      */
     public Function<Set<String>, List<QueryEntry>> getSearchFunction() {
          return searchFunction;
     }

     /**
      * gets the lines used to get the query entries
      * 
      * @return the query's keyset
      */
     public Set<String> getQueryLines() {
          return Collections.unmodifiableSet(query.keySet());
     }

     /**
      * gets the query results for a line of search and a stemmer
      * 
      * @param line    the line of search
      * @param stemmer the stemmer
      * @return the list of queries
      */
     public List<QueryEntry> getQueryResults(String line, SnowballStemmer stemmer) {
          TreeSet<String> stems = FileStemmer.uniqueStems(line, stemmer);

          if (stems.size() > 0) {
               return getQueryResults(stems, getSearchFromWords(stems));
          }

          return Collections.emptyList();
     }

     /**
      * gets the query results from a list of stems and a key
      * 
      * @param stems the stems
      * @param key   the key
      * @return the list of query results
      */
     public List<QueryEntry> getQueryResults(Set<String> stems, String key) {
          List<QueryEntry> val;

          val = query.get(key);

          if (val == null) {
               val = searchFunction.apply(stems);
          }

          return val;
     }

     /**
      * gets the query results for a line
      * 
      * @param line the line
      * @return the list of queries
      */
     public List<QueryEntry> getQueryResults(String line) {
          return getQueryResults(line, new SnowballStemmer(ENGLISH));
     }
}