package practica2PC.modules;

import java.io.File;
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
			
			System.out.println("Descargando archivo "+fileName+" ...");
			
			ArrayList<Thread> threads = new ArrayList<Thread>();
			ArrayList<CountDownLatch> latches = new ArrayList<CountDownLatch>();
			
			int groups = (int) Math.ceil((float) fileParts / (float) getMaxDownloads());
			
			for (int i = 0; i < groups-1; i++) {
				latches.add(new CountDownLatch(getMaxDownloads()));
			}
			
			latches.add(null);
			latches.add(0, null);
			
			// latches = [null, latch1, latch2, ..., latchN, null] Donde N es groups - 1
			
			for (int i = 0; i < fileParts; i++) {
				int group = i / getMaxDownloads();
				/* Calculo el grupo al que pertenecería el thread 
				 * en función de la parte (i) y del número máximo de threads concurrentes.
				 * 
				 * Le paso el latch como argumento a createDownloadThread (el latch de grupo siguiente y el suyo).
				 * 
				 * Menos los del primer grupo, el resto hacen await de su latch y hacen countdown del latch de grupo siguiente.
				 * */
				
				String partUrl = generatePartUrl(fileUrl, fileName, i);
				threads.add(createDownloadThread(fileName, partUrl, i, latches.get(group), latches.get(group+1)));
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
			latches.clear();
			
			createJoinPartsThread(fileName).start();
		}
	}
	
	private Thread createDownloadThread(String fileName, String url, int part, CountDownLatch thisLatch, CountDownLatch nextLatch) {
		return new Thread(() -> {
									//Si son los N primeros threads donde N es maxDownloads...
									if (thisLatch != null)
										try {
											thisLatch.await();
										} catch (InterruptedException e) {
											e.printStackTrace();									
										}
									
									RealDownloader.downloadFile(url, getDestinationFolder() + File.separator + fileName + LineParser.PART_STRING + part);
									
									//Si son los N últimos threads donde N es maxDownloads...
									if (nextLatch != null)
										nextLatch.countDown();
									
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