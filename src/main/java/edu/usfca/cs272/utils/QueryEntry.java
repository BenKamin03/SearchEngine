package edu.usfca.cs272.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QueryEntry implements Comparator<QueryEntry>, Comparable<QueryEntry> {
     private int totalWords;
     private int appliedWords;
     private String file;
     List<String> queries;

     public QueryEntry(String file, int totalWords) {
          this.file = file;
          this.totalWords = totalWords;
          appliedWords = 0;
          queries = new ArrayList<>();
     }

     public String getFile() {
          return file;
     }

     public void addQuery(String query, int addAppliedWords) {
          if (!queries.contains(query)) {
               appliedWords += addAppliedWords;
               queries.add(query);
          }
     }

     public double getScore() {
          return ((double) appliedWords / totalWords);
     }

     public String toString() {
          return "\"count\": " + appliedWords + ",\n"
                    + "\"score\": " + String.format("%.8f", getScore()) + ",\n"
                    + "\"where\": \"" + file + "\"";
     }

     public int getTotalWords() {
          return totalWords;
     }

     @Override
     public int compare(QueryEntry o1, QueryEntry o2) {
          return Comparator.comparing(QueryEntry::getScore).thenComparingInt(QueryEntry::getTotalWords).thenComparing(QueryEntry::getFile, Comparator.reverseOrder()).compare(o1, o2);
     }

     @Override
     public int compareTo(QueryEntry o) {
          return compare((QueryEntry) o, this);
     }

}
