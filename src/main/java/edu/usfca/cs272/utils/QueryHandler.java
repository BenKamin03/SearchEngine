package edu.usfca.cs272.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
      * The inverted index
      */
     private final InvertedIndex invertedIndex;

     /**
      * The query
      */
     private final TreeMap<String, TreeSet<QueryEntry>> query;

     /**
      * the setting for whether to include partial searches
      */
     private final boolean partial;

     /**
      * The constructor for a QueryHandler
      * 
      * @param invertedIndex the invertedIndex
      * @param partial whether the search should include partial matches
      */
     public QueryHandler(InvertedIndex invertedIndex, boolean partial) {
          this.invertedIndex = invertedIndex;
          query = new TreeMap<>();
          this.partial = partial;
     }

     /**
      * Handles the queries given a path and whether it's partial search
      * 
      * @param path    the input path
      * @throws IOException an IO exception
      */
     public void handleQueries(Path path) throws IOException {
          try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
               String line = null;
               SnowballStemmer stemmer = new SnowballStemmer(ENGLISH);

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
      * @param line the line
      * @param stemmer the stemmer
      */
     public void handleQueries(String line, SnowballStemmer stemmer) {
          TreeSet<String> stems = FileStemmer.uniqueStems(line, stemmer);
          if (stems.size() > 0) {
               if (partial) {
                    query.put(getSearchFromWords(stems), invertedIndex.partialSearch(stems));
               } else {
                    query.put(getSearchFromWords(stems), invertedIndex.exactSearch(stems));
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
     public void writeQuery(Path path) throws IOException {
          JsonWriter.writeMapCollectionObject(query, path);
     }

}