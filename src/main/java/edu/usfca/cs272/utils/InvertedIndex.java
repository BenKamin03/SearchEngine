package edu.usfca.cs272.utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class InvertedIndex {

// TODO Make both of these final

	/* TODO 
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> indexes;
	private TreeMap<String, Integer> counts;
	*/
	
     private SortedMap<String, SortedMap<String, ArrayList<Integer>>> indexes;
     private SortedMap<String, Integer> counts;

     public InvertedIndex() {
          indexes = new TreeMap<>();
          counts = new TreeMap<>();
     }

     public SortedMap<String, SortedMap<String, ArrayList<Integer>>> getIndexes() {
          return indexes;
     }

     public SortedMap<String, Integer> getCounts() {
          return counts;
     }

     /*
      * Method to check if a variable is null and set it to a safe value if it is
      */
     public static <T> T checkSafeValue(T variable, T safeValue) { // TODO Remove
          if (variable == null) {
               return safeValue;
          }
          return variable;
     }

     // TODO public void addIndex(String word, String location, int index) {
     public void addIndex(String stem, Path p, int index) {
    	 // TODO map.computeIfAbsent 
          SortedMap<String, ArrayList<Integer>> table = checkSafeValue(indexes.get(stem),
                    new TreeMap<>());
          ArrayList<Integer> list = checkSafeValue(table.get(p.toString()), new ArrayList<>());
          list.add(index);
          table.put(p.toString(), list);
          indexes.put(stem, table);
     }

     public void addCount(String file, int count) {
          counts.put(file , count);
     }

     /*
      * TODO Try to build up the general functionality
      */
}
