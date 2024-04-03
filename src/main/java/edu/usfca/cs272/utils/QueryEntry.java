package edu.usfca.cs272.utils;

import java.util.Comparator;

/**
 * Class responsible the Query
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class QueryEntry implements Comparator<QueryEntry>, Comparable<QueryEntry> { // TODO Remove Comparator<QueryEntry>
     /**
     * The total words in the file
     */
    private int totalWords; // TODO final
    
     /**
     * The total applied words in the file
     */
    private int appliedWords;
    
    // TODO Add a member to store the score, update when call addQuery
    
     /**
     * The file
     */
    private String file; // TODO final

     /**
      * The constructor for a QueryEntry Object
      * 
     * @param file the query File
     * @param totalWords the total words in the file
     */
    public QueryEntry(String file, int totalWords) {
          this.file = file;
          this.totalWords = totalWords;
          appliedWords = 0;
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
     }

     /**
      * A simple calculation for determining the score of the query in the file
      * 
     * @return the score
     */
    public double getScore() {
          return ((double) appliedWords / totalWords);
     }

     // TODO @Override
     public String toString() {
          return "\"count\": " + appliedWords + ",\n"
                    + "\"score\": " + String.format("%.8f", getScore()) + ",\n"
                    + "\"where\": \"" + file + "\"";
     }
     
     /* TODO 
     public void toJson(Writer writer, int level)
     */

     /**
      * A simple getter for the total words in the file
      * 
     * @return the total words
     */
    public int getTotalWords() {
          return totalWords;
     }

     @Override
     public int compare(QueryEntry o1, QueryEntry o2) { // TODO Remove this
          return Comparator.comparing(QueryEntry::getScore).thenComparingInt(QueryEntry::getTotalWords)

                    .thenComparing(QueryEntry::getFile, Comparator.reverseOrder()).compare(o1, o2);
     }

     @Override
     public int compareTo(QueryEntry o) {
          // TODO Just integrate compare directly into here
          return compare(o, this);
     }

}