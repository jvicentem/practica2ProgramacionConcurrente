package practica2PC.modules;

import java.io.File;
import java.io.FileNotFoundException;

import practica2PC.utils.FileAndFolderUtils;

public class Menu {
	private static final String DOWNLOADS_LIST_FILE = "." + File.separator + "download_list.txt";
	private static final String DOWNLOADS_DESTINATION_FOLDER = "." + File.separator + "downloads";
	private static final int MAX_CONCURRENT_DOWNLOADS = 4;
	
	private Menu() {};
	
	public static final void execute() {
		try {
			FileDownloader fileDownloader = new FileDownloader(MAX_CONCURRENT_DOWNLOADS, DOWNLOADS_LIST_FILE, DOWNLOADS_DESTINATION_FOLDER);
			
			FileAndFolderUtils.createFolder(DOWNLOADS_DESTINATION_FOLDER);
			
			System.out.println(fileDownloader.toString());
			
			System.out.println("Descargando... Espere por favor... \n");
			
			fileDownloader.downloadFiles();
			
			System.out.println("\nDescargas finalizadas.");
		} catch (FileNotFoundException e) {
			System.out.println("Archivo de descargas no encontrado. Cerrando programa...");
		} 
	}

}
