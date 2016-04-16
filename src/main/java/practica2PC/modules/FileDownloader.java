package practica2PC.modules;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import practica2PC.modules.threads.DownloaderMonitor;
import practica2PC.utils.FileAndFolderUtils;
import practica2PC.utils.LineParser;

public class FileDownloader {
	
	private int maxDownloads;
	private List<Map<String, Object>> downloadList;
	private String destinationFolder;
	
	public FileDownloader(int maxDownloads, String filePath, String destinationFolder) throws FileNotFoundException {
		this.downloadList = FileAndFolderUtils.readTextFile(filePath)
											  .stream().map(e -> (LineParser.lineAsMap(e)))
											  .collect(Collectors.toList());
		this.maxDownloads = maxDownloads;
		this.destinationFolder = destinationFolder;
	}
	
	public void downloadFiles() {
		for (Map<String, Object> file : getDownloadList()) {
			String fileName = (String) file.get(LineParser.NAME_MAP_KEY);
			String fileUrl = (String) file.get(LineParser.URL_MAP_KEY);
			int fileParts = (int) file.get(LineParser.PARTS_MAP_KEY);
			
			System.out.println("Descargando archivo "+fileName+" ...");
			
			ArrayList<Thread> threads = new ArrayList<Thread>();
						
			DownloaderMonitor monitor = new DownloaderMonitor();
			
			for (int i = 0; i < fileParts; i++) {
				String partUrl = generatePartUrl(fileUrl, fileName, i);
				threads.add(createDownloadThread(fileName, partUrl, i, monitor));
			}
			
			for (Thread th : threads) {
				th.start();
			}
			
			for (Thread th : threads) {
				try {
					th.join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			createJoinPartsThread(fileName).start();
			
			threads.clear();
		}
	}
	
	private Thread createDownloadThread(String fileName, String url, int part, DownloaderMonitor monitor) {
		return new Thread(() -> {
									try {
										monitor.downloadFile(getMaxDownloads(), fileName, url, part, getDestinationFolder());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
						, fileName + " part " + part);
	}
	
	private Thread createJoinPartsThread(String fileName) {
		return new Thread(() -> {
									FileAndFolderUtils.joinParts(fileName, getDestinationFolder());
								}
						 , "Joining " + fileName + " parts");
	}
	
	@Override
	public String toString() {
		if (getDownloadList().size() == 0) 
			return "Ningún archivo para descargar";
		else {
			String string = "Cola de descargas - " + getDownloadList().size() + " archivos a descargar. \n"
							+ "======================================================== \n";
	
			for (Map<String, Object> download : getDownloadList()) {
				string = string + download.get(LineParser.NAME_MAP_KEY) + " \n";
				
				for (int i = 0; i < (int) download.get(LineParser.PARTS_MAP_KEY); i++) {
					string = string + "| \n" 
									+ "--- " + generatePartUrl((String) download.get(LineParser.URL_MAP_KEY), (String) download.get(LineParser.NAME_MAP_KEY), i) + " \n";
				}
				
				string = string + "------------------------------------------------------------ \n";
			}
		
			return string + "\n";				
		}
	}
	
	private String generatePartUrl(String url, String fileName, int part) {
		return url + "/" + fileName + LineParser.PART_STRING + part;
	}
	
	private int getMaxDownloads() {
		return maxDownloads;
	}

	private List<Map<String, Object>> getDownloadList() {
		return downloadList;
	}
	
	private String getDestinationFolder() {
		return destinationFolder;
	}
	
}