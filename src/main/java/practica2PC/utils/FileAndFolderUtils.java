package practica2PC.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class FileAndFolderUtils {
	
	private FileAndFolderUtils() {}
	
	public static void createFolder(String path) {
		File folder = new File(path);
		
		if(folder.isDirectory()){
			try {
				FileUtils.forceDelete(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	public static void joinParts(String fileName, int parts, String folderPath) {
		 File ofile = new File(folderPath+"/"+fileName);
		 FileOutputStream fos;
		 FileInputStream fis;
		 byte[] fileBytes;
		 int bytesRead = 0;
		 String[] files = new File(folderPath).list((path, name) -> Pattern.matches(fileName+Pattern.quote(".")+"part.*", name));
		 Arrays.sort(files);
		 
		 try {
			 fos = new FileOutputStream(ofile,true);
			 
			 for (String file : files) {
				 File f = new File(folderPath+"/"+file);
				 //System.out.println(f.getAbsolutePath());
				 fis = new FileInputStream(f);
				 fileBytes = new byte[(int) f.length()];
				 bytesRead = fis.read(fileBytes, 0,(int) f.length());
				 assert(bytesRead == fileBytes.length);
				 assert(bytesRead == (int) f.length());
				 fos.write(fileBytes);
				 fos.flush();
				 fileBytes = null;
				 fis.close();
				 fis = null;
			 }
			 
			 fos.close();
			 fos = null;
		 } catch (Exception exception){
			 exception.printStackTrace();
		 }
	}
	
}