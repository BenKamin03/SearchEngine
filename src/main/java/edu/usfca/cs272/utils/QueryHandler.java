package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
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
      * the stemmer
      */
     private final SnowballStemmer stemmer;

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
          stemmer = new SnowballStemmer(ENGLISH);
     }

     /**
      * handles the queries given a line of search
      * 
      * @param line the line
      */
     @Override
	public void handleQueries(String line) {
          handleQueries(line, stemmer);
     }

     /**
      * handles the queries given a line of search and a stemmer
      * 
      * @param line    the line
      * @param stemmer the stemmer
      */
     @Override
     public void handleQueries(String line, SnowballStemmer stemmer) {
          Set<String> queries = FileStemmer.uniqueStems(line, stemmer);
          if (queries.size() > 0) {
               String key = QueryHandlerInterface.getSearchFromWords(queries);
               query.put(key, getQueryResults(queries, key));
          }
     }

     /**
      * Writes the query to a file
      * 
      * @param path the output file
      * @throws IOException an IO Exception
      */
     @Override
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
     @Override
	public Function<Set<String>, List<QueryEntry>> getSearchFunction() {
          return searchFunction;
     }

     /**
      * gets the lines used to get the query entries
      * 
      * @return the query's keyset
      */
     @Override
	public Set<String> getQueryLines() {
          return Collections.unmodifiableSet(query.keySet());
     }
     

     /**
      * gets the query results from a list of stems and a key
      * 
      * @param stems the stems
      * @param key   the key
      * @return the list of query results
      */
     @Override
	public List<QueryEntry> getQueryResults(Set<String> stems, String key) {
          List<QueryEntry> queries;

          queries = query.get(key);

          if (queries == null) {
               queries = searchFunction.apply(stems);
          }

          return queries;
     }

     /**
      * gets the query results for a line
      * 
      * @param line the line
      * @return the list of queries
      */
     @Override
	public List<QueryEntry> getQueryResults(String line) {
          return getQueryResults(line, stemmer);
     }
}