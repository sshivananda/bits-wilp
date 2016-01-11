package org.kiranmohan.bits.wilp.lecture.schedule;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class ParseAndCleanupScheduleHTMLTest {
    private static File sampleScheduleFile=Paths.get("src/test/resources/WILP Live Lecture Schedule.html").toFile();
    private static String charset="windows-1252";
    
	@Test
	public void testParseAndCleanupScheduleHTML() throws IOException {
		
		List<String> skipPatternList = new ArrayList<>();
		skipPatternList.add("monday.*" );
		skipPatternList.add("tuesday.*" );
		skipPatternList.add("wednesday.*" );
		skipPatternList.add("thursday.*" );
		skipPatternList.add("friday.*" );
		skipPatternList.add("saturday.*" );
		skipPatternList.add("sunday.*" );
		skipPatternList.add("\\d{0,2}[:\\.]\\d{0,2}.*-.*\\d{0,2}[:\\.]\\d{0,2}.*" ); // time pattern
		skipPatternList.add("Time/Day" );
		
		Map<String, String> searchReplaceMap = new HashMap<>();
		searchReplaceMap.put("SS\\s*ZG513", "Network Security");
		searchReplaceMap.put("SS\\s*ZG526", "Distributed Computing");
		searchReplaceMap.put("SS\\s*ZG527", "Cloud Computing");
		searchReplaceMap.put("SS\\s*ZG582", "Telecom Network Management");
		
		ParseAndCleanupScheduleHTML parser = new ParseAndCleanupScheduleHTML(skipPatternList, searchReplaceMap);
		parser.process(sampleScheduleFile, charset);
		Path cleanedUpHtml = parser.getCleanedUpHtml();
		assertNotNull(cleanedUpHtml);
		System.out.println("cleanedUpHtml :" + cleanedUpHtml);
	}

}
