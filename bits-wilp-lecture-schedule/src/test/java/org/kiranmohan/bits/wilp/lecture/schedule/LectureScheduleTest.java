package org.kiranmohan.bits.wilp.lecture.schedule;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.junit.BeforeClass;
import org.junit.Test;

public class LectureScheduleTest {

	@BeforeClass
	public static void initTest() {
		// initialize Preference factory
		System.setProperty("java.util.prefs.PreferencesFactory", "nz.net.ultraq.preferences.xml.XMLPreferencesFactory");

	}

	/**
	 * test that URL is actually read from the preference file and not the
	 * default url.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInit() throws Exception {
		// import the preference file.
		Preferences.importPreferences(ClassLoader.getSystemResourceAsStream("system_preferences_dummy_url.xml"));

		LectureSchedule schedule = new LectureSchedule();
		schedule.init();

		// lets check the private URL field using some reflection
		// powermock?
		Field urlField = LectureSchedule.class.getDeclaredField("url");
		urlField.setAccessible(true);
		assertEquals("http://myurl/onlineLecture/LectSchedule.htm", urlField.get(schedule));
	}

	@Test
	public void testShowCleanedUpHtmlInBrowser() throws IOException, InvalidPreferencesFormatException {
		// import the preference file.
		Preferences.importPreferences(ClassLoader.getSystemResourceAsStream("system_preferences.xml"));
		Preferences.importPreferences(ClassLoader.getSystemResourceAsStream("user_preferences.xml"));

		LectureSchedule schedule = new LectureSchedule();
		schedule.init();
		schedule.showCleanedUpHtmlInBrowser();
	}

}
