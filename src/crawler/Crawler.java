package crawler;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * 
 * @author Alberto Manuel Campos Clemente
 * @author Carlos Cambero Rojas
 * @version v4
 */
public class Crawler {

	private static Map<String, Ocurrencia> diccionario = new TreeMap<String, Ocurrencia>();
	private static Thesaurus thesauro = new Thesaurus();
	private static ThesaurusInverso thesauroInverso = new ThesaurusInverso();

	/*
	 * Método que permite contar los tokens de un fichero.
	 */
	public static void contarPalabras(String fileIn) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileIn));
		String linea;

		while ((linea = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(eliminarTildes(linea.toLowerCase()),
					" \",.:;(){}!°?\t''%/|[]<=>&#+*$-¨^~");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (thesauro.existe(s) || !thesauroInverso.existe(s)) {
					Object o = diccionario.get(s);
					if (o == null) {
						diccionario.put(s, new Ocurrencia(fileIn));
					} else {
						Ocurrencia ocurrencia = (Ocurrencia) o;
						ocurrencia.incrementarFrecuencia(fileIn);
						diccionario.put(s, ocurrencia);
					}
				}
			}
		}
		br.close();
	}

	/*
	 * Este método permite listar todos los ficheros de un directorio que cumplan
	 * con el filtro fileFilter y en caso de obtener un fichero diccionario.ser (que
	 * contiene la estructura con los diversos tokens analizado en alguna otra
	 * ejecución del crawler) permite combinar la información que contiene con el
	 * diccionario del crawler. Con esto se evita tener que analizar tokens
	 * previamente analizados. En caso de ser un fichero txt llama al método que
	 * permite procesar sus tokens.
	 */
	public static void listarContenido(File file) throws IOException, SAXException, TikaException {
		boolean diccGuardadoEnc = false;
		// File file = new File(pFile);
		if (!file.exists() || !file.canRead()) {
			System.out.println("ERROR. No puedo leer " + file);
			return;
		}
		if (file.isDirectory()) {

			File[] listFiles = file.listFiles();
			int i = 0;
			while (i < listFiles.length) {
				if (listFiles[i].getName().equals("diccionario.ser")) {
					diccGuardadoEnc = true;
					Map<String, Ocurrencia> diccGuardado = new TreeMap<String, Ocurrencia>();
					Objeto obj = new Objeto();
					diccGuardado = obj.cargarDiccionario(listFiles[i].getAbsolutePath());
					diccGuardado
							.forEach((key, value) -> diccionario.merge(key, value, (v1, v2) -> new Ocurrencia(v1, v2)));
				}
				i++;
			}

			if (!diccGuardadoEnc) {
				for (int j = 0; j < listFiles.length; j++) {
					listarContenido(listFiles[j]);
				}
			}

		} else
			try {

				String extension = file.getName().substring(file.getName().lastIndexOf("."));

				switch (extension) {
				// Se usa esta forma debido a que se esta empleando java 1.8 , para meter en un
				// case dos o más valores se necesita java 14+
				case ".java":
				case ".cpp":
				case ".c":
				case ".txt":
					contarPalabras(file.getAbsolutePath());
					break;
				case ".pdf":
					procesarPDF(file);
					break;
				case ".html":
					procesarHTML(file);
					break;
				case ".mp3":
					procesarMP3(file);
					break;
				default:
					break;

				}

			} catch (FileNotFoundException fnfe) {
				System.out.println("ERROR. Fichero desaparecido en combate  ;-)");
			}
	}

	public static void procesarMP3(File file) throws IOException, SAXException, TikaException {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext pcontext = new ParseContext();

		Mp3Parser pdfparser = new Mp3Parser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);
		procesadoArchivo(handler, file.getAbsolutePath());

	}

	public static void procesarHTML(File file) throws IOException, SAXException, TikaException {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext pcontext = new ParseContext();

		HtmlParser pdfparser = new HtmlParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);
		procesadoArchivo(handler, file.getAbsolutePath());

	}

	// El procesador de PDF lanza un ERROR sobre SLF4J, pero este no impide que
	// procese el PDF.
	public static void procesarPDF(File file) throws IOException, SAXException, TikaException {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext pcontext = new ParseContext();

		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);

		procesadoArchivo(handler, file.getAbsolutePath());

	}

	public static void procesadoArchivo(BodyContentHandler handler, String filePath) throws IOException {

		BufferedReader br = new BufferedReader(new StringReader(handler.toString()));
		String linea;

		while ((linea = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(eliminarTildes(linea.toLowerCase()),
					" \",.:;(){}!°?\t''%/|[]<=>&#+*$-¨^~");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				if (thesauro.existe(s) || !thesauroInverso.existe(s)) {
					Object o = diccionario.get(s);
					if (o == null) {
						diccionario.put(s, new Ocurrencia(filePath));
					} else {
						Ocurrencia ocurrencia = (Ocurrencia) o;
						ocurrencia.incrementarFrecuencia(filePath);
						diccionario.put(s, ocurrencia);
					}
				}
			}
		}
		br.close();
	}

	/*
	 * Escribe en un fichero tanto el token con su frecuencia global como la ruta de
	 * los fichero en la que aparecen y su frecuencia local
	 */
	public static void escribirTokensFichero() throws IOException {
		List<String> claves = new ArrayList<String>(diccionario.keySet());
		Collections.sort(claves);

		PrintWriter pr = new PrintWriter(new FileWriter("./SalidaGeneral.vista"));
		Iterator<String> i = claves.iterator();
		while (i.hasNext()) {
			Object k = i.next();
			Ocurrencia ocurrencia = diccionario.get(k);
			pr.println(k + " : " + ocurrencia.getFrecuencia());

			List<String> ficheros = new ArrayList<String>(ocurrencia.getDicc().keySet());
			Collections.sort(ficheros);

			Iterator<String> j = ficheros.iterator();
			while (j.hasNext()) {
				Object q = j.next();
				pr.println("        " + q + " : " + ocurrencia.getFrecuenciaFichero((String) q));
			}
		}
		pr.close();
	}

	// Método de ordenación
	public static void insercionBinaria(List<String> fichero, List<Integer> frecuencia) {
		Integer X;
		String ficheroX;

		Integer Der;
		Integer Izq;
		Integer Medio;

		for (int i = 1; i < frecuencia.size(); i++) {
			X = frecuencia.get(i);
			ficheroX = fichero.get(i);
			Izq = 0;
			Der = i - 1;
			while (Izq <= Der) {
				Medio = (Izq + Der) / 2;
				if (X > frecuencia.get(Medio))
					Der = Medio - 1;
				else
					Izq = Medio + 1;
			}
			for (int j = i - 1; j >= Izq; j--) {
				frecuencia.set(j + 1, frecuencia.get(j));
				fichero.set(j + 1, fichero.get(j));
			}
			frecuencia.set(Izq, X);
			fichero.set(Izq, ficheroX);
		}
	}

	/*
	 * Permite realizar una busqueda según un token.
	 */

	public static String busqueda(String token) {
		Boolean noIntersection = false;
		Boolean primeraVez = true;

		String resultado = "";

		Set<Entry<String, Integer>> intersection = new HashSet<>();
		Set<Entry<String, Integer>> copiaSetFichero;

		Ocurrencia ocurrencia;

		List<Integer> vectorFrecuencia = new ArrayList<>();
		List<String> vectorFichero = new ArrayList<>();

		String aux = eliminarTildes(token.toLowerCase());

		StringTokenizer st = new StringTokenizer(aux, "\" ,.:;(){}!°?\t''%/|[]<=>&#+*$-¨^~");
		Integer numToken = st.countTokens();
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (thesauro.existe(s) || !thesauroInverso.existe(s)) {
				ocurrencia = diccionario.get(s);

				if (ocurrencia == null) {
					resultado = resultado + "\nNo se ha podido encontrar ningún archivo que contenga '" + s + "'\n";
					noIntersection = true;
					intersection.retainAll(new HashSet<>()); // Colección vacía
				} else {
					if (numToken > 1 && !noIntersection) {
						if (primeraVez) {
							primeraVez = false;
							intersection = new HashSet<>(ocurrencia.getDicc().entrySet());

						} else {
							copiaSetFichero = new HashSet<>(ocurrencia.getDicc().entrySet());
							intersection.retainAll(copiaSetFichero);
						}
					}
					// Sinonimos, función solo disponible para 1 token.
					else if (numToken == 1) {
						if (thesauro.getSinonimos(s) != null) {
							for (String item : thesauro.getSinonimos(s)) {
								Ocurrencia o = diccionario.get(item);
								if (o != null) {
									resultado = resultado + "\n" + "Se han encontrado " + ocurrencia.getFrecuencia()
											+ " sinónimo de '" + s + "' (" + item + ") en los siguientes archivos:\n";
									List<String> ficheros = new ArrayList<String>(o.getDicc().keySet());
									Collections.sort(ficheros);

									Iterator<String> j = ficheros.iterator();
									while (j.hasNext()) {
										Object q = j.next();
										vectorFrecuencia.add(o.getFrecuenciaFichero((String) q));
										vectorFichero.add((String) q);
									}
									insercionBinaria(vectorFichero, vectorFrecuencia);
									for (int i = 0; i < vectorFrecuencia.size(); i++) {
										resultado = resultado + vectorFichero.get(i) + " : " + vectorFrecuencia.get(i)
												+ "\n";
									}
									vectorFrecuencia.clear();
									vectorFichero.clear();
								}
							}
						}
					}
					resultado = resultado + "\n" + "Se han encontrado " + ocurrencia.getFrecuencia() + " token '" + s
							+ "' en los siguientes archivos:\n";

					List<String> ficheros = new ArrayList<String>(ocurrencia.getDicc().keySet());
					Collections.sort(ficheros);

					Iterator<String> j = ficheros.iterator();
					while (j.hasNext()) {
						Object q = j.next();
						vectorFrecuencia.add(ocurrencia.getFrecuenciaFichero((String) q));
						vectorFichero.add((String) q);
					}
					insercionBinaria(vectorFichero, vectorFrecuencia);
					for (int i = 0; i < vectorFrecuencia.size(); i++) {
						resultado = resultado + vectorFichero.get(i) + " : " + vectorFrecuencia.get(i) + "\n";
					}
					vectorFrecuencia.clear();
					vectorFichero.clear();
				}
			}
		}

		if (numToken > 1) {
			String auxText = "Se ha encontrado '" + token + "' en los siguientes archivos:\n";
			if (intersection.isEmpty()) {
				auxText = auxText + "No se han encontrado ningún documento con todos esos tokens.\n";
			} else {
				Iterator<Map.Entry<String, Integer>> j = intersection.iterator();

				while (j.hasNext()) {
					Entry<String, Integer> q = j.next();
					auxText = auxText + q.getKey() + "\n";
				}
			}

			resultado = auxText + resultado;
		}

		if (numToken == 0) {
			resultado = "Introduzca algún token, por favor.";
		}
		return resultado;

	}

	public static String eliminarTildes(String cadena) {
		String cadenaSinTildes = cadena.replaceAll("[áÁ]", "a").replaceAll("[éÉ]", "e").replaceAll("[íÍ]", "i")
				.replaceAll("[óÓ]", "o").replaceAll("[úÚ]", "u");
		return cadenaSinTildes;
	}

	public static void main(String[] args) throws IOException, SAXException, TikaException {

		// Comprobación de entrada de 1 parámetro
		if (args.length < 1) {
			System.out.println("ERROR. Ejecutar: >java -jar Crawler.jar nombre_archivo");
			return;
		}

		File file = new File(args[0]);
		Objeto obj = new Objeto();

		String busqueda;
		String[] options = { "Salir", "Opción 2", "Opción 1" };
		int seleccion;

		JOptionPane.showMessageDialog(null, "Ruta seleccionada: " + args[0]);

		try {
			if (!thesauro.existeThesauro(file)) {
				thesauro.procesarThesauro();
			} else {
				thesauro.cargar(file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (!thesauroInverso.existeThesauro(file)) {
				thesauroInverso.procesarThesauro();
			} else {
				thesauroInverso.cargar(file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listarContenido(file);

		if (file.isDirectory()) {
			obj.salvarThesauroInverso(thesauroInverso.getThesauro(), file + "/ThesauroInverso.ser");
			obj.salvarThesauro(thesauro.getThesauro(), file + "/Thesauro.ser");
			obj.salvarDiccionario(diccionario, file + "/diccionario.ser");
		}
		escribirTokensFichero();

		do {
			seleccion = JOptionPane.showOptionDialog(null,
					"Seleccione una de las siguientes opciones \n 1. Búsqueda de tokens \n 2. Búsqueda de sinónimos \n 3. Salir del Crawler",
					"Seleccione una opción", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
					options[0]);
			switch (seleccion) {
			case 2:
				do {
					busqueda = JOptionPane.showInputDialog("¿Qué desea buscar?");
					if (busqueda != null) {
						String resultado = busqueda(busqueda);
						System.out.println(resultado);
						System.out.print("==========================");
						JOptionPane.showMessageDialog(null, resultado);

					}
				} while (busqueda != null);
				break;

			case 1:
				do {
					busqueda = JOptionPane.showInputDialog("¿De qué palabra desea buscar los sinónimos?");
					if (busqueda != null) {
						thesauro.sinonimos(busqueda);
					}
				} while (busqueda != null);
				break;
			default:
			}

		} while (seleccion != 0);
		JOptionPane.showMessageDialog(null, "El programa finalizó correctamente.");

	}

}
