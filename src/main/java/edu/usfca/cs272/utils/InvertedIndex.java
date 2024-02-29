package edu.usfca.cs272.utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	
     private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexes;
     private final TreeMap<String, Integer> counts;

     public InvertedIndex() {
          indexes = new TreeMap<>();
          counts = new TreeMap<>();
     }

     public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndexes() {
          return indexes;
     }

     public TreeMap<String, Integer> getCounts() {
          return counts;
     }

     public void addIndex(String word, String location, int index) {
          indexes.computeIfAbsent(word, k -> new TreeMap<>()).computeIfAbsent(location, k -> new TreeSet<>()).add(index);
     }

     public void addCount(String file, int count) {
          counts.put(file , count);
     }

     /*
      * TODO Try to build up the general functionality
      */
}
