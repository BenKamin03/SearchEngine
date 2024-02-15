package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.usfca.cs272.Driver;

public class IndexHandler {
     /**
      * Reads and creates an inversed lookup table of the contents of a file and
      * outputs it to a json
      * 
      * @param parser - the command line argument
      */
     public static void run(ArgumentParser parser) {
          if (parser.hasFlag("-index")) {
               Path in = parser.getPath("-text");

               Path out_backup = FileSystems.getDefault().getPath("index.json");
               Path out = parser.getPath("-index", out_backup);

               SortedMap<String, SortedMap<String, ArrayList<Integer>>> hash = new TreeMap<>();
               if (in != null)
                    fillHash(hash, in, false);

               try {
                    JsonWriter.writeObjectHash(hash, out);
               } catch (IOException e) {
               }
          }
     }

     /**
      * Fills Hash with stem info for Path p. This is used to generate the Hash from
      * files and directories
      * 
      * @param hash        - Map to fill with stems
      * @param p           - Path to file or directory to process ( recursive )
      * @param isDirectory - True if Path is a directory false if
      */
     public static void fillHash(SortedMap<String, SortedMap<String, ArrayList<Integer>>> hash, Path p,
               boolean isDirectory) {
          /*
           * ---------------------------------------------
           * 
           * Credit: Sophie Engle
           * https://github.com/usf-cs272-spring2024/cs272-lectures/blob/main/src/main/
           * java/edu/usfca/cs272/lectures/basics/io/DirectoryStreamDemo.java
           * 
           * ---------------------------------------------
           */
          if (Files.isDirectory(p)) {
               // Path is a Directory --> Recurse Through Each Sub Path
               try {
                    for (Path path : Files.newDirectoryStream(p)) {
                         fillHash(hash, path, true);
                    }
               } catch (Exception ex) {
                    ex.printStackTrace();
               }
          } else {
               // Path is a File --> Base Case
               if (p.getFileName().toString().toLowerCase().endsWith(".txt")
                         || p.getFileName().toString().toLowerCase().endsWith(".text") || !isDirectory) {
                    try {
                         handleFile(hash, p);
                    } catch (Exception ex) {
                         ex.printStackTrace();
                    }
               }
               return;
          }
     }

     public static void handleFile(SortedMap<String, SortedMap<String, ArrayList<Integer>>> hash, Path p)
               throws IOException {
          ArrayList<String> stems = FileStemmer.listStems(p);
          if (stems.size() > 0) {
               int i = 1;
               for (String s : stems) {
                    SortedMap<String, ArrayList<Integer>> table = Driver.checkSafeValue(hash.get(s), new TreeMap<>());
                    ArrayList<Integer> list = Driver.checkSafeValue(table.get(p.toString()), new ArrayList<>());
                    list.add(i++);
                    table.put(p.toString(), list);
                    hash.put(s, table);
               }
          }
     }
}
