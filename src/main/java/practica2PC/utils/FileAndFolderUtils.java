package practica2PC.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FileAndFolderUtils {
	
	private FileAndFolderUtils() {}
	
	public static void createFolder(String path) {
		File folder = new File(path);
		
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}	
	}
	
	public static BufferedReader openFile(String filePath) throws FileNotFoundException {
		try {
			return new BufferedReader(new FileReader(filePath));
		} catch(FileNotFoundException e) {
			throw e;
		}
	}
	
	public static List<String> readTextFile(String filePath) throws FileNotFoundException {
		BufferedReader reader;
	
		reader = openFile(filePath);
		
		ArrayList<String> lines = new ArrayList<>();
		
		String line = "";
		
		try {
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	
		return lines;
	}
	
	public static void mergeFileParts(String fileName, String folderPath) {
		 File ofile = new File(folderPath + File.separator + fileName);
		 FileOutputStream fos;
		 FileInputStream fis;
		 byte[] fileBytes;
		 int bytesRead = 0;
		 String[] files = new File(folderPath).list((path, name) -> Pattern.matches(fileName+Pattern.quote(".")+"part.*", name));
		 Arrays.sort(files);
		 
		 try {
			 fos = new FileOutputStream(ofile,true);
			 
			 for (String file : files) {
				 File f = new File(folderPath + File.separator + file);
				 fis = new FileInputStream(f);
				 fileBytes = new byte[(int) f.length()];
				 bytesRead = fis.read(fileBytes, 0,(int) f.length());
				 assert bytesRead == fileBytes.length;
				 assert bytesRead == (int) f.length();
				 fos.write(fileBytes);
				 fos.flush();
				 fis.close();
			 }
			 
			 fos.close();
			 
			 for (String file : files) 
				 deleteFileIfExists(folderPath + File.separator + file);
			 
		 } catch (Exception exception){
			 exception.printStackTrace();
		 }
	}
	
	public static void deleteFileIfExists(String filePath) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
