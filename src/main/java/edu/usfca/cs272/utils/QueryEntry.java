package edu.usfca.cs272.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;

/**
 * Class responsible for the Query
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class QueryEntry implements Comparable<QueryEntry> { 
     /**
      * The total words in the file
      */
     private final int totalWords; // 

     /**
      * The total applied words in the file
      */
     private int appliedWords;

     private double score;

     /**
      * The file
      */
     private final String file;

     /**
      * The constructor for a QueryEntry Object
      * 
      * @param file       the query File
      * @param totalWords the total words in the file
      */
     public QueryEntry(String file, int totalWords) {
          this.file = file;
          this.totalWords = totalWords;
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
     public void addQuery(int addAppliedWords) {
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
      * @param level the level
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