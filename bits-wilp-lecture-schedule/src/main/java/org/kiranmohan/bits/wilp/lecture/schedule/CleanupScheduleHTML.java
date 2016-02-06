package org.kiranmohan.bits.wilp.lecture.schedule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Kiran
 *
 */
public class CleanupScheduleHTML {

	private Pattern skipPatterns = null;
	private Map<String,String> searchReplaceMap = null;
	private Path cleanedUpHtml = null;
	
	private Map.Entry<String, String> cleanUpEntry = new AbstractMap.SimpleImmutableEntry<>(".*", "");

	/**
	 * @param skipPatterns
	 * @param searchReplaceMap
	 */
	public CleanupScheduleHTML(List<String> skipPatterns, Map<String,String> searchReplaceMap) {
		super();
		this.searchReplaceMap = searchReplaceMap;
		
		String regex = skipPatterns.stream().collect(Collectors.joining("|", "(", ")"));
		this.skipPatterns = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * @return
	 */
	public Path getCleanedUpHtml() {
		return cleanedUpHtml;
	}

	/**
	 * @param file
	 * @param charset
	 * @throws IOException
	 */
	public void process(File file, String charset) throws IOException {
		System.out.println("Parsing " + file);
		Document doc = Jsoup.parse(file, charset);
		process(doc);
	}

	/**
	 * @param url
	 * @throws IOException
	 */
	public void process(String url) throws IOException {

		System.out.println("Connecting to " + url + " and parsing it.");
		Document doc = Jsoup.connect(url).get();
		process(doc);
	}
	
	/**
	 * @param doc
	 */
	public void process(Document doc) {
		cleanedUpHtml = null;
		Elements elements = doc.select("blockquote > div > table");

		/*System.out.println("the tile: " + doc.title());
		System.out.println("number of tables: " + elements.size());*/

		elements.forEach(this::handleTable);
		writeToFile(doc);
	}

	private void writeToFile(Document doc) {
		try {
			cleanedUpHtml = Files.createTempFile("lecture-schedule-", ".html");
			Files.write(cleanedUpHtml, doc.outerHtml().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			cleanedUpHtml = null;
		}
	}
		
	/**
	 * extract events from the given table
	 * 
	 * @param table
	 */
	private void handleTable(Element table) {
		// System.out.println("Processing " + table.attr("id"));
		Elements cells = table.select("tbody > tr > td");
		cells.forEach(this::processCellText);
	}
	
	public void processCellText(Element cell){
		
		String text = trim(cell.text());
		
		//skip if text empty
		if (text.isEmpty())return;
		
		// skip if matching system level skip pattern
		Matcher m = skipPatterns.matcher(text);
		if (m.matches()) return;
		
		// find matching subject/key
		Entry<String, String> key = searchReplaceMap.entrySet()
								                .stream()
								                .filter(e -> text.matches(e.getKey()))
								                .findFirst()			// find the key
								                .orElse(cleanUpEntry);	// or return a key that will cleanup the cell
		                
		String newHtml = cell.html()
							 .replaceAll(key.getKey(), key.getValue());
		cell.html(newHtml);
						
	}
	
	public static String trim(String text) {
		// trim, unfortunately String.trim() doesn't work here.
		text = text.replaceAll("^\\p{javaSpaceChar}*","")		
			       .replaceAll("\\p{javaSpaceChar}*$", "");
		return text;
	}

}
