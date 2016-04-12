package practica2PC.utils;

public class LineParser {
	public static final String PART_STRING = ".part";
	private static final int NUM_OF_PARTS_INDEX = 2;
	private static final int FILE_NAME_INDEX = 1;
	private static final int URL_INDEX = 0;
	private static final String LINE_SEPARATOR = "\\s+";
	
	private LineParser() {}
	
	public int getNumberOfParts(String line) {
		return Integer.parseInt(splitLine(line)[NUM_OF_PARTS_INDEX]);
	}
	
	public String getFileName(String line) {
		return splitLine(line)[FILE_NAME_INDEX];
	}
	
	public String getUrl(String line) {
		return splitLine(line)[URL_INDEX];
	}
	
	private String[] splitLine(String line) {
		return line.split(LineParser.LINE_SEPARATOR);
	}
	
}
