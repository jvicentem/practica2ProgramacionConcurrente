package practica2PC.modules;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import practica2PC.utils.FileAndFolderUtils;
import practica2PC.utils.LineParser;
import practica2PC.utils.RealDownloader;

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
			
			System.out.println("Descargando archivo "+fileName+", Por favor, espere...");
			ArrayList<Thread> threads = new ArrayList<Thread>();
			
			CountDownLatch latch = new CountDownLatch(getMaxDownloads());
			
			for (int i = 0; i < fileParts; i++) {
				threads.add(createDownloadThread(fileName, fileUrl + LineParser.PART_STRING + i, i, latch));
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
			
			threads.clear();
			
			createJoinPartsThread(fileName).start();
		}
	}
	
	private Thread createDownloadThread(String fileName, String url, int part, CountDownLatch latch) {
		return new Thread(() -> {
									try {
										latch.await();
									} catch (InterruptedException e) {
										e.printStackTrace();									
									}
									
									RealDownloader.downloadFile(url, getDestinationFolder());
									
									latch.countDown();
									
									System.out.println();
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
			String string = getDownloadList().size() + " archivos a descargar. \n \n" 
					+ "Archivo \n"
					+ "======================================================== \n";
	
			for (Map<String, Object> download : getDownloadList()) {
				string = string + download.get(LineParser.NAME_MAP_KEY);
				
				for (int i = 0; i < (int) download.get(LineParser.PARTS_MAP_KEY); i++) {
					string = string + "| \n"
									+ "--- " + download.get(LineParser.URL_MAP_KEY) + " \n";
				}
				
				string = string + "------------------------------------------------------------ \n";
			}
		
			return string + "\n \n";				
		}

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