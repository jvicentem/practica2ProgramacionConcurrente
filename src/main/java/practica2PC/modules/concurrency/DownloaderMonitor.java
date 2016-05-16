package practica2PC.modules.concurrency;

import java.io.File;

import practica2PC.utils.LineParser;
import practica2PC.utils.RealDownloader;

public class DownloaderMonitor {
	private int concurrentDownloads;
	
	public DownloaderMonitor() {
		this.concurrentDownloads = 0;
	}
	
	public void downloadFile(int maxConcurrentDownloads, String fileName, String url, int part, String destinationFolder) throws InterruptedException {
		increaseConcurrentDownloadsCount(maxConcurrentDownloads);
		
		RealDownloader.downloadFile(url, destinationFolder + File.separator + fileName + LineParser.PART_STRING + part);
		
		decreaseConcurrentDownloadsCount();
	}
	
	private synchronized boolean maxConcurrentDownloadsReached(int maxConcurrentDownloads) {
		return getConcurrentDownloads() == maxConcurrentDownloads;
	}
	
	private synchronized void increaseConcurrentDownloadsCount(int maxConcurrentDownloads) throws InterruptedException {
		while (maxConcurrentDownloadsReached(maxConcurrentDownloads)) 
			wait();
		
		setConcurrentDownloads(Math.incrementExact(getConcurrentDownloads()));
	}
	
	private synchronized void decreaseConcurrentDownloadsCount() {
		setConcurrentDownloads(Math.decrementExact(getConcurrentDownloads()));
		notify();
	}

	private int getConcurrentDownloads() {
		return concurrentDownloads;
	}

	private void setConcurrentDownloads(int runningThreads) {
		this.concurrentDownloads = runningThreads;
	}
	
}
