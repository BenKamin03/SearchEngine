package edu.usfca.cs272.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @author Ben Kamin
 * @version Spring 2024
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents the writer on a new line by the specified number of times. Does
	 * nothing if the
	 * indentation level is 0 or less.
	 * 
	 * @param element the element to add 
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndentOnNewLine(String element, Writer writer, int indent) throws IOException {
		writer.write("\n");
		writeIndent(element, writer, indent);
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the
	 *                 initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent)
			throws IOException {
		var iterator = elements.iterator();

		writer.write("[");
		if (iterator.hasNext()) {
			writeIndentOnNewLine(iterator.next().toString(), writer, indent + 1);
			while (iterator.hasNext()) {
				writer.write(",");
				writeIndentOnNewLine(iterator.next().toString(), writer, indent + 1);
			}
		}
		writeIndentOnNewLine("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * @param a the variable name
	 * @param b the variable value
	 * @return the line for an object
	 */
	public static String getObjectLine(String a, String b) {
		return "\"" + a + "\": " + b;
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the
	 *                 initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent)
			throws IOException {
		var iterator = elements.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			var element = iterator.next();
			writeIndentOnNewLine(getObjectLine(element.getKey(), elements.get(element.getKey()).toString()), writer,
					indent + 1);
			while (iterator.hasNext()) {
				element = iterator.next();
				writer.write(",");
				writeIndentOnNewLine(getObjectLine(element.getKey(), elements.get(element.getKey()).toString()),
						writer, indent + 1);
			}
		}
		writeIndentOnNewLine("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the line for an Object Array
	 *
	 * @param elements the elements to use
	 * @param element  the element in the map
	 * @param writer   the writer
	 * @param indent   the indentation
	 * @throws IOException an IO exception 
	 *
	 * @see StringWriter
	 */
	public static void writeObjectArrayLine(Map<String, ? extends Collection<? extends Number>> elements,
			Entry<String, ? extends Collection<? extends Number>> element, Writer writer, int indent)
			throws IOException {
		writeQuote(element.getKey(), writer, indent + 1);
		writer.write(": ");
		writeArray(elements.get(element.getKey()), writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param map    the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *               inner elements are indented by one, and the last bracket is
	 *               indented at the
	 *               initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> map, Writer writer,
			int indent) throws IOException {
		var iterator = map.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			var element = iterator.next();
			writer.write("\n");
			writeObjectArrayLine(map, element, writer, indent);
			while (iterator.hasNext()) {
				element = iterator.next();
				writer.write(",\n");
				writeObjectArrayLine(map, element, writer, indent);
			}
		}
		writeIndentOnNewLine("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the line for an Array Object
	 *
	 * @param elements the elements to use
	 * @param element  the element in the map
	 * @param writer   the writer
	 * @param indent   the indentation
	 * @throws IOException an IO exception 
	 *
	 * @see StringWriter
	 */
	public static void writeArrayObjectsLine(Collection<? extends Map<String, ? extends Number>> elements,
			Map<String, ? extends Number> element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent + 1);
		writeObject(element, writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the
	 *                 initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		var iterator = elements.iterator();

		writer.write("[");
		if (iterator.hasNext()) {
			var element = iterator.next();
			writer.write("\n");
			writeArrayObjectsLine(elements, element, writer, indent);
			while (iterator.hasNext()) {
				element = iterator.next();
				writer.write(",\n");
				writeArrayObjectsLine(elements, element, writer, indent);
			}
		}
		writeIndentOnNewLine("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 * 
	 * @param hash the hash 
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeObjectHash(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> hash,
			Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectHash(hash, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects and arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeObjectHash(Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectHash(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the line for an Object Hash
	 *
	 * @param element  the element in the map
	 * @param writer   the writer
	 * @param indent   the indentation
	 * @throws IOException an IO exception 
	 *
	 * @see StringWriter
	 */
	public static void writeObjectHashLine(
			Entry<String, ? extends Map<String, ? extends Collection<? extends Number>>> element, Writer writer,
			int indent) throws IOException {
		writeQuote(element.getKey(), writer, indent + 1);
		writer.write(": ");
		writeObjectArrays(element.getValue(), writer, indent + 1);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 * 
	 * @param hash the hash 
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the
	 *                 initial indentation level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeObjectHash(Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> hash,
			Writer writer, int indent) throws IOException {

		var iterator = hash.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			var element = iterator.next();
			writer.write("\n");
			writeObjectHashLine(element, writer, indent);
			while (iterator.hasNext()) {
				element = iterator.next();
				writer.write(",\n");
				writeObjectHashLine(element, writer, indent);
			}
		}
		writeIndentOnNewLine("}", writer, indent);
	}

	public static void writeMapCollectionObjectLine(Entry<String, ? extends Collection<? extends Object>> element,
			Writer writer, int indent) throws IOException {
		writeQuote(element.getKey(), writer, indent + 1);
		writer.write(": [");
		writeCollectionObject(element.getValue(), writer, indent + 1);
	}

	public static void writeCollectionObjectLine(Object object, Writer writer, int indent) throws IOException {
		writeIndent("{\n", writer, indent);
		String str = object.toString();
		for (String s : str.split("\n")) {
			writeIndent(s, writer, indent + 1);
			writer.write("\n");
		} 
		writeIndent("}", writer, indent);
	}

	public static void writeCollectionObject(Collection<? extends Object> elements, Writer writer, int indent)
			throws IOException {
		if (elements != null) {
			var iterator = elements.iterator();
			if (iterator != null) {
				writer.write("");
				if (iterator.hasNext()) {
					var element = iterator.next();
					writer.write("\n");
					writeCollectionObjectLine(element, writer, indent + 1);
					while (iterator.hasNext()) {
						element = iterator.next();
						writer.write(",\n");
						writeCollectionObjectLine(element, writer, indent + 1);
					}
				}
				writeIndentOnNewLine("]", writer, indent);
			}
		} else {
			writeIndentOnNewLine("]", writer, indent);
		}
	}

	public static void writeMapCollectionObject(Map<String, ? extends Collection<? extends Object>> elements,
			Writer writer, int indent) throws IOException {
		var iterator = elements.entrySet().iterator();

		writer.write("{");
		if (iterator.hasNext()) {
			var element = iterator.next();
			writer.write("\n");
			writeMapCollectionObjectLine(element, writer, indent);
			while (iterator.hasNext()) {
				element = iterator.next();
				writer.write(",\n");
				writeMapCollectionObjectLine(element, writer, indent);
			}
		}
		writeIndentOnNewLine("}", writer, indent);
	}

	public static String writeMapCollectionObject(Map<String, ? extends Collection<? extends Object>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeMapCollectionObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	public static void writeMapCollectionObject(Map<String, ? extends Collection<? extends Object>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeMapCollectionObject(elements, writer, 0);
		}
	}

	/** Prevent instantiating this class of static methods. */
	private JsonWriter() {
	}
}
