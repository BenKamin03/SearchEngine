package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import edu.usfca.cs272.utils.InvertedIndex.QueryEntry;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

/**
 * QueryHandlerInterface
 */
public interface QueryHandlerInterface {
     
	/**
	 * handles the queries given a path
	 * 
	 * @param path the path
	 * @throws IOException an IO exception
	 */
     public default void handleQueries(Path path) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               String line = null;
               while ((line = reader.readLine()) != null) {
                    handleQueries(line);
               }
          }
     }
     
     /**
      * hands the queries given a line of query
      * 
      * @param line the line
      */
     public void handleQueries(String line);
     

     /**
      * handles the queries given a line and a stemmer
      * 
      * @param line the line
      * @param stemmer the stemmer
      */
     public void handleQueries(String line, SnowballStemmer stemmer);
     
     /**
      * gets the concatenated String of the words set
      * 
      * @param words the words
      * @return the string
      */
     public static String getSearchFromWords(Set<String> words) {
          return String.join(" ", words);
     }
     
     /**
      * writes the query to a file
      * 
      * @param path the path
      * @throws IOException an io exception
      */
     public void writeQuery(Path path) throws IOException;
     
     /**
      * gets the search function
      * 
      * @return the search function
      */
     public Function<Set<String>, List<QueryEntry>> getSearchFunction();

     /**
      * gets the query lines
      * 
      * @return the query lines
      */
     public Set<String> getQueryLines();
     
     /**
      * gets the query results given a line and a stemmer
      * 
      * @param line the line
      * @param stemmer the stemmer
      * @return the query results
      */
      public default List<QueryEntry> getQueryResults(String line, SnowballStemmer stemmer) {
          TreeSet<String> stems = FileStemmer.uniqueStems(line, stemmer);

          if (stems.size() > 0) {
               return getQueryResults(stems, QueryHandlerInterface.getSearchFromWords(stems));
          }

          return Collections.emptyList();
     }
     
     /**
      * gets the query result given a set of stems and the key
      * 
      * @param stems the set of stems
      * @param key the key
      * @return the query results
      */
     public List<QueryEntry> getQueryResults(Set<String> stems, String key);
     
     /**
      * gets the query results given a line
      * 
      * @param line the line
      * @return the query results
      */
      public default List<QueryEntry> getQueryResults(String line) {
          return getQueryResults(line, new SnowballStemmer(ENGLISH));
     }
}
