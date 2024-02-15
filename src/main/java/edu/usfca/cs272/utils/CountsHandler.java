package edu.usfca.cs272.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

public class CountsHandler {

     /**
     * Reads and counts the contents of a file and outputs it to a json
     * 
     * @param parser - The command line arguments parsed
     */
     public static void run(ArgumentParser parser) {

		if (parser.hasFlag("-counts")) {
			Path in = parser.getPath("-text");

			Path out_backup = FileSystems.getDefault().getPath("counts.json");
			Path out = parser.getPath("-counts", out_backup);

			SortedMap<String, Integer> hash = new TreeMap<>();
			if (in != null)
				fillHash(hash, in, false);

			try {
				JsonWriter.writeObject(hash, out);
			} catch (IOException e) {}
		}
	}

	/**
	* Fills Hash with stem info for Path p. This is used to generate the Hash from files and directories
	* 
	* @param hash - Map to fill with stem info
	* @param p - Path to use for filling Hashes ( can be Directory )
	* @param isDirectory - True if p is a Directory false if
	*/
	public static void fillHash(SortedMap<String, Integer> hash, Path p, boolean isDirectory) {
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
					int size = FileStemmer.listStems(p).size();
					if (size > 0)
						hash.put(p.toString(), (Integer) size);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return;
		}
	}
}
