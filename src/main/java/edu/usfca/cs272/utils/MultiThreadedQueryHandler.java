package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
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
public class MultiThreadedQueryHandler implements QueryHandlerInterface {

     /**
      * the query
      */
     private final TreeMap<String, List<QueryEntry>> query;

     /**
      * The search function
      */
     private final Function<Set<String>, List<QueryEntry>> searchFunction;

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
          this.workQueue = workQueue;
          queryLock = new MultiReaderLock();
          query = new TreeMap<>();
          searchFunction = partial ? invertedIndex::partialSearch : invertedIndex::exactSearch;
     }

     /**
      * Handles the queries given a path and whether it's partial search
      * 
      * @param path the input path
      * @throws IOException an IO exception
      */
     @Override
     public void handleQueries(Path path) throws IOException {
          QueryHandlerInterface.super.handleQueries(path);
          workQueue.finish();
     }

     /**
      * handles the queries given a line of search
      * 
      * @param line the line
      */
     @Override
     public void handleQueries(String line) {
          workQueue.execute(new QueryTask(line));
     }

     /**
      * handles the queries given a line of search and a stemmer
      * 
      * @param line    the line
      * @param stemmer the stemmer
      */
     @Override
     public void handleQueries(String line, SnowballStemmer stemmer) {
          workQueue.execute(new QueryTask(line, stemmer));
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
     @Override
     public List<QueryEntry> getQueryResults(Set<String> stems, String key) {
          if (stems.size() > 0) {
               List<QueryEntry> queries;

               queryLock.readLock().lock();
               try {
                    queries = query.get(key);
               } finally {
                    queryLock.readLock().unlock();
               }

               if (queries == null) {
                    queries = searchFunction.apply(stems);
               }

               return queries;
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
          return getQueryResults(queries, QueryHandlerInterface.getSearchFromWords(queries));
     }

     @Override
     public Function<Set<String>, List<QueryEntry>> getSearchFunction() {
          return searchFunction;
     }

     /**
      * The task for a query
      */
     public class QueryTask implements Runnable {
          /**
           * The line
           */
          private String line;

          private Stemmer stemmer;

          /**
           * The constructor
           * 
           * @param line the line to parse
           */
          public QueryTask(String line) {
               this.line = line;
               stemmer = new SnowballStemmer(ENGLISH);
          }

          public QueryTask(String line, Stemmer stemmer) {
               this.line = line;
               this.stemmer = stemmer;
          }

          @Override
          public void run() {
               final Set<String> val = FileStemmer.uniqueStems(line, stemmer);
               final String key = QueryHandlerInterface.getSearchFromWords(val);

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

     }
}