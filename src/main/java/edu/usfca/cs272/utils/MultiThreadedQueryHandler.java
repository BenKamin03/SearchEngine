package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.usfca.cs272.utils.InvertedIndex.QueryEntry;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/*
 * TODO Go the interface route instead of the extends route
 */

/**
 * Class responsible for handling the Queries
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class MultiThreadedQueryHandler extends QueryHandler {

     /**
      * the lock for the query
      */
     private final MultiReaderLock queryLock;

     /**
      * the work queue
      */
     private final WorkQueue workQueue;

     /**
      * The constructor for a QueryHandler
      * 
      * @param invertedIndex the invertedIndex
      * @param partial       whether the search should include partial matches
      * @param workQueue     the work queue
      */
     public MultiThreadedQueryHandler(InvertedIndex invertedIndex, boolean partial, WorkQueue workQueue) {
          super(invertedIndex, partial);
          this.workQueue = workQueue;
          queryLock = new MultiReaderLock();
     }

     /**
      * Handles the queries given a path and whether it's partial search
      * 
      * @param path the input path
      * @throws IOException an IO exception
      */
     @Override
     public void handleQueries(Path path) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               handleQueries(path, reader);
               workQueue.finish();
          }
     }

     /**
      * Handles the queries given a path and a reader
      * 
      * @param path   the path
      * @param reader the reader
      * @throws IOException an IO exception
      */
     public void handleQueries(Path path, BufferedReader reader) throws IOException {
          String line = null;
          while ((line = reader.readLine()) != null) {
               workQueue.execute(new QueryTask(line));
          }
     }

     /**
      * handles the queries given a line of search
      * 
      * @param line the line
      */
     @Override
     public void handleQueries(String line) {
          handleQueries(line, new SnowballStemmer(ENGLISH));

     }

     /**
      * handles the queries given a line of search and a stemmer
      * 
      * @param line    the line
      * @param stemmer the stemmer
      */
     @Override
     public void handleQueries(String line, SnowballStemmer stemmer) {

          final Set<String> val = FileStemmer.uniqueStems(line, stemmer);
          final String key = getSearchFromWords(FileStemmer.uniqueStems(line, stemmer)); // TODO Don't stem twice

          if (key.length() > 0) {
               List<QueryEntry> queryResults = getQueryResults(val, key);
               queryLock.writeLock().lock();
               try {
                    query.put(key, queryResults);
               } finally {
                    queryLock.writeLock().unlock();
               }
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
     @Override
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
      * gets the lines used to get the query entries
      * 
      * @return the query's keyset
      */
     @Override
     public Set<String> getQueryLines() {
          queryLock.readLock().lock();
          try {
               return Collections.unmodifiableSet(query.keySet());
          } finally {
               queryLock.readLock().unlock();
          }
     }

     /**
      * Gets the query results from a list of stems and the key
      * 
      * @param stems the stems
      * @param key   the key
      * @return the list of query entry matches
      */
     // TODO @Override
     public List<QueryEntry> getQueryResults(Set<String> stems, String key) {
          if (stems.size() > 0) {
               List<QueryEntry> val;

               queryLock.readLock().lock();
               try {
                    val = query.get(key);
               } finally {
                    queryLock.readLock().unlock();
               }

               if (val == null) {
                    val = super.getSearchFunction().apply(stems);
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
          Set<String> queries = FileStemmer.uniqueStems(line, stemmer);
          return getQueryResults(queries, getSearchFromWords(queries));
     }

     /**
      * gets the query results for a line
      * 
      * @param line the line
      * @return the list of queries
      */
     @Override
     public List<QueryEntry> getQueryResults(String line) {
          return getQueryResults(line, new SnowballStemmer(ENGLISH));
     }

     /**
      * The task for a query
      */
     public class QueryTask implements Runnable {
          /**
           * The line
           */
          private String line;

          /**
           * The constructor
           * 
           * @param line the line to parse
           */
          public QueryTask(String line) {
               this.line = line;
          }

          @Override
          public void run() {
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);
               handleQueries(line, stemmer);
          }

     }
}