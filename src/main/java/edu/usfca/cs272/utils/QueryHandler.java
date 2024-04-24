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
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/**
 * Class responsible for handling the Queries
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class QueryHandler {

     /**
      * The query
      */
     private final TreeMap<String, List<QueryEntry>> query;

     /**
      * the lock for the query
      */
     private final MultiReaderLock queryLock;

     /**
      * the search function
      */
     private final Function<Set<String>, List<QueryEntry>> searchFunction;

     /**
      * the work queue
      */
     private final WorkQueue workQueue;

     /**
      * The constructor for a QueryHandler
      * 
      * @param invertedIndex the invertedIndex
      * @param partial       whether the search should include partial matches
      * @param threads       the number of threads to use in the work queue
      */
     public QueryHandler(InvertedIndex invertedIndex, boolean partial, int threads) { // TODO Pass in the work queue
          query = new TreeMap<>();
          searchFunction = partial ? invertedIndex::partialSearch : invertedIndex::exactSearch;
          workQueue = new WorkQueue(threads);
          queryLock = new MultiReaderLock();
     }

     /**
      * Handles the queries given a path and whether it's partial search
      * 
      * @param path the input path
      * @throws IOException an IO exception
      */
     public void handleQueries(Path path) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);
               handleQueries(path, reader, stemmer);
               workQueue.join();
          }
     }

     public void handleQueries(Path path, BufferedReader reader, Stemmer stemmer) throws IOException {
          String line = null;
          while ((line = reader.readLine()) != null) {
               final Set<String> val = FileStemmer.uniqueStems(line, stemmer); // TODO Move stemming into the task too... but use a new local stemmer instance
               final String key = getSearchFromWords(FileStemmer.uniqueStems(line, stemmer));
               workQueue.execute(() -> {
                    if (key.length() > 0) {
                         queryLock.writeLock().lock();
                         try {
                              query.put(key, getQueryResults(val)); // TODO Search is happening inside of a write lock
                         } finally {
                              queryLock.writeLock().unlock();
                         }
                    }
               });
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
          workQueue.execute(() -> {
               TreeSet<String> stems = FileStemmer.uniqueStems(line, stemmer);
               if (stems.size() > 0) {
                    String key = getSearchFromWords(stems);
                    List<QueryEntry> val;

                    queryLock.readLock().lock();
                    try {
                         val = query.get(key);
                    } finally {
                         queryLock.readLock().unlock();
                    }

                    queryLock.writeLock().lock();
                    try {
                         if (val == null) {
                              query.put(key, searchFunction.apply(stems));
                         } else {
                              query.put(key, val);
                         }
                    } finally {
                         queryLock.writeLock().unlock();
                    }
               }
          });
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
          queryLock.readLock().lock();
          try {
               JsonWriter.writeQuery(query, path);
          } finally {
               queryLock.readLock().unlock();
          }
     }

     @Override
     public String toString() {
          queryLock.readLock().lock();
          try {
               return JsonWriter.writeQuery(query);
          } finally {
               queryLock.readLock().unlock();
          }
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
          queryLock.readLock().lock();
          try {
               return Collections.unmodifiableSet(query.keySet());
          } finally {
               queryLock.readLock().unlock();
          }
     }

     public List<QueryEntry> getQueryResults(Set<String> stems) {
          if (stems.size() > 0) {
               String key = getSearchFromWords(stems);
               List<QueryEntry> val;

               queryLock.readLock().lock();
               try {
                    val = query.get(key);
               } finally {
                    queryLock.readLock().unlock();
               }

               if (val == null) {
                    val = searchFunction.apply(stems);
               }

               return val;
          }

          return Collections.emptyList();
     }

     /**
      * gets the query results for a line of search and a stemmer
      * 
      * @param line    the line of search
      * @param stemmer the stemmer
      * @return the list of queries
      */
     public List<QueryEntry> getQueryResults(String line, Stemmer stemmer) {
          return getQueryResults(FileStemmer.uniqueStems(line, stemmer));
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