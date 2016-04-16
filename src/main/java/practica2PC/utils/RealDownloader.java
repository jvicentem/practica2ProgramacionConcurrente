package practica2PC.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class RealDownloader {
	private RealDownloader() {}
	
	public static void downloadFile(String url, String destinationFolder) {
		URL website = null;
		
		try {
			website = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			InputStream in = website.openStream();
			Path pathOut = Paths.get(destinationFolder);
			Files.copy(in, pathOut, StandardCopyOption.REPLACE_EXISTING);
			in.close();			
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}
	
}
