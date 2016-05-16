package practica2PC.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LineParser {
	public static final String PART_STRING = ".part";
	
	public static final String NAME_MAP_KEY = "name";
	public static final String URL_MAP_KEY = "url";
	public static final String PARTS_MAP_KEY = "parts";
	
	
	private static final int NUM_OF_PARTS_INDEX = 2;
	private static final int FILE_NAME_INDEX = 1;
	private static final int URL_INDEX = 0;
	private static final String LINE_SEPARATOR = "\\s+";
	
	private LineParser() {}
	
	private static int getNumberOfParts(String[] line) {
		return Integer.parseInt(line[NUM_OF_PARTS_INDEX]);
	}
	
	private static String getFileName(String[] line) {
		return line[FILE_NAME_INDEX];
	}
	
	private static String getUrl(String[] line) {
		return line[URL_INDEX];
	}
	
	private static String[] splitLine(String line) {
		return line.split(LineParser.LINE_SEPARATOR);
	}
	
	public static Map<String, Object> lineAsMap(String line) {
		String[] splittedLine = splitLine(line);
		
		Map<String, Object> lineMap = new HashMap<>();
		
		lineMap.put(NAME_MAP_KEY, getFileName(splittedLine));
		lineMap.put(URL_MAP_KEY , getUrl(splittedLine));
		lineMap.put(PARTS_MAP_KEY, getNumberOfParts(splittedLine));
		
		return Collections.unmodifiableMap(lineMap);
	}
	
}
