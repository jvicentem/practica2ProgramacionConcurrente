# Práctica 2: Gestor de descargas de ficheros

 El objetivo de esta práctica es desarrollar un gestor de descargas de ficheros concurrente.

 Para ello, se proporcionará una URL desde la que tendréis que descargar un fichero de texto plano con información de los ficheros a descargar.

 Cada línea tiene información de un fichero diferente. La información disponible para cada fichero será la siguiente:

<table>
  <tbody>
    <tr>
      <td>
         url

         nombre_fichero

         num_partes
      </td>
    </tr>
  </tbody>
</table>


donde  url  es  la  dirección  principal  donde  se  encuentra  el  fichero  dividido  en  partes, nombre_fichero es el nombre del fichero a descargar, y num_partes es el número de partes en  las  que  ese  fichero  se  encuentra  dividido.  A  partir  de  esta  información,  debemos reconstruir las direcciones de cada parte del fichero. Para ello, bastará con concatenar la url junto con el nombre del fichero, y añadir “.part” más el número de la parte que estamos descargando (comenzando en 0). Por ejemplo, para el fichero:

 http://web_descarga.com      fichero.pdf     3

 deberemos construir los siguientes enlaces:

 http://web_descarga.com/fichero.pdf.part0

 http://web_descarga.com/fichero.pdf.part1

 http://web_descarga.com/fichero.pdf.part2

 El destino de la descarga será una carpeta “downloads” creada en el mismo directorio desde el que se ejecuta la aplicación. Una vez descargadas todas las partes de un fichero, hay que unirlas para reconstruir el fichero original. Para unir los ficheros, debéis utilizar el método que tenéis disponible en el Código 1. Dicho método recibe como entrada el directorio donde se encuentran las partes descargadas y el nombre del fichero original (fichero.pdf en el ejemplo anterior). La ejecución de este método generará el fichero original en el directorio indicado.

 Una vez creado el fichero original, el programa deberá eliminar las partes descargadas.

 El  gestor  de  descargas  estará  implementado  en  la  clase  FileDownloader  y  debe  permitir realizar descargas simultáneas de varias partes de un mismo fichero. Para ello, el constructor de la clase recibirá como parámetro el número de descargas máximas que se pueden realizar de manera simultánea. Además, los ficheros deben ser descargados en orden, de manera que no comenzaremos la descarga de un nuevo fichero hasta que no haya finalizado la descarga de  todas  las  partes  del  fichero  anterior.  La  descarga  de  cada  una  de  las  partes  se  hará utilizando el código de apoyo que aparece en el Código 2.

 El número de ficheros a descargar será variable, así como el número de partes en las que se divide cada fichero, y el tamaño de cada una de las partes. El programa deberá asegurar que todos los threads que intenten descargar una parte en algún momento puedan hacerlo para evitar inanición.

 Ejemplo de fichero de descarga:

 https://dl.dropboxusercontent.com/u/1784661/download_list.txt

```java
File ofile = new File(dir+"/"+fileStart); 
FileOutputStream fos; 
FileInputStream fis; 
byte[] fileBytes; 
int bytesRead = 0; 
String[] files = new File(dir).list((path, name) -> Pattern.matches(fileStart+Pattern.quote(".")+"part.*", name)); 

Código 1: Reconstrucción de	un fichero dividido en diferentes partes.
try { 
	fos = new FileOutputStream(ofile,true); 

	for (String fileName : files) { 
		File f = new File(dir+"/"+fileName); 
		System.out.println(f.getAbsolutePath()); 
		fis = new FileInputStream(f); 
		fileBytes = new byte[(int) 
		f.length()]; 
		bytesRead = fis.read(fileBytes, 0,(int)  
		f.length()); 

		assert(bytesRead == fileBytes.length); 

		assert(bytesRead == (int) f.length()); 

		fos.write(fileBytes); 

		fos.flush(); 

		fileBytes = null; 

		fis.close(); fis = null; 

	} 

	fos.close(); 

	fos = null; 

} catch (Exception exception){ 
	exception.printStackTrace(); 
} 

```
Código 2: Descarga de un fichero dada una url
```java
  InputStream in = website.openStream(); 

  Path pathOut = Paths.get(path); 

  Files.copy(in, pathOut, StandardCopyOption.REPLACE_EXISTING); 

  in.close();
```


