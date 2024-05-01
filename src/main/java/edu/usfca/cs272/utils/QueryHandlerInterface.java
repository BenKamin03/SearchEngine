package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import edu.usfca.cs272.utils.InvertedIndex.QueryEntry;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * QueryHandlerInterface
 */
public interface QueryHandlerInterface {
     
     public void handleQueries(Path path) throws IOException;

     public void handleQueries(String line);

     public void handleQueries(String line, SnowballStemmer stemmer);

     public static String getSearchFromWords(Set<String> words) {
          return String.join(" ", words);
     }

     public void writeQuery(Path path) throws IOException;

     public Function<Set<String>, List<QueryEntry>> getSearchFunction();

     public Set<String> getQueryLines();

     public List<QueryEntry> getQueryResults(String line, SnowballStemmer stemmer);

     public List<QueryEntry> getQueryResults(Set<String> stems, String key);

     public List<QueryEntry> getQueryResults(String line);
}
