package org.kiranmohan.bits.wilp.lecture.schedule;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * @author Kiran
 *
 */
public class LectureSchedule {
	
	private static final String SYSTEM_PREFERENCES_FILE = "system_preferences.xml";
	private static final String USER_PREFERENCES_FILE = "user_preferences.xml";
	private static final String PREFS_BASE_NODE = "bits/wilp/lecture/schedule";
	private static final String PREFS_SKIP_PATTERN_NODE = "skipPatterns";
	private static final String PREFS_SEARCH_REPLACE_NODE = "searchAndReplace";
	private static final String PREFS_URL_KEY = "url"; 
	private static final String HTTP_PROXY_HOST_KEY = "http_proxyHost";
	private static final String HTTP_PROXY_PORT_KEY = "http_proxyPort";
	private static final String HTTPS_PROXY_HOST_KEY = "https_proxyHost";
	private static final String HTTPS_PROXY_PORT_KEY = "https_proxyPort";
	
	private static final String DEFAULT_URL = "http://vu.bits-pilani.ac.in/onlineLecture/LectSchedule.htm";
	
	private String url = null;
	private CleanupScheduleHTML parseAndCleanup = null;
	private boolean initialized = false;
	private List<String> skipPatterns = new ArrayList<>();
	private Map<String, String> searchReplaceMap = new HashMap<>();
	
	
	/**
	 * Read User/System preferences
	 * @throws Exception
	 */
	public void init() {
		if (!initialized) {
			readPreferences();
		}
		parseAndCleanup = new CleanupScheduleHTML(skipPatterns, searchReplaceMap);
		initialized = true;
	}
	
	
	/**
	 * Get path to cleaned up HTML file and show in browser
	 */
	public void showCleanedUpHtmlInBrowser(){
		if (!initialized) {
			throw new IllegalStateException("Not Initialized");
		}
		try {
			parseAndCleanup.process(url);
			Path p = parseAndCleanup.getCleanedUpHtml();
			System.out.println(p);
			Desktop.getDesktop().browse(p.toUri());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readPreferences() {
		readSystemPreferences();
		readUserPreferences();
	}
	
	
	private void readSystemPreferences() {
		Preferences systemNode;
		systemNode = Preferences.systemRoot().node(PREFS_BASE_NODE);
		url = systemNode.get(PREFS_URL_KEY, DEFAUL_URL);
		Preferences skipPatternsNode = systemNode.node(PREFS_SKIP_PATTERN_NODE);
		try {
			for(String key : skipPatternsNode.keys()) {
				skipPatterns.add(skipPatternsNode.get(key, ""));
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readUserPreferences() {
		System.setProperty("http.proxyHost", Preferences.userRoot().get(HTTP_PROXY_HOST_KEY, ""));
		System.setProperty("http.proxyPort", Preferences.userRoot().get(HTTP_PROXY_PORT_KEY, ""));
		System.setProperty("https.proxyHost", Preferences.userRoot().get(HTTPS_PROXY_HOST_KEY, ""));
		System.setProperty("https.proxyPort", Preferences.userRoot().get(HTTPS_PROXY_PORT_KEY, ""));
		
		Preferences searchReplaceNode = Preferences.userRoot().node(PREFS_BASE_NODE + "/" + PREFS_SEARCH_REPLACE_NODE);
		try {
			for(String key: searchReplaceNode.keys()) {
				searchReplaceMap.put(key, searchReplaceNode.get(key,key));
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// initialize Preference factory to load xml preferences 
		System.setProperty("java.util.prefs.PreferencesFactory", "nz.net.ultraq.preferences.xml.XMLPreferencesFactory");
		try {
			Preferences.importPreferences(ClassLoader.getSystemResourceAsStream(SYSTEM_PREFERENCES_FILE));
			Preferences.importPreferences(ClassLoader.getSystemResourceAsStream(USER_PREFERENCES_FILE));
		} catch (IOException | InvalidPreferencesFormatException e) {
			System.err.println("Could not load " + SYSTEM_PREFERENCES_FILE);
			e.printStackTrace();
		}
		
		LectureSchedule schedule = new LectureSchedule();
		schedule.init();
		schedule.showCleanedUpHtmlInBrowser();
	}

}
